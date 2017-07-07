package com.example.scy.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Message;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SecurityQuestion extends AppCompatActivity {

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:
                    JSONObject res = (JSONObject)msg.obj;
                    JSONObject temp ;
                    if (res == null) {
                        pDialog2.cancel();
                        Toast.makeText(SecurityQuestion.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        try {
                            JSONArray que = res.getJSONArray("data");
                            if (que.length()!=0);
                            {
                                for (int i = 0; i < que.length(); i++) {
                                    temp = (JSONObject) que.get(i);
                                    data_list.add(temp.getString("question"));
                                }
                                data_list1.add("问题一");
                                data_list2.add("问题二");
                                data_list3.add("问题三");
                                data_list1.addAll(data_list);
                                data_list2.addAll(data_list);
                                data_list3.addAll(data_list);

                                adapter1 = new ArrayAdapter<String>(SecurityQuestion.this, android.R.layout.simple_spinner_item, data_list1);
                                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                adapter2 = new ArrayAdapter<String>(SecurityQuestion.this, android.R.layout.simple_spinner_item, data_list2);
                                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                adapter3 = new ArrayAdapter<String>(SecurityQuestion.this, android.R.layout.simple_spinner_item, data_list3);
                                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner1.setAdapter(adapter1);
                                spinner2.setAdapter(adapter2);
                                spinner3.setAdapter(adapter3);
                            }
                            pDialog2.cancel();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 1:
                    int res1 = (int)msg.obj;
                    if (res1 == -1)
                    {
                        Toast.makeText(SecurityQuestion.this,"连接服务器失败",Toast.LENGTH_SHORT).show();
                        pDialog.cancel();
                    }
                    else if (res1==1)
                    {
                        pDialog.cancel();
                        Toast.makeText(SecurityQuestion.this,"提交成功",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SecurityQuestion.this,Login.class);
                        startActivity(intent);
                        finish();
                    }
                    else if (res1 == 2)
                    {
                        Toast.makeText(SecurityQuestion.this,"请勿重复设置密保问题",Toast.LENGTH_SHORT).show();
                        pDialog.cancel();
                    }
                    break;
            }
        }
    };
    private Spinner spinner1,spinner2,spinner3;
    private EditText a1,a2,a3;
    private Button sure;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private SweetAlertDialog pDialog;
    private SweetAlertDialog pDialog2;
    private SweetAlertDialog pDialog3;
    private List<String> data_list = new ArrayList<String>();
    private List<String> data_list1 = new ArrayList<String>();
    private List<String> data_list2= new ArrayList<String>();
    private List<String> data_list3 = new ArrayList<String>();
    private ArrayAdapter<String> adapter1;
    private ArrayAdapter<String> adapter2;
    private ArrayAdapter<String> adapter3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_question);
        pDialog2 = new SweetAlertDialog(SecurityQuestion.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog2.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog2.setTitleText("加载页面中");
        pDialog2.setCancelable(false);
        pDialog2.show();
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        spinner1 = (Spinner)findViewById(R.id.q1);
        spinner2 = (Spinner)findViewById(R.id.q2);
        spinner3 = (Spinner)findViewById(R.id.q3);
        a1 = (EditText)findViewById(R.id.a1);
        a2 = (EditText)findViewById(R.id.a2);
        a3 = (EditText)findViewById(R.id.a3);
        sure = (Button)findViewById(R.id.sure);
        sure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(((String) spinner1.getSelectedItem()).equals("问题一")||((String) spinner2.getSelectedItem()).equals("问题二")||((String) spinner3.getSelectedItem()).equals("问题三"))
                    Toast.makeText(SecurityQuestion.this,"问题不能为空",Toast.LENGTH_SHORT).show();
                else
                {
                    if ((a1.getText().toString().length()) == 0||(a2.getText().toString().length())==0||(a3.getText().toString().length())==0)
                        Toast.makeText(SecurityQuestion.this,"答案不能为空",Toast.LENGTH_SHORT).show();
                    else {
                        if ((((String) spinner1.getSelectedItem()) == ((String) spinner2.getSelectedItem())) || (((String) spinner1.getSelectedItem()) == ((String) spinner3.getSelectedItem())) || (((String) spinner2.getSelectedItem()) == ((String) spinner3.getSelectedItem()))) {
                            Toast.makeText(SecurityQuestion.this, "问题不能一样", Toast.LENGTH_SHORT).show();
                        } else {
                            pDialog = new SweetAlertDialog(SecurityQuestion.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog.setTitleText("提交信息中");
                            pDialog.setCancelable(false);
                            pDialog.show();
                            final List<String> reslist = new ArrayList<String>();
                            reslist.add((String) spinner1.getSelectedItem());
                            System.out.println((String) spinner1.getSelectedItem());
                            reslist.add((String) spinner2.getSelectedItem());
                            System.out.println((String) spinner2.getSelectedItem());
                            reslist.add((String) spinner3.getSelectedItem());
                            System.out.println((String) spinner3.getSelectedItem());
                            reslist.add(a1.getText().toString());
                            reslist.add(a2.getText().toString());
                            reslist.add(a3.getText().toString());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    int res = NewsService.postqus(preferences.getString("token", null), "addUserquestion", reslist);
                                    System.out.println(res);
                                    Message mag = new Message();
                                    mag.what = 1;
                                    mag.obj = res;
                                    handler.sendMessage(mag);
                                }
                            }).start();
                        }
                    }
                }
            }
        });
        GetList();
    }

    public void GetList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject res = NewsService.postSecure(preferences.getString("token",null),"getAllquestion",null);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = res;
                handler.sendMessage(msg);
            }
        }).start();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            pDialog3 = new SweetAlertDialog(SecurityQuestion.this, SweetAlertDialog.WARNING_TYPE);
            pDialog3.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog3.setTitleText("ERROR");
            pDialog3.setContentText("请先设置密保");
            pDialog3.setCancelable(false);
            pDialog3.setConfirmText("OK");
            pDialog3.show();
        }
        return super.onKeyDown(keyCode, event);
    }
}
