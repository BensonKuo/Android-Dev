package com.example.simpleui;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by bensonkuo on 2015/10/12.
 */
public class Utils {

    public static void writeFile(Context context, String fileName, String content){ //可寫在sd卡或是裝置記憶體（後者較安全）
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

        return null;
    }
}
