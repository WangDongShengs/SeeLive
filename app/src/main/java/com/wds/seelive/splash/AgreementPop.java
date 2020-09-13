package com.wds.seelive.splash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.m.k.mvp.widgets.MvpCommonPopView;
import com.wds.seelive.R;


public class AgreementPop extends MvpCommonPopView {
    private TextView mBtnAgree;
    private TextView mBtnCancel;
    private IPopClickListener mListener;
    public AgreementPop(Context context) {
        super(context);

    }

    @Override
    protected void setView(Context context) {
        super.setView(context);
        initView(context);
    }

    public void setListener(IPopClickListener listener) {
        this.mListener = listener;
    }
    private void initView(Context context){
        //充气布局
        View v  = LayoutInflater.from(context).inflate(R.layout.layout_splash_pop,null);
        //获取控件
        mBtnAgree = v.findViewById(R.id.splash_pop_btn_agree);
        mBtnCancel = v.findViewById(R.id.splash_pop_btn_stop);
        //确定监听
        mBtnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onAgree();
                }
            }
        });
        //取消监听
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onCancel();
                }
            }
        });
        //不获取焦点
        mBtnCancel.setFocusable(false);
        mBtnAgree.setFocusable(false);
        setContentView(v);
        //无法返回
        setOnBackKeyDismiss(false);
    }
    //接口   四个
    public interface IPopClickListener{
        void onCancel();
        void onAgree();
        //服务协议
        void onUserAgreement();
        //隐私政策
        void onPrivacyPolicy();
    }
}
