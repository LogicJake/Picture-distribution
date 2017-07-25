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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.varunest.sparkbutton.SparkEventListener;

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
import static com.example.scy.myapplication.NewsService.pic_root;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
public class Task extends AppCompatActivity {
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if ((int)msg.obj == 1)
                    {
                        pDialog.cancel();
                        if (urls.size() != 0) {
                            i = 0;
                            count.setText(Integer.toString(i + 1) + "/" + Integer.toString(MaxCount));
                            imageLoader.displayImage(pic_root + urls.get(i++).geturl(), img, options);
                            comfirm.setEnabled(TRUE);
                            next.setEnabled(TRUE);
                            heart.setChecked(false);
                        }
                    }
                    else if ((int)msg.obj == -1){
                        pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        pDialog.setTitleText("ERROR");
                        pDialog.setContentText("获取任务失败");
                        pDialog.setCancelable(false);
                        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                pDialog.cancel();
                                Intent intent = new Intent(Task.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                    presentShowcaseSequence();
                    break;
                case 2:
                    if ((boolean)msg.obj)
                    {
                        islike = 0;
                        heart.setChecked(false);
                        comfirm.setEnabled(TRUE);
                        next.setEnabled(TRUE);
                        Tags.setText("");

                        if (i < MaxCount) {
                            count.setText(Integer.toString(i + 1) + "/" + Integer.toString(MaxCount));
                            imageLoader.displayImage(pic_root+urls.get(i++).geturl(), img,options);
                        } else{
                            comfirm.setEnabled(FALSE);
                            next.setEnabled(FALSE);
                            pDialog3 = new SweetAlertDialog(Task.this, SweetAlertDialog.SUCCESS_TYPE);
                            pDialog3.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog3.setTitleText("SUCCESS");
                            pDialog3.setContentText("任务完成");
                            pDialog3.setCancelable(false);
                            pDialog3.setConfirmText("OK");
                            pDialog3.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    pDialog3.cancel();
                                    Intent intent = new Intent(Task.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            pDialog3.show();
                        }
                    }
                    else
                    {
                        Toast.makeText(Task.this, "提交失败", Toast.LENGTH_SHORT).show();
                        comfirm.setEnabled(TRUE);
                        next.setEnabled(TRUE);
                    }
                    break;
                case 3:
                    pDialog.setContentText("已加载"+LoadFinshnum+"/"+MaxCount);
                    break;
                case 4:
                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    pDialog.setTitleText("ERROR");
                    pDialog.setContentText("暂时没有此类任务");
                    pDialog.setCancelable(false);
                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            pDialog.cancel();
                            Intent intent = new Intent(Task.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    break;
            }
        }
    };
    private int LoadFinshnum = 0;
    private ImageView img;
    private Button next;
    private Button comfirm;
    private EditText Tags;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private TextView count;
    private int i;
    private int MaxCount;
    private List<Data> urls = new ArrayList<Data>();
    private DisplayImageOptions options;
    private SweetAlertDialog pDialog;
    private SweetAlertDialog pDialog3;
    private ImageLoader imageLoader;
    private SparkButton heart;
    private int islike;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        count = (TextView)findViewById(R.id.count) ;
        img = (ImageView) findViewById(R.id.iv1);
        next =(Button)findViewById(R.id.next);
        comfirm = (Button)findViewById(R.id.comfirm);
        Tags = (EditText) findViewById(R.id.Tags);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        heart = (SparkButton)findViewById(R.id.haert);
        editor = preferences.edit();
        getPic();
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)                               //启用内存缓存
                .cacheOnDisk(true)                                 //启用外存缓存
                .build();
        heart.setEventListener(new SparkEventListener(){
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if (buttonState) {
                    islike = 1;
                } else {
                    islike = 0;
                }
            }
        });

        comfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                comfirm.setEnabled(FALSE);
                next.setEnabled(FALSE);
                if(Tags.getText().toString().length() == 0)
                {
                    pDialog3 = new SweetAlertDialog(Task.this, SweetAlertDialog.WARNING_TYPE);
                    pDialog3.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog3.setTitleText("ERROR");
                    pDialog3.setContentText("标签不能为空，但你可以选择跳过");
                    pDialog3.setCancelable(false);
                    pDialog3.setConfirmText("OK");
                    pDialog3.show();
                    comfirm.setEnabled(TRUE);
                    next.setEnabled(TRUE);
                }
                else
                    pushTags(urls.get(i-1).getId(),Tags.getText().toString());  //提交内容
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                comfirm.setEnabled(FALSE);
                next.setEnabled(FALSE);
                islike = -1;
                pushTags(urls.get(i-1).getId(),"0");  //提交内容
            }
        });
    }
    public void getPic(){
        pDialog = new SweetAlertDialog(Task.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("加载任务中");
        pDialog.setCancelable(false);
        pDialog.show();
        try {
            Thread iThread = new Thread(new Runnable(){

                @Override
                public void run()
                {
                    final JSONArray jsonArray = NewsService.getimage(preferences.getString("token",null));
                    Message msg = new Message();
                    if (jsonArray == null||jsonArray.length() == 0){
                        msg.what = 1;
                        msg.obj = -1;
                        handler.sendMessage(msg);
                    }
                    else{
                        MaxCount = jsonArray.length();
                        if(MaxCount == 0)   //没有任务
                        {
                            Message msg2 = new Message();
                            msg2.what = 4;
                            handler.sendMessage(msg2);
                        }
                        else {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject temp = null;
                                try {
                                    temp = (JSONObject) jsonArray.get(i);
                                    urls.add(new Data(temp.getString("id"), temp.getString("time"), new String("null"), temp.getString("url"), 1));
                                    ImageLoader.getInstance().loadImage(pic_root + temp.optString("url"), options, new SimpleImageLoadingListener() {
                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                            super.onLoadingComplete(imageUri, view, loadedImage);
                                            LoadFinshnum++;
                                            Message msg2 = new Message();
                                            msg2.what = 3;
                                            handler.sendMessage(msg2);
                                            System.out.println(LoadFinshnum);
                                            if (LoadFinshnum == jsonArray.length()) {
                                                Message msg = new Message();
                                                msg.what = 1;
                                                msg.obj = 1;
                                                handler.sendMessage(msg);
                                            }
                                        }

                                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                            Message msg = new Message();
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
            });
            iThread.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void pushTags(final String img_id,final String tags){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess1 = NewsService.postSaveTag(preferences.getString("token",null),img_id,tags);
                boolean isSuccess2 = TRUE;
                if (islike == 1)
                    isSuccess2 = NewsService.pushLike(preferences.getString("token",null),"like",img_id);
                if (islike == -1)
                    isSuccess2 = NewsService.pushLike(preferences.getString("token", null), "dislike", img_id);
                Message msg = new Message();
                msg.what = 2;
                msg.obj = isSuccess1&isSuccess2;
                handler.sendMessage(msg);
            }
        }).start();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            pDialog3 = new SweetAlertDialog(Task.this, SweetAlertDialog.WARNING_TYPE);
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
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, ID_TASK);
        sequence.setConfig(config);
        sequence.addSequenceItem(count,"这是任务进度","下一条");
        sequence.addSequenceItem(heart,"喜欢本图片就点亮爱心","下一条");
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(Tags)
                        .setDismissText("下一条")
                        .setContentText("在此输入标签，不能为空")
                        .withRectangleShape(true)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(comfirm)
                        .setDismissText("下一条")
                        .setContentText("点击提交")
                        .withRectangleShape(true)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(next)
                        .setDismissText("结束教程")
                        .setContentText("不喜欢可以跳过")
                        .withRectangleShape(true)
                        .build()
        );

        sequence.start();
    }
}
