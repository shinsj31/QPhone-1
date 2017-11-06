package com.example.a502.drawex;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.example.a502.drawex.MainActivity.IP;


/**
 * Created by 502 on 2017-10-02.
 */

public class store_notice_click extends Fragment {
    EditText titleEdit;
    EditText contentEdit;
    TextView titleText;
    TextView contentText;
    ViewGroup rootView;
    Bundle bundle;

    //디비
    String postURL = "http://"+IP+":8090/test/noticeFile.jsp";    //10.02 ip update OK
    String shop_code,notice_title,notice_text,notice_day,token,value2;
    int listIndex;
    Button btn,createBtn; //등록버튼, new
    Button alterBtn,deleteBtn,cancelBtn;
    String[] getJsonData = {"", "",""};
    loadJsp task;
    SharedPreferences appData;

    //공지
    FirebaseDatabase database;
    DatabaseReference myRef;
    Thread notiThread;
    private static final String FCM_MESSAGE_URL="https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY="AAAAkSsuMds:APA91bELYbPZGcyba30wL_CsGMZkb4h79tm8wbdCa6CTcx_1nRLndyHlFSeS8TWhU9XlZFnntL1PFiSwZKXv2nYxhfUxlEdeVteY3a-XucbDrVxCah8xT56q32hSapl5s1PJiVtoh_YN";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.store_notice_click, container, false);

        titleEdit= rootView.findViewById(R.id.titleEdit);
        contentEdit=rootView.findViewById(R.id.contentEdit);
        titleText=rootView.findViewById(R.id.titleText);
        contentText=rootView.findViewById(R.id.contentText);

        database=FirebaseDatabase.getInstance();
        myRef=database.getInstance().getReference("message");

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        bundle = this.getArguments();
        if (bundle != null) {
            Log.e("log1", "notNull");
            final String value = bundle.getString("key");
            if (value.equals("newNotice")) {
                Log.e("log1", "newNotice");
                titleEdit.setVisibility(View.VISIBLE);
                contentEdit.setVisibility(View.VISIBLE);

                titleText.setVisibility(View.INVISIBLE);
                contentText.setVisibility(View.INVISIBLE);

                createBtn = rootView.findViewById(R.id.editBtn);
                createBtn.setText("등록");
                createBtn.setOnClickListener(new View.OnClickListener() { //리스너 등록
                    @Override
                    public void onClick(View view) {

                        notice_title = titleEdit.getText().toString();
                        notice_text = contentEdit.getText().toString();
                        notice_day =getCurDate();

                        Log.e("공지사항 생성",notice_day+"--"+notice_text+"--"+notice_title);

                        task=new loadJsp("create");
                        task.execute();

                    }
                });

                btn = rootView.findViewById(R.id.delBtn);
                btn.setVisibility(View.INVISIBLE);

            } else if (value.equals("showNotice")) {    //수정, 삭제, 취소
                Log.e("log1", "showNotice");
                titleEdit.setVisibility(View.VISIBLE);  //수정 가능하도록
                contentEdit.setVisibility(View.VISIBLE);

                titleEdit.setText(bundle.getString("title"));
                contentEdit.setText(bundle.getString("notice_text")+"\n\n\n\n\n\n\n"+bundle.getString("notice_day"));

                titleText.setVisibility(View.INVISIBLE);
                contentText.setVisibility(View.INVISIBLE);

                listIndex=bundle.getInt("getIndex");    //쿼리 위한 리스트 인덱스 얻어오기
                Log.e("리스트 인덱스",listIndex+"");

                alterBtn=rootView.findViewById(R.id.editBtn);   //버튼들 등록
                deleteBtn=rootView.findViewById(R.id.delBtn);
                cancelBtn=rootView.findViewById(R.id.calBtn);

                alterBtn.setOnClickListener(new View.OnClickListener() {    //수정 버튼
                    @Override
                    public void onClick(View view) {
                        notice_title = titleEdit.getText().toString();
                        notice_text = contentEdit.getText().toString();
                        notice_day =getCurDate();

                        Log.e("공지사항 수정",notice_day+"--"+notice_text+"--"+notice_title);

                        task=new loadJsp("alter");
                        task.execute();
                    }
                });

                deleteBtn.setOnClickListener(new View.OnClickListener() {   //공지사항 삭제
                    @Override
                    public void onClick(View view) {
                        task=new loadJsp("delete");
                        task.execute();
                    }
                });
            }
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //변경사항이 있을 때
                    final String value=dataSnapshot.getValue(String.class);
                    value2=value;
                    token = FirebaseInstanceId.getInstance().getToken();
                    Log.e("Token토큰 : ",token);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("공지사항 실패","failldfjka;lsfjd");
                }
            });
        }

        appData=getActivity().getSharedPreferences("appData", Context.MODE_PRIVATE);

        return rootView;
    }

    class loadJsp extends AsyncTask<Void, String, Void> {
        String code;
        Boolean stateCode=true;
        loadJsp(String code) {
            this.code = code;
            Log.e("생성자", code + "");
        }

        protected Void doInBackground(Void... params) {

            Log.e("create", "공지 생성" + code + ""+appData.getString("SHOP_CODE",""));
            if (code.equals("create")) {
                try {
                    Log.e("newMessage",notice_title);
                    String newMessage=notice_title;
                    myRef.setValue(newMessage);

                    HttpClient client = new DefaultHttpClient();
                    //ip주소
                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    param.add(new BasicNameValuePair("shop_code", appData.getString("SHOP_CODE","")));    //임의의 값

                    param.add(new BasicNameValuePair("notice_title", notice_title));
                    param.add(new BasicNameValuePair("notice_text", notice_text));
                    param.add(new BasicNameValuePair("notice_day", notice_day));
                    //makeToast(notice_day+notice_title+notice_text);

                    param.add(new BasicNameValuePair("code", code)); //코드 : 공지생성

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
                        Log.e("create 공지사항 : ", line);
                    }

                    JSONObject json = new JSONObject(result);
                    JSONArray jArr = json.getJSONArray("dataSend");

                    for (int i = 0; i < jArr.length(); i++) {
                        json = jArr.getJSONObject(i);

                        getJsonData[0] = json.getString("code");

                        if ("fail".equals(getJsonData[0])) {
                            stateCode=false;
                            makeToast("공지사항 등록 실패");
                        }
                    }
                    if(stateCode){
                        makeToast("공지사항 등록 완료");
                    }
                    Log.e("서버에서 온 데이터 : ", getJsonData[0]);

                    JSONObject root=new JSONObject();
                    JSONObject notification=new JSONObject();
                    notification.put("body",value2);
                    notification.put("title","QPhone");
                    root.put("notification",notification);
                    root.put("to","/topics/all");
                    Log.e("Token토큰 : ",token);

                    URL url=new URL(FCM_MESSAGE_URL);
                    HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-type", "application/json");
                    OutputStream os = conn.getOutputStream();
                    os.write(root.toString().getBytes("utf-8"));
                    os.flush();
                    conn.getResponseCode();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (code.equals("alter")) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    //ip주소
                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    param.add(new BasicNameValuePair("shop_code", appData.getString("SHOP_CODE","")));    //임의의 값

                    param.add(new BasicNameValuePair("notice_title", notice_title));
                    param.add(new BasicNameValuePair("notice_text", notice_text));
                    param.add(new BasicNameValuePair("notice_day", getCurDate()));
                    param.add(new BasicNameValuePair("notice_index", listIndex+""));    //인덱스도 추가

                    param.add(new BasicNameValuePair("code", code)); //코드 : 공지생성

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
                        Log.e("alter 공지사항 : ", line);
                    }

                    JSONObject json = new JSONObject(result);
                    JSONArray jArr = json.getJSONArray("dataSend");

                    for (int i = 0; i < jArr.length(); i++) {
                        json = jArr.getJSONObject(i);

                        getJsonData[0] = json.getString("code");

                        if ("fail".equals(getJsonData[0])) {
                            stateCode=false;
                            makeToast("공지사항 수정 실패");
                        }
                    }
                    if(stateCode){
                        makeToast("공지사항 수정 완료");
                    }
                    Log.e("서버에서 온 데이터 : ", getJsonData[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (code.equals("delete")) {   //공지사항 삭제
                try {
                    HttpClient client = new DefaultHttpClient();
                    //ip주소
                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    param.add(new BasicNameValuePair("shop_code", appData.getString("SHOP_CODE","")));    //임의의 값
                    param.add(new BasicNameValuePair("notice_index", listIndex+""));    //인덱스도 추가

                    param.add(new BasicNameValuePair("code", code)); //코드 : 공지생성

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
                        Log.e("delete 공지사항 : ", line);
                    }

                    JSONObject json = new JSONObject(result);
                    JSONArray jArr = json.getJSONArray("dataSend");

                    for (int i = 0; i < jArr.length(); i++) {
                        json = jArr.getJSONObject(i);

                        getJsonData[0] = json.getString("code");

                        if ("fail".equals(getJsonData[0])) {
                            stateCode=false;
                            makeToast("공지사항 삭제 실패");
                        }
                    }
                    if(stateCode){
                        makeToast("공지사항 삭제 완료");
                    }
                    Log.e("서버에서 온 데이터 : ", getJsonData[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
    public String getCurDate(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");

        return CurYearFormat.format(date).toString()+"."+CurMonthFormat.format(date).toString()+"."+CurDayFormat.format(date).toString();
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg)
        {

        }
    };
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

    public static class MyFirebaseMessagingService extends FirebaseMessagingService {
        public MyFirebaseMessagingService(){
        }

        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {    //메세지 받기
            super.onMessageReceived(remoteMessage);

            if (remoteMessage.getData().size() > 0) {
                sendNotification(remoteMessage.getData().get("message"));
               // Log.e("메세지1",remoteMessage.getData().get("message"));
            }
            if (remoteMessage.getNotification() != null) {
                sendNotification(remoteMessage.getNotification().getBody());
                //Log.e("메세지2",remoteMessage.getNotification().getBody());
            }

        }
        private void sendNotification(String messageBody) { //메세지 보내기
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("QPhone 이벤트")
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }

    public static class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
        public MyFirebaseInstanceIDService(){}
        @Override
        public void onTokenRefresh() {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d("MyFCM", "FCM token: " + FirebaseInstanceId.getInstance().getToken());

            sendRegistrationToServer(refreshedToken);
            // TODO: 이후 생성등록된 토큰을 서버에 보내 저장해 두었다가 추가 작업을 할 수 있도록 한다.
        }

        private void sendRegistrationToServer(String token) {
            // OKHTTP를 이용해 웹서버로 토큰값을 날려준다.
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("Token", token)
                    .build();

            //request
            Request request = new Request.Builder()
                    .url("토큰 저장할라고 보낼 URL")
                    .post(body)
                    .build();

            try {
                client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
