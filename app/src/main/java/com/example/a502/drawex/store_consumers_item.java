package com.example.a502.drawex;

/**
 * Created by 502 on 2017-10-20.
 */

public class store_consumers_item {
    private String consumerId ;
    private int hascouponNum ;

    public void setconsumerId(String id) {
        consumerId = id ;
    }
    public void sethascouponNum(int n) {
        hascouponNum = n ;
    }

    public String getId() {
        return this.consumerId ;
    }
    public int getHascouponNum()
    {
        return this.hascouponNum ;
    }

}
