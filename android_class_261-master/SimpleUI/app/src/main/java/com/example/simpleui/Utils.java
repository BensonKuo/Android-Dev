package com.example.simpleui;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

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

    // 給訂uri 他回傳真正內容
    public static byte[] uriToBytes(Context context, Uri uri){
        try{
            // 用來解讀uri
            InputStream is = context.getContentResolver().openInputStream(uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];  //一定小於來源檔案
            int len = 0;

            // .read() will return 讀了幾個byte
            // -1: 結束讀
            while( (len = is.read(buffer)) != -1){
                baos.write(buffer,0,len);
            }

            return baos.toByteArray();

        } catch(FileNotFoundException e){
            e.toString();
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] urlToBytes(String urlString){
        // 連線網路的方式有很多種 這裏是Java native
        try{
            URL url = new URL(urlString); // 看是不是有效合法的
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];  //一定小於來源檔案
            int len = 0;

            // .read() will return 讀了幾個byte
            // -1: 結束讀
            while( (len = is.read(buffer)) != -1){
                baos.write(buffer,0,len);
            }

            return baos.toByteArray();
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }



    public static String getGEOUrl(String address){
        try{
            address = URLEncoder.encode(address, "utf-8");
            return  "https://maps.googleapis.com/maps/api/geocode/json?address="+address;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return null;
    }



    public static String getLatLngFromJSON(String jsonstring){
        try{
            JSONObject obj = new JSONObject(jsonstring);
            JSONObject location = obj.getJSONArray("results").getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location");

            double lat = location.getDouble("lat");
            double lng = location.getDouble("lng");

            return lat+","+lng;

        } catch (JSONException e){
            e.printStackTrace();

        }
        return null;
    }


    public static String getStaticMapUrl(String center, String zoom, String size) {
        return String.format("https://maps.googleapis.com/maps/api/staticmap?center=%s&zoom=%s&size=%s", center, zoom, size);
    }

}
