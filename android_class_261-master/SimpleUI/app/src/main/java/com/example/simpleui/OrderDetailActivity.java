package com.example.simpleui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.security.PrivateKey;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView addressTextView;
    private WebView webView;
    private ImageView imageView;

    private SupportMapFragment mapFragment;  // support lib 新api用在old ver. android 因為新版本普及不高
    private GoogleMap googleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        addressTextView = (TextView) findViewById(R.id.address);

        webView = (WebView)findViewById(R.id.webView);
        imageView = (ImageView)findViewById(R.id.imageView);

        mapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        googleMap = mapFragment.getMap();

        String storeInfo = getIntent().getStringExtra("store_info");
        final String address = storeInfo.split(",")[1];
        Log.d("debug", address);

//        // async task
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // address -> url
//                String url = Utils.getGEOUrl(address);
//                // url -> bytes ->string
//                final String result = new String(Utils.urlToBytes(url));
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // string ->jsonobj ->string
//                        addressTextView.setText(Utils.getLatLngFromJSON(result));
//                    }
//                });
//
//            }
//        }).start();
//
        GeoCodingTask task = new GeoCodingTask();
        task.execute(address);
    }

    private void setupGoogleMap(String LatLngStr){
        Double Lat = Double.valueOf(LatLngStr.split(",")[0]);
        Double Lng = Double.valueOf(LatLngStr.split(",")[1]);

        LatLng LatLng = new LatLng(Lat,Lng);
        // camera is current viewing area
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng, 15));
    }

    //<doInBackground參數形式,onprogressupdate(),onPostExecute參數形式>
    private class GeoCodingTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params){  // connect internet on other thread
            String url = Utils.getGEOUrl(params[0]);
            String json = new String(Utils.urlToBytes(url));
            String latLng = Utils.getLatLngFromJSON(json);
            return latLng; // return value to onPostExecute()
//            String url = Utils.getGEOUrl("Taipei");
//            String result = new String(Utils.urlToBytes(url));
//            Log.d("debug", result);
//            return null;
        }

        // doInBackground執行後執行這裡
        @Override
        protected void onPostExecute(String latLng) {
            addressTextView.setText(latLng);

            setupGoogleMap(latLng);

            String staticMapUrl = Utils.getStaticMapUrl(latLng, "17", "300x600");
            webView.loadUrl(staticMapUrl);// webview 呈現地圖
//
            StaticMapTask task = new StaticMapTask();
            task.execute(latLng);
        }
    }

    // 給訂經緯度 獲得地圖 呈現在imageview
    private class StaticMapTask extends AsyncTask<String, Integer[], byte[]> {
        @Override
        protected byte[] doInBackground(String... params) {
            String staticMapUrl = Utils.getStaticMapUrl(params[0], "17", "500x800");
            Log.d("debug",staticMapUrl);
            byte[] r = Utils.urlToBytes(staticMapUrl);

            Log.d("debug",String.valueOf(r.length));
            return r;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bm);
        }
    }

}
