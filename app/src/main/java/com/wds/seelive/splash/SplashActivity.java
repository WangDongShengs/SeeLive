package com.wds.seelive.splash;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.m.k.mvp.base.BaseActivity;
import com.m.k.mvp.base.NoResultCallBack;
import com.m.k.mvp.data.BaseRepository;
import com.m.k.mvp.data.request.GetRequest;
import com.m.k.mvp.data.response.MvpResponse;
import com.m.k.mvp.manager.MvpManager;
import com.m.k.mvp.manager.MvpUserManager;
import com.m.k.systemui.SystemBarConfig;
import com.wds.seelive.Constrant;
import com.wds.seelive.R;
import com.wds.seelive.auth.AuthActivity;
import com.wds.seelive.data.entity.User;
import com.wds.seelive.home.HomeActivity;
import com.wds.seelive.utils.ParamsUtils;

import io.reactivex.rxjava3.functions.Consumer;


public class SplashActivity extends BaseActivity {

    private static final int SPLASH_TIME = 300; //  3秒后跳转。
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(MvpManager.isShowGuidePage()) { // 第一次安装，或者 升级，或者清除数据
            AgreementPop agreementPop = new AgreementPop(this);
            agreementPop.setListener(new AgreementPop.IPopClickListener() {
                @Override
                public void onCancel() {
                    // 退出，关闭当前activity
                    MvpManager.launchFail();
                    finish();
                }

                @Override
                public void onAgree() {
                    // 显示引导页
                    agreementPop.dismiss();
                    showGuidePage();
                }

                @Override
                public void onUserAgreement() {

                }

                @Override
                public void onPrivacyPolicy() {

                }
            });

            getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    //添加到window 上
                    agreementPop.showCenter(getWindow().getDecorView());
                }
            });
        }else{
            // 设置为全屏
            SystemBarConfig config = new SystemBarConfig(this).enterFullScreen(SystemBarConfig.MODE_HIDE_LEAN_BACK);
            //应用
           // config.apply();
            //跳转到主页
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                }
            },SPLASH_TIME);
            getUserInfo();

            // 发送一个网络请求获取
            // 获取用户信息。用户信息。之所以在此处获取用户信息，是为了用户进入我的页面时，用户信息以及提前准备好了。
        }
    }

    private void showGuidePage(){
        setContentView(R.layout.activity_splash);
        //跳转到登录页面
        startActivity(new Intent(this, AuthActivity.class));

    }

    @Override
    protected int getLayoutId() {
        return 0;
    }
    private void getUserInfo(){
        //获取token
        String token = MvpUserManager.getToken();
        //获取过期时间
        long expireTime = MvpUserManager.getTokenExpireTime();
        //如果token 为空就return
        if(TextUtils.isEmpty(token)){
            return;
        }
        //通过get请求获取用户的信息
        GetRequest<User> userRequest = new GetRequest<>(Constrant.URL.GET_USER);
        //传入公有参数
        userRequest.setParams(ParamsUtils.getCommonParams());
        //通过put 的方式将本地得到的token 传入
        userRequest.getParams().put(Constrant.RequestKey.KEY_TOKEN, token);
        //传入type
        userRequest.setType(User.class);
        //new 库 通过request  的方式获取数据
        new BaseRepository().doRequest(null, userRequest, new Consumer<MvpResponse<User>>() {
            @Override
            public void accept(MvpResponse<User> response) throws Throwable {
                if (response.isOk()){
                    //由于去掉用户信息的接口返回的user对象里面没有token  所有获取本地的token
                    User.Token tokens = new User.Token();
                    //传入user 中的token
                    tokens.setValue(token);
                    //传入user 中的time
                    tokens.setExpire_time(expireTime);
                    //将本地缓存的token传入response 中  也就是用户信息
                    response.getData().setToken(tokens);
                    //用于通知用户登录
                    MvpUserManager.login(response.getData());
                }
            }
        }, new NoResultCallBack<>());

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}