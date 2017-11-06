package com.example.a502.drawex;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

import static com.example.a502.drawex.MainActivity.IP;

/**
 * Created by 502 on 2017-09-29.
 */

public class register extends Fragment{
    EditText idEdit, pwEdit, phoneEdit, emailEdit, pwCheckEdit;
    Button joinBtn, idCheckBtn;
    String id, pw, phone, email, pwCheck;
    CheckBox normalCheck, storeCheck;
    loadJsp task;
    String postURL;    //10.02 ip update OK
    Handler mHandler;
    private String[] getJsonData = {"", "", "", ""};
    ViewGroup rootView;

    AppCompatActivity activity;

    Button registerBtn;
    int check;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.register, container, false);
        activity = (AppCompatActivity) getActivity();

        registerBtn = (Button) rootView.findViewById(R.id.registerBtn);

        idEdit = (EditText) rootView.findViewById(R.id.id); //회원가입
        pwEdit = (EditText) rootView.findViewById(R.id.pw);
        pwCheckEdit = (EditText) rootView.findViewById(R.id.check_pw);
        phoneEdit = (EditText) rootView.findViewById(R.id.phonenumber);
        emailEdit = (EditText) rootView.findViewById(R.id.email);

        idCheckBtn = (Button) rootView.findViewById(R.id.id_check);

        //번들로 넘어오는부분 ★★★★★★★★★★저어어엉고오오오으으으은 이부분임!!!!!
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            check = bundle.getInt("check");
        }

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = idEdit.getText().toString();
                pw = pwEdit.getText().toString();
                phone = phoneEdit.getText().toString();
                email = emailEdit.getText().toString();
                pwCheck = pwCheckEdit.getText().toString();

                if (!pw.equals(pwCheck)) {    //패스워드 안맞으면
                    Log.e("password check", "패스워드체크");
                    Toast.makeText(getActivity(), "비밀번호 오류", Toast.LENGTH_SHORT).show();  //패스워드 체크
                } else {
                    if (check == 1) {   //회원일 때
                        postURL = "http://"+IP+":8090/test/NewFile.jsp";
                        Toast.makeText(getActivity(), "회원", Toast.LENGTH_SHORT).show();  //패스워드 체크
                    } else if (check == 2) {    //가게일 때
                        postURL = "http://"+IP+":8090/test/directorFile.jsp";
                        Toast.makeText(getActivity(), "관리자", Toast.LENGTH_SHORT).show();  //패스워드 체크
                    }
                    task = new loadJsp("register"); //회원가입
                    task.execute();
                }

            }
        });
        idCheckBtn.setOnClickListener(new View.OnClickListener() {   //아이디 중복체크
            @Override
            public void onClick(View v) {

                if (check==1) {   //회원일 때
                    postURL = "http://"+IP+":8090/test/NewFile.jsp";
                } else if (check==2) {    //가게일 때
                    postURL = "http://"+IP+":8090/test/directorFile.jsp";
                }

                id = idEdit.getText().toString();

                task = new loadJsp("idCheck"); //회원가입
                task.execute();

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

            Log.e("register", "회원가입 버튼" + code + "");
            if (code.equals("register")) {
                try {
                    WifiManager mng = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = mng.getConnectionInfo();
                    String wifiMac = info.getMacAddress();
                   // Log.e("와이파이",wifiMac);

                    HttpClient client = new DefaultHttpClient();
                    //ip주소
                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    param.add(new BasicNameValuePair("id", id));
                    param.add(new BasicNameValuePair("pw", pw));
                    param.add(new BasicNameValuePair("phone", phone));
                    param.add(new BasicNameValuePair("email", email));
                    param.add(new BasicNameValuePair("mac", wifiMac));
                    param.add(new BasicNameValuePair("code", code)); //코드 : 회원가입

                    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(param, HTTP.UTF_8);
                    post.setEntity(ent);

                    HttpResponse responsePost = client.execute(post);
                    HttpEntity resEntity = responsePost.getEntity();

                    makeToast("회원가입 완료");

                    if (resEntity != null) {
                        Log.e("RESPONSE", EntityUtils.toString(resEntity));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() { //UI변경
                    @Override
                    public void run() {
                        Message msg=handler2.obtainMessage();
                        handler2.sendMessage(msg);
                    }
                }).start();
            } else if (code.equals("idCheck")) {
                try {
                    boolean idResult = true;   //성공 여부
                    HttpClient client = new DefaultHttpClient();
                    //ip주소
                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    param.add(new BasicNameValuePair("id", id));
                    param.add(new BasicNameValuePair("code", code)); //코드 : 회원가입

                    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(param, HTTP.UTF_8);
                    post.setEntity(ent);
                    Log.e("회원가입","들어옴");
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
                            new Thread(new Runnable() { //UI변경
                                @Override
                                public void run() {
                                    Message msg = handler.obtainMessage();
                                    handler.sendMessage(msg);

                                }
                            }).start();
                        }
                    }
                    if (idResult) {
                        makeToast("사용 가능한 아이디 입니다");
                    }

                    Log.e("서버에서 온 데이터 : ", getJsonData[0]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
    final Handler handler2 = new Handler() {
        public void handleMessage(Message msg)
        {
            if(check==1)
                ;
            else{
                Bundle bundle=new Bundle();
                bundle.putString("id",id);

                store_register s=new store_register();
                s.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, s).commit();

            }
        }
    };

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            idEdit.setText("");
            idEdit.setHint("아이디 중복");   //아이디중복 세팅
            idEdit.setHintTextColor(Color.RED);
        }
    };

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
            Toast.makeText(getContext(), msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
        }
    };
}