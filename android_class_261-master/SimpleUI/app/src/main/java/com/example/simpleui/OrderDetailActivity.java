package com.example.simpleui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

        // async task
        new Thread(new Runnable() {
            @Override
            public void run() {
                // address -> url
                String url = Utils.getGEOUrl(address);
                // url -> bytes ->string
                final String result = new String(Utils.urlToBytes(url));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // string ->jsonobj ->string
                        addressTextView.setText(Utils.getLatLngFromJSON(result));
                    }
                });

            }
        }).start();


    }
}
