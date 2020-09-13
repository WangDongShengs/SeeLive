package com.wds.seelive.data.repository;

import com.m.k.mvp.base.IBaseCallBack;
import com.m.k.mvp.data.BaseRepository;
import com.m.k.mvp.data.request.MvpRequest;
import com.m.k.mvp.data.response.MvpResponse;
import com.m.k.mvp.manager.MvpUserManager;
import com.trello.rxlifecycle4.LifecycleProvider;
import com.wds.seelive.auth.login.pwd.PasswordLoginContract;
import com.wds.seelive.data.entity.User;


import io.reactivex.rxjava3.functions.Consumer;

public class PasswordLoginRepository extends BaseRepository implements PasswordLoginContract.ILoginMode {
    @Override
    public void login(LifecycleProvider provider, MvpRequest request, IBaseCallBack<User> callBack) {
        doRequest(provider,request, new Consumer<MvpResponse<User>>() {
            @Override
            public void accept(MvpResponse<User> userMvpResponse) throws Throwable {
                    if(userMvpResponse.isOk()){
                        User user = userMvpResponse.getData();
                        MvpUserManager.login(user);
                    }
            }
        },callBack);
    }
}
