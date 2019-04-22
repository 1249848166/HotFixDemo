package mf.com.hotfixdemo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class CommonApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        HotFixUtil.getInstance().init(
                "http://bmob-cdn-19241.b0.upaiyun.com/2019/02/17/1bf1a7b4407565728024ff9d4cde6498.dex",
                getDir("odex",MODE_PRIVATE)+"/",
                "classes2.dex");
    }
}
