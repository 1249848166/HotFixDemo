package mf.com.hotfixdemo;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View v){
        HotFixUtil.getInstance().hotFix(MainActivity.this,getDir("odex",MODE_PRIVATE)+"/",
                "classes2.dex");
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                new ExceptionClass().fun(MainActivity.this);
            }
        },3000);
    }
}
