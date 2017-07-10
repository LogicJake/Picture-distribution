package com.example.scy.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import static android.provider.Telephony.Mms.Part.CHARSET;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class NewsService {
    private static String rooturl = "http://ruan.yuwenjie.cc/";
    public static String avator_root = "http://upload.ruan.yuwenjie.cc/";
    public static String pic_root = "http://upload.ruan.yuwenjie.cc/";
    public static String getMD5(String message) {
        String md5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");  // 创建一个md5算法对象
            byte[] messageByte = message.getBytes("UTF-8");
            byte[] md5Byte = md.digest(messageByte);              // 获得MD5字节数组,16*8=128位
            md5 = bytesToHex(md5Byte);                            // 转换为16进制字符串
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }
    // 二进制转十六进制
    public static String bytesToHex(byte[] bytes) {
        StringBuffer hexStr = new StringBuffer();
        int num;
        for (int i = 0; i < bytes.length; i++) {
            num = bytes[i];
            if(num < 0) {
                num += 256;
            }
            if(num < 16){
                hexStr.append("0");
            }
            hexStr.append(Integer.toHexString(num));
        }
        return hexStr.toString().toUpperCase();
    }
    /**
     * 登录验证
     *
     * @param user_name   姓名
     * @param user_passwd 密码
     * @return
     */
    public static JSONObject login(String user_name, String user_passwd) {          //登陆请求
        JSONObject jsonObject = null;
        try {
            user_passwd = getMD5(user_passwd);
            System.out.println(user_passwd);
            String path = rooturl+"index.php?_action=postLogin";
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data = "&user_name=" + URLEncoder.encode(user_name, "UTF-8") + "&user_passwd=" + URLEncoder.encode(user_passwd, "UTF-8");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                jsonObject = new JSONObject(baos.toString()).getJSONObject("data").getJSONObject("result");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 注册
     *
     * @param user_name     用户名
     * @param user_password 密码
     * @return 1：注册成功   2：已存在该用户名
     */
    public static JSONObject signup(String user_name, String user_password) {
        JSONObject jsonObject = null;
        try {
            user_password = getMD5(user_password);
            System.out.println(user_password);
            String path = rooturl+"index.php?_action=postSignup";
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data = "&user_name=" + URLEncoder.encode(user_name, "UTF-8") + "&user_passwd=" + URLEncoder.encode(user_password, "UTF-8");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                jsonObject = new JSONObject(baos.toString()).getJSONObject("data").getJSONObject("result");
            }
            else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject getinfo(String token,String type,String key) {
        JSONObject result = null;
        try {
            String path = rooturl+"index.php?_action=getInfo&token="+token;
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            System.out.println(token);
            String data = "&type="+URLEncoder.encode(type, "UTF-8");
            if (type.equals("update_sex")){
                int sex = (key.equals("男"))?0:1;
                data += "&sex="+sex;
            }
            if (type.equals("update_qq_num")){
                data += "&qq_num="+URLEncoder.encode(key, "UTF-8");
            }
            if (type.equals("update_phone_num")){
                data += "&phone_num="+URLEncoder.encode(key, "UTF-8");;
            }
            if (type.equals("update_user_sign")){
                data += "&user_sign="+URLEncoder.encode(key, "UTF-8");
            }
            System.out.println(data);
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                result = new JSONObject(baos.toString()).getJSONObject("data").getJSONObject("result");
                System.out.println(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject gethistory(String token,int page) {
        JSONObject result = null;
        try {
            String path = rooturl+"index.php?_action=postHistory&token="+token;
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
//            urlConnection.connect();
            String data = "&page="+page;
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                result = new JSONObject(baos.toString()).getJSONObject("data").getJSONObject("result");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONArray getimage(String token) {
        JSONArray res = null;
        try {
            String path = rooturl+"index.php?_action=getImage&token="+token;;
            URL url = new URL(path);
            // 获得连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos);
                JSONObject jsonObject = null;
                jsonObject = new JSONObject(baos.toString()).getJSONObject("data");
                if(jsonObject.getInt("doneAll") == 1)
                    res = new JSONArray();
                else
                    res = jsonObject.getJSONArray("img");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static boolean postSaveTag(String token, String image_id,String tag) {
        try {
            System.out.println(token+":"+tag);
            String path = rooturl+"index.php?_action=postSaveTag&token="+token;;
            System.out.println("tag:"+token);
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data = "&image_id=" + URLEncoder.encode(image_id, "UTF-8")+"&tag="+URLEncoder.encode(tag,"UTF-8");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                JSONObject jsonObject = new JSONObject(baos.toString());
                return jsonObject.getBoolean("data");
            }
            else
                return FALSE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FALSE;
    }

    public static Boolean Updateinterest(String token,String type,String key) {
        Boolean result = null;
        try {
            String path = rooturl+"index.php?_action=getInfo&token="+token;
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data = null;
            System.out.println("key:"+key);
            if (type.equals("add_interest"))
                data ="&type=add_interest&interest="+URLEncoder.encode(key,"UTF-8");
            else if (type.equals("delete_interest"))
                data ="&type=delete_interest&interest="+URLEncoder.encode(key,"UTF-8");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
//            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                result = new JSONObject(baos.toString()).getJSONObject("data").getBoolean("result");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONArray getinterest(String token) {
        JSONArray jsonObject = null;
        try {
            String path = rooturl+"index.php?_action=getAllInterest&token="+token;
            URL url = new URL(path);
            // 获得连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                jsonObject = new JSONObject(baos.toString()).getJSONArray("data");
                System.out.println(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject postSecure(String token,String type,String key){
        JSONObject res = null;
        try {
            String path = rooturl+"index.php?_action=postSecure&token="+token;
            URL url = new URL(path);
            String data= "&type=getAllquestion";
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            System.out.println(data);
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                res = new JSONObject(baos.toString());
                System.out.println(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static int postqus(String token,String type,List<String> key){
        int res = -1;
        try {
            String path = rooturl+"index.php?_action=postSecure&token="+token;
            URL url = new URL(path);
            String data = null;
            if (type.equals("addUserquestion")){
//                data += "&type=addUserquestion"+URLEncoder.encode(key,"UTF-8");
                data+="&type=addUserquestion"+"&q_1="+URLEncoder.encode(key.get(0),"UTF-8")+"&q_2="+URLEncoder.encode(key.get(1),"UTF-8")+"&q_3="+URLEncoder.encode(key.get(2),"UTF-8")+"&a_1="+URLEncoder.encode(key.get(3),"UTF-8")+"&a_2="+URLEncoder.encode(key.get(4),"UTF-8")+"&a_3="+URLEncoder.encode(key.get(5),"UTF-8");
            }
            System.out.print(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            System.out.println(data);
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
//            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                res = new JSONObject(baos.toString()).getInt("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static JSONObject GetQue(String token) {
        System.out.println(token);
        JSONObject result = null;
        try {
            String path = rooturl+"index.php?_action=postSecure&token="+token;
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data = "&type=getUserquestion";
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                result = new JSONObject(baos.toString()).getJSONObject("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int CheckAns(String token,List<String> key) {
        int result = -1;
        try {
            String path = rooturl+"index.php?_action=postSecure&token="+token;
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data = "&type=checkUserquestion&a_1="+URLEncoder.encode(key.get(0),"UTF-8")+"&a_2="+URLEncoder.encode(key.get(1),"UTF-8")+"&a_3="+URLEncoder.encode(key.get(2),"UTF-8");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                result = new JSONObject(baos.toString()).getJSONObject("data").getInt("result");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Boolean ChangePass(String token,String newpass) {
        Boolean result = false;
        try {
            newpass = getMD5(newpass);
            String path = rooturl+"index.php?_action=postChangepasswd&token="+token;
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data = "&new_passwd="+newpass;
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                result = new JSONObject(baos.toString()).getJSONObject("data").getBoolean("result");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject gettoken(String name) {
        JSONObject res = null;
        try {
            String path = rooturl+"index.php?_action=getForgetpasswd&user_name="+name;
            URL url = new URL(path);
            // 获得连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("GET");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                res = new JSONObject(baos.toString()).getJSONObject("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static Boolean pushLike(String token,String type,String imgid) {
        Boolean result = false;
        try {
            String path = rooturl+"index.php?_action=postLike&token="+token;
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data = "&type="+type+"&image_id="+imgid;
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println("islieke"+baos.toString());
                result = new JSONObject(baos.toString()).getBoolean("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getguide() {
        String res = null;
        try {
            String path = rooturl+"index.php?_action=getGuideinfo";
            URL url = new URL(path);
            // 获得连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("GET");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                res = new JSONObject(baos.toString()).getString("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public  static JSONObject avater(File file, String token){
        JSONObject result = null;
        int res = 0;
        String path = rooturl+"index.php?_action=postAvator&token="+token;
        URL url = null;
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        try {
            url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(50000);
            urlConnection.setConnectTimeout(50000);
            urlConnection.setDoInput(true); // 允许输入流
            urlConnection.setDoOutput(true); // 允许输出流
            urlConnection.setRequestMethod("POST"); // 请求方式
            urlConnection.setRequestProperty("Charset", CHARSET); // 设置编码
            urlConnection.setRequestProperty("connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="+ BOUNDARY);
            if (file != null) {
                DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                        .getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                res = urlConnection.getResponseCode();
                if (res == 200) {
                    InputStream input = urlConnection.getInputStream();
                    StringBuffer sb1 = new StringBuffer();
                    int ss;
                    while ((ss = input.read()) != -1) {
                        sb1.append((char) ss);
                    }
                    System.out.println(sb1.toString());
                    result = new JSONObject(sb1.toString()).getJSONObject("data");
                } else {
                    result = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public  static JSONArray get_judge_image(String token){
        JSONArray result = null;
        try {
            String path = rooturl+"index.php?_action=postJudgeimage&token="+token;
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data = "&type=get_judge_image";
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                JSONObject temp = new JSONObject(baos.toString());
                if (temp.getInt("code") != 1)
                    result = temp.getJSONArray("data");
                else
                    result = new JSONArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public  static int push_juede_res(String token,int id, int res){
        int result = 0;
        try {
            String path = rooturl+"index.php?_action=postJudgeimage&token="+token;
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data = "&type=judge_image"+"&right_or_wrong="+res+"&judge_id="+id;
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                result = new JSONObject(baos.toString()).getInt("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int logout(String token){
        int result = 0;
        System.out.println(token);
        try {
            String path = rooturl+"index.php?_action=postLogout&token="+token;
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("GET");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                result = new JSONObject(baos.toString()).getInt("code");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}