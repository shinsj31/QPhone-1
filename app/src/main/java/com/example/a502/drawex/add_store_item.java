package com.example.a502.drawex;

/**
 * Created by 고은 on 2017-11-02.
 */

public class add_store_item {
    private String storeName ;
    private String storePos ;

    public void setTitle(String title) {
        storeName = title ;
    }
    public void setPos(String pos) {
        storePos = pos ;
    }

    public String getTitle() {
        return this.storeName ;
    }
    public String getPos() {
        return this.storePos ;
    }
}
