package com.wds.seelive.data.entity;


import com.wds.bannerlib.banner.IBannerData;

public class BannerNews extends BaseNews implements IBannerData {
    @Override
    public String getTitle() {
        return getTheme();
    }
}
