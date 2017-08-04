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
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ResetPssword extends AppCompatActivity {

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if ((Boolean)msg.obj == true){
                pDialog.cancel();
                Toast.makeText(ResetPssword.this,R.string.modify_success,Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                pDialog.cancel();
                Toast.makeText(ResetPssword.this,R.string.modify_fail,Toast.LENGTH_SHORT).show();
            }
        }
    };
    private SharedPreferences preferences;
    private SweetAlertDialog pDialog;
    private SharedPreferences.Editor editor;
    private EditText pass1,pass2;
    private Button sure;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pssword);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        pass1 = (EditText)findViewById(R.id.pass1);
        pass2 = (EditText)findViewById(R.id.pass2);
        sure = (Button)findViewById(R.id.sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String str1 = pass1.getText().toString();
                String str2 = pass2.getText().toString();
                if (!str1.equals(str2))
                    Toast.makeText(ResetPssword.this,R.string.pass_no_same,Toast.LENGTH_SHORT).show();
                else {
                    if (str1.length() < 8) {
                        Toast.makeText(ResetPssword.this, R.string.pass_length_short, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (str1.matches("^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]{6,20})$")) {
                            pDialog = new SweetAlertDialog(ResetPssword.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog.setTitleText(getString(R.string.pushing_info));
                            pDialog.setCancelable(false);
                            pDialog.show();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Boolean res = NewsService.ChangePass(preferences.getString("token", null), str1);
                                    Message msg = new Message();
                                    msg.obj = res;
                                    handler.sendMessage(msg);
                                }
                            }).start();
                        }
                        else
                            Toast.makeText(ResetPssword.this, R.string.pass_format_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
