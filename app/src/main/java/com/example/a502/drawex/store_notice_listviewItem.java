package com.example.a502.drawex;

import android.graphics.drawable.Drawable;

/**
 * Created by 502 on 2017-10-01.
 */

public class store_notice_listviewItem {
        private Drawable iconDrawable ;
        private String titleStr ;
        private String dateStr ;

        public void setIcon(Drawable icon) {
            iconDrawable = icon ;
        }
        public void setTitle(String title) {
            titleStr = title ;
        }
        public void setDesc(String desc) {
            dateStr = desc ;
        }

        public Drawable getIcon() {
            return this.iconDrawable ;
        }
        public String getTitle() {
            return this.titleStr ;
        }
        public String getDesc() {
            return this.dateStr ;
        }


}
