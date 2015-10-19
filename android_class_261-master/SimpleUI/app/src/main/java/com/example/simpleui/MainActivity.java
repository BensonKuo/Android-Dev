package com.example.simpleui;

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
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {   // 改寫AppCompatActivity
        super.onCreate(savedInstanceState); //呼叫繼承的oncreate()  super()的用法
        setContentView(R.layout.activity_main);
        // 做出主要畫面 參照activity_main.xml來做  R.layout是參照位置

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "R1iEoTLPPHzpxF4OuLKZQcy5OVEonqBqMUad4zaS", "770fv893TsEb8rBYft7vWZ9aNXETrkBrVH82iRyD");

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

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

    }

    private void setHistory(){
        String[] rawData = Utils.readFile(this, "history.txt"). split("\n"); // 以換行隔開

        // 列表型式資料list map是把三樣資料包起來的型式<key ,value>的型態
        //前面是interface, ArrayList是有實作list的class,這樣寫彈性較高
        List<Map<String,String>> data = new ArrayList<>();

        /* 掃過整個data找出會用到的職 */
        for(int i=0; i<rawData.length; i++) {

            try {
                JSONObject obj = new JSONObject(rawData[i]);
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

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        // Mapping  Map key  with View.id for layout.
        String[] from = {"note","store_info","menu"};
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
        String[] data = getResources().getStringArray(R.array.storeInfo);
        // getResources() 取得所有res
        // getStringArray()

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, data);
        storeInfoSpinner.setAdapter(adapter);
    }

    public void submit(View v){ //view 參數可用來判斷是哪個button被案到了

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
            object.put("menu", new JSONArray(drinkMenuResult));

            text = object.toString(); // 把json obj轉成string

            // 下列為以前的功能
            Toast.makeText(this,text, Toast.LENGTH_LONG).show();  // to make toast!(little hints)

            Utils.writeFile(this, "history.txt", text+ "\n" ); // write file
            // this 指的是 main activity 整個class 因為他繼承了context

            //String fileContent = Utils.readFile(this, "history.txt");
            //Toast.makeText(this, fileContent, Toast.LENGTH_LONG).show();  //用toast顯示

            inputText.setText("");
            setHistory();


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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
