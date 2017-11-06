package com.example.a502.drawex;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class store_custom3 extends AppCompatActivity {
    CouponView couponView;
    CouponMaker maker;
    Bundle bundle;

    TextView tv_total;
    TextView tv_Cols;

    int stampNum=10;
    int colNum=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_custom3);

        couponView=(CouponView)findViewById(R.id.couponView);
        maker=new CouponMaker(couponView);
        couponView.setMaker(maker);

        Intent intent=getIntent();
        bundle=intent.getExtras();

        setPresetting();

        tv_total=(TextView)findViewById(R.id.stampNum);
        tv_Cols=(TextView)findViewById(R.id.rowNum);

        //쿠폰뷰 설정
        maker.setStampBefore(BitmapFactory.decodeResource(this.getResources(), R.drawable.default_stamp1));
        maker.setStampAfter(BitmapFactory.decodeResource(this.getResources(), R.drawable.default_stamp2));
        maker.setNumOfCol(colNum);
        maker.setNumOfStamp(stampNum);
        maker.setUserStamp(1);

        //next 버튼 설정
        //step2에서 결정 된 것: 총 도장갯수, 열의 갯수
        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putInt("stampNum",stampNum);
                bundle.putInt("colNum",colNum);
                Intent intent=new Intent(getApplicationContext(),store_custom4.class);
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
    }

    public void numberUpDown(View view)
    {
        switch (view.getId())
        {
            case R.id.btnUp:
                stampNum++;
                tv_total.setText(stampNum+"");
                break;
            case R.id.btnDown:
                if(stampNum==1)
                {
                    Toast.makeText(this,"적어도 1 이상이어야 합니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                stampNum--;
                tv_total.setText(stampNum+"");
                break;
            case R.id.btnRowUp:
                if(colNum>=stampNum)
                {
                    Toast.makeText(this,"총 개수를 초과합니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                colNum++;
                tv_Cols.setText(colNum+"");
                break;
            case R.id.btnRowDown:
                if(colNum==1)
                {
                    Toast.makeText(this,"적어도 1 이상이어야 합니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                colNum--;
                tv_Cols.setText(colNum+"");
                break;
        }
        maker.setNumOfStamp(stampNum);
        maker.setNumOfCol(colNum);
        maker.execute();
    }
}

