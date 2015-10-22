package com.example.simpleui;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by bensonkuo on 2015/10/12.
 */
public class Utils {

    public static void writeFile(Context context, String fileName, String content){
                //可寫在sd卡或是裝置記憶體（後者較安全）
                // context 用來寫進裝置記憶體 sd卡只要直接new file加上路徑就好
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_APPEND);
            fos.write(content.getBytes());
            fos.close();

        }catch (FileNotFoundException e ){
            e.printStackTrace();

        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public static String readFile(Context context, String fileName){
        try{
            FileInputStream fis = context.openFileInput(fileName);
            byte[] buffer = new byte[1024];

            fis.read(buffer, 0, buffer.length);
            fis.close();

            return new String(buffer); //變回字串

        }catch (FileNotFoundException e ){
            e.printStackTrace();

        } catch(IOException e){
            e.printStackTrace();
        }
        // initialization bug fixed
        return " ";
    }


    public static Uri getPhotoUri(){
        // 因為檔案較大所以會存sd card, 不會存app裝置資料夾
        // sd/Pictures folder
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (dir.exists() == false){
            dir.mkdirs();
        }

        File file = new File(dir, "simpleui_photo.png");
        return Uri.fromFile(file);  // 回傳檔案路徑

    }
}
