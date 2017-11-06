package com.example.a502.drawex;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class store_custom1 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_custom1);
        findViewById(R.id.btn_others).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"서비스 준비중입니다.",Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),store_custom2.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //super.onWindowFocusChanged(hasFocus);
        ImageView imageView=(ImageView)findViewById(R.id.img_ex);
        int width=imageView.getWidth();
        int height=(int)(width*2/3.0);
        ViewGroup.LayoutParams params=imageView.getLayoutParams();
        params.height=height;
        imageView.setLayoutParams(params);
    }
}
