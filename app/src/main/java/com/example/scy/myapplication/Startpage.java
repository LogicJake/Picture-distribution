package com.example.scy.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.json.JSONException;
import org.json.JSONObject;


public class Startpage extends Activity {
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            JSONObject result = (JSONObject) msg.obj;
            if(result != null) {
                int status = -1;
                String token = null;
                try {
                    status = result.getInt("status");
                    token = result.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status == 1) {      //登陆成功，获取最新的token
                    editor.putString("token",token);
                    editor.commit();
                    Intent intent = new Intent(Startpage.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(Startpage.this,Login.class);     //登陆界面
                    startActivity(intent);
                    finish();
                }
            }
            else
            {
                Intent intent = new Intent(Startpage.this,Login.class);     //登陆界面
                startActivity(intent);
                finish();
            }

        }
    };
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startpage);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        final String name = preferences.getString("userName",null);
        final String password = preferences.getString("userPassword", null);
        if (name == null||password == null) {		//之前未登陆，打开登陆界面
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Startpage.this,Login.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);

        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject result = NewsService.login(name, password);
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }
            }).start();
        }
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(this)
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY -2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2* 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100) //缓存的文件数量
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(this,10 * 1000, 10 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs()
                .build();//开始构建
        ImageLoader.getInstance().init(configuration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.startpage, menu);
        return true;
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

