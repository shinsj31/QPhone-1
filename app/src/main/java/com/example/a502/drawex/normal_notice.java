package com.example.a502.drawex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.HashMap;
import java.util.List;

import static com.example.a502.drawex.MainActivity.IP;

/**
 * Created by 502 on 2017-10-20.
 */

public class normal_notice extends AppCompatActivity {
    //디비
    ListView listview ;
    normal_notice_adapter adapter;
    Intent it;
    List list=new ArrayList();

    String postURL = "http://"+IP+":8090/test/noticeFile.jsp";    //10.02 ip update OK
    String shop_name;
    loadJsp task;
    String[] getJsonData = {"", "",""};
    SharedPreferences appData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.normal_notice);
        it=getIntent();
        shop_name=it.getStringExtra("shop_name");
        Log.e("normal_notice : ",shop_name);

        // Adapter 생성
        adapter = new normal_notice_adapter() ;

        task=new loadJsp("getStoreNotice");    //초기화면 리스트 달기
        task.execute();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listVIew);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                normal_noitce_item item = (normal_noitce_item) parent.getItemAtPosition(position) ;
                normal_notice_click sc=new normal_notice_click();
                Intent i=new Intent(getApplicationContext(), normal_notice_click.class);

                HashMap getHm=new HashMap();
                String title=null,content=null,notice_day=null;

                for(int k=0;k<list.size();k++){
                    getHm=(HashMap)list.get(k);
                   // String local_shop_name=(String)getHm.get("shop_name");  //수정

                    if(item.getTitle().equals((String)getHm.get("notice_title"))){
                        title=(String)getHm.get("notice_title");
                        content=(String)getHm.get("notice_text");
                        notice_day=(String)getHm.get("notice_day");

                        i.putExtra("title",title);
                        i.putExtra("content",content);
                        i.putExtra("notice_day",notice_day);
                        Log.e("normal_notice",title+content+notice_day);
                    }
                }
                startActivity(i);
            }
        }) ;

    }
    class loadJsp extends AsyncTask<Void, String, Void> {
        String code;

        loadJsp(String code) {
            this.code = code;
            Log.e("생성자", code + "");
        }

        protected Void doInBackground(Void... params) {
            if (code.equals("getStoreNotice")) { //내 가게 정보 가져오기
                Log.e("getStoreNotice",shop_name);
                try {
                    HttpClient client = new DefaultHttpClient();

                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    param.add(new BasicNameValuePair("shop_name", shop_name));
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
                        HashMap hm=new HashMap();
                        json = jArr.getJSONObject(i);

                        getJsonData[0] = json.getString("notice_title");
                        getJsonData[1] = json.getString("notice_day");
                        getJsonData[2] = json.getString("notice_text");

                        hm.put("notice_title",getJsonData[0]);
                        hm.put("notice_day",getJsonData[1]);
                        hm.put("notice_text",getJsonData[2]);
                        hm.put("shop_name",shop_name);

                        adapter.addItem(getJsonData[0].toString(), getJsonData[1].toString()) ;   //어댑터에 추가
                        Log.e("notice 데이터",getJsonData[0].toString()+"==="+getJsonData[1].toString());

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
