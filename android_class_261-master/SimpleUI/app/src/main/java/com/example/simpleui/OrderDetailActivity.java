package com.example.simpleui;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.security.PrivateKey;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView addressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        addressTextView = (TextView) findViewById(R.id.address);

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

    //<doInBackground參數形式,,onPostExcute參數形式>
    private class GeoCodingTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params){  // connect internet on other thread
            String url = Utils.getGEOUrl("Taipei");
            String json = new String(Utils.urlToBytes(url));
            String latLng = Utils.getLatLngFromJSON(json);
            return latLng;
//            String url = Utils.getGEOUrl("Taipei");
//            String result = new String(Utils.urlToBytes(url));
//            Log.d("debug", result);
//            return null;
        }

        // 執行之後做一些事情
        @Override
        protected void onPostExecute(String result) {
            //super.onPostExecute(aVoid);
            addressTextView.setText(result);
        }



    }

}
