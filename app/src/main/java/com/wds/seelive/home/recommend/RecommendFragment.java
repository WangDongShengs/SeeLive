package com.wds.seelive.home.recommend;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.m.k.mvp.base.v.BaseSmartFragment1;
import com.m.k.mvp.data.request.GetRequest;
import com.m.k.mvp.data.response.MvpResponse;
import com.m.k.mvp.manager.MvpUserManager;
import com.m.k.mvp.utils.Logger;
import com.m.k.mvp.widgets.MvpLoadingView;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.wds.seelive.Constrant;
import com.wds.seelive.R;
import com.wds.seelive.data.entity.ColumnData;
import com.wds.seelive.databinding.FragmentHomeRecommendBinding;
import com.wds.seelive.home.recommend.page.PageFragment;
import com.wds.seelive.utils.ParamsUtils;

import java.util.ArrayList;

public class RecommendFragment extends BaseSmartFragment1<ColumnData> {

    FragmentHomeRecommendBinding binding;
    private int current;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_recommend;
    }


    private boolean isDragging;
    @Override
    protected void bindView(View view) {
        super.bindView(view);
        binding = FragmentHomeRecommendBinding.bind(view);
        binding.newsViewPager.setOffscreenPageLimit(1);

        binding.newsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Logger.d(" position = %s positionOffset = %s ,current = %s", position, positionOffset, current);
                if(isDragging){
                    changeTabColor(position,positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if(state == ViewPager.SCROLL_STATE_DRAGGING){ // 手指按在屏幕上没有松开时
                    isDragging = true;
                }else {
                    isDragging = false;
                    if(state == ViewPager.SCROLL_STATE_SETTLING){ // 手指抬起，自动滑动，如果
                        changeTabColor(binding.newsViewPager.getCurrentItem(),0);
                    }
                }
            }
        });

        showFullLoading();
        loadColumnData();
    }


    @Override
    public boolean isNeedAddToBackStack() {
        return false;
    }


    @Override
    public boolean isNeedAnimation() {
        return false;
    }

    private void changeTabColor(int position, float offset) {

        int current =  binding.newsViewPager.getCurrentItem();

        int next;
        if(current > position){ // 反
            next = current - 1;
        }else{
            next  = current + 1;
        }
        if(next >= current){ // 正
            if(offset > 0.2){
                setTabViewTextColor(current,Color.BLACK);
                setTabIndicatorColor(next);
            }else if( offset >= 0){
                setTabViewTextColor(current,Color.WHITE);
                setTabIndicatorColor(current);
            }
        }else {

            if(offset < 0.8){
                setTabViewTextColor(current,Color.BLACK);
                setTabIndicatorColor(next);
            }else if (offset < 1){
                setTabViewTextColor(current,Color.WHITE);
                setTabIndicatorColor(current);
            }
        }
    }




    private void setTabIndicatorColor(int index){
        NewsPagerAdapter pagerAdapter = (NewsPagerAdapter) binding.newsViewPager.getAdapter();
        ColumnData.Column column =  pagerAdapter.getColumns().get(index);

        if(binding.slidingTabLayout.getIndicatorColor() != column.getColor()){
            binding.slidingTabLayout.setIndicatorColor(column.getColor());
        }
    }
    private void setTabViewTextColor(int index, int color){
        TextView view = binding.slidingTabLayout.getTitleView(index);
        if(view.getPaint().getColor() != color){
            Logger.d("---------------- %s 由 %s 变成 %s",view.getText().toString(),getColorString(view.getPaint().getColor()),getColorString(color));
            view.setTextColor(color);
        }

    }

    private String getColorString(int color){
        if(color == Color.WHITE){
            return "白色";
        }else if(color == Color.BLACK){
            return "黑色";
        }

        return "其他";

    }

    private void loadColumnData() {
        GetRequest<ColumnData> request = new GetRequest<>(Constrant.URL.COLUMN_MANAGER);
        request.setParams(ParamsUtils.getCommonParams());

        if (MvpUserManager.getToken() != null) {
            request.getParams().put(Constrant.RequestKey.KEY_TOKEN, MvpUserManager.getToken());
        }

        doRequest(request);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }

    @Override
    public void onResult1(MvpResponse<ColumnData> response) {
        if (response.isOk()) {
            closeLoading();
            binding.newsViewPager.setAdapter(new NewsPagerAdapter(getChildFragmentManager(), response.getData().getList().getMyColumn()));
            binding.slidingTabLayout.setViewPager(binding.newsViewPager);
        }else{
            onError(response.getMsg(), new MvpLoadingView.OnRetryCallBack() {
                @Override
                public void onRetry() {
                    loadColumnData();
                }
            });
        }
    }


    private class NewsPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<ColumnData.Column> mColumns;

        public NewsPagerAdapter(@NonNull FragmentManager fm, ArrayList<ColumnData.Column> columns) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            mColumns = columns;
        }


        public ArrayList<ColumnData.Column> getColumns() {
            return mColumns;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(mColumns.get(position).getId(),mColumns.get(position).getName());
        }


        @Override
        public int getCount() {
            return mColumns == null ? 0 : mColumns.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mColumns.get(position).getName();

        }
    }
}
