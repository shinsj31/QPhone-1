package com.example.a502.drawex;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 502 on 2017-10-14.
 */

public class couponlist_click extends AppCompatActivity {
    private CouponView couponView;
    private CouponMaker maker;
    String shop_phone,shop_locate,num_of_coupon,shop_code,shop_name;
    String IP=MainActivity.IP;
    TextView couponTxt,posTxt,telTxt;

    //이미지
    int hascouponNum;
    int alterCoupon=0,subCoupon=0;
    int totalStamp=0;

    //디비
    String postURL = "http://"+IP+":8090/test/couponFile.jsp";    //10.02 ip update OK
    loadJsp task;
    private String[] getJsonData = {"", "", "", "","","",""};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.couponlist_click);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);

        Intent i =getIntent();
        couponView=(CouponView)findViewById(R.id.couponView);
        maker=new CouponMaker(couponView);
        couponView.setMaker(maker);

        couponTxt=(TextView)findViewById(R.id.text_numOfCoupon);
        posTxt=(TextView)findViewById(R.id.pos_text);
        telTxt=(TextView)findViewById(R.id.tel_text);


        //setPresetting();
        task=new loadJsp("getMemberCouponImage");   //쿠폰 이미지 가져오기
        task.execute();

        if (i != null) {

            String storename = i.getStringExtra("storename");
            shop_name=storename;
            collapsingToolbar.setTitle(storename);

            shop_phone=i.getStringExtra("shop_phone");
            shop_locate=i.getStringExtra("shop_locate");
            num_of_coupon=i.getStringExtra("num_of_coupon");
            shop_code=i.getStringExtra("shop_code");
            Log.e("couponlist_click",shop_code);

            hascouponNum=Integer.parseInt(num_of_coupon);   //현재 갖고있는 쿠폰 총 갯수
            alterCoupon=hascouponNum;   //쿠폰 갯수 초기화

            new Thread(new Runnable() { //UI변경
                @Override
                public void run() {
                    Message msg=handler.obtainMessage();
                    handler.sendMessage(msg);
                }
            }).start();
        }
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
        //이부분을 DB에서 받아온 정보로 적용해야한다. 여기는 가게 ID로 해당 가게의 쿠폰 정보 받아오기
        Paint temp;
        temp=maker.getBackPaint();
        temp.setColor(Integer.parseInt(getJsonData[1].toString())); //background_color
        maker.setBackPaint(temp);
        temp=maker.getInsidePaint();
        temp.setColor(Integer.parseInt(getJsonData[0].toString())); //inside_color
        maker.setInsidePaint(temp);
        temp=maker.getLinePaint();
        temp.setColor(Integer.parseInt(getJsonData[2].toString()));    //line_color
        maker.setLinePaint(temp);
        maker.setNumOfStamp(Integer.parseInt(getJsonData[3].toString()));    //total_stamp
        maker.setNumOfCol(Integer.parseInt(getJsonData[4].toString()));   //column_stamp


        /////////////이부분은 string형으로 받아온 이벤트 리스트를 boolean형으로 바꾸는 구간
        ///////////일단은 임의로 설정////////////////////////////////////////////
        String eventList=getJsonData[5].toString();    //event_array
        for(int i=0;i<100;i++)
            eventList+='F';
        char[] arr=eventList.toCharArray();
        //arr[9]='T';
        //eventList=arr.toString();
        boolean[] events=new boolean[100];
        for(int i=0; i<events.length; i++)
        {
            if(eventList.charAt(i)=='T')
                events[i]=true;
            else
                events[i]=false;
        }
        /////////////////////////////////////////
        maker.setEvents(events);
        maker.setStr(getJsonData[6].toString());    //event_string

        ///////////////////////유저로부터 받아올 정보///////////////////////////
        //maker.setUserStamp(Integer.parseInt(num_of_coupon));  //

        /////////////////////////////기본설정값/////////////////////////////////
        maker.setStampBefore(BitmapFactory.decodeResource(this.getResources(),R.drawable.default_stamp1));
        maker.setStampAfter(BitmapFactory.decodeResource(this.getResources(),R.drawable.default_stamp2));
        maker.setEventBefore(BitmapFactory.decodeResource(this.getResources(),R.drawable.default_event1));
        maker.setEventAfter(BitmapFactory.decodeResource(this.getResources(),R.drawable.default_event2));

    }

    public void onClick(View v)
    {
        Intent i= new Intent(this, normal_notice.class);
        i.putExtra("shop_name",shop_name);
        startActivity(i);
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg)
        {
            couponTxt.setText(num_of_coupon);
            posTxt.setText(shop_locate);
            telTxt.setText(shop_phone);
        }
    };

    class loadJsp extends AsyncTask<Void, String, Void> {
        String code;

        loadJsp(String code) {
            this.code = code;
            Log.e("생성자", code + "");
        }

        protected Void doInBackground(Void... params) {
            if (code.equals("getMemberCouponImage")) { //내 가게 정보 가져오기
                Log.e("getMemberCouponImage", "쿠폰 이미지 가져오기" + code + "");
                try {
                    HttpClient client = new DefaultHttpClient();

                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    param.add(new BasicNameValuePair("shop_code", shop_code));
                    param.add(new BasicNameValuePair("code", code)); //코드 : 로그인

                    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(param, HTTP.UTF_8);
                    post.setEntity(ent);

                    HttpResponse responsePost = client.execute(post);
                    HttpEntity resEntity = responsePost.getEntity();

                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(responsePost.getEntity().getContent(), "utf-8"));
                    String line = null;
                    String result = "";

                    //줄단위로 읽어오기
                    while ((line = bufReader.readLine()) != null) {
                        result += line;
                        //Log.e("notice 데이터",line);
                    }

                    JSONObject json = new JSONObject(result);
                    JSONArray jArr = json.getJSONArray("dataSend");

                    for (int i = 0; i < jArr.length(); i++) {

                        json = jArr.getJSONObject(i);
                        HashMap hm=new HashMap();

                        getJsonData[0] = json.getString("inside_color");
                        getJsonData[1] = json.getString("background_color");
                        getJsonData[2] = json.getString("line_color");
                        getJsonData[3] = json.getString("total_stamp");
                        getJsonData[4] = json.getString("column_stamp");
                        getJsonData[5] = json.getString("event_array");
                        getJsonData[6] = json.getString("event_string");

                        totalStamp=Integer.parseInt(getJsonData[3]);
                        //Log.e("해쉬맵",json.getString("shop_code").toString());

                    }

                    new Thread(new Runnable() { //UI변경
                        @Override
                        public void run() { //이미지 업데이트
                            Message msg=handler2.obtainMessage();
                            handler2.sendMessage(msg);
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int coupon_count=alterCoupon/totalStamp;
            couponTxt.setText("사용가능 한 쿠폰 수 "+coupon_count+"");


            subCoupon=alterCoupon%totalStamp;   //초기
            maker.setUserStamp(subCoupon);    //0으로
            maker.execute();

            Log.e("쿠폰넘넘넘",(alterCoupon%totalStamp)+"");
        }
    }
    final Handler handler2 = new Handler() {
        public void handleMessage(Message msg)
        {
            setPresetting();
        }
    };
}
