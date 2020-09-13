package com.wds.seelive.auth;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.m.k.mvp.base.BaseActivity;
import com.m.k.mvp.manager.MvpFragmentManager;
import com.m.k.mvp.manager.MvpUserManager;
import com.wds.seelive.R;
import com.wds.seelive.auth.login.code.CodeLoginFragment;
import com.wds.seelive.auth.login.pwd.PasswordLoginFragment;
import com.wds.seelive.data.entity.User;


public class AuthActivity extends BaseActivity {
    private MvpUserManager.IUserCallBack<User> userCallBack;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_auth;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MvpFragmentManager.addOrShowFragment(getSupportFragmentManager(), CodeLoginFragment.class,null,R.id.layout);
        userCallBack  = MvpUserManager.registerUserStateCallBack(new MvpUserManager.IUserCallBack<User>() {
            @Override
            public void onUserLogin(User user) {
                String token = MvpUserManager.getToken();
                showToast(token);

            }
        });
    }

}