package com.example.a502.drawex;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

public class store_custom2 extends AppCompatActivity {
    CouponMaker maker;
    CouponView couponView;
    SeekBar[] backSeekBars;
    SeekBar[] insideSeekBars;
    SeekBar[] dashSeekBars;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_custom2);

        couponView=(CouponView)findViewById(R.id.couponView);
        maker=new CouponMaker(couponView);
        couponView.setMaker(maker);

        //Background SeekBar setting
        backSeekBars = new SeekBar[3];
        backSeekBars[0] = (SeekBar) findViewById(R.id.sb_r_b);
        backSeekBars[1] = (SeekBar) findViewById(R.id.sb_g_b);
        backSeekBars[2] = (SeekBar) findViewById(R.id.sb_b_b);
        settingSeekListener(backSeekBars,0);

        //Inside SeekBar setting
        insideSeekBars = new SeekBar[3];
        insideSeekBars[0] = (SeekBar) findViewById(R.id.sb_r_i);
        insideSeekBars[1] = (SeekBar) findViewById(R.id.sb_g_i);
        insideSeekBars[2] = (SeekBar) findViewById(R.id.sb_b_i);
        settingSeekListener(insideSeekBars,1);

        //dash line SeekBar setting
        dashSeekBars = new SeekBar[3];
        dashSeekBars[0] = (SeekBar) findViewById(R.id.sb_r_d);
        dashSeekBars[1] = (SeekBar) findViewById(R.id.sb_g_d);
        dashSeekBars[2] = (SeekBar) findViewById(R.id.sb_b_d);
        settingSeekListener(dashSeekBars,2);

        //next 버튼 클릭 리스너(액티비티 전환)
        //step1에서 결정 된 사항: 배경색, 안쪽색, 점선색
        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),store_custom3.class);
                Bundle bundle=new Bundle();
                bundle.putInt("backCol",maker.getBackPaint().getColor());
                bundle.putInt("inCol",maker.getInsidePaint().getColor());
                bundle.putInt("lineCol",maker.getLinePaint().getColor());
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


    public void settingSeekListener(final SeekBar[] seekBars, int type)
    {
        final int t=type;
        for(int i=0; i<seekBars.length; i++)
            seekBars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    Paint temp;
                    switch (t)
                    {
                        case 0:
                            temp=maker.getBackPaint();
                            temp.setColor(Color.rgb(seekBars[0].getProgress(),seekBars[1].getProgress(),seekBars[2].getProgress()));
                            maker.setBackPaint(temp);
                            break;
                        case 1:
                            temp=maker.getInsidePaint();
                            temp.setColor(Color.rgb(seekBars[0].getProgress(),seekBars[1].getProgress(),seekBars[2].getProgress()));
                            maker.setInsidePaint(temp);
                            break;
                        case 2:
                            temp=maker.getLinePaint();
                            temp.setColor(Color.rgb(seekBars[0].getProgress(),seekBars[1].getProgress(),seekBars[2].getProgress()));
                            maker.setLinePaint(temp);
                            break;
                    }
                    maker.execute();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
    }
}
