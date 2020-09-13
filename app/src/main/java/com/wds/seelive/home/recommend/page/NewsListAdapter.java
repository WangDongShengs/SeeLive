package com.wds.seelive.home.recommend.page;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


import com.m.k.mvp.utils.Logger;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.wds.libvideo.MkAutoPlayVideoHolder;
import com.wds.libvideo.WDSVideo;
import com.wds.seelive.GlideApp;
import com.wds.seelive.R;
import com.wds.seelive.data.entity.News;
import com.wds.seelive.databinding.ItemAdPicBigBinding;
import com.wds.seelive.databinding.ItemAdPicSmallBinding;
import com.wds.seelive.databinding.ItemAdVideoBinding;
import com.wds.seelive.databinding.ItemNewsFlashBinding;
import com.wds.seelive.databinding.ItemNewsNewsLeftBinding;
import com.wds.seelive.databinding.ItemNewsNewsRightBinding;
import com.wds.seelive.databinding.ItemNewsSpecialBinding;
import com.wds.seelive.databinding.ItemNewsVideoBinding;
import com.wds.seelive.detail.DetailsActivity;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class NewsListAdapter extends ListAdapter<News, NewsListAdapter.NewsHolder> {


    private static final int TYPE_LEFT_PIC = 0x1; // 左图
    private static final int TYPE_RIGHT_PIC = 0x3; // right pic
    private static final int TYPE_FlASH_PIC = 0x5; // only text not pic align left and right
    private static final int TYPE_VIDEO_PIC = 0x4; // video
    private static final int TYPE_SPECIAL_PIC = 0x2; //  big pic


    private static final int TYPE_AD_SMALL_PIC = 0X6; //  小图广告
    private static final int TYPE_AD_BIG_PIC = 0X7; //  大图广告
    private static final int TYPE_AD_VIDEO = 0X8; //  视频广告
    private  String mPlayTag;
    private int mPageType;
    public NewsListAdapter(String playTag,int pageType) {
        super(new DiffUtil.ItemCallback<News>() {
            @Override
            public boolean areItemsTheSame(@NonNull News oldItem, @NonNull News newItem) {
                if(oldItem.getType() != newItem.getType()){
                    return false;
                }
                if(oldItem.getType() == 7){
                    return oldItem.getAd().getId().equals(newItem.getAd().getId());
                }else{
                    return oldItem.getId().equals(newItem.getId());
                }
            }
            @Override
            public boolean areContentsTheSame(@NonNull News oldItem, @NonNull News newItem) {
                if(oldItem.getType() != newItem.getType()){
                    return false;
                }
                if(oldItem.getType() == 7){
                    return oldItem.getAd().getAd_url().equals(newItem.getAd().getAd_url());
                }else{
                    return oldItem.getTheme().equals(newItem.getTheme());
                }
            }
        });
        mPlayTag = playTag;
        this.mPageType=pageType;
    }
    public void loadMore(ArrayList<News> moreData){
        List<News> news = new ArrayList<>();
        news.addAll(getCurrentList());
        Logger.d("------- 原来的size = %s",news.size() );
        news.addAll(moreData);
        Logger.d("------- 后来的的size = %s",news.size() );
        submitList(news);
    }
    @NonNull
    @Override
    public NewsListAdapter.NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Logger.d();
        int layoutId;
        Class<? extends NewsHolder> aClass;
        switch (viewType){
            case TYPE_RIGHT_PIC:{
                layoutId = R.layout.item_news_news_right;
                aClass = RightHolder.class;
                break;
            }
            case TYPE_SPECIAL_PIC:{
                layoutId = R.layout.item_news_special;
                aClass = BigHolder.class;
                break;
            }
            case TYPE_FlASH_PIC:{
                layoutId = R.layout.item_news_flash;
                aClass = FlashHolder.class;
                break;
            }
            case TYPE_VIDEO_PIC:{
                layoutId = R.layout.item_news_video;
                aClass = VideoHolder.class;
                break;
            }
            case TYPE_AD_SMALL_PIC:{
                layoutId = R.layout.item_ad_pic_small;
                aClass = AdSmallHolder.class;
                break;
            }
            case TYPE_AD_BIG_PIC:{
                layoutId = R.layout.item_ad_pic_big;
                aClass = AdBigHolder.class;
                break;
            }
            case TYPE_AD_VIDEO:{
                layoutId = R.layout.item_ad_video;
                aClass = AdVideoHolder.class;
                break;
            }
            default:{
                layoutId = R.layout.item_news_news_left;
                aClass = LeftHolder.class;
                break;
            }
        }
        try {
            Constructor<? extends NewsHolder> constructor = aClass.getConstructor(NewsListAdapter.class,View.class);
            return constructor.newInstance(this,LayoutInflater.from(parent.getContext()).inflate(layoutId,parent,false));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void submitList(@Nullable List<News> list) {
        super.submitList(list);
        Logger.d("------- 原来的size = %s",getItemCount() );
    }
    @Override
    public void onBindViewHolder(@NonNull NewsListAdapter.NewsHolder holder, int position) {
        Logger.d();
        holder.bindData(getCurrentList().get(position));
    }
    @Override
    public int getItemViewType(int position) {
        if(getCurrentList().get(position).getType() != 7){
            return getCurrentList().get(position).getView_type();
        }else{
            int adType = getCurrentList().get(position).getAd().getLayout();
            if(adType == 3){
                return TYPE_AD_SMALL_PIC;
            }else if(adType == 4 || adType == 5){
                return TYPE_AD_BIG_PIC;
            }else if(adType == 6 || adType == 7){
                return TYPE_AD_VIDEO;
            }else{
                return TYPE_AD_SMALL_PIC;
            }

        }

    }

    public News getNewsByPosition(int position){

        return getCurrentList().get(position);
    }
/*
    @Override
    public int getItemCount() {
        return news == null ? 0 : news.size();
    }*/


    public  class NewsHolder extends RecyclerView.ViewHolder{

        public NewsHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  DetailsActivity.startDetailActivity(itemView.getContext(),getNewsByPosition(getBindingAdapterPosition()));
                }
            });
        }

        public void bindData(News news){ }
    }


    private  class LeftHolder extends NewsHolder{

        private ItemNewsNewsLeftBinding binding;

        public LeftHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemNewsNewsLeftBinding.bind(itemView);
        }

        @Override
        public void bindData(News news) {
            binding.title.setText(news.getTheme());
            GlideApp.with(itemView).load(news.getImageUrl()).into(binding.newsPic);
            binding.label.setText(news.getColumn_name());
        }
    }

    private   class RightHolder extends NewsHolder{

        private ItemNewsNewsRightBinding binding;
        public RightHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemNewsNewsRightBinding.bind(itemView);
        }

        @Override
        public void bindData(News news) {
            binding.title.setText(news.getTheme());
            GlideApp.with(itemView).load(news.getImageUrl()).into(binding.newsPic);
            binding.label.setText(news.getColumn_name());
        }
    }

    private  class BigHolder extends NewsHolder{

        private ItemNewsSpecialBinding binding;

        public BigHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemNewsSpecialBinding.bind(itemView);
        }

        @Override
        public void bindData(News news) {
            binding.title.setText(news.getTheme());
            GlideApp.with(itemView).load(news.getImageUrl()).into(binding.newsPic);
            binding.label.setText(news.getColumn_name());
        }
    }

    private  class FlashHolder extends NewsHolder{

        private ItemNewsFlashBinding binding;
        public FlashHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemNewsFlashBinding.bind(itemView);
        }

        @Override
        public void bindData(News news) {
            binding.title.setText(news.getTheme());
            binding.content.setText(news.getContent());
            binding.time.setText(news.getEdit_time());

        }
    }


    private   class VideoHolder extends NewsHolder{

        private ItemNewsVideoBinding binding;
        private GSYVideoOptionBuilder gsyVideoOptionBuilder;
        public VideoHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemNewsVideoBinding.bind(itemView);
            gsyVideoOptionBuilder=new GSYVideoOptionBuilder();
        }

        @Override
        public void bindData(News news) {
            binding.title.setText(news.getTheme());
            // GlideApp.with(itemView).load(news.getImageUrl()).into(binding.cover);
            binding.label.setText(news.getColumn_name());
            gsyVideoOptionBuilder
                    .setIsTouchWiget(false)
                    //.setThumbImageView(imageView)
                    .setUrl(news.getVideo_url())
                    .setCacheWithPlay(false)
                    .setRotateViewAuto(true)
                    .setLockLand(true)
                    .setPlayTag(mPlayTag)
                    .setShowFullAnimation(true)
                    .setNeedShowWifiTip(false)
                    .setNeedLockFull(true)
                    .setVideoAllCallBack(new GSYSampleCallBack() {
                        @Override
                        public void onPrepared(String url, Object... objects) {
                            super.onPrepared(url, objects);
                            if (!binding.playerVideo.isIfCurrentIsFullscreen()) {
                                //静音
                                GSYVideoManager.instance().setNeedMute(true);
                            }

                        }

                        @Override
                        public void onQuitFullscreen(String url, Object... objects) {
                            super.onQuitFullscreen(url, objects);
                            //全屏不静音
                            GSYVideoManager.instance().setNeedMute(true);
                        }

                        @Override
                        public void onEnterFullscreen(String url, Object... objects) {
                            super.onEnterFullscreen(url, objects);
                            GSYVideoManager.instance().setNeedMute(false);
                            binding.playerVideo.getCurrentPlayer().getTitleTextView().setText((String)objects[0]);
                        }
                    }).build(binding.playerVideo);
            //增加title
            //binding.playerVideo.getTitleTextView().setVisibility(View.VISIBLE);

            //设置返回键
            binding.playerVideo.getBackButton().setVisibility(View.VISIBLE);
            GlideApp.with(itemView).load(news.getImageUrl()).into( binding.playerVideo.getCoverView());
            //设置全屏按键功能
            binding.playerVideo.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resolveFullBtn(binding.playerVideo);
                }
            });

        }

    }
    /**
     * 全屏幕按键处理
     */
    private void resolveFullBtn(final StandardGSYVideoPlayer standardGSYVideoPlayer) {
        standardGSYVideoPlayer.startWindowFullscreen(standardGSYVideoPlayer.getContext(), true, true);
    }



    public class AdSmallHolder extends NewsHolder{
        ItemAdPicSmallBinding binding;

        public AdSmallHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemAdPicSmallBinding.bind(itemView);
        }

        @Override
        public void bindData(News news) {

            GlideApp.with(itemView).load(news.getAd().getAd_url()).into(binding.itemAdIvPicSmall);
        }
    }



    public class AdBigHolder extends NewsHolder{
        ItemAdPicBigBinding binding;
        public AdBigHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemAdPicBigBinding.bind(itemView);
        }

        @Override
        public void bindData(News news) {

            GlideApp.with(itemView).load(news.getAd().getAd_url()).into( binding.itemAdIvPicBig);

            if(TextUtils.isEmpty(news.getAd().getTitle())){
                binding.itemAdTvTitle.setVisibility(View.GONE);
            }else{
                binding.itemAdTvTitle.setVisibility(View.VISIBLE);
                binding.itemAdTvTitle.setText(news.getAd().getTitle());
            }
        }
    }

    public class AdVideoHolder extends NewsHolder implements MkAutoPlayVideoHolder {
        ItemAdVideoBinding binding;
        GSYVideoOptionBuilder gsyVideoOptionBuilder;
        public AdVideoHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemAdVideoBinding.bind(itemView);
            gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
        }

        @Override
        public void bindData(News news) {
            GlideApp.with(itemView).load(news.getAd().getAd_url()).into( binding.itemAdVideo.getCoverView());

            if(TextUtils.isEmpty(news.getAd().getTitle())){
                binding.itemAdVideoTvTitle.setVisibility(View.GONE);
            }else{
                binding.itemAdVideoTvTitle.setVisibility(View.VISIBLE);
                binding.itemAdVideoTvTitle.setText(news.getAd().getTitle());
            }

            gsyVideoOptionBuilder
                    .setIsTouchWiget(true)
                    .setUrl(news.getAd().getAd_url())
                    .setCacheWithPlay(false)
                    .setRotateViewAuto(true)
                    .setLockLand(true) //
                    .setPlayTag(mPlayTag)
                    .setShowFullAnimation(true)
                    .setNeedLockFull(true)
                    .setNeedShowWifiTip(false)
                    .setPlayPosition(getAbsoluteAdapterPosition())
                    .setVideoAllCallBack(new GSYSampleCallBack() {
                        @Override
                        public void onEnterFullscreen(String url, Object... objects) {
                            super.onEnterFullscreen(url, objects);
                            binding.itemAdVideo.getCurrentPlayer().getTitleTextView().setVisibility(View.VISIBLE);
                            binding.itemAdVideo.getCurrentPlayer().getTitleTextView().setText(news.getAd().getTitle());
                        }
                    }).build(binding.itemAdVideo);


            //增加title
            binding.itemAdVideo.getTitleTextView().setVisibility(View.GONE);

            //设置返回键
            binding.itemAdVideo.getBackButton().setVisibility(View.GONE);

            //设置全屏按键功能
            binding.itemAdVideo.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resolveFullBtn(binding.itemAdVideo);
                }
            });


        }

        @Override
        public WDSVideo getVideoVIew() {
            return binding.itemAdVideo;
        }
    }



}
