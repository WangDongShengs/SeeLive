package com.wds.seelive.data.repository;

import androidx.annotation.NonNull;

import com.m.k.mvp.base.IBaseCallBack;
import com.m.k.mvp.data.BaseRepository;
import com.m.k.mvp.data.request.MvpRequest;
import com.m.k.mvp.data.request.RequestType;
import com.m.k.mvp.data.response.MvpResponse;
import com.m.k.mvp.data.response.ResponseType;
import com.m.k.mvp.manager.MvpManager;
import com.m.k.mvp.utils.Logger;
import com.m.k.mvp.utils.MvpDataFileCacheUtils;
import com.trello.rxlifecycle4.LifecycleProvider;

import com.wds.seelive.Constrant;
import com.wds.seelive.data.entity.News;
import com.wds.seelive.data.entity.RecommendData;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * OkHttp 网络请求：
 *
 *
 * map:负责把json 串转成对象  把对象包装到response上
 *
 *
 * doOnNext :插入一个做耗时操作  ：
 *
 * 可以：
 * 做数据缓存 在子线程   拿到MvpResponse对象    有一个Recommend对象
 *
 *
 * IO 线程    把线程切入到工作线程
 *
 * 主线程  ：负责把线程切入到主线程
 *
 * compose ：制定生命周期执行时接触订阅关系
 *
 * observer: 观察者

 *

 *

 * 推荐页的请求方式：
 *          1.第一次  Fragment 创建时（启动）请求
 *                   a.冷启动App 时：
 *                              i: 内存里面的缓存肯定没有，尝试加载Sdcard，如果scard没有就请求服务器(数据回来后，存储内存和sdcard缓存)，如果有，在子线程里面去读取sdcard缓存的数据，（可以放入内存中）然后主线程里面回调给P层
 *                      b.ViewPager切换时导致Fragment 销毁重建时
 *                                i:从内存里面读取，把上一次该栏目请求的所有的数据，回调给P层，比如你在推荐栏目加载（loading）了5页数据，然后你切换到请求栏目，这个时候推荐Fragment 是不是已经销毁了，再次回到推荐是，应该恢复到上次销毁之前的状态（5页数据不变）。为了实现这个效果。必须对每个栏目的数据做缓存
 *                       如果缓存没有:从服务器加载，加载回来写入缓存：
 *                                 内存缓存： 清空缓存（本来就没有）
 *          2.刷新：下拉时请求
 *                       a.肯定不能读任何缓存，刷新成功后，把对应栏目的缓存清空，用新的数据替换，不管是内存还是sdcard
 *                                i: 内存缓存：清空缓存，放入新的
 *                                ii: 替换原来的缓存
 *           3.加载更多： 上拉时请求
 *                        a.也不能读缓存，加载成功后，往对应的栏目的缓存里面的newsList 里面追加数据（还需要把服务器返回的start,nmber,pointTitem 赋值给缓存对应的对象   为了页面切换是，从缓存里面读取数据时加载更多时能衔接上），同时还有替换sdcard里面的缓存。
 *              缓存规则：
 *                                    内存替换
 *                                     sdcard：替换

 * 准备工作：
 * 1.定义一个内存缓存的数据结构  hashMap<String ,RecommendData>
 *  2.写一个工具类：
 *                   a.用于缓存对象（把对象转出 json, 加密写入文件）
 *                   b.用于读取缓存对象（把加密后的解密成 json,并把json 转出我们需要的对象）
 */
//使用双检索单列模式
public class RecommendNewsRepository extends BaseRepository {

    private volatile static RecommendNewsRepository mInstance;

    private static final String CACHE_FILE_NAME_PREFIX = "cache_";
    private RecommendNewsRepository(){

    }
    // 内存缓存， key 对应的栏目Id, value : 该栏目对应的数据
    private HashMap<String, RecommendData> mMemoryCache = new HashMap<>();


    public static  RecommendNewsRepository getInstance(){
        if(mInstance == null)
            synchronized (RecommendNewsRepository.class){
                if(mInstance == null){
                    mInstance = new RecommendNewsRepository();
                }
            }

        return mInstance;
    }






    @Override
    public <T> void doRequest(LifecycleProvider lifecycleProvider, MvpRequest<T> request, IBaseCallBack<T> callBack) {
        // super.doRequest(lifecycleProvider, request, callBack);

        String columnId = request.getParams().get(Constrant.RequestKey.KEY_COLUMN_ID).toString();

        switch (request.getRequestType()){

            case FIRST:{ // 第一次
                // step1  先从内存里面查找，有直接回调到P
                MvpResponse response = getFromMemory(columnId);
                if(response != null){
                    callBack.onResult(response); // 指的是viewpager 切换时发生的请求
                    return;
                }


                // step2 如果内存没有，从sdcard 查找 然后回调到P

                Observable.create((ObservableOnSubscribe<MvpResponse>) emitter -> {
                    MvpResponse<RecommendData> cacheData = readFromSdcard(columnId);
                    if(cacheData != null){
                        saveToMemory(cacheData,RequestType.FIRST,columnId);
                        emitter.onNext(cacheData);
                    }else{
                        emitter.onError(new NullPointerException("no cache data from sdcard"));
                    }

                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mvpResponse -> callBack.onResult(mvpResponse), new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                // step3 ,如果sdcard 没有，就从服务器取
                                doRequest(lifecycleProvider,request,new CacheTask<>(request),callBack);
                            }
                        });


                break;
            }

            default:{ // 刷新和加载更多
                // 请求服务器
                doRequest(lifecycleProvider,request,new CacheTask<>(request),callBack);
                break;
            }

        }

    }


    private  class  CacheTask<T> implements Consumer<MvpResponse<T>>{

        private MvpRequest request;

        CacheTask(MvpRequest request) {
            this.request = request;
        }
        @SuppressWarnings("ALL")
        @Override
        public void accept(MvpResponse<T> mvpResponse) throws Throwable {

            // 接收到上游发送的数据，做缓存

            News news = new News();
            news.setType(7);
            News.Ad ad = new News.Ad();
            ad.setLayout(5);
            ad.setId("1232143");
            ad.setTitle("这个一个我们自己插入的大图广告");
            ad.setAd_url("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3830946526,2769361828&fm=26&gp=0.jpg");
            ad.setTarget_href("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3830946526,2769361828&fm=26&gp=0.jpg");
            news.setAd(ad);
            RecommendData recommendData = (RecommendData) mvpResponse.getData();
            recommendData.getNewsList().add(0,news);



            news = new News();
            news.setType(7);
            ad = new News.Ad();
            ad.setLayout(4);
            ad.setId("1232144");
            ad.setAd_url("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2428034512,2003179618&fm=26&gp=0.jpg");
            ad.setTarget_href("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1599046487679&di=0f28f382115333e2e94f89689616bc88&imgtype=0&src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2Fddd6368f875f0434e979aa256832cfc48dc490e6113af-tMIEBd_fw658");
            news.setAd(ad);
            recommendData = (RecommendData) mvpResponse.getData();
            recommendData.getNewsList().add(3,news);



            news = new News();
            news.setType(7);
            ad = new News.Ad();
            ad.setLayout(6);
            ad.setId("1232145");
            ad.setAd_url("https://res.exexm.com/cw_145225549855002");
            ad.setTarget_href("https://res.exexm.com/cw_145225549855002");
            ad.setTitle("我是自己插入的视频广告");
            news.setAd(ad);
            recommendData = (RecommendData) mvpResponse.getData();
            recommendData.getNewsList().add(4,news);


            news = new News();
            news.setType(7);
            ad = new News.Ad();
            ad.setLayout(7);
            ad.setId("1232146");
            ad.setAd_url("http://7xjmzj.com1.z0.glb.clouddn.com/20171026175005_JObCxCE2.mp4");
            ad.setTarget_href("http://7xjmzj.com1.z0.glb.clouddn.com/20171026175005_JObCxCE2.mp4");
            news.setAd(ad);
            recommendData = (RecommendData) mvpResponse.getData();
            recommendData.getNewsList().add(5,news);

            String key =  request.getParams().get(Constrant.RequestKey.KEY_COLUMN_ID).toString();

            saveToMemory((MvpResponse<RecommendData>) mvpResponse,request.getRequestType(),key);

            saveToSdcard((MvpResponse<RecommendData>) mvpResponse,key);
        }
    }


    private void saveToMemory(MvpResponse<RecommendData> response, RequestType type,String key){

        if(type != RequestType.LOAD_MORE){ // 不是加载更多的情况下，清空，替换
            mMemoryCache.remove(key); // 清空内存


            RecommendData recommendData = new RecommendData();
            RecommendData serverData = response.getData();
            recommendData.setAlbumId(serverData.getAlbumId());
            recommendData.setAlbumNews(serverData.getAlbumNews());
            recommendData.setAlbumTitle(serverData.getAlbumTitle());
            recommendData.setBannerList(serverData.getBannerList());
            recommendData.setFlashId(serverData.getFlashId());
            recommendData.setFlashNews(serverData.getFlashNews());
            recommendData.setMore(serverData.getMore());
            recommendData.setPointTime(serverData.getPointTime());
            recommendData.setStart(serverData.getStart());


            recommendData.setNewsList(new ArrayList<>(serverData.getNewsList()));
            mMemoryCache.put(key,recommendData);

            Logger.d(" list code = %s",response.getData().getNewsList().hashCode());
        }else{

            // 加载更多，追加
            RecommendData cacheData = mMemoryCache.get(key);

            RecommendData serverData = response.getData();
            Logger.d("  load more list code = %s",cacheData.getNewsList().hashCode());

            if(cacheData != null && serverData != null){
                cacheData.setStart(serverData.getStart());
                cacheData.setNumber(serverData.getNumber());
                cacheData.setPointTime(serverData.getPointTime());
                cacheData.setMore(serverData.getMore());
                cacheData.getNewsList().addAll(serverData.getNewsList());
            }


        }
    }


    private  void saveToSdcard(MvpResponse<RecommendData> response,String key){

        MvpDataFileCacheUtils.saveEncryptedDataToFile(getCacheDataSdcardFile(key),response.getData());
    }

    private MvpResponse getFromMemory(String key){
        RecommendData data = mMemoryCache.get(key);
        if(data != null){
            MvpResponse<RecommendData> response = new MvpResponse<>();
            return response.setData(data).setCode(1).requestType(RequestType.FIRST).responseType(ResponseType.MEMORY);
        }
        return null;
    }

    private MvpResponse<RecommendData> readFromSdcard(String key){
        RecommendData data =  MvpDataFileCacheUtils.getencryptedDataFromFile(getCacheDataSdcardFile(key),RecommendData.class);

        if( data != null){
            MvpResponse<RecommendData> response = new MvpResponse<>();
            return response.setData(data).setCode(1).requestType(RequestType.FIRST).responseType(ResponseType.SDCARD);
        }
        return null;
    }

    private File getCacheDataSdcardFile(String columnId){
        return  new File(MvpManager.getExternalCacheDir(),CACHE_FILE_NAME_PREFIX + columnId);
    }

    public static void destroy(){
        mInstance = null;
    }

}
