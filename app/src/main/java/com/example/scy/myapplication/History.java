package com.example.scy.myapplication;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import static com.example.scy.myapplication.NewsService.pic_root;

public class History extends Activity {
    private Handler handler = new Handler(){
        JSONObject jsonObject;
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    if ((int)msg.obj == 1){
                        if (page-1 == 1)
                            pDialog.cancel();
                        myAdapter.notifyDataSetChanged();// 通知listView刷新数据
                        if (isFinished == 1){
                            pg.setVisibility(View.GONE);
                            bt.setVisibility(View.GONE);
                            finish.setVisibility(View.VISIBLE);
                        }
                        else {
                            bt.setVisibility(View.VISIBLE);
                            pg.setVisibility(View.GONE);
                        }
                    }
                    break;
                case 1:
                    jsonObject = (JSONObject) msg.obj;
                    if (jsonObject == null) {
                        Toast.makeText(History.this, R.string.get_his_fail, Toast.LENGTH_SHORT).show();
                        finish();
                        pDialog.cancel();
                    }
                    break;
                case 2:
                    if (msg.getData().getBoolean("result"))
                    {
                        Tag_item.setText(msg.getData().getString("tag"));
                        Toast.makeText(History.this, R.string.modify_success,Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(History.this, R.string.modify_fail,Toast.LENGTH_SHORT).show();
                    }
                    break;
            }

        }};

    public StickyListHeadersListView list;
    private MyAdapter myAdapter;
    private List<Data> Mydatas;
    private View moreview;
    private TextView bt;
    private ProgressBar pg;
    private View finish;
    private int isFinished;
    private int page = 1;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int LoadFinshnum = 0;
    private SweetAlertDialog pDialog;
    private Thread iThread;
    private TextView Tag_item;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)                               //启用内存缓存
            .cacheOnDisk(true)                                 //启用外存缓存
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        moreview = getLayoutInflater().inflate(R.layout.progress, null);
        list = (StickyListHeadersListView) findViewById(R.id.list);
        bt = (TextView) moreview.findViewById(R.id.bt_load);
        pg = (ProgressBar) moreview.findViewById(R.id.pg);
        finish = (TextView) moreview.findViewById(R.id.finish);
        Mydatas = new ArrayList<Data>();
        myAdapter = new MyAdapter(this, Mydatas);
        GetHistory();
        pDialog = new SweetAlertDialog(History.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(getString(R.string.getting_history));
        pDialog.setCancelable(false);
        pDialog.show();
        list.setOnItemClickListener(new OnPlanItemClick());
        list.addFooterView(moreview,null,false);
        list.setAdapter(myAdapter);
        bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pg.setVisibility(View.VISIBLE);// 将进度条可见
                bt.setVisibility(View.GONE);// 按钮不可见
                GetHistory();
            }
        });
    }


    private class OnPlanItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Data data = (Data) parent.getAdapter().getItem(position);
            Tag_item = (TextView) view.findViewById(R.id.text_tags);
            if (data.getStatus() == 0) {    //可修改
                LayoutInflater inflater = LayoutInflater.from(History.this);
                View imgEntryView = inflater.inflate(R.layout.dialog_photo_display, null); // 加载自定义的布局文件
                ImageView img = (ImageView) imgEntryView.findViewById(R.id.large_image);
                final TextView tag = (TextView) imgEntryView.findViewById(R.id.Tag);

                tag.setText(data.gettags());
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .build();
                //加载图片
                ImageLoader.getInstance().displayImage(data.geturl(), img);
                final AlertDialog.Builder builder = new AlertDialog.Builder(History.this);
                builder.setView(imgEntryView);
                builder.setPositiveButton(R.string.comfirm_modify, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if(tag.getText().toString().length() == 0){
                                        Message msg = new Message();
                                        msg.what = 2;
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("result", false);  //往Bundle中存放数据
                                        bundle.putString("tag", tag.getText().toString());  //往Bundle中put数据
                                        msg.setData(bundle);
                                        handler.sendMessage(msg);
                                    }
                                    else {
                                        boolean result = NewsService.postSaveTag(preferences.getString("token", null), data.getId(), tag.getText().toString());
                                        Message msg = new Message();
                                        msg.what = 2;
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("result", result);  //往Bundle中存放数据
                                        bundle.putString("tag", tag.getText().toString());  //往Bundle中put数据
                                        msg.setData(bundle);
                                        handler.sendMessage(msg);
                                    }
                                }
                            }).start();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.create().show();
            }
            else
                Toast.makeText(History.this, R.string.not_modify, Toast.LENGTH_SHORT).show();
        }
    }

//    private class OnPlanItemLongClick implements AdapterView.OnItemLongClickListener {
//
//        @Override
//        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//            Data oLangyaSimple = Mydatas.get(position);
//
//            System.out.println("tag"+oLangyaSimple.toString());
//
//            Mydatas.remove(oLangyaSimple);
//
//            updateData();
//
//            return true;
//        }
//    }


    public void GetHistory(){
        LoadFinshnum = 0;
        iThread = new Thread(new Runnable(){       //获取数据
            @Override
            public void run()
            {
                        JSONObject result =  NewsService.gethistory(preferences.getString("token",null),page++);
                        final JSONArray jsonArray;
                        if (result != null)
                        {
                            try {
                                jsonArray = result.getJSONArray("data");
                                isFinished = result.getInt("done");
                                if (result.getInt("count") == 0){
                                    Message msg = new Message();
                                    msg.what = 0;
                                    msg.obj = 1;
                                    handler.sendMessage(msg);
                                }
                                else {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject temp = (JSONObject) jsonArray.get(i);
                                        temp = (JSONObject) jsonArray.get(i);
                                        System.out.println(temp.getInt("done"));
                                        Mydatas.add(new Data(temp.getString("id"), temp.getString("update_time") + "000", temp.getString("tag"), pic_root + temp.getString("url"), temp.getInt("done")));
                                        ImageLoader.getInstance().loadImage(pic_root + temp.optString("url"), options, new SimpleImageLoadingListener() {
                                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                super.onLoadingComplete(imageUri, view, loadedImage);
                                                LoadFinshnum++;
                                                if (LoadFinshnum == jsonArray.length()) {
                                                    Message msg = new Message();
                                                    msg.what = 0;
                                                    msg.obj = 1;
                                                    handler.sendMessage(msg);
                                                }
                                            }
                                        });
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = result;
                        handler.sendMessage(msg);

            }
        });
       iThread.start();
    }
}
