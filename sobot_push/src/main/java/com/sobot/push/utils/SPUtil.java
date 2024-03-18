package com.sobot.push.utils;

/**
 * @author: Sobot
 * 2021/10/12
 */
import android.content.Context;
import android.content.SharedPreferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class SPUtil {
    private static SharedPreferences sharedPreferences;
    private static String CONFIG = "config";

    public static void saveStringData(Context context, String key, String value) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CONFIG,
                    Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putString(key, value).commit();
    }

    public static String getStringData(Context context, String key,
                                       String defValue) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CONFIG,
                    Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(key, defValue);
    }

    public static void saveBooleanData(Context context, String key, boolean value) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CONFIG,
                    Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public static Boolean getBooleanData(Context context, String key,
                                         Boolean defValue) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CONFIG,
                    Context.MODE_PRIVATE);
        }
        return sharedPreferences.getBoolean(key, defValue);
    }

    //save object
    public static void saveObject(Context context, String key ,Object obj){
        SharedPreferences preferences = context.getSharedPreferences("base64",
                Context.MODE_PRIVATE);

        //创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            //创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);

            //将对象写入字节流
            oos.writeObject(obj);

            //将字节流编码成base64的字符窜
            String productBase64 = new String(Base64.encode(baos
                    .toByteArray()));

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, productBase64);

            editor.commit();
        } catch (IOException e) {
            //  Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Object getObjectByKey(Context context, String key){
        SharedPreferences preferences = context.getSharedPreferences("base64",
                Context.MODE_PRIVATE);
        String productBase64 = preferences.getString(key, null);
        if (productBase64 == null) {
            return null;
        }

        //读取字节
        byte[] base64 = Base64.decode(productBase64);

        //封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            //再次封装
            ObjectInputStream bis = new ObjectInputStream(bais);
            try {
                //读取对象
                return bis.readObject();
            } catch (ClassNotFoundException e) {
                //  Auto-generated catch block
                e.printStackTrace();
            }
        } catch (StreamCorruptedException e) {
            //  Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            //  Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static void saveIntData(Context context, String key, int value) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public static int getIntData(Context context, String key, int defValue) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getInt(key, defValue);
    }

    public static void removeKey(Context context,String key){
        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().remove(key).commit();
    }
}
