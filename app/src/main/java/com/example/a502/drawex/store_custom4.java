package com.example.a502.drawex;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class store_custom4 extends AppCompatActivity {
    CouponView couponView;
    CouponMaker maker;
    Bundle bundle;

    EditText editText;
    EditText numOfStemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_custom4);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        couponView=(CouponView)findViewById(R.id.couponView);
        maker=new CouponMaker(couponView);
        couponView.setMaker(maker);

        Intent intent=getIntent();
        bundle=intent.getExtras();

        setPresetting();

        editText=(EditText)findViewById(R.id.et_str);
        findViewById(R.id.btn_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maker.setStr(editText.getText().toString());
                maker.execute();
            }
        });

        numOfStemp=(EditText)findViewById(R.id.et_stamp);
        findViewById(R.id.btn_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num;
                num=Integer.parseInt(numOfStemp.getText().toString());
                if(num<0||num>maker.getNumOfStamp())
                {
                    Toast.makeText(getApplicationContext(),"잘못된 입력입니다.",Toast.LENGTH_LONG).show();
                    return;
                }
                boolean[] temp=maker.getEvents();
                temp[num-1]=true;
                maker.setEvents(temp);
                maker.execute();
            }
        });
        findViewById(R.id.btn_minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num;
                num=Integer.parseInt(numOfStemp.getText().toString());
                if(num<0||num>maker.getNumOfStamp())
                {
                    Toast.makeText(getApplicationContext(),"잘못된 입력입니다.",Toast.LENGTH_LONG).show();
                    return;
                }
                boolean[] temp=maker.getEvents();
                temp[num-1]=false;
                maker.setEvents(temp);
                maker.execute();
            }
        });
        //next 버튼 설정
        //step3에서 결정 된 것: 이벤트리스트, 쿠폰문구
        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putString("str",maker.getStr());
                bundle.putBooleanArray("events",maker.getEvents());
                Intent intent=new Intent(getApplicationContext(),store_custom5.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //super.onWindowFocusChanged(hasFocus);
        int width = couponView.getWidth();
        int height = (int) (width * 2 / 3.0);
        ViewGroup.LayoutParams params = couponView.getLayoutParams();
        params.height = height;
        couponView.setLayoutParams(params);
    }
    public void setPresetting()
    {
        //이전 값 쿠폰에 적용
        Paint temp;
        temp=maker.getBackPaint();
        temp.setColor(bundle.getInt("backCol"));
        maker.setBackPaint(temp);
        temp=maker.getInsidePaint();
        temp.setColor(bundle.getInt("inCol"));
        maker.setInsidePaint(temp);
        temp=maker.getLinePaint();
        temp.setColor(bundle.getInt("lineCol"));
        maker.setLinePaint(temp);

        maker.setNumOfStamp(bundle.getInt("stampNum"));
        maker.setNumOfCol(bundle.getInt("colNum"));
        maker.setStampBefore(BitmapFactory.decodeResource(this.getResources(), R.drawable.default_stamp1));
        maker.setStampAfter(BitmapFactory.decodeResource(this.getResources(), R.drawable.default_stamp2));
        maker.setEventBefore(BitmapFactory.decodeResource(this.getResources(), R.drawable.default_event1));
        maker.setEventAfter(BitmapFactory.decodeResource(this.getResources(), R.drawable.default_event2));
    }
}