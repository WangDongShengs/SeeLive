package com.wds.seelive.home;


import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.m.k.mvp.base.BaseActivity;
import com.m.k.mvp.base.BaseFragment;
import com.m.k.mvp.manager.MvpFragmentManager;
import com.m.k.mvp.utils.Logger;
import com.m.k.systemui.SystemBarConfig;
import com.wds.bannerlib.navigation.BottomNavigationView;
import com.wds.seelive.R;
import com.wds.seelive.data.repository.RecommendNewsRepository;
import com.wds.seelive.databinding.ActivityHomeBinding;
import com.wds.seelive.home.me.MeFragment;
import com.wds.seelive.home.recommend.RecommendFragment;
import com.wds.seelive.home.splash.SpecialNewsFragment;
import com.wds.seelive.home.video.VideoFragment;

//首页
public class HomeActivity extends BaseActivity {
    private ActivityHomeBinding binding;
    private BaseFragment mCurrentFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取系统配置
        SystemBarConfig config = new SystemBarConfig(this);
        //通过配置设置状态栏的模式
        config.setStatusBarLightMode(true);
        //通过配置设置状态栏的颜色  应用
        config.setStatusBarColor(Color.WHITE).apply();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        //通过自定义底部导航栏 添加 和监听
        binding.bottomNavigation.addItem(R.drawable.tab_recommend_selector,getString(R.string.text_tab_recommend))
                .addItem(R.drawable.tab_video_selector,getString(R.string.text_tab_video))
                .addItem(R.drawable.tab_special_selector,getString(R.string.text_tab_special))
                .addItem(R.drawable.tab_mine_selector,getString(R.string.text_tab_mine))
                .apply();

        binding.bottomNavigation.setTabSelectedListener(new BottomNavigationView.OnTabSelectedListener() {
            @Override
            public void onTabSelect(View tab, int position) {
                //如果position 为0
                if(position == 0){
                    //使用fragment的回退栈 形式 来跳转到第一页页面
                    mCurrentFragment = MvpFragmentManager.addOrShowFragment(getSupportFragmentManager(), RecommendFragment.class,mCurrentFragment,R.id.home_fragmentContainer);
                    //mCurrentFragment = MvpFragmentManager.addOrShowFragment(getSupportFragmentManager(), TestWBannerFragment.class,mCurrentFragment,R.id.home_fragmentContainer);
                }else if(position == 1){
                    //
                    mCurrentFragment =  MvpFragmentManager.addOrShowFragment(getSupportFragmentManager(), VideoFragment.class,mCurrentFragment,R.id.home_fragmentContainer);
                }else if(position == 2){
                    mCurrentFragment =  MvpFragmentManager.addOrShowFragment(getSupportFragmentManager(), SpecialNewsFragment.class,mCurrentFragment,R.id.home_fragmentContainer);
                }else if (position==3){
                     mCurrentFragment=MvpFragmentManager.addOrShowFragment(getSupportFragmentManager(), MeFragment.class,mCurrentFragment,R.id.home_fragmentContainer);
                }
               // MvpFragmentManager.addOrShowFragment(getSupportFragmentManager(), RecommendFragment.class,null,R.id.home_fragmentContainer);
            }

            @Override
            public void onTabUnSelect(View tab, int position) {
            }

            @Override
            public void onTabReSelected(View tab, int position) {

            }
        });
        binding.drawer.getRoot().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        // binding.drawerLayout.openDrawer(Gravity.LEFT);



    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Logger.d("++++++++------");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Logger.d("++++++++");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RecommendNewsRepository.destroy();
    }

    @Override
    protected void bindingView(View view) {
        binding  = ActivityHomeBinding.bind(view);
    }
}
