package com.example.a502.drawex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
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
import java.util.List;
import java.util.Map;

import static com.example.a502.drawex.MainActivity.IP;

/**
 * Created by 502 on 2017-09-29.
 */

public class normal_home extends Fragment {
    ViewGroup rootView;
    AppCompatActivity activity;
    ListView listview;
    couponlist_adpater adapter;
    SharedPreferences appData;

    //디비
    String postURL = "http://"+IP+":8090/test/couponFile.jsp";    //10.02 ip update OK
    loadJsp task;
    private String[] getJsonData = {"", "", "", "","",""};
    List list=new ArrayList();

    ImageView add_store;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.normal_home, container, false);
        activity=(AppCompatActivity)getActivity();

        add_store =(ImageView)rootView.findViewById(R.id.registerStore);

        // Adapter 생성
        adapter = new couponlist_adpater() ;

        appData=activity.getSharedPreferences("appData", Context.MODE_PRIVATE);

        task=new loadJsp("getMemberCouponList");    //초기화면 리스트 달기
        task.execute();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView)rootView.findViewById(R.id.couponList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                couponlist_listviewitem item = (couponlist_listviewitem) parent.getItemAtPosition(position) ;
                Log.e("log1","click "+ position+ "  "+item.getStore_name());

                Intent i =new Intent(activity, couponlist_click.class);
                i.putExtra("storename",item.getStore_name());

                HashMap getHm=new HashMap();
                String num_of_coupon=null,shop_phone=null,shop_locate=null,shop_name=null,shop_code=null;

                for(int k=0;k<list.size();k++){
                    getHm=(HashMap)list.get(k);
                    shop_name=(String)getHm.get("shop_name");

                    Log.e("해쉬맵2",shop_name+item.getStore_name()+list.size()+"");
                    if(item.getStore_name().equals(shop_name)){
                        shop_locate=(String)getHm.get("shop_locate");
                        shop_phone=(String)getHm.get("shop_phone");
                        num_of_coupon=(String)getHm.get("num_of_coupon");
                        shop_code=(String)getHm.get("shop_code");

                        i.putExtra("shop_locate",shop_locate);
                        i.putExtra("shop_phone",shop_phone);
                        i.putExtra("num_of_coupon",num_of_coupon);
                        i.putExtra("shop_code",shop_code);
                    }
                }
                startActivity(i);
            }
        }) ;

        add_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2= new Intent(activity,add_store.class);
                startActivity(i2);
            }
        });
        return rootView;
    }
    class loadJsp extends AsyncTask<Void, String, Void> {
        String code;

        loadJsp(String code) {
            this.code = code;
            Log.e("생성자", code + "");
        }

        protected Void doInBackground(Void... params) {
            if (code.equals("getMemberCouponList")) { //내 가게 정보 가져오기
                Log.e("getMemberCouponList", "공지사항 리스트 가져오기" + code + "");
                try {
                    HttpClient client = new DefaultHttpClient();

                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    param.add(new BasicNameValuePair("id", appData.getString("ID","")));
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

                        getJsonData[0] = json.getString("num_of_coupon");   hm.put("num_of_coupon",json.getString("num_of_coupon").toString());
                        getJsonData[1] = json.getString("shop_name");   hm.put("shop_name",json.getString("shop_name").toString());
                        getJsonData[2] = json.getString("shop_locate"); hm.put("shop_locate",json.getString("shop_locate").toString());
                        getJsonData[3] = json.getString("shop_phone");  hm.put("shop_phone",json.getString("shop_phone").toString());
                        getJsonData[4] = json.getString("total_coupon");
                        getJsonData[5] = json.getString("shop_code");    hm.put("shop_code",json.getString("shop_code").toString());

                        adapter.addItem(getJsonData[1], Integer.parseInt(getJsonData[0].toString()),Integer.parseInt(getJsonData[4].toString()));   //어댑터에 추가
                        Log.e("해쉬맵",json.getString("shop_code").toString());

                        list.add(hm);
                    }

                    adapter.notifyDataSetChanged(); //리스트뷰 갱신

                    new Thread(new Runnable() { //UI변경
                        @Override
                        public void run() {
                            Message msg=handler.obtainMessage();
                            handler.sendMessage(msg);
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
    final Handler handler = new Handler() {
        public void handleMessage(Message msg)
        {
            listview.setAdapter(adapter);
        }
    };
}