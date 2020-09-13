package com.wds.seelive.auth.login.pwd;



import com.m.k.mvp.base.IBaseCallBack;
import com.m.k.mvp.base.p.BasePresenter;
import com.m.k.mvp.data.request.PostRequest;
import com.m.k.mvp.data.response.MvpResponse;
import com.wds.seelive.Constrant;
import com.wds.seelive.MyApp;
import com.wds.seelive.data.entity.User;
import com.wds.seelive.data.repository.PasswordLoginRepository;
import com.wds.seelive.utils.AppLoginUtils;
import com.wds.seelive.utils.ParamsUtils;

import java.util.HashMap;



public class PasswordLoginPresenter extends BasePresenter<PasswordLoginContract.ILoginView> implements PasswordLoginContract.ILoginPresenter {
    private PasswordLoginContract.ILoginMode baseMode;
    public PasswordLoginPresenter() {
        baseMode=new PasswordLoginRepository();
    }
    //登录
    @Override
    public void login(String userCount, String password) {
        if (!AppLoginUtils.isOnInternet(MyApp.getApp())){
            mView.onNetError();
            return;
        }
        if(!AppLoginUtils.isValidUserCount(userCount)){
            mView.onInputFail("用户名格式不对");
            return;
        }

        if(!AppLoginUtils.isValidUserPassword(password)){
            mView.onInputFail("密码格式不对");
            return;
        }
        mView.showsLoading();
        PostRequest postRequest = new PostRequest(Constrant.URL.LOGIN);
        HashMap hashMap = ParamsUtils.getCommonParams();

        hashMap.put(Constrant.RequestKey.KEY_USER_COUNT,userCount);
        hashMap.put(Constrant.RequestKey.KEY_USER_PASSWORD,password);
        postRequest.setType(User.class);
        postRequest.setParams(hashMap);
        baseMode.doRequest(getLifecycleProvider(),postRequest, new IBaseCallBack<User>() {

            @Override
            public void onResult(MvpResponse<User> response) {
                mView.closesLoading();
                mView.onLoginSuccess(response.getData());
            }
        });

    }


    @Override
    public boolean cancelRequest() {
            return false;
    }
}
