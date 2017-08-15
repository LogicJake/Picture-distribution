package com.example.scy.myapplication;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.scy.myapplication.NewsService.avator_root;
public class Login extends AppCompatActivity {
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    String name = editName.getText().toString().trim();
                    String password = editPassword.getText().toString().trim();
                    int status = -1;
                    int id = -1;
                    int complete = -1;
                    String token = null;
                    String picurl = null;
                    super.handleMessage(msg);
                    JSONObject result = (JSONObject) msg.obj;
                    if (result == null) {
                        pDialog.cancel();
                        Toast.makeText(Login.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                        if (name.equals("admin") && password.equals("admin")) {       //断网下测试使用
                            editor.putString("userName", name);
                            editor.putString("userPassword", password);
                            editor.putInt("id", 9);
                            editor.putInt("complete", 1);
                            editor.commit();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        try {
                            status = result.getInt("status");
                            id = result.getInt("id");
                            complete = result.getInt("has_complete");
                            token = result.getString("token");
                            picurl = result.getString("avator_url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (status == 1) {
                            if (checkBox.isChecked()) {
                                editor.putString("userName", name);
                                editor.putString("userPassword", password);
                                editor.putInt("id", id);
                                editor.putInt("complete", complete);
                                editor.putString("token",token);
                                editor.putString("avator_url",picurl);
                                editor.commit();
                                saveBitmapToSharedPreferences(avator_root+picurl);
                            } else {        //不自动登陆清除数据
                                editor.putString("userName", name);
                                editor.putInt("id", id);
                                editor.putInt("complete", complete);
                                editor.putString("token",token);
                                editor.remove("userPassword");
                                editor.putString("avator_url",picurl);
                                editor.commit();
                                saveBitmapToSharedPreferences(avator_root + picurl);
                            }
                        }
                        if (status == 0) {
                            editor.remove("userName");
                            editor.remove("userPassword");
                            editor.remove("id");
                            editor.remove("complete");
                            editor.remove("token");
                            editor.remove("image");
                            editor.remove("avator_url");
                            editor.commit();
                            pDialog.cancel();
                            Toast.makeText(Login.this, R.string.password_error, Toast.LENGTH_SHORT).show();
                        }
                        if (status == 2) {
                            editor.remove("userName");
                            editor.remove("userPassword");
                            editor.remove("id");
                            editor.remove("complete");
                            editor.remove("token");
                            editor.remove("image");
                            editor.remove("avator_url");
                            editor.commit();
                            pDialog.cancel();
                            Toast.makeText(Login.this, R.string.non_existent_name, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case 1:
                    JSONObject inf = (JSONObject) msg.obj;
                    String uid = null;
                    String token2 = null;
                    try {
                        uid = inf.getString("user_id");
                        token2 = inf.getString("token");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (uid == null){
                        pDialog.cancel();
                        Toast.makeText(Login.this, R.string.server_error, Toast.LENGTH_SHORT).show();}
                    else if (uid.equals("0")) {
                        pDialog.cancel();
                        Toast.makeText(Login.this, R.string.non_existent_name, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        pDialog.cancel();
                        editor.putString("token",token2);
                        editor.commit();
                        Intent intent = new Intent(Login.this, CheckID.class);
                        System.out.println(uid);
                        startActivity(intent);
                    }
                    break;
                case 2:
                    if((int)msg.obj == 1) {
                        pDialog.cancel();
                        Toast.makeText(Login.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        pDialog.cancel();
                        Toast.makeText(Login.this, R.string.down_avator_fail, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
            }
        }
    };
    private Button bt_login,bt_sign_up;
    private TextView forget;
    private CheckBox checkBox;
    private EditText editName, editPassword;
    private SharedPreferences preferences;
    private SweetAlertDialog pDialog;
    private SharedPreferences.Editor editor;
    private ImageView eye;
    private Boolean eyeOpen = false;
    public int error_time = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bt_login = (Button)findViewById(R.id.login);
        bt_sign_up = (Button)findViewById(R.id.sign_up);
        forget = (TextView) findViewById(R.id.forget_pass);
        editName = (EditText)findViewById(R.id.editText1);
        editPassword = (EditText)findViewById(R.id.editText2);
        checkBox = (CheckBox)findViewById(R.id.cb);
        eye = (ImageView)findViewById(R.id.eye);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        String name = preferences.getString("userName",null);
        String password = preferences.getString("userPassword", null);
        if (name == null||password == null) {
            checkBox.setChecked(false);
        } else {
            editName.setText(name);
            editPassword.setText(password);
            checkBox.setChecked(true);
        }
        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( eyeOpen ){
                    //密码 TYPE_CLASS_TEXT 和 TYPE_TEXT_VARIATION_PASSWORD 必须一起使用
                    editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eye.setImageResource( R.drawable.close_eye );
                    eyeOpen = false ;
                }else {
                    //明文
                    editPassword.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                    eye.setImageResource( R.drawable.open_eye );
                    eyeOpen = true ;
                }
            }
        });
        bt_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText(getString(R.string.loging));
                pDialog.setCancelable(false);
                pDialog.show();
                try {
                    final String name = editName.getText().toString();
                    final String password = editPassword.getText().toString();
                    if(name.length() == 0)
                        Toast.makeText(Login.this, R.string.name_no_empty, Toast.LENGTH_SHORT).show();
                    else {
                        if (password.length() == 0)
                            Toast.makeText(Login.this, R.string.password_no_empty, Toast.LENGTH_SHORT).show();
                        else {
                            new Thread(new Runnable(){
                                @Override
                                public void run()
                                {
                                    JSONObject result = NewsService.login(name, password);
                                    Message msg = new Message();
                                    msg.what = 0;
                                    msg.obj = result;
                                    handler.sendMessage(msg);

                                }
                            }).start();
                        }
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        bt_sign_up.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Sign_up.class);
                startActivity(intent);
            }
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.app.AlertDialog.Builder builder;
                final EditText et = new EditText(Login.this);
                builder = new android.app.AlertDialog.Builder(Login.this);
                builder.setTitle(R.string.enter_name);
                builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                        pDialog.setTitleText(getString(R.string.searching));
                        pDialog.setCancelable(false);
                        pDialog.show();
                        try {
                            new Thread(new Runnable(){
                                @Override
                                public void run()
                                {
                                    String name = et.getText().toString();
                                    JSONObject uid = NewsService.gettoken(name);
                                    System.out.println(uid);
                                    Message msg = new Message();
                                    msg.what = 1;
                                    msg.obj = uid;
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
                builder.setNegativeButton(R.string.cancel, null);
                builder.create().show();
            }
        });
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    public void saveBitmapToSharedPreferences(String url){
        if (url == null){
            Message msg = new Message();
            msg.what = 2;
            msg.obj = 0;
            handler.sendMessage(msg);
        }else
            getavator(url);
    }

    public void getavator(String url){
        DisplayImageOptions options = new DisplayImageOptions.Builder().build();
        ImageLoader.getInstance().loadImage(url, options, new SimpleImageLoadingListener() {    //加载存到本地
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            loadedImage.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String imageString = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
            editor.putString("image", imageString);
            editor.commit();
            Message msg = new Message();
            msg.what = 2;
            msg.obj = 1;
            handler.sendMessage(msg);
        }
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            error_time++;
            System.out.println(error_time);
            if(error_time == 3) {       //失败三次就作废
                Message msg = new Message();
                msg.what = 2;
                msg.obj = 0;
                handler.sendMessage(msg);
                System.out.println(failReason);
            }
            else
                getavator(imageUri);
        }
    });
    }
}