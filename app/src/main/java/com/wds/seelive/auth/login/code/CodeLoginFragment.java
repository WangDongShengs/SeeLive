package com.wds.seelive.auth.login.code;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.m.k.mvp.base.p.BaseSmartPresenter1;
import com.m.k.mvp.base.v.BaseSmartFragment1;
import com.m.k.mvp.data.request.PostRequest;
import com.m.k.mvp.data.request.RequestMethod;
import com.m.k.mvp.data.response.MvpResponse;
import com.m.k.mvp.manager.MvpFragmentManager;
import com.m.k.mvp.widgets.CleanEditButton;
import com.m.k.mvp.widgets.EditTextButton;
import com.wds.seelive.Constrant;
import com.wds.seelive.R;
import com.wds.seelive.auth.login.pwd.PasswordLoginFragment;
import com.wds.seelive.auth.register.RegisterFragment;
import com.wds.seelive.data.entity.User;
import com.wds.seelive.data.repository.CodeLoginRepository;
import com.wds.seelive.utils.ParamsUtils;

import java.util.HashMap;
import java.util.Locale;

public class CodeLoginFragment extends BaseSmartFragment1<User> {
    private TextView mTvLicense;
    private CleanEditButton mCleanEditButton;
    private EditText mEditCount;
    private EditText mEditCode;
    private TextView mTvMiLogin;
    private TextView mGoToRegister;
    private EditTextButton mBtnLogin;
    private TextView mGetCode;

    @Override
    protected void initView() {
        mTvLicense=findViewById(R.id.auth_code_login_tv);
        mEditCount = findViewById(R.id.auth_code_login_edt_count);
        mEditCode = findViewById(R.id.auth_code_login_edt_code);
        mTvMiLogin = findViewById(R.id.auth_code_login_tv_login_view);
        mCleanEditButton = findViewById(R.id.auth_code_login_iv_clean_phone_num);
        mGoToRegister = findViewById(R.id.auth_code_login_tv_register_view);
        mBtnLogin = findViewById(R.id.auth_code_login_btn_login);
        mGetCode = findViewById(R.id.auth_code_login_tv_get_code);
        mCleanEditButton.bindEditText(mEditCount);
        mBtnLogin.bindEditText(mEditCode);
        mBtnLogin.bindEditText(mEditCount);
        mGetCode.setOnClickListener(v -> {
            //获取验证码
            CodeLoginFragment.this.initRequest();
            //倒计时
            CodeLoginFragment.this.initTimer();
        });
        mBtnLogin.setOnClickListener(v -> initLogin());
        mTvMiLogin.setOnClickListener(v -> MvpFragmentManager.addOrShowFragment(getFragmentManager(), PasswordLoginFragment.class, CodeLoginFragment.this,R.id.layout));
        mGoToRegister.setOnClickListener(v -> MvpFragmentManager.addOrShowFragment(getFragmentManager(), RegisterFragment.class, CodeLoginFragment.this,R.id.layout));
        initText();

    }



    //验证码登录
    private void initLogin() {
        String phone = mEditCount.getText().toString().trim();
        String trim = mEditCode.getText().toString().trim();
        PostRequest postRequest = new PostRequest(Constrant.URL.CODE_LOGIN);
        HashMap<String, Object> commonParams = ParamsUtils.getCommonParams();
        commonParams.put(Constrant.RequestKey.KEY_USER_COUNT,phone);
        commonParams.put("captcha",trim);
        postRequest.setParams(commonParams);
        postRequest.setRequestMethod(RequestMethod.POST);
        doRequest(postRequest);
    }
    private int CountDown=60;
    //倒计时
    private void initTimer() {
        new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {//倒计时中
                String format = String.format(Locale.CHINA, "%ds", CountDown--);
                mGetCode.setText(format);
            }

            @Override
            public void onFinish() {//结束
                mGetCode.setText("获取验证码");
                mGetCode.setEnabled(false);
            }
        }.start();
        mGetCode.setEnabled(true);
    }
    //获取验证码
    private void initRequest() {
        String phone = mEditCount.getText().toString().trim();
        PostRequest postRequest = new PostRequest(Constrant.URL.CODE);
        HashMap<String, Object> commonParams = ParamsUtils.getCommonParams();
        commonParams.put(Constrant.RequestKey.KEY_USER_COUNT,phone);
        commonParams.put("type",1);
        postRequest.setParams(commonParams);
        postRequest.setRequestMethod(RequestMethod.POST);
        doRequest(postRequest);
    }

    private void initText() {
        SpannableString style = new SpannableString("注册即是同意见道的用户协议和隐私政策");
        //点击事件
        ClickableSpan clickableSpan=new ClickableSpan() {
            @Override
            public void onClick(@NonNull View pView) {
                showToast("点击了用户协议");
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.RED);
            }
        };
        ClickableSpan clickableSpan1=new ClickableSpan() {
            @Override
            public void onClick(@NonNull View pView) {
                showToast("点击了隐私政策");
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.RED);
            }
        };
        style.setSpan(clickableSpan,9,13,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(clickableSpan1,14,18,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //配置给TextView
        mTvLicense.setMovementMethod(LinkMovementMethod.getInstance());
        mTvLicense.setText(style);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login_code;
    }

/*    @Override
    public IBasePresenter getPresenter() {
        return new BaseSmartPresenter1(new CodeLoginRepository());
    }*/

    @Override
    public BaseSmartPresenter1<User, ?> createPresenter() {
        return new BaseSmartPresenter1<>(new CodeLoginRepository());
    }

    @Override
    public void onResult1(MvpResponse<User> response) {
        if (response.getCode()==1){
            showToast(response.getCode()+"");
            Log.d("TAG", "onResult1: "+response.getCode());
        }else {
            showToast("获取验证码成功");
        }

    }

}
