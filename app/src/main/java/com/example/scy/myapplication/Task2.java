package com.example.scy.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.varunest.sparkbutton.SparkButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.example.scy.myapplication.MainActivity.ID_TASK;
import static com.example.scy.myapplication.MainActivity.ID_TASK2;
import static com.example.scy.myapplication.NewsService.pic_root;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.valueOf;

public class Task2 extends AppCompatActivity {

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if ((int) msg.obj == 1) {
                        pDialog.cancel();
                        if (urls.size() != 0) {
                            i = 0;
                            count.setText(Integer.toString(i + 1) + "/" + Integer.toString(MaxCount));
                            tag.setText(urls.get(i).gettags());
                            imageLoader.displayImage(pic_root + urls.get(i++).geturl(),img, options);
                            yes.setEnabled(true);
                            no.setEnabled(true);
                            unsure.setEnabled(true);
                        }
                    } else if ((int) msg.obj == -1) {
                        pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        pDialog.setTitleText("ERROR");
                        pDialog.setContentText("加载任务失败");
                        pDialog.setCancelable(false);
                        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                pDialog.cancel();
                                Intent intent = new Intent(Task2.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                    presentShowcaseSequence();
                    break;
                case 2:
                    if ((int)msg.obj == 1)
                    {
                        yes.setEnabled(true);
                        no.setEnabled(true);
                        unsure.setEnabled(true);
                        if (i < MaxCount) {
                            count.setText(Integer.toString(i + 1) + "/" + Integer.toString(MaxCount));
                            tag.setText(urls.get(i).gettags());
                            imageLoader.displayImage(pic_root+urls.get(i++).geturl(), img,options);
                        } else{
                            yes.setEnabled(false);
                            no.setEnabled(false);
                            unsure.setEnabled(false);
                            pDialog3 = new SweetAlertDialog(Task2.this, SweetAlertDialog.SUCCESS_TYPE);
                            pDialog3.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog3.setTitleText("SUCCESS");
                            pDialog3.setContentText("任务完成");
                            pDialog3.setCancelable(false);
                            pDialog3.setConfirmText("OK");
                            pDialog3.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    pDialog3.cancel();
                                    Intent intent = new Intent(Task2.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            pDialog3.show();
                        }
                    }
                    else
                    {
                        Toast.makeText(Task2.this, "提交失败", Toast.LENGTH_SHORT).show();
                        yes.setEnabled(true);
                        no.setEnabled(true);
                        unsure.setEnabled(true);
                    }
                    break;
                case 3:
                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    pDialog.setTitleText("ERROR");
                    pDialog.setContentText("暂时没有此类任务");
                    pDialog.setCancelable(false);
                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            pDialog.cancel();
                            Intent intent = new Intent(Task2.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    break;
                case 4:
                    pDialog.setContentText("已加载"+LoadFinshnum+"/"+MaxCount);
                    break;
            }
        }
    };
    private int LoadFinshnum = 0;
    private int i;
    private int MaxCount;

    private TextView count,tag;
    private Button yes,no,unsure;
    private ImageView img;
    private SweetAlertDialog pDialog;
    private SweetAlertDialog pDialog3;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    private List<Data> urls = new ArrayList<Data>();
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task2);
        count = (TextView)findViewById(R.id.count);
        tag = (TextView)findViewById(R.id.Tag);
        yes = (Button)findViewById(R.id.yes);
        no = (Button)findViewById(R.id.no);
        unsure = (Button)findViewById(R.id.unsure);
        img = (ImageView)findViewById(R.id.iv1);

        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)                                  //启用内存缓存
                .cacheOnDisk(true)                                 //启用外存缓存
                .build();

        getPic();
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                yes.setEnabled(false);
                no.setEnabled(false);
                unsure.setEnabled(false);
                pushres(urls.get(i-1).getId(),2);  //提交内容
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                yes.setEnabled(false);
                no.setEnabled(false);
                unsure.setEnabled(false);
                pushres(urls.get(i-1).getId(),0);  //提交内容
            }
        });
        unsure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                yes.setEnabled(false);
                no.setEnabled(false);
                unsure.setEnabled(false);
                pushres(urls.get(i-1).getId(),1);  //提交内容
            }
        });
    }

    public void getPic(){
        pDialog = new SweetAlertDialog(Task2.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("加载任务中");
        pDialog.setCancelable(false);
        pDialog.show();
        try {
            new Thread(new Runnable(){

                @Override
                public void run()
                {
                    final JSONArray jsonArray = NewsService.get_judge_image(preferences.getString("token",null));
                    Message msg = new Message();
                    if (jsonArray == null){
                        msg.what = 1;
                        msg.obj = -1;
                        handler.sendMessage(msg);
                    }
                    else {
                        MaxCount = jsonArray.length();
                        if (MaxCount == 0) {       //没有任务
                            msg.what = 3;
                            handler.sendMessage(msg);
                        }
                        else {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject temp = null;
                                try {
                                    temp = (JSONObject) jsonArray.get(i);
                                    urls.add(new Data(temp.getString("judge_id"), new String("null"), temp.getString("tag"), temp.getString("image_url"), 1));
                                    ImageLoader.getInstance().loadImage(pic_root + temp.optString("image_url"), options, new SimpleImageLoadingListener() {
                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                            super.onLoadingComplete(imageUri, view, loadedImage);
                                            LoadFinshnum++;
                                            Message msg2 = new Message();
                                            msg2.what = 4;
                                            handler.sendMessage(msg2);
                                            if (LoadFinshnum == jsonArray.length()) {
                                                Message msg = new Message();
                                                ;
                                                msg.what = 1;
                                                msg.obj = 1;
                                                handler.sendMessage(msg);
                                            }
                                        }

                                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                            Message msg = new Message();
                                            ;
                                            msg.what = 1;
                                            msg.obj = -1;
                                            handler.sendMessage(msg);
                                            System.out.println(failReason);
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }).start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void pushres(final String id, final int res){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int isSuccess1= NewsService.push_juede_res(preferences.getString("token",null),Integer.parseInt(id), res);
                Message msg = new Message();
                msg.what = 2;
                msg.obj = isSuccess1;
                handler.sendMessage(msg);
            }
        }).start();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            pDialog3 = new SweetAlertDialog(Task2.this, SweetAlertDialog.WARNING_TYPE);
            pDialog3.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog3.setTitleText("ERROR");
            pDialog3.setContentText("不能中途退出任务");
            pDialog3.setCancelable(false);
            pDialog3.setConfirmText("OK");
            pDialog3.show();
        }
        return super.onKeyDown(keyCode, event);
    }
    private void presentShowcaseSequence() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, ID_TASK2);
        sequence.setConfig(config);
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(count)
                        .setDismissText("下一条")
                        .setContentText("这是任务进度")
                        .withRectangleShape(true)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(tag)
                        .setDismissText("下一条")
                        .setContentText("这是需要判断的标签")
                        .withRectangleShape(true)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(yes)
                        .setDismissText("下一条")
                        .setContentText("标签符合图片就点我")
                        .withRectangleShape(true)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(no)
                        .setDismissText("下一条")
                        .setContentText("标签不符合图片就点我")
                        .withRectangleShape(true)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(unsure)
                        .setDismissText("结束教程")
                        .setContentText("把握不定就点我")
                        .withRectangleShape(true)
                        .build()
        );

        sequence.start();
    }
}
