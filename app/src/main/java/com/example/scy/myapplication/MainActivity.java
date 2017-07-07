package com.example.scy.myapplication;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.ByteArrayInputStream;
import cn.pedant.SweetAlert.SweetAlertDialog;
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    if ((String)msg.obj != null){
                        System.out.println((String)msg.obj);
                        guide.setText(((String)msg.obj).replace("\\n","\n"));}
                    break;
                case 1:
                    if((int)msg.obj == 0) {
                        editor.clear();
                        editor.commit();
                        Toast.makeText(MainActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, Login.class);            //转到登陆界面
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "退出账号失败", Toast.LENGTH_SHORT).show();
                        editor.clear();
                        editor.commit();
                        Toast.makeText(MainActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, Login.class);            //转到登陆界面
                        startActivity(intent);
                        finish();
                    }
                    break;
            }
        }
    };
    private Button btn1;
    private Button btn2;
    private TextView guide;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private de.hdodenhof.circleimageview.CircleImageView avatar;
    private SweetAlertDialog pDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        guide = (TextView)findViewById(R.id.guide) ;
        btn1 = (Button)findViewById(R.id.starttask1);
        btn2 = (Button)findViewById(R.id.starttask2);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        getguide();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String name = preferences.getString("userName",null);
        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.name);
        avatar = (de.hdodenhof.circleimageview.CircleImageView)hView.findViewById(R.id.imageView);
        Bitmap avator = getBitmapFromSharedPreferences();
        if (avator != null){
            System.out.println("pic!!!");
            avatar.setImageBitmap(avator);
        }
        nav_user.setText(name);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Personinformation.class);
                startActivity(intent);
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(preferences.getInt("complete",-1000)== 0) {
                    pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("ERROR");
                    pDialog.setContentText("请先勾选个人兴趣");
                    pDialog.setConfirmText("现在就去");
                    pDialog.setCancelText("稍后再去");
                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Intent intent = new Intent(MainActivity.this, Favourite.class);
                            startActivity(intent);
                            pDialog.cancel();
                        }
                    });
                    pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            pDialog.cancel();
                        }
                    });
                    pDialog.show();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, Task.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Task2.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.imformation) {
            Intent intent = new Intent(MainActivity.this,Personinformation.class);	//转到登陆界面
            startActivity(intent);
        } else if (id == R.id.records) {
            Intent intent = new Intent(MainActivity.this,History.class);			//转到历史记录界面
            startActivity(intent);
        } else if (id == R.id.guide) {
            Intent intent = new Intent(MainActivity.this,Guide.class);			//转到指南界面
            startActivity(intent);
        } else if (id == R.id.exit) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int res = NewsService.logout(preferences.getString("token",null));
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = res;
                    handler.sendMessage(msg);
                }
            }).start();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getguide(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String guide = NewsService.getguide();
                Message msg = new Message();
                msg.what = 0;
                msg.obj = guide;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private Bitmap getBitmapFromSharedPreferences(){
        Bitmap bitmap = null;
        String imageString=preferences.getString("image", "null");
        if(imageString.equals("null"))
            bitmap = null;
        else {
            byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
        }
        return bitmap;
    }
}
