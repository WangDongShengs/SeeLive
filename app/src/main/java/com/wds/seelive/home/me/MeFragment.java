package com.wds.seelive.home.me;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.m.k.mvp.base.BaseFragment;
import com.wds.seelive.R;
import com.wds.seelive.auth.AuthActivity;
import com.wds.seelive.home.me.set.SetActivity;

public class MeFragment extends BaseFragment implements View.OnClickListener{

    private Button btnMeItem;
    private ImageView clRlImage;
    private TextView clRlTV1;
    private RelativeLayout clRL;
    private ImageView ivRlJiFen;
    private RelativeLayout clLlJiFen;
    private ImageView ivRlStart;
    private RelativeLayout clLlStart;
    private ImageView ivRlMes;
    private RelativeLayout clLlMes;
    private ImageView ivRlSet;
    private RelativeLayout clLlSet;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_me;
    }


    @Override
    protected void initView() {
        super.initView();
        btnMeItem = findViewById(R.id.btn_me_item);
        clRlImage = findViewById(R.id.cl_rl_image);
        clRlTV1 = findViewById(R.id.cl_rl_tv1);
        clRL = findViewById(R.id.cl_rl);
        ivRlJiFen = findViewById(R.id.iv_rl_jifeng);
        clLlJiFen = findViewById(R.id.cl_ll_jifeng);
        ivRlStart = findViewById(R.id.iv_rl_start);
        clLlStart = findViewById(R.id.cl_ll_start);
        ivRlMes = findViewById(R.id.iv_rl_mes);
        clLlMes = findViewById(R.id.cl_ll_mes);
        ivRlSet = findViewById(R.id.iv_rl_set);
        clLlSet = findViewById(R.id.cl_ll_set);
        clRlImage.setOnClickListener(this::onClick);
        clLlJiFen.setOnClickListener(this::onClick);
        clLlStart.setOnClickListener(this::onClick);
        clLlMes.setOnClickListener(this::onClick);
        clLlSet.setOnClickListener(this::onClick);
        btnMeItem.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), AuthActivity.class);
        switch (view.getId()) {
            case R.id.cl_rl_image:
                startActivity(intent);
                break;
            case R.id.cl_ll_jifeng:
                startActivity(intent);
                break;
            case R.id.cl_ll_start:
                startActivity(intent);
                break;
            case R.id.cl_ll_mes:
                startActivity(intent);
                break;
            case R.id.cl_ll_set:
                Intent intent1 = new Intent(getActivity(), SetActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_me_item:
                startActivity(intent);
                break;
        }
    }
}
