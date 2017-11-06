package com.example.a502.drawex;

import android.Manifest;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnCompleteListener<Void> {
    private BackPressCloseHandler backPressCloseHandler;

    public static final String IP="192.168.0.3";

    private DrawerLayout mDrawerLayout;

    //자동로그인 기능
    private SharedPreferences appData;
    private boolean saveLoginData;
    private String id;
    private String pw;
    private int login_type;

    //지오펜스 기능
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }
    private ArrayList<Geofence> mGeofenceList;//지오펜스리스트(다모은 쿠폰 가게 리스트)
    private GeofencingClient mGeofencingClient;
    private PendingIntent mGeofencePendingIntent; //지오펜스 추가나 삭제
    static final String GEOFENCES_ADDED_KEY = "com.example.a502.drawex.GEOFENCES_ADDED_KEY";
    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appData=getSharedPreferences("appData",MODE_PRIVATE);
        load();

        mGeofenceList=new ArrayList<>();
        mGeofencePendingIntent = null;

        populateGeofenceList();
        mGeofencingClient = LocationServices.getGeofencingClient(this);

        Intent intent = new Intent(this, loading.class);
        startActivity(intent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        backPressCloseHandler = new BackPressCloseHandler(this);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.navigation_item_login:
                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        login_select l=new login_select();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, l).commit();
                        break;

                    case R.id.navigation_item_register:
                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        register_select r=new register_select();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, r).commit();
                        break;

                    case R.id.navigation_item_home:
                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        normal_home n=new normal_home();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, n).commit();
                        break;


                    case R.id.navigation_item_logout:
                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        login_select l2=new login_select();
                        SharedPreferences.Editor editor=appData.edit();
                        editor.clear();
                        editor.commit();
                        Bundle bundle = new Bundle();
                        bundle.putInt("logout",1);
                        l2.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, l2).commit();

                        Intent intent2=new Intent(getApplicationContext(),BlueTooth_service.class);
                        stopService(intent2);

                        Log.e("service","stop");
                        break;

                    case R.id.navigation_item_mystore:
                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        store_mystore sm=new store_mystore();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, sm).commit();
                        break;
                    case R.id.navigation_item_coupon_custom:
                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(getApplicationContext(), store_custom1.class);
                        //로그인 한 사람의 id정보(가게 ID)를 extra로 전송해야할 것 같다.
                        startActivity(intent);
                        break;
                    case R.id.navigation_item_notice:
                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        store_notice sn=new store_notice();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, sn).commit();
                        break;

                    case R.id.navigation_item_consumers:
                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        store_consumers sc=new store_consumers();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, sc).commit();
                        break;

                }

                return true;
            }
        });

        //기본화면으로 로그인 select 띄우기
        login_select l=new login_select();
        Bundle bundle =new Bundle();
        bundle.putBoolean("bLogin",saveLoginData);
        bundle.putString("ID",id);
        bundle.putString("PW",pw);
        bundle.putInt("type",login_type);
        l.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, l).commit();

        addGeofences();

        FirebaseMessaging.getInstance().subscribeToTopic("all");
    }

    @Override
    public void onStart() {
        //시작하기 전 권한을 체크한다. 만약 위치설정 권한이 켜져있지 않으면 요구한다.
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            performPendingGeofenceTask();
        }
    }
    //권한 체크 및 권한 요청
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
        } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
            removeGeofences();
        }
    }
    @SuppressWarnings("MissingPermission")
    private void addGeofences() {
        if (!checkPermissions()) {
            Toast.makeText(this,"GPS기능을 켜주세요.",Toast.LENGTH_LONG).show();
            return;
        }

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }

    /* 지오펜스를 제거한다. */
    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {
        if (!checkPermissions()) {
            Toast.makeText(this,"위치 권한을 찾을 수 없습니다.",Toast.LENGTH_LONG).show();
            return;
        }

        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        /*INITIAL_TRIGGER_ENTER 플래그: 지오펜싱이 추가 되고, 이미 지오펜스 안에 있는 경우
        * 지오 코딩서비스가 GEOFENCE_TRANSITION_ENTER를 트리거 해야한다. (들어와 있음으로 처리하도록하는 플래그)*/
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // 지오펜싱 서비스로 모니터링할 지오펜스 추가
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /*여기가 바로 지오펜스 리스트를 만드는 곳인 것 같다. 테스트용이므로 그냥 광운대학교를 추가하자*/
    private void populateGeofenceList() {
        //여기서는 컨텐츠에 있는 녀석들을 리스트로 추가했네! 컨텐츠 내용을 바꿔야겠듬

        mGeofenceList.add(new Geofence.Builder()
        .setRequestId("광운대학교")
        .setCircularRegion(37.6194970,127.0596960,1060)
        .setExpirationDuration(60 * 60 * 1000)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
        .build());
    }
    @Override public void onBackPressed()
    {
        backPressCloseHandler.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static SharedPreferences getSharedPreference()
    {
        MainActivity main=new MainActivity();
        return main.getSharedPreferences("appData",MODE_PRIVATE);
    }
    private void load()
    {
        //SharedPreferences 객체에 저장된 이름, 기본값)
        //저장된 이름이 존재하지 않을 경우 기본값

        saveLoginData=appData.getBoolean("SAVE_LOGIN_DATA",false);
        id=appData.getString("ID","");
        pw=appData.getString("PW","");
        login_type=appData.getInt("type",0);
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            Log.w(TAG, "error");
        }
    }
    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                GEOFENCES_ADDED_KEY, false);
    }
    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(GEOFENCES_ADDED_KEY, added)
                .apply();
    }
}