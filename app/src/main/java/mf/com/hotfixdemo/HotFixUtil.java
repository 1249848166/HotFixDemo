package mf.com.hotfixdemo;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dalvik.system.DexClassLoader;

public class HotFixUtil {

    private static HotFixUtil instance;
    private HotFixUtil(){}
    public static HotFixUtil getInstance(){
        if(instance==null){
            synchronized (HotFixUtil.class){
                if(instance==null){
                    instance=new HotFixUtil();
                }
            }
        }
        return instance;
    }

    String TAG="HotFixUtil";
    ExecutorService executorService=Executors.newFixedThreadPool(1);

    public void init(final String dexUrl, final String privateBasePath , final String fileName){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn=null;
                try{
                    URL url=new URL(dexUrl);
                    conn= (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.connect();
                    int code=conn.getResponseCode();
                    if(code==200){
                        System.out.println("获取dex文件成功");
                        InputStream inputStream=conn.getInputStream();
                        System.out.println("创建了私有目录");
                        FileOutputStream fileOutputStream=new FileOutputStream(privateBasePath+fileName);
                        byte[] buffer=new byte[1024];
                        int len;
                        System.out.println("写入私有目录前");
                        while((len=inputStream.read(buffer))!=-1){
                            fileOutputStream.write(buffer,0,len);
                        }
                        inputStream.close();
                        fileOutputStream.close();
                        conn.disconnect();
                        System.out.println("成功写入dex文件到私有目录");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void hotFix(final Context context,final String privateBasePath , final String fileName){
        File file = new File(privateBasePath+fileName);
        System.out.println(file.getAbsolutePath());
        if (file.exists()) {
            inject(context,privateBasePath+fileName);
            System.out.println("成功替换dex");
        } else {
            Log.e(TAG, privateBasePath+fileName + "不存在");
        }
    }

    private void inject(Context context,String path) {
        try {
            Class<?> cl = Class.forName("dalvik.system.BaseDexClassLoader");
            Object pathList = getField(cl, "pathList", context.getClassLoader());
            Object baseElements = getField(pathList.getClass(), "dexElements", pathList);
            String dexopt = context.getDir("dexopt", 0).getAbsolutePath();
            DexClassLoader dexClassLoader = new DexClassLoader(path, dexopt, dexopt, context.getClassLoader());
            Object obj = getField(cl, "pathList", dexClassLoader);
            Object dexElements = getField(obj.getClass(), "dexElements", obj);
            Object combineElements = combineArray(dexElements, baseElements);
            setField(pathList.getClass(), "dexElements", pathList, combineElements);
            Object object = getField(pathList.getClass(), "dexElements", pathList);
            int length = Array.getLength(object);
            Log.e(TAG, "length = " + length);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private Object getField(Class<?> cl, String fieldName, Object object) throws NoSuchFieldException, IllegalAccessException {
        Field field = cl.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    private void setField(Class<?> cl, String fieldName, Object object, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = cl.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    private Object combineArray(Object firstArr, Object secondArr) {
        int firstLength = Array.getLength(firstArr);
        int secondLength = Array.getLength(secondArr);
        int length = firstLength + secondLength;
        Class<?> componentType = firstArr.getClass().getComponentType();
        Object newArr = Array.newInstance(componentType, length);
        for (int i = 0; i < length; i++) {
            if (i < firstLength) {
                Array.set(newArr, i, Array.get(firstArr, i));
            } else {
                Array.set(newArr, i, Array.get(secondArr, i - firstLength));
            }
        }
        return newArr;
    }
}
