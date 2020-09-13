package com.wds.seelive.data.repository;

import com.m.k.mvp.base.IBaseCallBack;
import com.m.k.mvp.data.BaseRepository;
import com.m.k.mvp.data.request.MvpRequest;
import com.m.k.mvp.data.response.MvpResponse;
import com.m.k.mvp.manager.MvpUserManager;
import com.trello.rxlifecycle4.LifecycleProvider;
import com.wds.seelive.data.entity.User;

import io.reactivex.rxjava3.functions.Consumer;

public class CodeLoginRepository extends BaseRepository {
    @Override
    public <T> void doRequest(LifecycleProvider lifecycleProvider, MvpRequest<T> request, Consumer<MvpResponse<T>> doBackground, IBaseCallBack<T> callBack) {
        doRequest(lifecycleProvider, request, new Consumer<MvpResponse<T>>() {

            public void accept(MvpResponse<T> response) throws Throwable {
                if(response.isOk()){
                    User user =(User) response.getData();
                    MvpUserManager.login(user);
                }
            }
        },callBack);
    }

}
