package com.example.simpleui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText; // import class:  option+enter
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText inputText;
    private CheckBox hideCheckBox;

    private SharedPreferences sp;  //只能讀取
    private SharedPreferences.Editor editor ; //用來編輯

    private ListView historyListView;

    private Spinner storeInfoSpinner;

    private int  REQUEST_DRINK_MENU = 1;
    String drinkMenuResult;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {   // 改寫AppCompatActivity
        super.onCreate(savedInstanceState); //呼叫繼承的oncreate()  super()的用法
        setContentView(R.layout.activity_main);
        // 做出主要畫面 參照activity_main.xml來做  R.layout是參照位置


        sp = getSharedPreferences("settings", MODE_PRIVATE);
        editor = sp.edit(); // 修改偏好設定

        inputText = (EditText)findViewById(R.id.inputText);  // cast 成edittext 型別
        // difficult part~
        inputText.setOnKeyListener(new View.OnKeyListener() {  //onkeylistener 是一個介面
            @Override   // 實作interface的method
            public boolean onKey(View v, int i, KeyEvent keyEvent) {

                String text = inputText.getText().toString();
                editor.putString("inputText",text); //參數為(index,value)
                editor.commit();

                //先判斷是按下還是放開 擇一送出就好
                if (keyEvent.getAction() == keyEvent.ACTION_DOWN){
                    if (i == KeyEvent.KEYCODE_ENTER){
                        submit(null); //沒有用到所以填null
                        return true; // event terminate
                    }
                }
                return false; //才會讓event繼續傳下去

            }
        });

        hideCheckBox = (CheckBox)findViewById(R.id.hideCheckBox); //取得實體
        //hideCheckBox.setChecked(true); // 用來看有無拿到實體
        hideCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("hideCheckBox", isChecked);
                editor.commit();
            }
        });

        historyListView = (ListView)findViewById(R.id.historyListView);
        storeInfoSpinner = (Spinner)findViewById(R.id.storeInfoSpinner);

        inputText.setText(sp.getString("inputText", ""));//程式重開後 在input顯示儲存的
        hideCheckBox.setChecked(sp.getBoolean("hideCheckBox", false));// 顯示有無勾選

        setHistory();
        setStoreInfo();

        //實體化
        progressDialog = new ProgressDialog(this);

    }

    private void setHistory(){

        // 下query specify class(table name)
        ParseQuery<ParseObject> query = new ParseQuery<>("Order");
        //row = Parse object

        //List<ParseObject> rawData = null;
        //query.whereEqualTo()

        // 藉由find in background 可以讓畫面先執行完成在顯示資料部分 不會整個畫面都是白的
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    // 傳出query結果的obj
                    // 負責做出listview
                    orderObjectToListView(objects);
                    historyListView.setVisibility(View.VISIBLE);
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                }
            }
        });
        /*
        try{
            rawData = query.find();// find()會造成UI lag
            // find return value type is List, alike Array.
        }catch (ParseException e){
            e.printStackTrace();
        }
        */

        //String[] rawData = Utils.readFile(this, "history.txt"). split("\n"); // 以換行隔開


    }

    private void orderObjectToListView(List<ParseObject> rawData) {
        // 列表型式資料list map是把三樣資料包起來的型式<key ,value>的型態
        //前面是interface, ArrayList是有實作list的class,這樣寫彈性較高
        List<Map<String,String>> data = new ArrayList<>();

        /* 掃過整個data找出會用到的職 */
        for(int i=0; i<rawData.size(); i++) {
                //JSONObject obj = new JSONObject(rawData[i]);
                ParseObject obj = rawData.get(i);
                //取出需要的項目
                String note = obj.getString("note");
                String store_info = obj.getString("store_info");
                JSONArray menu = obj.getJSONArray("menu");

                //前面是interface, HashMap是有實作Map的class,這樣寫彈性較高
                //<s,s>說明存放的key value型態，在這裏都是string
                Map<String, String> item = new HashMap<>();
                item.put("note", note);
                item.put("store_info", store_info);
                item.put("drink_number", getDrinkNumber(menu));
                // item.get("note")會獲得note值

                // 加回list
                data.add(item);


        }
        // Mapping  Map key  with View.id for layout.
        String[] from = {"note","store_info","drink_number"};
        int[] to = new int[]{R.id.note, R.id.store_info, R.id.drink_number};

        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.listview_item, from, to);
        // 用來把資料放進列表格式
        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data );
        // (context, item style, array_data)
        historyListView.setAdapter(adapter);//真正做出來呈現
    }

    private String getDrinkNumber(JSONArray menu){
        return "13";
    }

    private void setStoreInfo(){

        ParseQuery<ParseObject> query = new ParseQuery<>("StoreInfo");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                Log.d("debug", "size:" + objects.size());

                String[] data = new String[objects.size()];

                for (int i=0; i<objects.size(); i++){
                    ParseObject obj = objects.get(i);
                    data[i] = obj.getString("name") +", " + obj.getString("address");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, data);
                storeInfoSpinner.setAdapter(adapter);
            }
        });
        // String[] data = getResources().getStringArray(R.array.storeInfo);
        // getResources() 取得所有res
        // getStringArray()

    }

    public void submit(View v){ //view 參數可用來判斷是哪個button被案到了

        progressDialog.setTitle("Loading...");
        progressDialog.show();

        String text = inputText.getText().toString(); //取得輸入內容

        if (hideCheckBox.isChecked()){
            text = "***";
        }
        // 寫入所有資訊
        JSONObject object = new JSONObject();
        try{
            object.put("note", text);
            object.put("store_info", (String)storeInfoSpinner.getSelectedItem());
            // both key and value are string
            //     object.put("menu", drinkMenuResult);
            // let value be JSONArray
            // object.put("menu", new JSONArray(drinkMenuResult));

            // text = object.toString(); // 把json obj轉成string
            // next more detail on parse
            ParseObject orderObject = new ParseObject("Order");
            orderObject.put("note", text);
            orderObject.put("store_info", (String)storeInfoSpinner.getSelectedItem());
            if (drinkMenuResult != null){
                orderObject.put("menu", new JSONArray(drinkMenuResult));
            }
            orderObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.d("debug", "[line: 218] done");
                    progressDialog.dismiss();

                    setHistory();
                    // 儲存成功後才顯示toast this refers to SaveCallback, so needs to specify
                    Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_LONG).show();  // to make toast!(little hints)

                }
            }); //在背景執行的 用save callback呼叫確認是否成功

            Log.d("debug", "line_221"); //這行可能比上一行早出現


            Utils.writeFile(this, "history.txt", object+ "\n" ); // write file
            // this 指的是 main activity 整個class 因為他繼承了context

            //String fileContent = Utils.readFile(this, "history.txt");
            //Toast.makeText(this, fileContent, Toast.LENGTH_LONG).show();  //用toast顯示

            inputText.setText("");


        }catch(JSONException e){
            e.printStackTrace();
        }



    }

    public void goToDrinkMenu(View view){
        String storeInfoString = (String) storeInfoSpinner.getSelectedItem();
        Intent intent = new Intent();  // Intent用來觸發頁面跳動或是通知
        intent.setClass(this, DrinkMenuActivy.class);  //設定頁面跳轉的起點和終點
        intent.putExtra("store_info", storeInfoString);// 傳送額外資訊
        startActivityForResult(intent, REQUEST_DRINK_MENU);
        //進行跳轉 ForResult()是因為等一下要跳回來 不然只需startActivity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_DRINK_MENU) {
            if(resultCode == RESULT_OK){
                drinkMenuResult = data.getStringExtra("result"); //儲存傳來的訂單資訊
                Log.d("debug", drinkMenuResult);
            }
        }
    }



}
