package com.example.a502.drawex;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.widget.SeekBar;

/**
 * Created by soo on 2017-09-25.
 */

public class ColorChoice {
    public int[] colors;
    Context mContext;

    public ColorChoice(Context context)
    {
        colors=new int[10];
        mContext=context;
        setColors();
    }

    //단순 ID값으로 하면 이상한 색만 나온다. 반드시 ContextCompat으로 color값을 가져오자
    private void setColors()
    {
        colors[0]= ContextCompat.getColor(mContext,R.color.pastelBlue);
        colors[1]=ContextCompat.getColor(mContext,R.color.pastelBrown);
        colors[2]=ContextCompat.getColor(mContext,R.color.pastelGreen);
        colors[3]=ContextCompat.getColor(mContext,R.color.pastelMint);
        colors[4]=ContextCompat.getColor(mContext,R.color.pastelRed);

        colors[5]=ContextCompat.getColor(mContext,R.color.pastelViolet);
        colors[6]=ContextCompat.getColor(mContext,R.color.pastelOrange);
        colors[7]=ContextCompat.getColor(mContext,R.color.pastelPeach);
        colors[8]=ContextCompat.getColor(mContext,R.color.pastelPink);
        colors[9]=ContextCompat.getColor(mContext,R.color.pastelYellow);
    }

    public int getColor(int index) { return colors[index];}
    public int getColor(SeekBar[] rgbSeekBar)
    {
        int r=rgbSeekBar[0].getProgress();
        int g=rgbSeekBar[1].getProgress();
        int b=rgbSeekBar[2].getProgress();

        return Color.rgb(r,g,b);
    }
}
