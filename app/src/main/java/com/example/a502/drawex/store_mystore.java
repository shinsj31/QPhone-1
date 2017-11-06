package com.example.a502.drawex;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBar;

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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.jar.Manifest;

import static android.app.Activity.RESULT_OK;
import static com.example.a502.drawex.MainActivity.IP;

/**
 * Created by 502 on 2017-09-30.
 */

public class store_mystore extends Fragment {

    ViewGroup rootView;
    TextView codeTxt,posTxt,telTxt;
    Switch sw;
    AppCompatActivity activity;

    final int REQ_CODE_SELECT_IMAGE=100;
    final int CROP=200;
    private ImageView imgview;
    String id;   //관리자 아이디

    CollapsingToolbarLayout collapsingToolbar;
    Bundle id_bundle;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;

    //디비
    String shop_code;
    boolean shop_open;
    String postURL = "http://"+IP+":8090/test/directorFile.jsp";    //10.02 ip update OK
    SharedPreferences appData;

    loadJsp task;
    private String[] getJsonData = {"", "", "", "",""};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.store_mystore, container, false);
        collapsingToolbar = (CollapsingToolbarLayout)rootView.findViewById(R.id.collapsing_toolbar);
        activity = (AppCompatActivity) getActivity();

        sw=(Switch)rootView.findViewById(R.id.openClose);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true){
                    sw.setText("가게 열림");
                    shop_open=true;
                }
                else{
                    sw.setText("가게 닫힘");
                    shop_open=false;
                }
                task = new loadJsp("setOpen");    //가게 오픈 설정 변경
                task.execute();
            }
        });

        imgview=(ImageView)rootView.findViewById(R.id.mystore_image);
        codeTxt=(TextView)rootView.findViewById(R.id.code_text);
        posTxt=(TextView)rootView.findViewById(R.id.pos_text);
        telTxt=(TextView)rootView.findViewById(R.id.tel_text);

        Button button = (Button) rootView.findViewById(R.id.pic_btn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkPermission();  //접근 허용

            }
        });

        //id_bundle=this.getArguments();  //회원가입
        appData=activity.getSharedPreferences("appData", Context.MODE_PRIVATE);

       // if (id_bundle != null) {
            id = appData.getString("ID","");   //공유 데이터에서 가져오기
       // }
        task = new loadJsp("getData");
        task.execute();
        return rootView;
    }

    public void getPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK);

        File tempFile = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
        Uri tempUri = Uri.fromFile(tempFile);

        intent.putExtra("crop", "true");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);

        intent.setType("image/*");

        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       // Toast.makeText(activity, "resultCode : "+resultCode,Toast.LENGTH_SHORT).show();
        if(requestCode == REQ_CODE_SELECT_IMAGE) {

            if (resultCode == Activity.RESULT_OK) {
                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    //String name_Str = getImageNameToUri(data.getData());
                    Uri uri=data.getData();
                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap 	= MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);

                    //배치해놓은 ImageView에 set
                    imgview.setImageBitmap(image_bitmap);

                    //Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();


                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    Log.e("test3", e.getMessage());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.e("test1", e.getMessage());
                } catch (Exception e)
                {
                    e.getStackTrace();
                }

            }
        }


    }
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(activity,android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CONTACTS)) {
                // Explain to the user why we need to write the permission.
                Log.d("test", "WRITE_EXTERNAL_STORAGE");
            }

            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            getPhoto();
            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant

        } else {
            // 다음 부분은 항상 허용일 경우에 해당이 됩니다.
            getPhoto();

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("test", "Permission granted");

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    Log.d("test", "Permission always deny");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }
    class loadJsp extends AsyncTask<Void, String, Void> {
        String code;

        loadJsp(String code) {
            this.code = code;
            Log.e("생성자", code + "");
        }

        protected Void doInBackground(Void... params) {
            if (code.equals("getData")) { //내 가게 정보 가져오기
                Log.e("getData", "로그인 버튼" + code + "");
                try {
                    HttpClient client = new DefaultHttpClient();

                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    param.add(new BasicNameValuePair("id", id));
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
                        getJsonData[0] = json.getString("shop_name").toString();
                        getJsonData[1] = json.getString("shop_locate").toString();
                        getJsonData[2] = json.getString("shop_phone").toString();
                        getJsonData[3] = json.getString("shop_open").toString();
                        getJsonData[4] = json.getString("shop_code").toString();

                        Log.e("store_mystore",json.getString("shop_name").toString() + json.getString("shop_locate").toString());
                    }

                    if (resEntity != null) {
                        //Log.e("RESPONSE", EntityUtils.toString(resEntity));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() { //UI변경
                    @Override
                    public void run() {
                        Message msg=handler.obtainMessage();
                        handler.sendMessage(msg);
                    }
                }).start();

            } else if (code.equals("setOpen")) {    //가게 오픈 설정
                try {
                    HttpClient client = new DefaultHttpClient();

                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    param.add(new BasicNameValuePair("shop_code",  appData.getString("SHOP_CODE","")));   //10.31
                    param.add(new BasicNameValuePair("code", code)); //코드 : 로그인

                    if(shop_open) {
                        param.add(new BasicNameValuePair("shop_open", "true"));
                        Log.e("set open",shop_open+"");
                    }
                    else {
                        param.add(new BasicNameValuePair("shop_open", "false"));
                        Log.e("set open",shop_open+"");
                    }

                    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(param, HTTP.UTF_8);
                    post.setEntity(ent);

                    HttpResponse responsePost = client.execute(post);
                    HttpEntity resEntity = responsePost.getEntity();
                    Log.e("set open result",shop_open+"");

                    if (resEntity != null) {
                        Log.e("RESPONSE", EntityUtils.toString(resEntity));
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
            collapsingToolbar.setTitle(getJsonData[0].toString());
            codeTxt.setText(appData.getString("SHOP_CODE",""));
            posTxt.setText(getJsonData[1].toString());
            telTxt.setText(getJsonData[2].toString());
        }
    };
}
