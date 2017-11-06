package com.example.a502.drawex;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by 502 on 2017-10-11.
 */

public class login_select extends Fragment {
    ViewGroup rootView;
    AppCompatActivity activity;

    Button memberBtn;
    Button storeBtn;

    NavigationView navigationView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.login_select, container, false);

        activity = (AppCompatActivity) getActivity();

        Bundle bundle=getArguments();
        navigationView=(NavigationView)activity.findViewById(R.id.navigation_view);

        try{
            //로그인 기록이 남아 있는 경우
            if(bundle.getBoolean("bLogin",false)&&bundle.getInt("type")!=0)
            {
                login l =new login();
                Bundle args = new Bundle();
                args.putBoolean("bLogin",true);
                args.putInt("check",bundle.getInt("type"));
                args.putString("ID",bundle.getString("ID"));
                args.putString("PW",bundle.getString("PW"));
                l.setArguments(args);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, l).commit();
            }
            if(bundle.getInt("logout")==1){
                navigationView.getMenu().setGroupVisible(R.id.noLogin,true);
                navigationView.getMenu().setGroupVisible(R.id.after_login_store,false);
                navigationView.getMenu().setGroupVisible(R.id.after_login_normal,false);
            }
        }
        catch (Exception e){
            Log.e("bLogin", e.getMessage());

        }

        memberBtn=(Button)rootView.findViewById(R.id.memberCheck);
        storeBtn=(Button)rootView.findViewById(R.id.storeCheck);

        memberBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               // Toast.makeText(activity, "회원", Toast.LENGTH_LONG).show();
                login l =new login();
                Bundle bundle = new Bundle();
                bundle.putInt("check",1);
                l.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, l).commit();
            }
        });
        storeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               // Toast.makeText(activity, "가게", Toast.LENGTH_LONG).show();
                login l =new login();
                Bundle bundle = new Bundle();
                bundle.putInt("check",2);
                l.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, l).commit();


            }
        });
        return rootView;


    }




}
