package com.example.a502.drawex;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by 고은 on 2017-11-02.
 */

public class BlueTooth_service extends Service {
    String token, value2;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseDatabase database;
        DatabaseReference myRef;

        /* 블루투스 */
        database = FirebaseDatabase.getInstance();
        myRef = database.getInstance().getReference("bluetooth");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //변경사항이 있을 때
                final String value = dataSnapshot.getValue(String.class);
                value2 = value;
                token = FirebaseInstanceId.getInstance().getToken();

                Log.e("firebase", "service start");
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                if (!mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.enable();
                    Log.e("bluetooth", "블루투스 활성화");
                }
                else{
                    mBluetoothAdapter.disable();
                    Log.e("bluetooth", "블루투스 비활성화");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("공지사항 실패", "failldfjka;lsfjd");
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
