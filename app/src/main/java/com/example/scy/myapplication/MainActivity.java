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

import java.io.ByteArrayInputStream;
import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

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
                        Toast.makeText(MainActivity.this, R.string.exit_success, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, Login.class);            //转到登陆界面
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(MainActivity.this, R.string.exit_fail, Toast.LENGTH_SHORT).show();
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(MainActivity.this, Login.class);            //转到登陆界面
                        startActivity(intent);
                        finish();
                    }
                    setReset();
                    break;
            }
        }
    };
    private Button bt_task1;
    private Button bt_task2;
    private TextView guide;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private de.hdodenhof.circleimageview.CircleImageView avatar;
    private SweetAlertDialog pDialog;
    public static final String ID_MAIN = "id_main";
    public static final String ID_TASK = "id_task";
    public static final String ID_TASK2 = "id_task2";

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
        bt_task1 = (Button)findViewById(R.id.starttask1);
        bt_task2 = (Button)findViewById(R.id.starttask2);
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
        presentShowcaseSequence();

        bt_task1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(preferences.getInt("complete",-1000)== 0) {
                    pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText(getString(R.string.ERROR));
                    pDialog.setContentText(getString(R.string.first_choose_interest));
                    pDialog.setConfirmText(getString(R.string.now_action));
                    pDialog.setCancelText(getString(R.string.wait_action));
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
        bt_task2.setOnClickListener(new View.OnClickListener() {
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

    private void presentShowcaseSequence() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, ID_MAIN);
        sequence.setConfig(config);
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(bt_task1)
                        .setDismissText(getString(R.string.next_tip))
                        .setContentText(getString(R.string.tip1))
                        .withRectangleShape(true)
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(bt_task2)
                        .setDismissText(getString(R.string.finish_guide))
                        .setContentText(getString(R.string.tip2))
                        .withRectangleShape(true)
                        .build()
        );
        sequence.start();
    }
    public void setReset(){
        MaterialShowcaseView.resetSingleUse(this, ID_MAIN);
        MaterialShowcaseView.resetSingleUse(this, ID_TASK);
        MaterialShowcaseView.resetSingleUse(this, ID_TASK2);
    }
}
