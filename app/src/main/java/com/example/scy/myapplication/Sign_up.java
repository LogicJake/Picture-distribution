package com.example.scy.myapplication;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Sign_up extends AppCompatActivity {
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject result = (JSONObject) msg.obj;
            if (result == null){
                pDialog.cancel();
                Toast.makeText(Sign_up.this, "服务器连接错误", Toast.LENGTH_SHORT).show();
            }
            else {
                int status = 0;
                try {
                    status = result.getInt("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status == 1) {
                    String token = null;
                    try {
                        token = result.getString("token");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pDialog.cancel();
                    editor.putString("token", token);
                    editor.commit();
                    Toast.makeText(Sign_up.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Sign_up.this, SecurityQuestion.class);
                    startActivity(intent);
                    finish();
                } else if(status == 2) {
                        pDialog.cancel();
                        Toast.makeText(Sign_up.this, "已存在该用户名", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    private Button bt1;
    private EditText editName, editPassword, editPassword2;
    private SweetAlertDialog pDialog;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        editName = (EditText)findViewById(R.id.name);
        editPassword = (EditText)findViewById(R.id.password);
        editPassword2 = (EditText)findViewById(R.id.password2);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        bt1 = (Button)findViewById(R.id.signup);
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pDialog = new SweetAlertDialog(Sign_up.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("提交信息中");
                pDialog.setCancelable(false);
                pDialog.show();
                try {
                    final String name = editName.getText().toString();
                    final String password = editPassword.getText().toString();
                    String password2 = editPassword2.getText().toString();
                    if(!password.equals(password2)){             //两次密码不一致
                        pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        pDialog.setTitleText("ERROR");
                        pDialog.setContentText("两次密码不一致");
                        pDialog.setConfirmText("OK");
                        pDialog.setCancelable(false);
                        pDialog.show();
                    }
                    else {
                        if (name.length() == 0) {
                            pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            pDialog.setTitleText("ERROR");
                            pDialog.setContentText("用户名不能为空");
                            pDialog.setConfirmText("OK");
                            pDialog.setCancelable(false);
                            pDialog.show();
                        } else {
                            if (password.length() < 8) {
                                pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText("ERROR");
                                pDialog.setContentText("密码长度至少为8");
                                pDialog.setConfirmText("OK");
                                pDialog.setCancelable(false);
                                pDialog.show();
                            } else {
                                if (password.matches("^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]{6,20})$")) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                JSONObject res = NewsService.signup(name, password);
                                                Message msg = new Message();
                                                msg.obj = res;
                                                handler.sendMessage(msg);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                } else {
                                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    pDialog.setTitleText("ERROR");
                                    pDialog.setContentText("密码必须且只能由数字和字母组成");
                                    pDialog.setConfirmText("OK");
                                    pDialog.setCancelable(false);
                                    pDialog.show();
                                }
                            }
                        }
                    }
                }catch (Exception e) {e.printStackTrace();}
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up, menu);
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
