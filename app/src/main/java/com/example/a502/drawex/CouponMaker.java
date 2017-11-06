package com.example.a502.drawex;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.Log;

/**
 * Created by soo on 2017-10-11.
 */

public class CouponMaker {
    //담당하는 CouponView
    private CouponView couponView;
    //Paints
    private Paint backPaint;
    private Paint insidePaint;
    private Paint linePaint;
    private Paint circlePaint;

    //Bitmap
    private Bitmap stampBefore;
    private Bitmap stampAfter;
    private Bitmap eventBefore;
    private Bitmap eventAfter;

    //num of stamp
    private int numOfStamp=0;
    private int numOfCol=1;
    private int userStamp=0;

    //event
    private String str="";
    private boolean[] events;

    public CouponMaker(CouponView couponView)
    {
        this.couponView=couponView;

        backPaint=new Paint();
        backPaint.setColor(Color.rgb(253,166,157));
        backPaint.setStyle(Paint.Style.FILL);

        insidePaint=new Paint();
        insidePaint.setColor(Color.WHITE);
        insidePaint.setStyle(Paint.Style.FILL);

        linePaint=new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.LTGRAY);
        linePaint.setStrokeWidth(4);
        DashPathEffect effect=new DashPathEffect(new float[] {15,5 }, 0);
        linePaint.setPathEffect(effect);

        circlePaint=new Paint();
        circlePaint.setColor(Color.WHITE);
        events= new boolean[100];
        for(int i=0;i< events.length;i++)
            events[i]=false;
    }

    public void execute(){couponView.invalidate();}

    public void setBackPaint(Paint backPaint) { this.backPaint = backPaint; }
    public Paint getBackPaint() { return backPaint; }
    public void setInsidePaint(Paint insidePaint) { this.insidePaint = insidePaint;}
    public Paint getInsidePaint() {return insidePaint;}
    public void setLinePaint(Paint linePaint) {this.linePaint = linePaint;}
    public Paint getLinePaint() {return linePaint;}
    public void setCirclePaint(Paint circlePaint) {this.circlePaint = circlePaint;}
    public Paint getCirclePaint() {return circlePaint;}

    public void setStampBefore(Bitmap stampBefore) {this.stampBefore = stampBefore;}
    public Bitmap getStampBefore() {return stampBefore;}
    public void setStampAfter(Bitmap stampAfter) {this.stampAfter = stampAfter;}
    public Bitmap getStampAfter() {return stampAfter;}

    public void setNumOfCol(int numOfCol) {this.numOfCol = numOfCol;}
    public int getNumOfCol() {return numOfCol;}
    public void setNumOfStamp(int numOfStamp) {this.numOfStamp = numOfStamp; Log.e("set stamp",numOfStamp+"");}
    public int getNumOfStamp() {return numOfStamp;}
    public void setUserStamp(int userStamp) {this.userStamp = userStamp;}
    public int getUserStamp() {return userStamp;}

    public void setStr(String str) {this.str = str;}
    public String getStr() {return str;}

    public boolean[] getEvents() {return events;}
    public void setEvents(boolean[] events) {this.events = events;}

    public Bitmap getEventAfter() {return eventAfter;}
    public void setEventAfter(Bitmap eventAfter) {this.eventAfter = eventAfter;}
    public Bitmap getEventBefore() {return eventBefore;}
    public void setEventBefore(Bitmap eventBefore) {this.eventBefore = eventBefore;}
}
