package com.wds.seelive.auth.register;

import android.os.CountDownTimer;
import android.widget.EditText;
import android.widget.TextView;

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
import com.wds.seelive.auth.login.code.CodeLoginFragment;
import com.wds.seelive.auth.login.pwd.PasswordLoginFragment;
import com.wds.seelive.data.entity.User;
import com.wds.seelive.data.repository.RegisterRepository;
import com.wds.seelive.utils.ParamsUtils;

import java.util.HashMap;
import java.util.Locale;

public class RegisterFragment extends BaseSmartFragment1<User> {

    private TextView mGoToCode;
    private TextView mGoToLogin;
    private EditText mEditCount;
    private CleanEditButton mCleanPhone;
    private EditTextButton mEditRegister;
    private EditText mEditCode;
    private TextView mGetCode;

    @Override
    protected void initView() {
        findViewById(R.id.auth_register_btn_register);
        mEditCode = findViewById(R.id.auth_register_edt_code);
        mEditCount = findViewById(R.id.auth_register_edt_count);
        mCleanPhone = findViewById(R.id.auth_register_iv_clean_phone_num);
        mGoToCode = findViewById(R.id.auth_register_tv_code_view);
        mGoToLogin = findViewById(R.id.auth_register_tv_login_view);
        mEditRegister = findViewById(R.id.auth_register_btn_register);
        mGetCode = findViewById(R.id.auth_code_register_tv_get_code);
        mEditRegister.bindEditText(mEditCount);
        mEditRegister.bindEditText(mEditCode);
        mCleanPhone.bindEditText(mEditCode);
        mGetCode.setOnClickListener(v -> { initRequest();initTimer();});
        mEditRegister.setOnClickListener(v -> {});
        mGoToCode.setOnClickListener(v -> MvpFragmentManager.addOrShowFragment(getFragmentManager(), CodeLoginFragment.class, RegisterFragment.this,R.id.layout));
        mGoToLogin.setOnClickListener(v -> MvpFragmentManager.addOrShowFragment(getFragmentManager(), PasswordLoginFragment.class, RegisterFragment.this,R.id.layout));
    }
    //注册，获取验证码
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
            }
        }.start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register;
    }

    @Override
    public BaseSmartPresenter1<User, ?> createPresenter() {
        return new BaseSmartPresenter1<>(new RegisterRepository());
    }

    @Override
    public void onResult1(MvpResponse<User> response) {
        //showToast(response.getMsg()+"");
    }
}
