package com.example.a502.drawex;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.a502.drawex.MainActivity.IP;

/**
 * Created by 502 on 2017-10-20.
 */

public class store_consumers_item_click extends Fragment {

    ViewGroup rootView;
    AppCompatActivity activity;
    CouponView couponView;
    CouponMaker maker;
    TextView idTextView;
    TextView numTextView;
    Button saveBtn;

    ImageView useCouponView,stampView,refreshView;  //10.31
    String postURL = "http://"+IP+":8090/test/couponFile.jsp";    //10.02 ip update OK
    loadJsp task;
    private String[] getJsonData = {"","","","","","",""};
    String id;
    SharedPreferences appData;
    int hascouponNum;
    int alterCoupon=0,subCoupon=0;
    int totalStamp=0;
    int startNum=0;
    boolean checkThread=false,makerCheck=true;
    Thread makerThread=null;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.store_consumers_item_click, container, false);
        activity=(AppCompatActivity)getActivity();
        couponView=(CouponView)rootView.findViewById(R.id.couponView);
        maker=new CouponMaker(couponView);
        couponView.setMaker(maker);
        idTextView=(TextView)rootView.findViewById(R.id.idText);
        numTextView=(TextView)rootView.findViewById(R.id.hasCouponNum) ;
        saveBtn=(Button)rootView.findViewById(R.id.saveBtn);

        appData=activity.getSharedPreferences("appData", Context.MODE_PRIVATE); //10.31

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id=bundle.getString("id");
            hascouponNum = bundle.getInt("num");

            alterCoupon=hascouponNum;   //현재 쿠폰 수 초기화

            idTextView.setText(id);
            numTextView.setText(hascouponNum + "");
        }

        ViewTreeObserver vto = rootView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                int width = couponView.getWidth();
                int height = (int) (width * 2 / 3.0);
                ViewGroup.LayoutParams params = couponView.getLayoutParams();
                params.height = height;
                couponView.setLayoutParams(params);
                //Log.e("log1","height : "+ height);
            }
        });

        useCouponView=(ImageView)rootView.findViewById(R.id.usecoupon); //사용
        useCouponView.setOnClickListener(new ImageView.OnClickListener(){   //쿠폰 빼기
            @Override
            public void onClick(View view) {
                if(checkThread) {
                    task=new loadJsp("subCoupon");  //토탈만큼 차감
                    task.execute();
                }
            }
        });

        refreshView=(ImageView)rootView.findViewById(R.id.refresh); //10.31
        refreshView.setOnClickListener(new ImageView.OnClickListener(){   //쿠폰 빼기
            @Override
            public void onClick(View view) {
                if(checkThread) {
                    alterCoupon--;
                    subCoupon--;
                    maker.setUserStamp(subCoupon);
                    maker.execute();

                    Log.e("maker1",alterCoupon+";;"+subCoupon+"");
                }
            }
        });

        stampView=(ImageView)rootView.findViewById(R.id.stamp);
        stampView.setOnClickListener(new ImageView.OnClickListener(){   //쿠폰 추가
            @Override
            public void onClick(View view) {
                if(checkThread) {
                    subCoupon++;
                    if(subCoupon==totalStamp)
                    {
                        subCoupon=0;
                        int temp=Integer.parseInt(numTextView.getText().toString());
                        temp++;
                        numTextView.setText(temp+"");
                    }
                    alterCoupon++;
                    maker.setUserStamp(subCoupon);
                    maker.execute();
                    Log.e("maker2",alterCoupon+";;"+subCoupon+"");
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(alterCoupon<hascouponNum&&checkThread){  //감소시킬때
                    task=new loadJsp("spendCoupon");
                    task.execute();
                }
                else{
                    task=new loadJsp("addCoupon");
                    task.execute();
                }
            }
        });

        //setPresetting();
        task=new loadJsp("getMemberCouponImage");   //쿠폰 이미지 가져오기
        task.execute();

        try {   //UI뜨는 시간
            Thread.sleep(1000);
        }
        catch (Exception e){
            Log.e("Thread","슬립오류");
        }
        return rootView;
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

        /////////////////////////////기본설정값/////////////////////////////////
        maker.setStampBefore(BitmapFactory.decodeResource(this.getResources(),R.drawable.default_stamp1));
        maker.setStampAfter(BitmapFactory.decodeResource(this.getResources(),R.drawable.default_stamp2));
        maker.setEventBefore(BitmapFactory.decodeResource(this.getResources(),R.drawable.default_event1));
        maker.setEventAfter(BitmapFactory.decodeResource(this.getResources(),R.drawable.default_event2));

    }
    class loadJsp extends AsyncTask<Void, String, Void> {
        String code;


        loadJsp(String code) {
            this.code = code;
            Log.e("생성자", code + "");
        }

        protected Void doInBackground(Void... params) {
            if (code.equals("spendCoupon")) { //쿠폰사용
                Log.e("getData", "로그인 버튼" + code + "");
                try {
                    HttpClient client = new DefaultHttpClient();

                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    param.add(new BasicNameValuePair("id", id));
                    param.add(new BasicNameValuePair("coupon_count",alterCoupon+""));
                    param.add(new BasicNameValuePair("shop_code", appData.getString("SHOP_CODE","")));
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
                    }

                    JSONObject json = new JSONObject(result);
                    JSONArray jArr = json.getJSONArray("dataSend");

                    for (int i = 0; i < jArr.length(); i++) {
                        json = jArr.getJSONObject(i);
                        getJsonData[0] = json.getString("code");
                    }

                    if (resEntity != null) {
                        Log.e("RESPONSE", EntityUtils.toString(resEntity));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (code.equals("addCoupon")) { //쿠폰적립
                Log.e("getData", "로그인 버튼" + code + "");
                try {
                    HttpClient client = new DefaultHttpClient();

                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    param.add(new BasicNameValuePair("id", id));
                    param.add(new BasicNameValuePair("coupon_count",alterCoupon+""));
                    param.add(new BasicNameValuePair("shop_code", appData.getString("SHOP_CODE","")));  //10.31
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
                    }

                    JSONObject json = new JSONObject(result);
                    JSONArray jArr = json.getJSONArray("dataSend");

                    for (int i = 0; i < jArr.length(); i++) {
                        json = jArr.getJSONObject(i);
                        getJsonData[0] = json.getString("code");
                    }

                    if(getJsonData[0].equals("success")){
                        makeToast("쿠폰 적립 완료");

                    }
                    else{
                        makeToast("쿠폰 적립 실패");
                    }

                    if (resEntity != null) {
                        Log.e("RESPONSE", EntityUtils.toString(resEntity));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (code.equals("getMemberCouponImage")) { //내 가게 정보 가져오기
                Log.e("getMemberCouponImage", "쿠폰 이미지 가져오기" + code + "");
                try {
                    HttpClient client = new DefaultHttpClient();

                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    param.add(new BasicNameValuePair("shop_code", appData.getString("SHOP_CODE","")));
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
                                 Message msg = handler2.obtainMessage();
                                 handler2.sendMessage(msg);
                        }
                    }).start();

                    checkThread=true;


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (code.equals("subCoupon")) { //쿠폰사용
                Log.e("getData", "로그인 버튼" + code + "");
                try {
                    HttpClient client = new DefaultHttpClient();

                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    int alterCoupon=hascouponNum-totalStamp;
                    if(alterCoupon<0)
                        return null;

                    param.add(new BasicNameValuePair("id", id));
                    param.add(new BasicNameValuePair("coupon_count",alterCoupon+""));
                    param.add(new BasicNameValuePair("shop_code", appData.getString("SHOP_CODE","")));
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
                    }

                    JSONObject json = new JSONObject(result);
                    JSONArray jArr = json.getJSONArray("dataSend");

                    for (int i = 0; i < jArr.length(); i++) {
                        json = jArr.getJSONObject(i);
                        getJsonData[0] = json.getString("code");
                    }

                    if(getJsonData[0].equals("success")){
                        makeToast("쿠폰 사용 완료");
                    }
                    else{
                        makeToast("쿠폰 사용 실패");
                    }

                    if (resEntity != null) {
                        Log.e("RESPONSE", EntityUtils.toString(resEntity));
                    }
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
            numTextView.setText(coupon_count+"");

            if(totalStamp==0){
                makeToast("쿠폰을 등록하세요");
            }
            else {
                subCoupon = alterCoupon % totalStamp;   //초기
                maker.setUserStamp(subCoupon);    //0으로
                maker.execute();
            }
            Log.e("쿠폰넘넘넘",(alterCoupon%totalStamp)+"");
        }
    }
    public void makeToast(String str) {
        Message status = toaster.obtainMessage();
        Bundle datax = new Bundle();
        datax.putString("msg", str);
        status.setData(datax);
        toaster.sendMessage(status);
    }
    public Handler toaster = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getContext(), msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
        }
    };

    final Handler handler2 = new Handler() {
        public void handleMessage(Message msg)
        {
            setPresetting();
        }
    };

}
