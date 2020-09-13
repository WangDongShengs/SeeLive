package com.wds.seelive.auth.login.pwd;


import com.m.k.mvp.base.IBaseCallBack;
import com.m.k.mvp.base.m.IBaseMode;
import com.m.k.mvp.base.p.IBasePresenter;
import com.m.k.mvp.base.v.IBaseView;
import com.m.k.mvp.data.request.MvpRequest;
import com.trello.rxlifecycle4.LifecycleProvider;
import com.wds.seelive.data.entity.User;


public interface PasswordLoginContract {
        public interface ILoginView extends IBaseView<ILoginPresenter> {
            void onLoginSuccess(User user);
            void onNetError();
            void onInputFail(String mag);
            void showsLoading();
            void closesLoading();
        }
        public interface ILoginPresenter extends IBasePresenter<ILoginView> {
            void login(String userCount, String password);
        }
        public interface ILoginMode extends IBaseMode {
            void login(LifecycleProvider provider, MvpRequest request, IBaseCallBack<User> callBack);
        }
}
