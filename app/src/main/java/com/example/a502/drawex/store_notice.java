package com.example.a502.drawex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

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

/**
 * Created by 502 on 2017-10-01.
 */

public class store_notice extends Fragment {
    ViewGroup rootView;
    AppCompatActivity activity;
    ImageView newNotice;

    //디비
    ListView listview ;
    String postURL = "http://"+IP+":8090/test/noticeFile.jsp";    //10.02 ip update OK
    String shop_code;
    loadJsp task;
    store_notice_Adapter adapter;
    String[] getJsonData = {"", "",""};
    SharedPreferences appData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.store_notice, container, false);
        activity = (AppCompatActivity)getActivity();
        appData=activity.getSharedPreferences("appData", Context.MODE_PRIVATE); //10.31

        // Adapter 생성
        adapter = new store_notice_Adapter() ;

        task=new loadJsp("getList");    //초기화면 리스트 달기
        task.execute();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView)rootView.findViewById(R.id.listVIew);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {    //공지사항 수정

                store_notice_listviewItem item = (store_notice_listviewItem) parent.getItemAtPosition(position) ;
                Log.e("log1","click "+ position+ "  "+item.getTitle());

                store_notice_click sc=new store_notice_click();

                Bundle bundle = new Bundle();

                bundle.putString("key","showNotice");
                bundle.putString("title",item.getTitle());
                bundle.putString("notice_text",getJsonData[2].toString());
                bundle.putString("notice_day",item.getDesc());

                sc.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, sc).commit();
            }
        }) ;

        newNotice = (ImageView)rootView.findViewById(R.id.addNoticeBtn);    //새로운 공지사항
        newNotice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                store_notice_click sc=new store_notice_click();

                Bundle bundle2 = new Bundle();
                bundle2.putString("key","newNotice");
                bundle2.putInt("index",listview.getCheckedItemPosition());
                sc.setArguments(bundle2);

                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, sc).commit();
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
            if (code.equals("getList")) { //내 가게 정보 가져오기
                Log.e("getList", "공지사항 리스트 가져오기" + code + "");
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
                        getJsonData[0] = json.getString("notice_title").toString();
                        getJsonData[1] = json.getString("notice_day").toString();
                        getJsonData[2] = json.getString("notice_text").toString();

                        adapter.addItem(getJsonData[0].toString(), getJsonData[1].toString()) ;   //어댑터에 추가
                        Log.e("notice 데이터",getJsonData[0].toString()+"==="+getJsonData[1].toString());
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
