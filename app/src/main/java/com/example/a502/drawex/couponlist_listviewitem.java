package com.example.a502.drawex;

/**
 * Created by 502 on 2017-10-14.
 */

public class couponlist_listviewitem {

    private String store_name;
    private int current_coupon;
    private int total_coupon;

    public void setStore_name(String n){
        store_name=n;
    }
    public void setCurrent_coupon(int n){
        current_coupon=n;
    }
    public void setTotal_coupon(int n){
        total_coupon=n;
    }


    public String getStore_name(){
        return this.store_name;
    }
    public int getCurrent_coupon(){
        return this.current_coupon;
    }
    public int getTotal_coupon(){
        return this.total_coupon;
    }

}
