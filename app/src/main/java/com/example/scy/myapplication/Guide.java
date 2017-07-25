package com.example.scy.myapplication;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

import static com.example.scy.myapplication.MainActivity.ID_MAIN;
import static com.example.scy.myapplication.MainActivity.ID_TASK;
import static com.example.scy.myapplication.MainActivity.ID_TASK2;

public class Guide extends AppCompatActivity {

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if (msg.obj == null){
                Toast.makeText(Guide.this,"请检查网络",Toast.LENGTH_SHORT);
            }
            else
                guide.setText(((String)msg.obj).replace("\\n", "\n"));
        }
    };
    private TextView guide;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        guide = (TextView)findViewById(R.id.guide);
        new Thread(new Runnable(){
            @Override
            public void run() {
                String data = NewsService.getguide();
                Message msg = new Message();
                msg.obj = data;
                handler.sendMessage(msg);
            }
        }).start();
    }
}
