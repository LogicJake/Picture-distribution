# 更新日志
**2017-08-07**
- 更新：历史记录显示积分变化

**2017-07-25**
- 更新：新增教程引导功能

**2017-07-10**
- 更新：获取图片的返回值  

**2017-07-07**
- 修复：解决头像过大，加载时阻塞UI的问题
- 修复：解决加载启动图时的短暂黑屏问题
- 更新：加载图片时显示进度
- 更新：修改app图标
- 更新：修改任务完成时的文案

[toc]
# 一、实现功能
## 1. 基本功能
### 1.1 登陆
登陆时可以通过==SharedPreferences==在本地记住密码，实现下次自动登陆。密码MD5加密。
### 1.2 注册
密码必须由字母和数字同时组成，否则报错。密码MD5加密。
### 1.3 设置密保问题
本应用只能通过密保问题来修改密码，所以注册成功后会跳转到设置密码界面，必须设置三个问题。
### 1.4 修改密码 and 忘记密码
用户可以修改密码，回答正确当初设置的三个密保问题后，将跳转到修改密码界面进行修改；登陆时如果忘记密码，可以点击忘记密码按钮，输入要修改的用户名后，进行前面修改密码操作。密码MD5加密。
### 1.5 查看个人信息
个人信息包括：头像，用户名，UID，手机号，QQ，性别，积分，个性签名，兴趣。  
### 1.6 修改个人信息
用户可以修改头像（大小限制为1M），手机号，QQ，性别，个性签名和兴趣。在没有设置兴趣之前将无法进行答题操作。
### 1.7 使用指南
查看使用指南，获取操作提示
### 1.8 标注记录 and 修改标签
可以查看用户以前的标注记录，包括图片内容，标注时间，标注标签。在图片被管理员设置为完成之前可以修改标签。
## 2. 图片标签化
### 2.1 打标签
点击“打标签”按钮后，app向后台申请一组任务。任务加载完毕后，页面显示待标注图片，任务进度（已完成数量/总图片数量），爱心（喜欢就点亮，以后将多推送此类图片），输入框（输入标签内容，以空格隔开），确认标注按钮（提交标注内容），不喜欢按钮（跳过这张图片，以后将减少此类图片）。
### 2.2 判定标签
用户除了自己打标签之外，还可以判定别人的标签是否合理正确。点击“判断标签”按钮后，app向后台申请一组任务。任务加载完毕后，显示任务进度（已完成数量/总数量），图片，待判断标签，正确按钮（标注正确），错误按钮（标注错误），不确定按钮（无法确认）。
# 二、文件内容说明
## 1. JAVA文件

类名 | 说明
---|---
CheckID | 回答密保问题页面相关逻辑
Data | 自定义数据类型，存放图片的相关信息
Favourite | 兴趣页面相关逻辑
Guide | 用户指南页面相关逻辑
History | 历史记录页面相关逻辑
Login  | 登陆页面相关逻辑
Mainactivity|主界面相关逻辑
MyAdapter|历史纪录ListView的Adapter
NewService|相关接口
Personinformation|个人信息页面的相关逻辑
ResetPssword|重置密码页面的相关逻辑
SecurityQuestion|设置密保问题页面的相关逻辑
SelectPicPopupWindow|点击头像页面下端的选择窗口的相关逻辑
Sign_up|注册页面的相关逻辑
Startpage|启动页页面的相关逻辑
Task|打标注页面的相关逻辑
Task2|判断标签页面的相关逻辑
UILImageLoader|galleryfinal开源项目需要的图片展示类

## 2. 布局文件（XML文件）

XML名 | 说明
---|---
activity_check_id |回答密保问题页面
activity_favourite|兴趣页面
activity_guide|用户指南页面
activity_history|历史记录页面
activity_login|登陆页
activity_main|主页面（app_bar_main+nav_view）
activity_personinformation|个人信息页面
activity_reset_password|重置密码页面
activity_security_question|设置密保问题页面
activity_sign_up|注册页面
activity_startpage|启动页
activity_task|打标签页面
activity_task2|判断标签页面
app_bar_main|主页面的app_bar_main部分
content_main|主页面实际内容
dialog_photo_display|AlertDialog中的图片显示
list_item_proj_plan|历史记录的list_item
nav_header_main|主页面抽屉页面的header内容
popwindow|下端弹出页面
progress|历史记录每一页的分界线
proj_plans_header|历史记录listview的头界面

# 三、API汇总
urlroot = 网址/项目名称/。如：http://www.******.com/image-android/
## 1. 注册接口（POST方法）
### 1.1 接口地址
urlroot+index.php?_action=postSignup
### 1.2 传入数据
&user_name=user_name&user_passwd=user_passwd  
user_name:用户输入的用户名（中文需转换成UTF-8格式）  
user_passwd:用户输入的密码
### 1.3 返回形式
```
{
    "code": 0,
    "data": {
        "result": {
            "status": 1,
            "token": "*******"
        }
    }
}
```
status：1：成功；-1：失败 2：已存在该用户名  
token：用户的唯一标签
## 2. 登陆接口（POST方式）
### 2.1 接口地址
urlroot+index.php?_action=postLogin
### 2.2 传入数据
&user_name=user_name&user_passwd=user_passwd  
user_name:用户输入的用户名（中文需转换成UTF-8格式）   
user_passwd:用户输入的密码
### 2.3 返回形式
```
{
    "code": 0,
    "data": {
        "result": {
            "status": 1,
            "id": "9",
            "user_name": "admin",
            "has_complete": "1",
            "avator_url":"upload/avator/*******",
            "is_admin": "1",
            "token": "*******"
        }
    }
}
```
status：1：成功；0：密码错误；2：不存在该用户名  
id: 用户的UID  
has_complete: 是否完成兴趣填写  
avator_url: 头像地址  
is_admin: 是否为管理员账户  
token: 最新的token
## 3. 获取个人信息（POST方法）
### 3.1 接口地址
urlroot+index.php?_action=getInfo
### 3.2 传入数据
&token=token&type=get_info  
token：登陆时获取的token值
### 3.3 返回形式
```
{
    "code": 0,
    "data": {
        "result": {
            "id": "2",
            "user_id": "9",
            "phone_num": "**********",
            "qq_num": "************",
            "user_sign": "************",
            "score": "0",
            "work_done": "0",
            "task_num": "0",
            "sex": "0",
            "interest": "1 2 5 6 9 ",
            "avator_url":"upload/avator/************"
        }
    }
}
```
user_id：用户UID  
phone_num：手机号  
qq_num：QQ号  
user_sign：个性签名  
score：积分  
sex：0：男 1：女 -1：未设置  
interest：数字代表兴趣领域编号  
avator_url：头像地址
## 4. 修改个人信息（POST方法）
### 4.1 接口地址
urlroot+index.php?_action=getInfo
### 4.2 传入数据
修改性别：&token=token&type=update_sex&&sex=key  
修改qq号：&token=token&type=update_qq_num&&qq_num=key  
修改手机号：&token=token&type=update_phone_num&&&phone_num=key  
修改个性签名：&token=token&type=update_user_sign&user_sign=key  
key：需要修改的值
### 4.3 返回形式
```
{
    "code": 0,
    "data": {
        "result": {
            "status": 1,
            "key": *******
        }
    }
}
```
status:1：成功；0：失败  
key:修改后的值
## 5. 获取历史标记记录（POST方法）
### 5.1 接口地址
rooturl+index.php?_action=postHistory
### 5.2 传入数据
&token=token&page=page  
page：int值，从0开始递增
### 5.3 返回形式
```
{
    "code": 0,
    "data": {
        "result": {
            "count": 10,
            "data": [
                {
                    "id": "34",
                    "user_id": "9",
                    "image_id": "34",
                    "tag": "表情包",
                    "add_time": "1498222177",
                    "status": "1",
                    "update_time": "1498437139",
                    "like_or_not": "1",
                    "url": "upload/2cc28f6720c0f14401deb0341acc0b9d78ddfaedab64034f31c3879ba6c379310b551dc2.jpg",
                    "time": "1496485038",
                    "tag_num": "1"
                },
                {
                    "id": "32",
                    "user_id": "9",
                    "image_id": "32",
                    "tag": "表情包",
                    "add_time": "1498222176",
                    "status": "1",
                    "update_time": "1498437130",
                    "like_or_not": "1",
                    "url": "upload/2cc28f6720c0f14401deb0341acc0b9d45857e115f0662a9.jpg",
                    "time": "1496485038",
                    "tag_num": "1"
                },
                {
                    "id": "55",
                    "user_id": "9",
                    "image_id": "55",
                    "tag": "诗词",
                    "add_time": "1498218910",
                    "status": "1",
                    "update_time": "1498218910",
                    "like_or_not": "1",
                    "url": "upload/c11e55a9f8bb10680df19aee29e3553ammexport1491638988044.jpg",
                    "time": "1496657417",
                    "tag_num": "2"
                },
                {
                    "id": "56",
                    "user_id": "9",
                    "image_id": "56",
                    "tag": "校服",
                    "add_time": "1498218901",
                    "status": "1",
                    "update_time": "1498218901",
                    "like_or_not": "1",
                    "url": "upload/bb265867407f7d8a04b805e8e171db1a20170605180710.jpg",
                    "time": "1496657433",
                    "tag_num": "2"
                },
                {
                    "id": "57",
                    "user_id": "9",
                    "image_id": "57",
                    "tag": "眼镜",
                    "add_time": "1498218889",
                    "status": "1",
                    "update_time": "1498218889",
                    "like_or_not": "1",
                    "url": "upload/bdfc620aaac1207ebabdff6d8ad8778e20170605180735.jpg",
                    "time": "1496657533",
                    "tag_num": "2"
                },
                {
                    "id": "58",
                    "user_id": "9",
                    "image_id": "58",
                    "tag": "学生",
                    "add_time": "1498218866",
                    "status": "1",
                    "update_time": "1498218866",
                    "like_or_not": "1",
                    "url": "upload/5c285ebeb99c36ba4eb9a59650301c4d20170605180735.jpg",
                    "time": "1496657534",
                    "tag_num": "2"
                },
                {
                    "id": "59",
                    "user_id": "9",
                    "image_id": "59",
                    "tag": "学生",
                    "add_time": "1498218857",
                    "status": "1",
                    "update_time": "1498218857",
                    "like_or_not": "1",
                    "url": "upload/37cb05ff29f43cade23d9b3663a9530e1.jpg",
                    "time": "1496657572",
                    "tag_num": "2"
                },
                {
                    "id": "60",
                    "user_id": "9",
                    "image_id": "60",
                    "tag": "高铁",
                    "add_time": "1498218843",
                    "status": "1",
                    "update_time": "1498218843",
                    "like_or_not": "1",
                    "url": "upload/6d48ad8c9a1fcff91c40c4dd52e4fc611.jpg",
                    "time": "1496657889",
                    "tag_num": "2"
                },
                {
                    "id": "61",
                    "user_id": "9",
                    "image_id": "61",
                    "tag": "金发",
                    "add_time": "1498218833",
                    "status": "1",
                    "update_time": "1498218833",
                    "like_or_not": "1",
                    "url": "upload/2fbbba9ae192457d5e35723b4dcc25a1109951162945018213.jpg",
                    "time": "1498216637",
                    "tag_num": "2"
                },
                {
                    "id": "63",
                    "user_id": "9",
                    "image_id": "63",
                    "tag": "苹果",
                    "add_time": "1498218796",
                    "status": "1",
                    "update_time": "1498218796",
                    "like_or_not": "1",
                    "url": "upload/e3d9e11e0a4093b7c81ea4d308ba822f109951162945018213.jpg",
                    "time": "1498216684",
                    "tag_num": "2"
                }
            ],
            "done": 0
        }
    }
}
```
image_id:图片的标识符  
tag:标注的tag  
add_time:记录添加时间  
status:1:可以被修改 0：不可以被修改  
update_time:标记被修改时间  
like_or_not:是否被标注为喜欢  
url:图像地址  
done:是否还有下一页
## 6. 获取标注任务（POST方式）
### 6.1 接口地址
rooturl+index.php?_action=getImage
### 6.2 传入数据
&token=+token
### 6.3 返回形式
```
{
    "code": 0,
    "data": [
        {
            "url": "upload/2cc28f6720c0f14401deb0341acc0b9d7A2BE29FA3E8.jpg",
            "id": "30",
            "time": "1496485038"
        },
        {
            "url": "upload/2cc28f6720c0f14401deb0341acc0b9d7C701E098E1D.jpg",
            "id": "31",
            "time": "1496485038"
        },
        {
            "url": "upload/2cc28f6720c0f14401deb0341acc0b9d45857e115f0662a9.jpg",
            "id": "32",
            "time": "1496485038"
        },
        {
            "url": "upload/2cc28f6720c0f14401deb0341acc0b9d395d03087bf40ad11aea827e5e2c11dfa8eccec3.jpg",
            "id": "33",
            "time": "1496485038"
        },
        {
            "url": "upload/2cc28f6720c0f14401deb0341acc0b9d78ddfaedab64034f31c3879ba6c379310b551dc2.jpg",
            "id": "34",
            "time": "1496485038"
        }
    ]
}
```
url：图片地址  
id：图片id  
## 7. 提交标签（POST）
### 7.1 接口地址
rooturl+index.php?_action=postSaveTag
### 7.2 传入数据
&token=token&image_id=image_id&tag=tag  
image_id:获取任务里传来的id
tag：标签内容
### 7.3 返回形式

```
{
    "code":0,
    "data":true
    
}
```
data:true:成功 false:失败
## 8. 获取所有兴趣项（POST）
### 8.1 接口地址
rooturl+index.php?_action=getAllInterest
### 8.2 传入数据
&token=token
### 8.3 返回形式

```
{
    "code": 0,
    "data": [
        {
            "id": "1",
            "interest": "风景",
            "status": 1
        },
        {
            "id": "2",
            "interest": "植物",
            "status": 1
        },
        {
            "id": "3",
            "interest": "人文",
            "status": 0
        },
    ]
}
```
id：兴趣的id  
interest：兴趣内容  
status：1：用户感兴趣 0：用户不感兴趣
## 9. 更新兴趣
### 9.1 接口地址
rooturl+index.php?_action=getInfo
### 9.2 传入数据
添加兴趣：&type=add_interest&token=token&interest=key  
删除兴趣：&type=delete_interest&token=token&interest=key  
key的值由兴趣id组成，如添加兴趣时key="1 2 3"，说明用户新增id为1，2，3的兴趣
### 9.3 返回形式

```
{
    "code": 0,
    "data": {
        "result": true
    }
}
```
result：true:成功 false:失败
## 10. 获取所有密保问题（POST）
### 10.1 接口地址
rooturl+index.php?_action=postSecure
### 10.2 传入数据
&token=token&type=getAllquestion
### 10.3 返回形式

```
{
    "data": [
        {
            "id": "1",
            "question": "你爸爸是谁"
        },
        {
            "id": "2",
            "question": "你妈妈是谁"
        },
        {
            "id": "3",
            "question": "你是家乡是"
        },
    ],
    "code": 0
}
```
id：问题的id  
question：问题内容
## 11. 用户添加密保问题（POST）
### 11.1 接口地址
rooturl+index.php?_action=postSecure
### 11.2 传入数据
&token=token&type=addUserquestion&q_1=q_1&q_2=q_2&q_3=q_3&a_1=a_1&a_2=a_2&a_3=a_3  
q_1:问题一内容  
a_1:问题一的答案
### 11.3 返回形式

```
{
    "code":0,
    "data":1
    
}
```
data:1:成功 0:失败
## 12. 获取用户的密保问题（POST）
### 12.1 接口地址
rooturl+index.php?_action=postSecure
### 12.2 传入数据
&token=token&type=getUserquestion
### 12.3 返回形式

```
{
    "code": 0,
    "data": {
        "q_1": "你爸爸是谁",
        "q_2": "你妈妈是谁",
        "q_3": "你的家乡在哪儿"
    }
}
```
## 13. 检查密保答案是否正确（POST）
### 13.1 接口地址
rooturl+index.php?_action=postSecure
### 13.2 传入数据
&type=checkUserquestion&token=token&a_1=a_1&a_2=a_2&a_3=a_3  
按照题目顺序将答案返回
### 13.3 返回形式

```
{
    "code": 0,
    "data": {
        "result": 0
    }
}
```
result：0：失败 1：成功
## 14. 修改密码（POST）
### 14.1 接口地址
rooturl+index.php?_action=postChangepasswd
### 14.2 传入数据
&token=token&new_passwd=newpass  
new_passwd:新密码
### 14.3 返回形式

```
{
    "code": 0,
    "data": {
        "result": true
    }
}
```
result：true:成功 false:失败
## 15. 图片的喜爱程度（POST）
### 15.1 接口地址
rooturl+index.php?_action=postLike
### 15.2 传入数据
&token=token&type=type&image_id=imgid  
type: "like":喜欢 "dislike":不喜欢
### 15.3 返回形式
```
{
    "code": 0,
    "data": true
}
```
data:true:成功 false:失败
## 16. 获取用户指南（GET）
### 16.1 接口地址
rooturl+index.php?_action=getGuideinfo
### 16.2 返回形式

```
{
    "code": 0,
    "data": "guide"
}
```
data：指南内容
## 17. 获取判断标签任务（POST）
### 17.1 接口地址
rooturl+index.php?_action=postJudgeimage
### 17.2 传入数据
&type=get_judge_image&token=token
### 17.3 返回形式

```
{
    "code": 0,
    "data": [
        {
            "judge_id": "106",
            "image_url": "upload/bb265867407f7d8a04b805e8e171db1a20170605180710.jpg",
            "tag": "12"
        },
        {
            "judge_id": "107",
            "image_url": "upload/c11e55a9f8bb10680df19aee29e3553ammexport1491638988044.jpg",
            "tag": "21"
        },
        {
            "judge_id": "108",
            "image_url": "upload/0d364c61abbb0d6849496095a89e512f1474083995537.jpg",
            "tag": "31"
        },
    ]
}
```
judge_id：任务项id  
image_url:图片地址  
tag：需要判断的标签  
## 18. 提交判断标签任务（POST）
### 18.1 接口地址
rooturl+index.php?_action=postJudgeimage
### 18.2 传入数据
&type=judge_image&right_or_wrong=res&judge_id=id&token=token  
res: 0:不正确 1:不确定 2:正确
### 18.3 返回形式

```
{
    "code": 0,
    "data": 1
}
```
data：1：成功 0：失败













