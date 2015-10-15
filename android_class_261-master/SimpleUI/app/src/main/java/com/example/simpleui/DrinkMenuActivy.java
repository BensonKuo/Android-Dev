package com.example.simpleui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DrinkMenuActivy extends AppCompatActivity {

    private TextView storeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_menu_activy);

        storeInfo = (TextView)findViewById(R.id.storeInfo);
        String storeInfoStr = getIntent().getStringExtra("store_info");
        storeInfo.setText(storeInfoStr);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    // 按下按鈕後的動作
    public void add(View view){
        Button button = (Button)view;
        int count = Integer.parseInt(button.getText().toString());//取得button的文字 轉成字串再轉int
        button.setText(String.valueOf(count+1));
    }
}
