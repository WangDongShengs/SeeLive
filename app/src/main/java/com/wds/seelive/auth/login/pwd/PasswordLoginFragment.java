package com.wds.seelive.auth.login.pwd;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.m.k.mvp.base.p.IBasePresenter;
import com.m.k.mvp.base.v.MvpBaseFragment;
import com.m.k.mvp.manager.MvpFragmentManager;
import com.m.k.mvp.widgets.CleanEditButton;
import com.m.k.mvp.widgets.EditTextButton;
import com.m.k.mvp.widgets.TogglePasswordButton;
import com.wds.seelive.R;
import com.wds.seelive.auth.login.code.CodeLoginFragment;
import com.wds.seelive.auth.register.RegisterFragment;
import com.wds.seelive.data.entity.User;


public class PasswordLoginFragment extends MvpBaseFragment<PasswordLoginContract.ILoginPresenter> implements PasswordLoginContract.ILoginView {

    private EditText mCount;
    private EditText mPassword;
    private EditTextButton button;
    private CleanEditButton cleanPhone;
    private CleanEditButton cleanPassword;
    private TogglePasswordButton togglePassword;
    private TextView mGotoCode;
    private TextView mGotoRegister;


    @Override
    protected void initView() {
        button = findViewById(R.id.auth_password_login_btn_login);
        mCount = findViewById(R.id.auth_password_login_edt_count);

        mPassword = findViewById(R.id.auth_password_login_edt_password);
        cleanPhone = findViewById(R.id.auth_password_login_iv_clean_phone_num);
        cleanPassword = findViewById(R.id.auth_password_login_iv_clean_password);
        togglePassword = findViewById(R.id.auth_password_login_iv_toggle_password);
        mGotoCode = findViewById(R.id.auth_password_login_tv_code_view);
        mGotoRegister= findViewById(R.id.auth_password_login_tv_register_view);
        cleanPhone.bindEditText(mCount);
        cleanPassword.bindEditText(mPassword);
        togglePassword.bindEditText(mPassword);
        button.bindEditText(mPassword);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        mGotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MvpFragmentManager.addOrShowFragment(getFragmentManager(), RegisterFragment.class, PasswordLoginFragment.this,R.id.layout);
            }
        });
        mGotoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MvpFragmentManager.addOrShowFragment(getFragmentManager(), CodeLoginFragment.class, PasswordLoginFragment.this,R.id.layout);
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login_password;
    }

    private void login() {
        String count = mCount.getText().toString().trim();
        String password =mPassword.getText().toString().trim();
        mPresenter.login(count, password);
    }

    @Override
    public void onLoginSuccess(User user) {
        showToast(user.getToken().getValue());
    }

    @Override
    public void onNetError() {
        showToast("网络错误");
    }

    @Override
    public void onInputFail(String mag) {
        showToast(mag+"");
    }

    @Override
    public void showsLoading() {
        showPopLoading();

    }
    @Override
    public void closesLoading() {
        closeLoading();
    }
        //loading 页面
    @Override
    public IBasePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public PasswordLoginContract.ILoginPresenter createPresenter() {
        return new PasswordLoginPresenter();
    }

}
