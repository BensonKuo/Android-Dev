package com.example.simpleui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DrinkMenuActivy extends AppCompatActivity {

    private TextView storeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_menu_activy); //做出主要畫面

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

    // 按下飲料按鈕後的動作
    public void add(View view){
        Button button = (Button)view;
        int count = Integer.parseInt(button.getText().toString());//取得button的文字 轉成字串再轉int
        button.setText(String.valueOf(count + 1));
    }
    // 送出訂單後的動作
    public void done(View view){
        Intent data = new Intent();
        data.putExtra("result",getValue().toString()); // 把json 轉成string後傳回
        setResult(RESULT_OK, data); //傳回去
        finish();
    }

    private JSONArray getValue(){
        JSONArray result = new JSONArray(); // json array(裡面放json obj)
        LinearLayout root = (LinearLayout)findViewById(R.id.root);

        int len = root.getChildCount(); // 計算root下面有幾個元素
        for (int i=1; i<len-1; i++){
            LinearLayout ll = (LinearLayout) root.getChildAt(i);// getChildAt(i)得到的是view
            String name = ((TextView)ll.getChildAt(0)).getText().toString();// drink name
            int l = Integer.parseInt(((Button) ll.getChildAt(1)).getText().toString());// 大杯volume
            int m = Integer.parseInt(((Button)ll.getChildAt(2)).getText().toString());// 小杯 volume

            // 存成一個json obj
            JSONObject object = new JSONObject();
             try {
                    object.put("name", name);  // put("key", value);
                    object.put("l", l);
                    object.put("m", m);
                    result.put(object);  // 把obj 放入json array
             } catch (JSONException e) {
                 e.printStackTrace();
             }
        }

        return result;

    }
}
