package com.wds.seelive.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RecommendData extends NewsData{
    @SerializedName("article_list")
    private ArrayList<News> news;


    @Override
    public ArrayList<News> getNewsList() {
        return news;
    }

    @Override
    public void setNewsList(ArrayList<News> list) {
        this.news = list;
    }
}
