package com.wds.seelive.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.MergeAdapter;

import com.m.k.mvp.base.v.BaseSmartFragment1;
import com.m.k.mvp.data.request.RequestType;
import com.m.k.mvp.data.response.MvpResponse;
import com.m.k.mvp.data.response.ResponseType;
import com.m.k.mvp.utils.Logger;
import com.m.k.mvp.widgets.MvpLoadingView;

import com.m.k.systemui.uitils.SystemFacade;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.wds.libvideo.MkVideoScrollListener;
import com.wds.seelive.R;
import com.wds.seelive.data.entity.NewsData;
import com.wds.seelive.databinding.FragmentRecommendNewsPageBinding;
import com.wds.seelive.home.recommend.page.AlbumAdapter;
import com.wds.seelive.home.recommend.page.BannerAdapter;
import com.wds.seelive.home.recommend.page.NewsListAdapter;

import java.util.ArrayList;

public abstract class NewsFragment<T extends NewsData> extends BaseSmartFragment1<T> {



   private static final String PLAY_TAG_PREFIX = "PLAY_TAB_";

   public static final  int PAGE_TYPE_RECOMMEND = 0x0; // 推荐页
    public static final  int PAGE_TYPE_VIDEO = 0x1; // 视屏页
    public static final  int PAGE_TYPE_SPECIAL = 0x2; // 专栏页

   private FragmentRecommendNewsPageBinding binging;

   private MergeAdapter mAdapter;
   private BannerAdapter mBannerAdapter;
   private NewsListAdapter mNewsAdapter;
   private AlbumAdapter mAlbumAdapter;


   private MvpResponse<T> response;


    private long pointTime;
    private int number;
    private int start;
    private int targetPosition;
    private int targetOffset;
    private int more;




    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recommend_news_page;
    }

    @Override
    protected void bindView(View view) {
        super.bindView(view);
       binging = FragmentRecommendNewsPageBinding.bind(view);

    }
//*************************************用于获页面中第一个itme 的数据*************************************************************
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        LinearLayoutManager layoutManager =  ((LinearLayoutManager)binging.newsRecyclerView.getLayoutManager());
        int fistVisiblePosition =  layoutManager.findFirstVisibleItemPosition();

        Logger.d("fistVisiblePosition = %s",fistVisiblePosition);
        View itemView = layoutManager.findViewByPosition(fistVisiblePosition);
        if(itemView == null){
            return;
        }
        int y = (int) itemView.getY();

        outState.putInt("position",fistVisiblePosition);
        outState.putInt("offset",y);

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            targetPosition = savedInstanceState.getInt("position");
            targetOffset = savedInstanceState.getInt("offset");
        }
    }
//*************************************用于获页面中第一个itme 的数据*************************************************************


    @Override
    protected void initView() {
        //刷新和加载更多
        binging.smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refresh();
            }
        });
        //new 个合并适配器
        mAdapter = new MergeAdapter();
        //将banner 适配器   和新闻列表适配器合并
        mBannerAdapter = new BannerAdapter(this);
        mNewsAdapter = new NewsListAdapter(makeTag(),getPageType());



        mAdapter.addAdapter(mBannerAdapter);
        mAdapter.addAdapter(mNewsAdapter);


        binging.newsRecyclerView.setAdapter(mAdapter);
        binging.newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recycler 监听视频播放
        binging.newsRecyclerView.addOnScrollListener(new MkVideoScrollListener((LinearLayoutManager) binging.newsRecyclerView.getLayoutManager()));
        //加载数据
        loadData();

    }
    //加载第一次进入的数据
    protected void loadData() {
        //动画没背景
        showFullLoading();
        loadData(RequestType.FIRST,start,number,pointTime);
    }
    //刷新数据
    private void refresh(){
        loadData(RequestType.REFRESH,0,0,0);
    }
    //加载更多数据
    private void loadMore(){
        loadData(RequestType.LOAD_MORE,start,number,pointTime);
    }

    //创建loadData 的加载数据  的抽象方法  传入参数为 类型  ，开始，数，时间
    protected abstract void loadData(RequestType type, int start, int number, long pointTime);


//结果
    @Override
    public void onResult1(MvpResponse<T> response) {
       //如果获取数据成功
        if(response.isOk()){
            //新闻开始
            start = response.getData().getStart();
            //新闻数量
            number = response.getData().getNumber();
            //新闻时间
            pointTime = response.getData().getPointTime();
            //更多
            more = response.getData().getMore();
            //如果是第一次进入
            if (response.getRequestType() == RequestType.FIRST) {
                //关闭动画
                closeLoading();
                //bannner list数据不为空
                if (!SystemFacade.isListEmpty(response.getData().getBannerList())) {
                    //获取bannerAdapter 的banner 数据
                    ArrayList<BannerAdapter.BannerWrapData> arrayList = new ArrayList<>();
                    //添加到集合中  在banner 适配器的BannerWrapData  bean 类中传入两个参数 ，一个是banner滑动数据，一个是跑马灯数据
                    arrayList.add(new BannerAdapter.BannerWrapData(response.getData().getBannerList(), response.getData().getFlashNews()));
                    //适配器获取set list 方法传入arrayList
                    mBannerAdapter.submitList(arrayList);
                }
                //将新闻类的数据传入到新闻接口中
                mNewsAdapter.submitList(response.getData().getNewsList());
                //滚到到目标位置
                scrollToTargetPosition();

                //如果结果类型为sd空中获取数据 就 自动刷新5毫秒
                if (response.getType() == ResponseType.SDCARD) {
                    binging.smartRefreshLayout.autoRefresh(500);
                } else {
                     //否则就判断是否是位于屏幕播放广告
                    MkVideoScrollListener.playIfNeed(binging.newsRecyclerView);

                }
            }//如果类型是刷新
            else if (response.getRequestType() == RequestType.REFRESH) {
                //如果banner 数据不为空
                if (!SystemFacade.isListEmpty(response.getData().getBannerList())) {
                    ArrayList<BannerAdapter.BannerWrapData> arrayList = new ArrayList<>();
                    arrayList.add(new BannerAdapter.BannerWrapData(response.getData().getBannerList(), response.getData().getFlashNews()));
                    mBannerAdapter.submitList(arrayList);
                }
                mNewsAdapter.submitList(response.getData().getNewsList());
                //刷新
                binging.smartRefreshLayout.finishRefresh();
                //如果恢复原来状态
                if (isResumed()) {
                    //就播放广告
                    MkVideoScrollListener.playIfNeed(binging.newsRecyclerView);
                }

            }//如果类型是加载更多
            else if (response.getRequestType() == RequestType.LOAD_MORE) {
                //加载数据
                mNewsAdapter.loadMore(response.getData().getNewsList());
                //加载更多 延时5毫秒
                binging.smartRefreshLayout.finishLoadMore(500);
            }


            if(more == 0){ // 没有更多了
                //恢复没有更多数据的原始状态
                binging.smartRefreshLayout.setNoMoreData(true);
            }

        }//获取数据失败
        else{
            //如果类型是第一次进入
            if (response.getRequestType() == RequestType.FIRST) {
                //监听到loading的错误页面  并监听加载更多按钮
                onError(response.getMsg(), new MvpLoadingView.OnRetryCallBack() {
                    @Override
                    public void onRetry() {
                        loadData();
                    }
                });
            }//如果类型是刷新
            else if (response.getRequestType() == RequestType.REFRESH) {
                binging.smartRefreshLayout.finishRefresh();
                showToast("刷新失败");
            }//如果类型是加载更多
            else if (response.getRequestType() == RequestType.LOAD_MORE) {
                binging.smartRefreshLayout.finishLoadMore(500);
                showToast("加载更多失败");
            }

        }

    }
   //滚动到目标位置
    private void scrollToTargetPosition() {

        binging.newsRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager layoutManager = (LinearLayoutManager) binging.newsRecyclerView.getLayoutManager();
                int position = layoutManager.findFirstVisibleItemPosition();

                if(position != targetPosition){
                    layoutManager.scrollToPositionWithOffset(targetPosition,targetOffset);
                }

            }
        });
    }




    @Override
    public void onResume() {
        super.onResume();
        //播放广告
        MkVideoScrollListener.playIfNeed(binging.newsRecyclerView);
    }


    @Override
    public void onPause() {
        super.onPause();
        Logger.d("%s  play tag = %s  hashcode =%s","", GSYVideoManager.instance().getPlayTag(),hashCode());
        //如果暂停 视频管理
        if (GSYVideoManager.instance().getPlayPosition() > 0 && GSYVideoManager.instance().getPlayTag().equals(makeTag())) {
            //视频管理为暂停
            GSYVideoManager.onPause();
        }



    }
    private String makeTag(){
        return PLAY_TAG_PREFIX + hashCode();
    }

    public abstract int getPageType();
}
