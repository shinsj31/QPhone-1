package com.example.a502.drawex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.example.a502.drawex.MainActivity.IP;

public class store_custom5 extends AppCompatActivity {
    CouponView couponView;
    CouponMaker maker;
    Bundle bundle;

    //디비
    Button saveBtn;
    Boolean idResult=true;
    loadJsp task;
    String postURL="http://"+IP+":8090/test/couponFile.jsp",shop_code,str,events="";    //10.02 ip update OK
    int back,in,line,stampNum,colNum;
    private String[] getJsonData = {"", "", "", ""};
    SharedPreferences appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_custom5);

        couponView=(CouponView)findViewById(R.id.couponView);
        maker=new CouponMaker(couponView);
        couponView.setMaker(maker);

        Intent intent=getIntent();
        bundle=intent.getExtras();

        appData=getSharedPreferences("appData",MODE_PRIVATE);

        setPresetting();

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //데이터베이스에 값을 저장하는 부분.
                back=bundle.getInt("backCol");
                in=bundle.getInt("inCol");
                line=bundle.getInt("lineCol");
                stampNum=bundle.getInt("stampNum");
                colNum=bundle.getInt("colNum");
                str=bundle.getString("str");
                events="";
                boolean[] eventArray=bundle.getBooleanArray("events");
                for(int i=0;i<eventArray.length;i++)
                {
                    if(eventArray[i])
                        events+="T";
                    else
                        events+="F";
                }
                Log.e("컬러",back+","+in+","+line+",");
                task=new loadJsp("registCoupon");   //쿠폰 등록
                task.execute();
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
        maker.setEvents(bundle.getBooleanArray("events"));
        maker.setStr(bundle.getString("str"));
    }
    class loadJsp extends AsyncTask<Void, String, Void> {
        String code;

        loadJsp(String code) {
            this.code = code;
            Log.e("생성자", code + "");
        }

        protected Void doInBackground(Void... params) {

            Log.e("register", "쿠폰 등록 버튼" + code + "");
            if (code.equals("registCoupon")) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    //ip주소
                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    param.add(new BasicNameValuePair("shop_code", appData.getString("SHOP_CODE","")));
                    param.add(new BasicNameValuePair("code", code));

                    param.add(new BasicNameValuePair("back", back+""));
                    param.add(new BasicNameValuePair("in", in+""));
                    param.add(new BasicNameValuePair("line", line+""));
                    param.add(new BasicNameValuePair("stampNum", stampNum+""));
                    param.add(new BasicNameValuePair("colNum", colNum+""));
                    param.add(new BasicNameValuePair("str", str));
                    param.add(new BasicNameValuePair("events", events));

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
                        Log.e("idcheck 데이터 : ", line);
                    }

                    JSONObject json = new JSONObject(result);
                    JSONArray jArr = json.getJSONArray("dataSend");

                    for (int i = 0; i < jArr.length(); i++) {
                        json = jArr.getJSONObject(i);

                        getJsonData[0] = json.getString("code");

                        if ("fail".equals(getJsonData[0])) {
                            idResult = false;
                        }
                    }
                    if (idResult) {
                        makeToast("쿠폰 등록 완료");
                    }
                    else{
                        makeToast("쿠폰 등록 실패");
                    }

                    Log.e("서버에서 온 데이터 : ", getJsonData[0]);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public void makeToast(String str) {
        Message status = toaster.obtainMessage();
        Bundle datax = new Bundle();
        datax.putString("msg", str);
        status.setData(datax);
        toaster.sendMessage(status);
    }

    public Handler toaster = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
        }
    };
}
