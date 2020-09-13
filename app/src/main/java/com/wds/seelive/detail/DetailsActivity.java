package com.wds.seelive.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.m.k.mvp.base.BaseActivity;
import com.m.k.mvp.manager.MvpFragmentManager;
import com.m.k.mvp.utils.MvpUtils;
import com.m.k.systemui.SystemBarConfig;
import com.umeng.socialize.UMShareAPI;
import com.wds.seelive.R;
import com.wds.seelive.data.entity.BaseNews;

public class DetailsActivity extends BaseActivity{
    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }
    public static final String KEY_URL = "url";
    public static final String KEY_NEW_ID = "newsId";
    public static final String KEY_NEW_TITLE = "newsTitle";
    public static final String KEY_NEW_IMAGE_URL = "imageUrl";
    public static final String KEY_NEW_DESCRIPTION = "description";
    public static final String KEY_NEW_SHARE_LINK = "shareLink";


    private Bundle paramBundle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemBarConfig config = new SystemBarConfig(this);

        if(MvpUtils.hasM()){
            config.setStatusBarColor(Color.WHITE);
            config.setStatusBarLightMode(true);
        }else{
            config.setStatusBarColor(Color.GRAY);
        }

        config.apply();

        paramBundle = getIntent().getBundleExtra("bundle");

        MvpFragmentManager.addOrShowFragment(getSupportFragmentManager(),DetailFragment.class,null,R.id.detail_fragment_Container,paramBundle);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        UMShareAPI.get(this).onActivityResult(resultCode,resultCode,data);
    }

    public static void startDetailActivity(Context context, BaseNews news){
        Intent intent = new Intent(context, DetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(DetailsActivity.KEY_URL,news.getLink());
        bundle.putString(DetailsActivity.KEY_NEW_ID,news.getId());
        bundle.putString(DetailsActivity.KEY_NEW_TITLE,news.getTheme());
        bundle.putString(DetailsActivity.KEY_NEW_IMAGE_URL,news.getImageUrl());
        bundle.putString(DetailsActivity.KEY_NEW_DESCRIPTION,news.getDescription());
        bundle.putString(DetailsActivity.KEY_NEW_SHARE_LINK,news.getShareLink());

        intent.putExtra("bundle",bundle);
        context.startActivity(intent);
    }
}