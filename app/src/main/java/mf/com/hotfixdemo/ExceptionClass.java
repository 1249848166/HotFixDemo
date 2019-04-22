package mf.com.hotfixdemo;

import android.content.Context;
import android.widget.Toast;

public class ExceptionClass {

    public void fun(Context context){
        Toast.makeText(context, "这里有一个运行时错误。。。", Toast.LENGTH_SHORT).show();
        throw new RuntimeException("这里有一个运行时错误");
    }
}
