package com.example.a502.drawex;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by soo on 2017-09-23.
 */

public class
CouponView extends View {
    private TypedArray types;
    private Context mCotext;

    private CouponMaker maker;

    //생성자
    public CouponView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mCotext=context;
        types=context.obtainStyledAttributes(attrs, R.styleable.CouponView);
    }
    //쿠폰메이커 등록
    public void setMaker(CouponMaker couponMaker){maker=couponMaker;}
    public CouponMaker getMaker(){return maker;}

    //쿠폰 뷰 그리기
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height=getHeight();
        int width=getWidth();

        Paint couponBack=maker.getBackPaint();
        Paint couponIn=maker.getInsidePaint();

        Point start=new Point(height/7-15,height/7);
        Point end=new Point(width-(height/7)+15,height-(height/7));
        int coupon_height=end.y-start.y;
        int coupon_width=end.x-start.x;

        //패널 그리기
        canvas.drawRoundRect(0,0,width,height,height/7,height/7,couponBack);
        canvas.drawRect(start.x,start.y,end.x,end.y,couponIn);
        //점선무늬 그리기
        Path path=new Path();
        path.reset();
        path.moveTo(start.x+20,start.y+(coupon_height/10));
        path.lineTo(end.x-20,start.y+(coupon_height/10));
        canvas.drawPath(path,maker.getLinePaint());
        path.reset();
        path.moveTo(start.x+20,end.y-(coupon_height/10));
        path.lineTo(end.x-20,end.y-(coupon_height/10));
        canvas.drawPath(path,maker.getLinePaint());
        //도장칸 그리기
        //시작좌표와 끝좌표를 바꿔준다.
        start.x=start.x+(coupon_width/10);
        end.x=end.x-(coupon_width/10);
        start.y=start.y+(coupon_height/8);
        end.y=end.y-(coupon_height/4);

        width=end.x-start.x;
        height=end.y-start.y;
        /////////////////////////////////////////
        //백그라운드 원 그리기
        //도장칸 그리기
        int widthUnitSize=width/maker.getNumOfCol();
        int rowNum=maker.getNumOfStamp()/maker.getNumOfCol();
        if(maker.getNumOfStamp()%maker.getNumOfCol()>0)
            rowNum++;
        int heightUnitSize=0;
        if(rowNum!=0)
            heightUnitSize=height/rowNum;

        int halfWidth=widthUnitSize/2;
        int halfHeight=heightUnitSize/2;

        //백그라운드 원 그리기
        Paint circlePaint=maker.getCirclePaint();
        int rad= (halfHeight>halfWidth)?halfWidth-23:halfHeight-23;
        int cx;
        int cy;
        Bitmap stampBefore=null;
        Bitmap stampAfter=null;
        Bitmap eventBefore=null;
        Bitmap eventAfter=null;

        if(maker.getNumOfStamp()>0)
        {
            stampBefore=Bitmap.createScaledBitmap(maker.getStampBefore(), rad*2, rad*2, true);
            stampAfter=Bitmap.createScaledBitmap(maker.getStampAfter(), rad*2, rad*2, true);
        }
        if(maker.getEventAfter()!=null)
        {
            eventBefore=Bitmap.createScaledBitmap(maker.getEventBefore(), rad*2, rad*2, true);
            eventAfter=Bitmap.createScaledBitmap(maker.getEventAfter(), rad*2, rad*2, true);
        }


        for(int i=0; i<maker.getNumOfStamp(); i++)
        {

            cx=widthUnitSize*(i%maker.getNumOfCol())+halfWidth+start.x;
            cy=heightUnitSize*(i/maker.getNumOfCol())+halfHeight+start.y;
            canvas.drawCircle(cx,cy,rad,circlePaint);
            Log.i("log",maker.getEvents().length+"");
            if(!maker.getEvents()[i])
                canvas.drawBitmap(stampBefore,cx-rad,cy-rad,circlePaint);
            else
                canvas.drawBitmap(eventBefore,cx-rad,cy-rad,circlePaint);
        }

        for(int i=0; i<maker.getUserStamp();i++)
        {
            cx=widthUnitSize*(i%maker.getNumOfCol())+halfWidth+start.x;
            cy=heightUnitSize*(i/maker.getNumOfCol())+halfHeight+start.y;
            if(!maker.getEvents()[i])
                canvas.drawBitmap(stampAfter,cx-rad,cy-rad,circlePaint);
            else
                canvas.drawBitmap(eventAfter,cx-rad,cy-rad,circlePaint);
        }

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextSize(30);

        canvas.drawText(maker.getStr(),getWidth()/2,end.y+30,textPaint);
    }
}
