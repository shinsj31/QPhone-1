package com.example.a502.drawex;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 502 on 2017-10-14.
 */

public class couponlist_adpater extends BaseAdapter {

    private ArrayList<couponlist_listviewitem> listViewItemList = new ArrayList<couponlist_listviewitem>() ;

    public couponlist_adpater(){

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.couponlist_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득

        TextView storeName = (TextView) convertView.findViewById(R.id.store_name) ;
        TextView currentCoupon = (TextView) convertView.findViewById(R.id.current_coupon) ;
        TextView totalCoupon = (TextView) convertView.findViewById(R.id.total_coupon) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        couponlist_listviewitem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        storeName.setText(listViewItem.getStore_name());
        int cn=listViewItem.getCurrent_coupon();
        int tn=listViewItem.getTotal_coupon();
        if(tn-cn<=2)
            currentCoupon.setTextColor(Color.RED);
        else
            currentCoupon.setTextColor(Color.BLACK);

        currentCoupon.setText(String.valueOf(cn));
        totalCoupon.setText(String.valueOf(tn));


        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String s, int cn, int tn) {
        couponlist_listviewitem item = new couponlist_listviewitem();

        item.setStore_name(s);
        item.setCurrent_coupon(cn);
        item.setTotal_coupon(tn);

        listViewItemList.add(item);
    }

}
