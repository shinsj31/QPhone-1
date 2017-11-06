package com.example.a502.drawex;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.example.a502.drawex.MainActivity.IP;

/**
 * Created by 502 on 2017-10-01.
 */

public class store_register extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;
    public  static String SHOP_CODE;
    final int REQ_CODE_SELECT_IMAGE=100;

    ViewGroup rootView;
    AppCompatActivity activity;

    Button photoBtn;
    Button storeRegisterBtn;
    ImageView img;

    //디비
    TextView storeCode;
    EditText storeName, storeCall, storePosition;
    loadJsp task;
    String postURL = "http://"+IP+":8090/test/directorFile.jsp", code, shop_name, shop_phone, shop_position,shop_code,d_id;    //10.02 ip update OK
    private String[] getJsonData = {"", "", "", ""};
    Bundle id_bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.store_register, container, false);

        activity=(AppCompatActivity)getActivity();

        photoBtn=(Button)rootView.findViewById(R.id.photoBtn);
        storeRegisterBtn=(Button)rootView.findViewById(R.id.store_register_btn);
        img=(ImageView)rootView.findViewById(R.id.imgView);

        storeName = rootView.findViewById(R.id.storeName);
        storeCall = rootView.findViewById(R.id.storeCall);
        storePosition = rootView.findViewById(R.id.storePosition);
        storeCode = rootView.findViewById(R.id.storeCode);

        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkPermission();  //접근 허용


            }
        });

        storeRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //login_select l=new login_select();
                //activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, l).commit();
                shop_name = storeName.getText().toString();     //가게이름
                shop_phone = storeCall.getText().toString();    //가게번호
                shop_position = storePosition.getText().toString();     //가게위치

                task = new loadJsp("shop_register"); //회원가입
                task.execute();

                login l =new login();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, l).commit();

            }
        });
        id_bundle=this.getArguments();

        task = new loadJsp("getCode");    //맨처음 shop_code 가져오기기
        task.execute();
        return rootView;
    }

    public void getPhoto()
    {
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
                    Bitmap image_bitmap    = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);

                    //배치해놓은 ImageView에 set
                    img.setImageBitmap(image_bitmap);

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
        if (ContextCompat.checkSelfPermission(activity,android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CONTACTS))
            {
                // Explain to the user why we need to write the permission.
                Log.d("test", "WRITE_EXTERNAL_STORAGE");
            }

            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            getPhoto();

            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant


        }
        else
        {
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

            Log.e("register", "가게등록 버튼" + code + "");
            if (code.equals("shop_register")) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    //ip주소
                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
                    param.add(new BasicNameValuePair("shop_code", shop_code));  //가게이름
                    param.add(new BasicNameValuePair("shop_name", shop_name));  //가게이름
                    param.add(new BasicNameValuePair("shop_position", shop_position));  //장소
                    param.add(new BasicNameValuePair("shop_phone", shop_phone));  //번호
                    // param.add(new BasicNameValuePair("shop_code", "goeun7"));  //이미지
                    param.add(new BasicNameValuePair("code", code)); //코드 : 회원가입

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
                        Log.e("register data : ", line);
                    }

                    JSONObject json = new JSONObject(result);
                    JSONArray jArr = json.getJSONArray("dataSend");

                    for (int i = 0; i < jArr.length(); i++) {
                        json = jArr.getJSONObject(i);
                        getJsonData[0] = json.getString("code");  //shop_code받아오기
                    }
                    makeToast("등록되었습니다.");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (code.equals("getCode")) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    //ip주소
                    HttpPost post = new HttpPost(postURL);
                    ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

                    d_id=id_bundle.getString("id");

                    param.add(new BasicNameValuePair("id", d_id));
                    param.add(new BasicNameValuePair("code", code)); //코드 : 회원가입

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
                        Log.e("getCode data : ", line);
                    }

                    JSONObject json = new JSONObject(result);
                    JSONArray jArr = json.getJSONArray("dataSend");

                    for (int i = 0; i < jArr.length(); i++) {
                        json = jArr.getJSONObject(i);
                        getJsonData[0] = json.getString("code");
                        shop_code=getJsonData[0].toString();    //shop_code설정
                        SHOP_CODE=shop_code;
                        new Thread(new Runnable() { //UI변경
                            @Override
                            public void run() {
                                Message msg=handler.obtainMessage();
                                handler.sendMessage(msg);
                            }
                        }).start();
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
            storeCode.setText(getJsonData[0].toString());   //가게코드 세팅
            storeCode.setTextColor(Color.RED);
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
            //Toast.makeText(getContext(), msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
        }
    };
}