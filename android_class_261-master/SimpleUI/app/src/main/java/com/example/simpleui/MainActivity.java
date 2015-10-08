package com.example.simpleui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText; // import class:  option+enter
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText inputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {   // 改寫AppCompatActivity
        super.onCreate(savedInstanceState); //呼叫繼承的oncreate()  super()的用法
        setContentView(R.layout.activity_main);
        // 做出主要畫面 參照activity_main.xml來做  R.layout是參照位置

        inputText = (EditText)findViewById(R.id.inputText);  // cast 成edittext 型別

        inputText.setOnKeyListener(new View.OnKeyListener() {  //onkeylistener 是一個介面
            @Override   // 實作interface的method
            public boolean onKey(View v, int i, KeyEvent keyEvent) {
                //先判斷是按下還是放開
                if (keyEvent.getAction()==keyEvent.ACTION_DOWN){
                    if (i == KeyEvent.KEYCODE_ENTER){
                        submit(null); //沒有用到所以填null
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void submit(View v){ //view 參數可用來判斷是哪個button被案到了


        String text = inputText.getText().toString();
        Toast.makeText(this,text, Toast.LENGTH_LONG).show();

        inputText.setText("");

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
