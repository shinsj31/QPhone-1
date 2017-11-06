package com.example.a502.drawex;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.example.a502.drawex.MainActivity.IP;

/**
 * Created by 502 on 2017-10-20.
 */

public class store_consumers extends Fragment {

    ViewGroup rootView;
    AppCompatActivity activity;
    ListView listview ;
    store_consumers_adapter adapter;

    SharedPreferences appData;
    BluetoothAdapter mBluetoothAdapter;

    String postURL = "http://"+IP+":8090/test/couponFile.jsp",token,value2;;
    loadJsp task;
    String[] getJsonData = {"", "","","","","",""};
    ArrayList<String> mArrayAdapter=new ArrayList<>();

    FirebaseDatabase database;
    DatabaseReference myRef;
    Thread notiThread;
    boolean bluetoothState=false;
    private static int updateNum=0;
    private static final String FCM_MESSAGE_URL="https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY="AAAAkSsuMds:APA91bELYbPZGcyba30wL_CsGMZkb4h79tm8wbdCa6CTcx_1nRLndyHlFSeS8TWhU9XlZFnntL1PFiSwZKXv2nYxhfUxlEdeVteY3a-XucbDrVxCah8xT56q32hSapl5s1PJiVtoh_YN";

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.store_consumers, container, false);
        activity=(AppCompatActivity)getActivity();
        adapter= new store_consumers_adapter();

        /* 블루투스 */
        database=FirebaseDatabase.getInstance();
        myRef=database.getInstance().getReference("bluetooth");
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        appData=activity.getSharedPreferences("appData", Context.MODE_PRIVATE);


        task=new loadJsp("getMemberList");    //초기화면 리스트 달기
        task.execute();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView)rootView.findViewById(R.id.consumers);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                store_consumers_item item = (store_consumers_item) parent.getItemAtPosition(position) ;

                String consumerId = item.getId() ;
                int hasCouponNum = item.getHascouponNum() ;

                Bundle bundle = new Bundle();
                bundle.putString("id",consumerId);
                bundle.putInt("num",hasCouponNum);

                store_consumers_item_click scc=new store_consumers_item_click();
                scc.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, scc).commit();
            }
        }) ;
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //변경사항이 있을 때
                final String value=dataSnapshot.getValue(String.class);
                value2=value;
                token = FirebaseInstanceId.getInstance().getToken();

                if(bluetoothState){
                    Log.e("firebase","store_consumers");
                    BluetoothAdapter mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
                    if(mBluetoothAdapter==null){
                        Log.e("bluetooth","블루투스 지원 X");
                    }
                    else{
                        if(!mBluetoothAdapter.isEnabled()){
                            mBluetoothAdapter.enable();
                            Log.e("bluetooth","블루투스 활성화");
                            bluetoothState=false;
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("공지사항 실패","failldfjka;lsfjd");
            }
        });
        task=new loadJsp("bluetooth");
        task.execute();

        BroadcastReceiver mReceiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mArrayAdapter.add(device.getAddress().toString());

                Log.e("블루투스",device.getAddress().toString());
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(mReceiver, filter); // 리시버 등록

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        if(!mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.startDiscovery();

        return rootView;
    }
    class loadJsp extends AsyncTask<Void, String, Void> {
        String code;

        loadJsp(String code) {
            this.code = code;
            Log.e("생성자", code + "");
        }

        protected Void doInBackground(Void... params) {
            if (code.equals("getMemberList")) { //내 가게 정보 가져오기
                Log.e("Member", "공지사항 리스트 가져오기" + code + "");
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

                        getJsonData[0] = json.getString("m_id");
                        getJsonData[1] = json.getString("num_of_coupon");
                        getJsonData[2] = json.getString("mac_address");

                        adapter.addItem(getJsonData[0].toString(), Integer.parseInt(getJsonData[1].toString())) ;   //어댑터에 추가
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
            else if (code.equals("bluetooth")) {
                try {
                    bluetoothState=true;
                    updateNum++;    //회원들 들어가면 static변수가 계속 증가해서 db update -> myRef통해서 블투를 켬
                    myRef.setValue(updateNum+"");
                    Log.e("블루투스활성화", updateNum+"");

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

    public static class MyFirebaseMessagingService2 extends FirebaseMessagingService {
        public MyFirebaseMessagingService2(){
        }

        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {    //메세지 받기
            super.onMessageReceived(remoteMessage);

            Log.e("firebase","store_consumers");
            /*블루투스 활성화*/
            BluetoothAdapter mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
            if(mBluetoothAdapter==null){
                Log.e("bluetooth","블루투스 지원 X");
            }
            else{
                if(!mBluetoothAdapter.isEnabled()){
                    mBluetoothAdapter.enable();
                    Log.e("bluetooth","블루투스 활성화");
                }
                else{
                    mBluetoothAdapter.disable();
                    Log.e("bluetooth","블루투스 비활성화");
                }

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
                    .setContentTitle("QPhone 블루투스 활성화")
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }

    public static class MyFirebaseInstanceIDService2 extends FirebaseInstanceIdService {
        public MyFirebaseInstanceIDService2(){}
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
