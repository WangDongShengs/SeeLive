package com.wds.seelive.home.recommend.page;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.m.k.mvp.utils.Logger;

import com.wds.seelive.GlideApp;
import com.wds.seelive.R;
import com.wds.seelive.data.entity.News;
import com.wds.seelive.databinding.ItemNewsFlashBinding;
import com.wds.seelive.databinding.ItemNewsNewsLeftBinding;
import com.wds.seelive.databinding.ItemNewsNewsRightBinding;
import com.wds.seelive.databinding.ItemNewsSpecialBinding;
import com.wds.seelive.databinding.ItemNewsVideoBinding;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {


    private static final int TYPE_LEFT_PIC = 0x1; // 左图
    private static final int TYPE_RIGHT_PIC = 0x3; // right pic
    private static final int TYPE_FlASH_PIC = 0x5; // only text not pic align left and right
    private static final int TYPE_VIDEO_PIC = 0x4; // video
    private static final int TYPE_SPECIAL_PIC = 0x2; //  big pic

    private ArrayList<News> news;
    public NewsAdapter(){

    }
    public void set(ArrayList<News> moreData){
        this.news=moreData;
        notifyDataSetChanged();
    }
    public void refresh(ArrayList<News> moreData){
        set(moreData);
    }
    public void loadMore(ArrayList<News> moreData){
        int size = news.size();
        news.addAll(moreData);
        notifyItemRangeInserted(size,moreData.size());
    }



    @NonNull
    @Override
    public NewsAdapter.NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Logger.d("");

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
            default:{
                layoutId = R.layout.item_news_news_left;
                aClass = LeftHolder.class;
                break;
            }
        }
        try {
            Constructor<? extends NewsHolder> constructor = aClass.getConstructor(NewsAdapter.class,View.class);
            return constructor.newInstance(this,LayoutInflater.from(parent.getContext()).inflate(layoutId,parent,false));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.NewsHolder holder, int position) {
        Logger.d("");

        holder.bindData(news.get(position));
    }


    @Override
    public int getItemViewType(int position) {
        return news.get(position).getView_type();
    }
    @Override
    public int getItemCount() {
        return news == null ? 0 : news.size();
    }


    public  class NewsHolder extends RecyclerView.ViewHolder{

        public NewsHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bindData(News news){

        }
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
        public VideoHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemNewsVideoBinding.bind(itemView);
        }

        @Override
        public void bindData(News news) {
            binding.title.setText(news.getTheme());
            GlideApp.with(itemView).load(news.getImageUrl()).into(binding.cover);
            binding.label.setText(news.getColumn_name());
        }
    }


}
