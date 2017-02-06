package com.mx.panneltest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by xff on 2017-2-6 11:33:47
 */
public class PannelActivity extends AppCompatActivity {
    FrameLayout test_layout;
    TextView tv_title;
    Button btn_test;
    PanelBottonLayout mPanelBottonLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pannel);
        mPanelBottonLayout=(PanelBottonLayout)findViewById(R.id.mPanelBottonLayout);
        test_layout= (FrameLayout) findViewById(R.id.test_layout);
        tv_title= (TextView) findViewById(R.id.tv_title);
        btn_test= (Button) findViewById(R.id.btn_test);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PannelActivity.this,MainActivity.class));
            }
        });
        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPanelBottonLayout.target();
            }
        });
    }
}
