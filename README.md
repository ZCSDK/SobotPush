# Android 推送 SDK

#### 相关限制及注意事项

1、开启网络请求权限

2、appkey、包名配置正确

3、离线推送需注册华为、小米、oppo、vivo、魅族、荣耀厂商推送，需在app中添加MfrMessageActivity文件并配置到AndroidManifest中

4、隐私协议中添加友盟推送的隐私协议

#### 文档介绍

##### ● 文件说明

**推送SDK相关说明文档包含初始化、配置参数、设置别名、接收推送，点击推送消息等功能。**

| 文件名   | 说明   |备注|
|:----|:----|:----|
| SobotPushConstants | 推送参数配置 |    |
| SobotPushHelper | 推送预初始化、初始化、绑定别名、移除别名 |    |

#### 集成方式

##### ● 手动集成

将 sobot_push复制到您的项目中，并在 build.gradle 中添加项目依赖，或者File-->New-->Import Module from Source
选择解压后的sobot_push，然后 Build --> Clean Project 一下。

完成上述步骤之后 app build.gradle 中如下所示：

```js
dependencies {
      implementation project(':sobot_push')
}
```

##### ● 设置参数

需在Application中onCreate中设置appkey等参数

 ```js
//设置友盟的appkey
SobotPushConstants.APP_KEY="填写友盟上申请的Appkey";
SobotPushConstants.MESSAGE_SECRET="填写友盟申请的UmengMessageSecret";
SobotPushConstants.CHANNEL="渠道名称，修改为您App的发布渠道名称";
SobotPushConstants.OPPO_KEY="填写您在OPPO后台APP对应的app key";
SobotPushConstants.OPPO_SECRET="填写您在OPPO后台APP对应的app secret";
SobotPushConstants.MI_ID="填写您在小米后台APP对应的xiaomi id";
SobotPushConstants.MI_KEY="填写您在小米后台APP对应的xiaomi key";
SobotPushConstants.MEI_ZU_ID="填写您在魅族后台APP对应的app id";
SobotPushConstants.MEI_ZU_KEY="填写您在魅族后台APP对应的app key";
 ```

##### ● 隐私协议

需在app隐私协议中添加友盟的隐私协议，隐私权政策链接：https://www.umeng.com/page/policy
,也可参考[友盟合规指南](https://developer.umeng.com/docs/67966/detail/207155)

##### ● 初始化

初始化推送方法，在 Application 中的 onCreate 方法中添加 initUmeng 代码：

```
private void initUmeng() {
        //设置友盟的appkey
        SobotPushConstants.APP_KEY="填写友盟上申请的Appkey";
        SobotPushConstants.MESSAGE_SECRET="填写友盟申请的UmengMessageSecret";
        SobotPushConstants.CHANNEL="渠道名称，修改为您App的发布渠道名称";
        SobotPushConstants.OPPO_KEY="填写您在OPPO后台APP对应的app key";
        SobotPushConstants.OPPO_SECRET="填写您在OPPO后台APP对应的app secret";
        SobotPushConstants.MI_ID="填写您在小米后台APP对应的xiaomi id";
        SobotPushConstants.MI_KEY="填写您在小米后台APP对应的xiaomi key";
        SobotPushConstants.MEI_ZU_ID="填写您在魅族后台APP对应的app id";
        SobotPushConstants.MEI_ZU_KEY="填写您在魅族后台APP对应的app key";

        //预初始化
        SobotPushHelper.preInit(this);
        //是否同意隐私协议,可替换成自己的
//        boolean isAgreementPrivacy = SharedPreferencesUtil.getBooleanData(instance, ConstantUtils.SOBOT_PRIVACY_AGREEMENT, false);
//        if (!isAgreementPrivacy) {
//            return;
//        }
        boolean isMainProcess = UMUtils.isMainProgress(this);
        if (isMainProcess) {
            //App启动速度优化：可以在子线程中调用SDK初始化接口
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SobotPushHelper.initUpush(getApplicationContext());
                }
            }).start();
        }
    }
```

##### ● 设置别名

别名绑定，将某一类型的别名ID绑定至某设备，别名ID和deviceToken是一对一的映射关系，绑定成功后，老的绑定设备信息被覆盖。

```js

 //设置别名
 SobotPushHelper.bindAlias(MainActivity.this, alias, aliasType, new SobotPushCallBack() {
       @Override
       public void onResultBack(boolean isSuccess, String message) {
            LogUtils.d("==addAlias==isSuccess=" + isSuccess + "====message=" + message);
       }
});
    
```

##### ● 移除别名

可根据情况是否调用移除别名

```js
SobotPushHelper.unBindAlias(MainActivity.this, alias, aliasType, new SobotPushCallBack() {
        @Override
        public void onResultBack(boolean isSuccess, String message) {
             LogUtils.d("==deleteAlias==isSuccess=" + isSuccess + "====message=" + message);
          }
 });
    
```

##### ● 点击推送消息事件

点击推送消息跳转App指定页面或者打开链接

```
 /**
     * 推送消息的点击事件
     */
    private void setMsgClick(){
        SobotPushHelper.setMsgClickListener(new SobotMsgClick() {
            @Override
            public void msgClick(UMessage msg) {
                final String body = msg.getRaw().toString();
                LogUtils.d("body: " + body);
                if (!TextUtils.isEmpty(body)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) findViewById(R.id.tv)).setText(body);
                        }
                    });
                    try {
                        JSONObject obj = new JSONObject(body);
                        if(obj.has("after_open")){
                            String after_open = obj.optString("after_open","");
                            if(after_open.equals("go_app")) {
                                Intent intentclause = new Intent(MainActivity.this, MainActivity.class);
                                intentclause.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intentclause);
                            } else if(after_open.equals("go_url")){
                                String url = obj.optString("url","");
                                Intent intentclause = new Intent(MainActivity.this, WebViewActivity.class);
                                intentclause.putExtra("url", url);
                                intentclause.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intentclause);
                            } else if(after_open.equals("go_activity")){
                                //协议好的activity
                                String activity = obj.optString("activity","");
                                if(activity.equals("com.sobot.custom.activity.LoginActivity")){
//                                Intent intentclause = new Intent(MfrMessageActivity.this, LoginActivity.class);
//                                intentclause.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intentclause);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });
    }
```

添加厂商推送，离线消息，需将demo中的 MfrMessageActivity 复制到 APP 中，并在AndroidManifest.xml添加MfrMessageActivity。