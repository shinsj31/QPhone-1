package com.example.a502.drawex;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.HashMap;
import java.util.List;

import static com.example.a502.drawex.MainActivity.IP;

/**
 * Created by 502 on 2017-11-02.
 */

public class add_store extends AppCompatActivity {
    //디비
    String postURL = "http://"+IP+":8090/test/NewFile.jsp";    //10.02 ip update OK
    loadJsp task;
    private String[] getJsonData = {"", "", "", "","",""};
    List list=new ArrayList();
    ListView listview ;
    add_store_adapter adapter;
    SharedPreferences appData;
    String num_of_coupon=null,m_id,shop_name,shop_code=null;

    boolean startCheck=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_store);

        // Adapter 생성
        adapter = new add_store_adapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listVIew);
        appData=getSharedPreferences("appData", Context.MODE_PRIVATE);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                add_store_item item = (add_store_item)parent.getItemAtPosition(position);

                HashMap getHm=new HashMap();
                for(int k=0;k<list.size();k++){
                    getHm=(HashMap)list.get(k);
                    shop_name=(String)getHm.get("shop_name");

                    Log.e("add_store",shop_name+item.getTitle()+list.size()+"");

                    if(item.getTitle().equals(shop_name)){  //선택 한 가게이름 추가
                        m_id=appData.getString("ID","");
                        shop_code=(String)getHm.get("shop_code");

                        task=new loadJsp("addStore");
                        task.execute();
                    }
                }

                String titleStr = item.getTitle();
                Toast toast = Toast.makeText(getApplicationContext(), titleStr+"을 추가하였습니다.", Toast.LENGTH_SHORT);
                toast.show();

            }
        }) ;

        task=new loadJsp("getShopList");
        task.execute();
    }
    class loadJsp extends AsyncTask<Void, String, Void> {
        String code;

        loadJsp(String code) {
            this.code = code;
            Log.e("생성자", code + "");
        }

        protected Void doInBackground(Void... params) {
            if (code.equals("getShopList")) { //내 가게 정보 가져오기
                Log.e("getShopList", "공지사항 리스트 가져오기" + code + "");
                try {
                    HttpClient client = new DefaultHttpClient();

                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    //param.add(new BasicNameValuePair("id", appData.getString("ID","")));
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
                        HashMap hm = new HashMap();
                        getJsonData[0] = json.getString("shop_name");
                        hm.put("shop_name", json.getString("shop_name").toString());

                        getJsonData[1] = json.getString("shop_locate");
                        hm.put("shop_locate", json.getString("shop_locate").toString());

                        getJsonData[2] = json.getString("shop_code");
                        hm.put("shop_code", json.getString("shop_code").toString());

                        adapter.addItem(getJsonData[0].toString(), getJsonData[1].toString());   //어댑터에 추가
                        Log.e("해쉬맵",json.getString("shop_name").toString()+json.getString("shop_locate").toString());

                        list.add(hm);
                    }

                    adapter.notifyDataSetChanged(); //리스트뷰 갱신
                    new Thread(new Runnable() { //UI변경
                        @Override
                        public void run() {
                            Message msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (code.equals("addStore")) { //내 가게 정보 가져오기
                Log.e("getShopList", "공지사항 리스트 가져오기" + code + "");
                try {
                    HttpClient client = new DefaultHttpClient();

                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    param.add(new BasicNameValuePair("m_id", appData.getString("ID", "")));
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