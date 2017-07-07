package com.example.scy.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CheckID extends AppCompatActivity {

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    JSONObject res = (JSONObject) msg.obj;
                    try {
                        q1.setText("问题一："+res.getString("q_1"));
                        q2.setText("问题二："+res.getString("q_2"));
                        q3.setText("问题三："+res.getString("q_3"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    int res1 = (int)msg.obj;
                    if(res1 == -1) {
                        pDialog.cancel();
                        Toast.makeText(CheckID.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    }
                    else if (res1 == 1)
                    {
                        pDialog.cancel();
                        Intent intent = new Intent(CheckID.this,ResetPssword.class);
                        startActivity(intent);
                        finish();
                    }
                    else if (res1 == 0){
                        pDialog.cancel();
                        Toast.makeText(CheckID.this,"回答错误",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    private SharedPreferences preferences;
    private SweetAlertDialog pDialog;
    private SharedPreferences.Editor editor;
    private TextView q1,q2,q3;
    private EditText a1,a2,a3;
    private Button sure;
    private List<String> qlist = new ArrayList<String>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_id);
        q1 = (TextView)findViewById(R.id.q1);
        q2 = (TextView)findViewById(R.id.q2);
        q3 = (TextView)findViewById(R.id.q3);
        a1 = (EditText)findViewById(R.id.a1);
        a2 = (EditText)findViewById(R.id.a2);
        a3 = (EditText)findViewById(R.id.a3);
        sure = (Button)findViewById(R.id.sure);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        GetQuestion();
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new SweetAlertDialog(CheckID.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("验证中");
                pDialog.setCancelable(false);
                pDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<String> qlist = new ArrayList<String>();
                        qlist.add(a1.getText().toString());
                        qlist.add(a2.getText().toString());
                        qlist.add(a3.getText().toString());
                        int res = NewsService.CheckAns(preferences.getString("token", null), qlist);
                        Message message = new Message();
                        message.what = 1;
                        message.obj = res;
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });
    }
    public void GetQuestion(){
        try {
            new Thread(new Runnable(){
                @Override
                public void run()
                {
                    JSONObject result = NewsService.GetQue(preferences.getString("token",null));
                    if (result!=null) {
                        System.out.println(result);
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }
            }).start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
