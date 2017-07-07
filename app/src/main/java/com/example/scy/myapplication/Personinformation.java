package com.example.scy.myapplication;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
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

import net.qiujuer.genius.blur.StackBlur;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.List;
import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER_HORIZONTAL;
import static com.example.scy.myapplication.NewsService.avator_root;

public class Personinformation extends AppCompatActivity {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            JSONObject result = null;
            String key = null;
            int flag = -1;
            switch(msg.what)
            {
                case 0:
                    super.handleMessage(msg);
                    result = (JSONObject) msg.obj;
                    String user_name = preferences.getString("userName",null);
                    int id = preferences.getInt("id",-1);
                    String stele = null;
                    String sqq = null;
                    int ssex = -1;
                    int sscore = -1;
                    String ssigh = null;
                    String aurl = null;
                    if (result == null)
                        Toast.makeText(Personinformation.this, "获取数据失败！", Toast.LENGTH_SHORT).show();
                    else {
                        try {
                            stele = result.getString("phone_num");
                            sqq = result.getString("qq_num");
                            ssex = result.getInt("sex");
                            sscore = result.getInt("score");
                            ssigh = result.getString("user_sign");
                            aurl = result.getString("avator_url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final Bitmap avator = getBitmapFromSharedPreferences();
                        if (avator != null){
                            avatar.setImageBitmap(avator);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Bitmap newBitmap = StackBlur.blur(avator, 20, false);                //显示背景
                                    Message msg = new Message();
                                    msg.what = 7;
                                    msg.obj = newBitmap;
                                    handler.sendMessage(msg);
                                }
                            }).start();
                            if(avator == null && preferences.getString("avator_url",null) != null)      //没有缓存
                                saveBitmapToSharedPreferences(avator_root+aurl);
                        }

                        name.setText(user_name);
                        UID.setText(Integer.toString(id));
                        if (stele.equals("null"))
                            tele.setText("未设置");
                        else
                            tele.setText(stele);
                        if (sqq.equals("null"))
                            qq.setText("未设置");
                        else
                            qq.setText(sqq);
                        if (ssigh.equals("null"))
                            sigh.setText(" ");
                        else
                            sigh.setText(ssigh);
                        if (ssex == 0)
                            sex.setText("男");
                        else if (ssex == 1)
                            sex.setText("女");
                        else if (ssex == -1)
                            sex.setText("未设置");
                        score.setText(Integer.toString(sscore));

                    }
                    break;

                case 1:
                    super.handleMessage(msg);
                    result = (JSONObject) msg.obj;
                    try {
                        flag = result.getInt("status");
                        key = result.getString("key");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (flag == 0)
                        Toast.makeText(Personinformation.this, "修改失败！", Toast.LENGTH_SHORT).show();
                    else if(flag == 1)
                        Toast.makeText(Personinformation.this, "修改成功！", Toast.LENGTH_SHORT).show();
                        tele.setText(key);
                    break;
                case 2:
                    super.handleMessage(msg);
                    result = (JSONObject) msg.obj;
                    try {
                        flag = result.getInt("status");
                        key = result.getString("key");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (flag == 0)
                        Toast.makeText(Personinformation.this, "修改失败！", Toast.LENGTH_SHORT).show();
                    else if(flag == 1) {
                        Toast.makeText(Personinformation.this, "修改成功！", Toast.LENGTH_SHORT).show();
                        qq.setText(key);
                    }
                    break;
                case 3:
                    super.handleMessage(msg);
                    result = (JSONObject) msg.obj;
                    int rsex = -1;
                    try {
                        flag = result.getInt("status");
                        rsex = result.getInt("key");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (flag == 0)
                        Toast.makeText(Personinformation.this, "修改失败！", Toast.LENGTH_SHORT).show();
                    else if(flag == 1) {
                        Toast.makeText(Personinformation.this, "修改成功！", Toast.LENGTH_SHORT).show();
                        if (rsex == 0)
                            sex.setText("男");
                        else
                            sex.setText("女");
                    }
                    break;
                case 4:
                    super.handleMessage(msg);
                    result = (JSONObject) msg.obj;
                    try {
                        flag = result.getInt("status");
                        key = result.getString("key");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (flag == 0)
                        Toast.makeText(Personinformation.this, "修改失败！", Toast.LENGTH_SHORT).show();
                    else if(flag == 1) {
                        Toast.makeText(Personinformation.this, "修改成功！", Toast.LENGTH_SHORT).show();
                        sigh.setText(key);
                    }
                    break;
                case 5:
                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .cacheInMemory(true)                               //启用内存缓存
                            .cacheOnDisk(true)                                 //启用外存缓存
                            .build();
                    JSONObject res = (JSONObject) msg.obj;
                    if (res == null) {
                        pDialog2.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        pDialog2.setTitleText("ERROR");
                        pDialog2.setContentText("上传到服务器失败");
                        pDialog2.setCancelable(true);
                        pDialog2.show();
                    }
                    else {
                        int status = 0;
                        try {
                            status = res.getInt("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (status == 200) {
                            try {
                                final String url = avator_root + res.getString("avator_url");
                                System.out.println(res.getInt("status"));
                                if (res.getInt("status") == 200) {
                                    ImageLoader.getInstance().loadImage(url, options, new SimpleImageLoadingListener() {    //加载存到本地
                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                            saveBitmapToSharedPreferences(loadedImage);         //存到本地
                                            ImageLoader.getInstance().displayImage(url, avatar);      //显示头像
                                            Bitmap newBitmap = StackBlur.blur(loadedImage, 20, false);                //显示背景

                                            NavigationView navigationView = (NavigationView) LayoutInflater.from(Personinformation.this).inflate(R.layout.activity_main, null).findViewById(R.id.nav_view);
                                            View hView = navigationView.getHeaderView(0);
                                            avatar2 = (de.hdodenhof.circleimageview.CircleImageView) hView.findViewById(R.id.imageView); //更新侧边栏头像
                                            avatar2.setImageBitmap(loadedImage);
                                            System.out.println("gengxinwanbi");

                                            pic.setBackground(new BitmapDrawable(newBitmap));
                                            pDialog2.cancel();
                                            if (loadedImage != null && !loadedImage.isRecycled()) {
                                                loadedImage.recycle();
                                                loadedImage = null;
                                                System.gc();
                                            }
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else if(status == 1){
                            pDialog2.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            pDialog2.setTitleText("ERROR");
                            pDialog2.setContentText("图片大小不能超过2MB");
                            pDialog2.setCancelable(true);
                            pDialog2.show();
                        }
                        else if(status == 2) {
                            pDialog2.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            pDialog2.setTitleText("ERROR");
                            pDialog2.setContentText("文件上传发生错误");
                            pDialog2.setCancelable(true);
                            pDialog2.show();
                        }
                        else if(status == 3) {
                            pDialog2.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            pDialog2.setTitleText("ERROR");
                            pDialog2.setContentText("不支持的图片扩展名");
                            pDialog2.setCancelable(true);
                            pDialog2.show();
                        }
                        else if(status == 404)
                        {
                            pDialog2.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            pDialog2.setTitleText("ERROR");
                            pDialog2.setContentText("无正确的文件上传");
                            pDialog2.setCancelable(true);
                            pDialog2.show();

                        }
                    }
                    break;
                case 6:
                    if((int)msg.obj == 1) {
                        Bitmap avator2 = getBitmapFromSharedPreferences();
                        if (avator2 != null) {
                            avatar.setImageBitmap(avator2);
                            Bitmap newBitmap = StackBlur.blur(avator2, 20, false);                //显示背景
                            pic.setBackground(new BitmapDrawable(newBitmap));
                            NavigationView navigationView = (NavigationView) LayoutInflater.from(Personinformation.this).inflate(R.layout.activity_main, null).findViewById(R.id.nav_view);
                            View hView = navigationView.getHeaderView(0);
                            avatar2 = (de.hdodenhof.circleimageview.CircleImageView) hView.findViewById(R.id.imageView); //更新侧边栏头像
                            avatar2.setImageBitmap(newBitmap);
                            pDialog.cancel();
                        } else {
                            Toast.makeText(Personinformation.this, "加载头像失败", Toast.LENGTH_SHORT);
                            pDialog.cancel();
                        }
                    }
                    else {
                        Toast.makeText(Personinformation.this, "加载头像失败", Toast.LENGTH_SHORT);
                        pDialog.cancel();
                    }
                    break;
                case 7:
                    Bitmap newBitmap = (Bitmap) msg.obj;
                    pic.setBackground(new BitmapDrawable(newBitmap));
                    pDialog.cancel();
                    break;
            }
        }
    };
    private TableRow trtele;
    private TableRow trqq;
    private TableRow trsex;
    private TableRow trsigh;
    private TableRow trhobby;
    private TextView name;
    private TextView UID;
    private TextView score;
    private TextView tele;
    private TextView qq;
    private TextView sex;
    private TextView sigh;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Button sure;
    private ImageView avatar;
    private SweetAlertDialog pDialog;
    private SweetAlertDialog pDialog2;
    String single[] = {"男","女"};
    String singleChoice;
    SelectPicPopupWindow menuWindow;
    private de.hdodenhof.circleimageview.CircleImageView avatar2;

    private int REQUEST_CODE_GALLERY = 200;
    private int REQUEST_CODE_CAMERA = 100;
    private FunctionConfig functionConfig;
    LinearLayout pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personinformation);
        avatar = (ImageView)findViewById(R.id.avatar);
        pDialog = new SweetAlertDialog(Personinformation.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("获取信息中");
        pDialog.setCancelable(false);
        pDialog.show();
        trtele = (TableRow)findViewById(R.id.trtele);
        trqq = (TableRow)findViewById(R.id.trqq);
        trsex = (TableRow)findViewById(R.id.trsex);
        trsigh = (TableRow)findViewById(R.id.trsigh);
        trhobby = (TableRow)findViewById(R.id.trhobby);

        name = (TextView)findViewById(R.id.name);
        UID = (TextView)findViewById(R.id.UID);
        tele = (TextView)findViewById(R.id.tele);
        qq = (TextView)findViewById(R.id.qq);
        sex = (TextView)findViewById(R.id.sex);
        score = (TextView)findViewById(R.id.score);
        sigh = (TextView)findViewById(R.id.sigh);

        pic = (LinearLayout)findViewById(R.id.pic);
        InitGalleryFinal();                 //初始化图片选择器

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuWindow = new SelectPicPopupWindow(Personinformation.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(Personinformation.this.findViewById(R.id.avatar), BOTTOM|CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            }
        });

        sure = (Button)findViewById(R.id.changepassword);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Personinformation.this,CheckID.class);
                startActivity(intent);
            }
        });

        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        renew();

        trtele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.app.AlertDialog.Builder builder;
                final EditText et = new EditText(Personinformation.this);
                et.setInputType(InputType.TYPE_CLASS_PHONE);
                et.setText(tele.getText());
                builder = new android.app.AlertDialog.Builder(Personinformation.this);
                builder.setTitle("修改手机号码");
                builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            new Thread(new Runnable(){
                                @Override
                                public void run()
                                {
                                    String key = et.getText().toString();
                                    JSONObject result =  NewsService.getinfo(preferences.getString("token",null),"update_phone_num",key);
                                    Message msg = new Message();
                                    msg.obj = result;
                                    msg.what = 1;
                                    handler.sendMessage(msg);
                                }
                            }).start();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
                builder.setView(et);
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }
        });

        trqq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.app.AlertDialog.Builder builder;
                final EditText et = new EditText(Personinformation.this);
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                et.setText(qq.getText());
                builder = new android.app.AlertDialog.Builder(Personinformation.this);
                builder.setTitle("修改QQ号码");
                builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            new Thread(new Runnable(){
                                @Override
                                public void run()
                                {
                                    String key = et.getText().toString();
                                    JSONObject result =  NewsService.getinfo(preferences.getString("token",null),"update_qq_num",key);
                                    Message msg = new Message();
                                    msg.obj = result;
                                    msg.what = 2;
                                    handler.sendMessage(msg);
                                }
                            }).start();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
                builder.setView(et);
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }
        });

        trsex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final android.app.AlertDialog.Builder builder;
                builder = new android.app.AlertDialog.Builder(Personinformation.this);
                builder.setTitle("您的性别是？");
                builder.setSingleChoiceItems(single,3, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        singleChoice = single[which];
                    }
                });
                builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            new Thread(new Runnable(){
                                @Override
                                public void run()
                                {
                                    String key = singleChoice;
                                    JSONObject result =  NewsService.getinfo(preferences.getString("token",null),"update_sex",key);
                                    Message msg = new Message();
                                    msg.obj = result;
                                    msg.what = 3;
                                    handler.sendMessage(msg);
                                }
                            }).start();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }
        });

        trsigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final android.app.AlertDialog.Builder builder;
                final EditText et = new EditText(Personinformation.this);
                et.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                et.setMinLines(1);
                et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                et.setSingleLine(false);
                et.setMovementMethod(ScrollingMovementMethod.getInstance());
                builder = new android.app.AlertDialog.Builder(Personinformation.this);
                builder.setTitle("修改个性签名(50字符以内)");
                et.setText(sigh.getText());
                builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            new Thread(new Runnable(){
                                @Override
                                public void run()
                                {
                                    String key = et.getText().toString();
                                    JSONObject result =  NewsService.getinfo(preferences.getString("token",null),"update_user_sign",key);
                                    Message msg = new Message();
                                    msg.obj = result;
                                    msg.what = 4;
                                    handler.sendMessage(msg);
                                }
                            }).start();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
                builder.setView(et);
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }
        });

        trhobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Personinformation.this, Favourite.class);
                startActivity(intent);
            }
        });
    }
    public void renew(){
        try {         //初始化个人信息
            new Thread(new Runnable(){
                @Override
                public void run()
                {
                    JSONObject result = NewsService.getinfo(preferences.getString("token",null),"get_info",null);
                    Message msg = new Message();
                    msg.obj = result;
                    msg.what = 0;
                    handler.sendMessage(msg);
                }
            }).start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(Personinformation.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener(){

        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_take_photo:
                    GalleryFinal.openCamera(REQUEST_CODE_CAMERA, functionConfig, mOnHanlderResultCallback);
                    break;
                case R.id.btn_pick_photo:
                    GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, functionConfig, mOnHanlderResultCallback);
                    break;
                default:
                    break;
            }
        }
    };

    public void InitGalleryFinal(){
        ThemeConfig theme = new ThemeConfig.Builder().build();

        functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(true)
                .build();

        cn.finalteam.galleryfinal.ImageLoader imageloader = new UILImageLoader();

        CoreConfig coreConfig = new CoreConfig.Builder(Personinformation.this, imageloader, theme).setFunctionConfig(functionConfig).build();
        GalleryFinal.init(coreConfig);
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback=new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int i, List<PhotoInfo> list) {
            pDialog2 = new SweetAlertDialog(Personinformation.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog2.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog2.setTitleText("上传头像中");
            pDialog2.setCancelable(false);
            pDialog2.show();
            final String filepath = list.get(0).getPhotoPath();
            if (list!=null){
                if (i==100){
                    System.out.println("openCamera");
                }else if (i==200){
                    System.out.println("openGallerySingle");
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject res = NewsService.avater(new File(filepath), preferences.getString("token", ""));
                        Message msg = new Message();
                        msg.what = 5;
                        msg.obj = res;
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        }
        @Override
        public void onHanlderFailure(int i, String s) {
            Toast.makeText(Personinformation.this,"设置头像失败",Toast.LENGTH_SHORT);
        }
    };
    public void saveBitmapToSharedPreferences(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        byte[] byteArray=byteArrayOutputStream.toByteArray();
        String imageString=new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
        editor.putString("image", imageString);
        editor.commit();
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

    public void saveBitmapToSharedPreferences(String url){
        DisplayImageOptions options = new DisplayImageOptions.Builder().build();
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
        ImageLoader.getInstance().loadImage(url, options, new SimpleImageLoadingListener() {    //加载存到本地
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                loadedImage.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
                byte[] byteArray=byteArrayOutputStream.toByteArray();
                String imageString = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
                editor.putString("image", imageString);
                editor.commit();
                Message msg = new Message();
                msg.what = 6;
                msg.obj = 1;
                handler.sendMessage(msg);
            }
            public void onLoadingFailed(String imageUri, View view, FailReason failReason){
                Message msg = new Message();;
                msg.what = 6;
                msg.obj = 0;
                handler.sendMessage(msg);
                System.out.println(failReason);
            }
        });
    }

}

