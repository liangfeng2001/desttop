package com.gprotechnologies.gprodesktop.bean;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class AppInfo {
    @Override
    public String toString() {
        return "AppInfo{" +
                "packageName='" + packageName + '\'' +
                ", ico=" + ico +
                ", Name='" + Name + '\'' +
                ", intent=" + intent +
                '}';
    }

    private String packageName; //包名
    private Drawable ico;       //图标
    private String Name;        //应用标签
    private Intent intent;     //启动应用程序的Intent ，一般是Action为Main和Category为Lancher的Activity
 
    public Intent getIntent() {
        return intent;
    }
 
    public void setIntent(Intent intent) {
        this.intent = intent;
    }
 
 
    public String getPackageName() {
        return packageName;
    }
 
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
 
    public Drawable getIco() {
        return ico;
    }
 
    public void setIco(Drawable ico) {
        this.ico = ico;
    }
 
    public String getName() {
        return Name;
    }
 
    public void setName(String name) {
        Name = name;
    }
}
