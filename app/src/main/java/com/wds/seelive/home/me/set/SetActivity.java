package com.wds.seelive.home.me.set;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.wds.seelive.R;
import com.wds.seelive.auth.AuthActivity;

public class SetActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_cl_set;
    private TextView tv_cl_set;
    private RelativeLayout rl_set_a;
    private RelativeLayout rl_set_b;
    private RelativeLayout rl_set_c;
    private RelativeLayout rl_set_d;
    private RelativeLayout rl_set_e;
    private RelativeLayout rl_set_f;
    private RelativeLayout rl_set_g;
    private RelativeLayout rl_set_h;
    private View view;
    private Button btn_set_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        initView();
    }

    private void initView() {
        iv_cl_set = (ImageView) findViewById(R.id.iv_cl_set);
        tv_cl_set = (TextView) findViewById(R.id.tv_cl_set);
        rl_set_a = (RelativeLayout) findViewById(R.id.rl_set_a);
        rl_set_b = (RelativeLayout) findViewById(R.id.rl_set_b);
        rl_set_c = (RelativeLayout) findViewById(R.id.rl_set_c);
        rl_set_d = (RelativeLayout) findViewById(R.id.rl_set_d);
        rl_set_e = (RelativeLayout) findViewById(R.id.rl_set_e);
        rl_set_f = (RelativeLayout) findViewById(R.id.rl_set_f);
        rl_set_g = (RelativeLayout) findViewById(R.id.rl_set_g);
        rl_set_h = (RelativeLayout) findViewById(R.id.rl_set_h);
        view = (View) findViewById(R.id.view);
        btn_set_item = (Button) findViewById(R.id.btn_set_item);

        btn_set_item.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_set_item:
                Intent intent = new Intent(SetActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.iv_cl_set:
                finish();
                break;
        }
    }
}