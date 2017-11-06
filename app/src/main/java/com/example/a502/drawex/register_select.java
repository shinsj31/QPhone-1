package com.example.a502.drawex;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by 502 on 2017-10-11.
 */

public class register_select extends Fragment {
    ViewGroup rootView;
    AppCompatActivity activity;

    Button memberBtn;
    Button storeBtn;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.register_select, container, false);

        activity = (AppCompatActivity) getActivity();

        memberBtn=(Button)rootView.findViewById(R.id.memberCheck_r);
        storeBtn=(Button)rootView.findViewById(R.id.storeCheck_r);

        memberBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "일반 회원가입", Toast.LENGTH_LONG).show();
                register r =new register();
                Bundle bundle = new Bundle();
                bundle.putInt("check",1);
                r.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, r).commit();

            }
        });

        storeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "가게회원가입", Toast.LENGTH_LONG).show();
                register r =new register();
                Bundle bundle = new Bundle();
                bundle.putInt("check",2);
                r.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, r).commit();

            }
        });
        return rootView;
    }


}
