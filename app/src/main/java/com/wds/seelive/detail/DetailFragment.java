package com.wds.seelive.detail;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.m.k.mvp.base.p.IBasePresenter;
import com.m.k.mvp.base.v.MvpBaseFragment;
import com.m.k.mvp.manager.MvpFragmentManager;
import com.m.k.mvp.utils.Logger;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.wds.seelive.R;
import com.wds.seelive.databinding.FragmentDetailBinding;
//创建fragment 用来显示页面
public class DetailFragment extends MvpBaseFragment {

    private FragmentDetailBinding detailBinding;
    private DetailContentFragment contentFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detail;
    }

    @Override
    public IBasePresenter createPresenter() {
        return null;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
    }

    @Override
    protected void initView() {
        super.initView();
        contentFragment= (DetailContentFragment) MvpFragmentManager.addOrShowFragment(getChildFragmentManager(), DetailContentFragment.class, null, R.id.detail_content_fragment_Container, getArguments());
        detailBinding.newsDetailShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UMWeb web = new UMWeb(getArguments().getString(DetailsActivity.KEY_NEW_SHARE_LINK));
                web.setTitle(getArguments().getString(DetailsActivity.KEY_NEW_TITLE));//标题
                UMImage thumb = new UMImage(getActivity(),getArguments().getString(DetailsActivity.KEY_NEW_IMAGE_URL));
                web.setThumb(thumb);  //缩略图
                web.setDescription(getArguments().getString(DetailsActivity.KEY_NEW_DESCRIPTION));//描述

                new ShareAction(getActivity()).withMedia(web).setDisplayList(SHARE_MEDIA.SINA,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.QQ)
                        .setCallback(new SampleShareListener() {
                            @Override
                            public void onResult(SHARE_MEDIA share_media) {
                                Logger.d();
                                //調用contentFragment 的* 发送分享成功增加积分
                               contentFragment.sendShareSuccess();
                            }

                        }).open();
            }
        });
        detailBinding.newsDetailBottomIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 调用contentFragment 的显示评论输入框
                contentFragment.showCommentPop(view);
            }
        });
    }

    @Override
    protected void bindView(View view) {
        super.bindView(view);
        detailBinding = FragmentDetailBinding.bind(view);
    }
    //是否显示动画
    @Override
    public boolean isNeedAnimation() {
        return false;
    }
    //是否添加返回堆栈
    @Override
    public boolean isNeedAddToBackStack() {
        return false;
    }
}
