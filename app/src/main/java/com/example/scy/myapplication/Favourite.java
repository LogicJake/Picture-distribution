package com.example.scy.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

class CheckInfo {
    String name;
    String id;
    int status;
    public CheckInfo(String name,String id,int status){
        this.name = name;
        this.id = id;
        this.status = status;
    }
    public String getnmae(){return name;}
    public String getid(){return id;}
    public int getStatus(){return status;}
}

public class Favourite extends AppCompatActivity {
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    JSONArray res = (JSONArray)msg.obj;
                    if (res == null)
                        Toast.makeText(Favourite.this,R.string.server_error,Toast.LENGTH_SHORT);
                    else {
                        for (int i = 0; i < res.length(); i++) {
                            try {
                                JSONObject temp = (JSONObject) res.get(i);
                                checkInfoList.add(new CheckInfo(temp.getString("interest"), temp.getString("id"), temp.getInt("status")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        for (int i = 0; i < checkInfoList.size(); i++) {
                            CheckBox cb = new CheckBox(Favourite.this);
                            ly.addView(cb);
                            CheckInfo temp = checkInfoList.get(i);
                            cb.setText(temp.getnmae());
                            cb.setTextSize(20);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(0, 0, 80, 20);
                            cb.setLayoutParams(lp);
                            if (temp.getStatus() == 1)
                                cb.setChecked(true);
                            checkBoxs.add(cb);
                        }
                        bt = new Button(Favourite.this);
                        bt.setText(R.string.submit);
                        ly.addView(bt);
                        bt.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                editor.putInt("complete", 1);
                                editor.commit();
                                String keyadd = "",keydel="";
                                for (int i = 0; i < checkInfoList.size(); i++) {
                                    CheckBox cb = checkBoxs.get(i);
                                    if (cb.isChecked()) {
                                        if (checkInfoList.get(i).getStatus() == 0)
                                            keyadd+=(checkInfoList.get(i).getid()+" ");
                                    }
                                    else{
                                        if (checkInfoList.get(i).getStatus() == 1)
                                            keydel+=(checkInfoList.get(i).getid()+" ");
                                    }
                                }
                                postToserve(keyadd,keydel);      //提交
                            }
                        });
                    }
                    break;
                case 1:
                    if (msg.obj == null || (Boolean) msg.obj!=true)
                        Toast.makeText(Favourite.this, R.string.submit_fail, Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(Favourite.this, R.string.submit_success, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;

            }
        }
    };
    private Button bt;
    private LinearLayout ly;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private List<CheckInfo> checkInfoList=new ArrayList<CheckInfo>();;
    private List<CheckBox> checkBoxs = new ArrayList<CheckBox>();
    private SweetAlertDialog pDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        ly = (LinearLayout)findViewById(R.id.lyMain);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        GetItem();
        pDialog = new SweetAlertDialog(Favourite.this, SweetAlertDialog.WARNING_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Tips");
        pDialog.setContentText(getString(R.string.interest_tip));
        pDialog.setCancelable(false);
        pDialog.setConfirmText(getString(R.string.OK));
        pDialog.show();
    }


    public void GetItem()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray res = NewsService.getinterest(preferences.getString("token",null));
                Message msg = new Message();
                msg.obj = res;
                msg.what = 0;
                handler.sendMessage(msg);
            }
        }).start();
    }

    public void postToserve(final String key1,final String key2){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Boolean res1 = true,res2 = true;
                if (key1.length()!=0)
                    res1 = NewsService.Updateinterest(preferences.getString("token",null),"add_interest",key1);
                if (key2.length()!=0)
                    res2 = NewsService.Updateinterest(preferences.getString("token",null),"delete_interest",key2);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = res1&res2;
                handler.sendMessage(msg);
            }
        }).start();
    }
}