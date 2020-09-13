package com.wds.seelive.home.recommend.page;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


import com.m.k.mvp.utils.Logger;


import com.wds.bannerlib.banner.SimpleBannerAdapter;
import com.wds.seelive.GlideApp;
import com.wds.seelive.R;
import com.wds.seelive.data.entity.BannerNews;
import com.wds.seelive.data.entity.FlashNews;
import com.wds.seelive.detail.DetailsActivity;

import java.util.ArrayList;

public class BannerAdapter extends ListAdapter<BannerAdapter.BannerWrapData,BannerAdapter.BannerHolder> {


    private LifecycleOwner mLifecycleOwner;

    public BannerAdapter(LifecycleOwner owner) {

       super(new DiffUtil.ItemCallback<BannerWrapData>() {
           @Override
           public boolean areItemsTheSame(@NonNull BannerWrapData oldItem, @NonNull BannerWrapData newItem) {
               return oldItem.areItemsTheSame(newItem);
           }

           @Override
           public boolean areContentsTheSame(@NonNull BannerWrapData oldItem, @NonNull BannerWrapData newItem) {
               return oldItem.areContentsTheSame(newItem);
           }
       });
        mLifecycleOwner = owner;
    }


    @NonNull
    @Override
    public BannerAdapter.BannerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Logger.d("onCreateViewHolder");
        return new BannerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_banner,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BannerAdapter.BannerHolder holder, int position) {
        Logger.d("onBindViewHolder");
        holder.bindData(getCurrentList().get(0).bannerNews,getCurrentList().get(0).flashNews);
    }





    public class BannerHolder extends RecyclerView.ViewHolder{

        private com.wds.seelive.databinding.ItemHomeBannerBinding binding;

        public BannerHolder(@NonNull View itemView) {
            super(itemView);
            binding = com.wds.seelive.databinding.ItemHomeBannerBinding.bind(itemView);
            binding.banner.setLifecycleOwner(mLifecycleOwner);
        }


        private void bindData(ArrayList<BannerNews> news, ArrayList<FlashNews> flashNews){
            binding.banner.setData(new SimpleBannerAdapter<BannerNews>(news) {


                @Override
                public void bindData(ImageView view, BannerNews data,int position) {
                    GlideApp.with(view).load(data.getImageUrl()).into(view);
                }

                @Override
                public void onClick(BannerNews data, int position) {
         /*           Intent intent = new Intent(itemView.getContext(), DetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("url", data.getLink());
                    intent.putExtra("bundel",bundle);
                    itemView.getContext().startActivity(intent);*/
                    DetailsActivity.startDetailActivity(itemView.getContext(),data);
                }
            });

            if(flashNews != null && flashNews.size() > 0){
                binding.flashGroup.setVisibility(View.VISIBLE);
                binding.flashView.setClickableText(flashNews);
            }else{
                binding.flashGroup.setVisibility(View.GONE);
            }
        }

    }



    public static class BannerWrapData{

        private ArrayList<BannerNews> bannerNews;
        private ArrayList<FlashNews> flashNews;

        public BannerWrapData(ArrayList<BannerNews> bannerNews, ArrayList<FlashNews> flashNews) {
            this.bannerNews = bannerNews;
            this.flashNews = flashNews;
        }

        private boolean areItemsTheSame(BannerWrapData data){
            if(bannerNews.size() != data.bannerNews.size()){
                return false;
            }

            for(int i = 0; i < bannerNews.size();i ++){
                if(!bannerNews.get(i).getId().equals(data.bannerNews.get(i).getId())){
                    return false;
                }
            }

            if(flashNews.size() != data.flashNews.size()){
                return false;
            }

            for(int i = 0; i < flashNews.size();i ++){
                if(!flashNews.get(i).getId().equals(data.flashNews.get(i).getId())){
                    return false;
                }
            }

            return true;
        }



        private boolean areContentsTheSame(BannerWrapData data){
            if(bannerNews.size() != data.bannerNews.size()){
                return false;
            }

            for(int i = 0; i < bannerNews.size();i ++){
                if(!bannerNews.get(i).getTitle().equals(data.bannerNews.get(i).getTitle())){
                    return false;
                }
            }

            if(flashNews.size() != data.flashNews.size()){
                return false;
            }

            for(int i = 0; i < flashNews.size();i ++){
                if(!flashNews.get(i).getTheme().equals(data.flashNews.get(i).getTheme())){
                    return false;
                }
            }

            return true;
        }


    }
}
