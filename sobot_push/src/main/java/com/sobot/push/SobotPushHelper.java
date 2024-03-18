package com.sobot.push;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.sobot.push.receiver.HWMessageReceiver;
import com.sobot.push.receiver.OppoMessageReceiver;
import com.sobot.push.receiver.VivoMessageReceiver;
import com.sobot.push.utils.LogUtils;
import com.sobot.push.utils.SPUtil;
import com.sobot.push.utils.SobotPushCallBack;
import com.taobao.accs.ACCSClient;
import com.taobao.accs.AccsClientConfig;
import com.taobao.agoo.TaobaoRegister;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.utils.UMUtils;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.api.UPushAliasCallback;
import com.umeng.message.api.UPushRegisterCallback;
import com.umeng.message.entity.UMessage;

import org.android.agoo.fcm.FCMRegister;
import org.android.agoo.honor.HonorRegister;
import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.vivo.VivoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

/**
 * PushSDK集成帮助类
 */
public class SobotPushHelper {

    private static final String TAG = SobotPushHelper.class.getSimpleName();
    public static SobotMsgClick sobotMsgClick ;

    /**
     * 预初始化，已添加子进程中初始化sdk。
     * 使用场景：用户未同意隐私政策协议授权时，延迟初始化
     *
     * @param context 应用上下文
     */
    public static void preInit(Context context) {
        UMConfigure.preInit(context, SobotPushConstants.APP_KEY, SobotPushConstants.CHANNEL);
    }

    public static void initUpush(Context context) {
        // 在此处调用基础组件包提供的初始化函数 相应信息可在应用管理 -> 应用信息 中找到 http://message.umeng.com/list/apps
        // 参数一：当前上下文context；
        // 参数二：应用申请的Appkey；
        // 参数三：渠道名称；
        // 参数四：设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机；
        // 参数五：Push推送业务的secret 填充Umeng Message Secret对应信息
        UMConfigure.init(
                context,
                SobotPushConstants.APP_KEY,
                SobotPushConstants.CHANNEL,
                UMConfigure.DEVICE_TYPE_PHONE,
                SobotPushConstants.MESSAGE_SECRET
        );
        //友盟日志输出开关
        UMConfigure.setLogEnabled(true);
        //获取消息推送实例
        final PushAgent pushAgent = PushAgent.getInstance(context);

        pushAdvancedFunction(context);

        //注册推送服务，每次调用register方法都会回调该接口
        pushAgent.register(new UPushRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                SPUtil.saveStringData(context, "umToken", deviceToken);
                LogUtils.i("友盟注册成功 deviceToken: " + deviceToken);
            }

            @Override
            public void onFailure(String errCode, String errDesc) {
                LogUtils.i("友盟注册失败：--> " + "code:" + errCode + ",desc:" + errDesc);
            }
        });
        registerDeviceChannel(context);
        try {
            //解决推送消息显示乱码的问题
            AccsClientConfig.Builder builder = new AccsClientConfig.Builder();
            builder.setAppKey("umeng:" + SobotPushConstants.APP_KEY);
            builder.setAppSecret(SobotPushConstants.MESSAGE_SECRET);
            builder.setTag(AccsClientConfig.DEFAULT_CONFIGTAG);
            ACCSClient.init(context, builder.build());
            TaobaoRegister.setAccsConfigTag(context, AccsClientConfig.DEFAULT_CONFIGTAG);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //友盟开发渠道 调试测试使用
        init(context);
        // 选用LEGACY_AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_AUTO);
        // 支持在子进程中统计自定义事件
        UMConfigure.setProcessEvent(true);
        //如果是debug模式那么不统计异常   注释打开就是关闭异常统计
        MobclickAgent.setCatchUncaughtExceptions(false);
    }

    /**
     * 初始化。
     * 场景：用户已同意隐私政策协议授权时
     *
     * @param context 应用上下文
     */
    public static void init(Context context) {
        //华为token
        LogUtils.d("==推送==厂商注册");
        //华为token
        HWMessageReceiver.getToken(context);
        //oppo token
        OppoMessageReceiver.register(context, SobotPushConstants.OPPO_KEY, SobotPushConstants.OPPO_SECRET);
        //vivo token
        VivoMessageReceiver.register(context);
    }

    /**
     * 注册设备推送通道（小米、华为等设备的推送）
     *
     * @param context 应用上下文
     */
    private static void registerDeviceChannel(Context context) {
        //小米通道，填写您在小米后台APP对应的xiaomi id和key
        MiPushRegistar.register(context, SobotPushConstants.MI_ID, SobotPushConstants.MI_KEY);
        //华为，注意华为通道的初始化参数在minifest中配置
        HuaWeiRegister.register((Application) context.getApplicationContext());
        //魅族，填写您在魅族后台APP对应的app id和key
        MeizuRegister.register(context, SobotPushConstants.MEI_ZU_ID, SobotPushConstants.MEI_ZU_KEY);
        //OPPO，填写您在OPPO后台APP对应的app key和secret
        OppoRegister.register(context, SobotPushConstants.OPPO_KEY, SobotPushConstants.OPPO_SECRET);
        //vivo，注意vivo通道的初始化参数在minifest中配置
        VivoRegister.register(context);
        //荣耀
        HonorRegister.register(context);
        //FCM
        FCMRegister.register(context);
    }

    /**
     * 是否运行在主进程
     *
     * @param context 应用上下文
     * @return true: 主进程；false: 子进程
     */
    public static boolean isMainProcess(Context context) {
        return UMUtils.isMainProgress(context);
    }

    //推送高级功能集成说明
    private static void pushAdvancedFunction(Context context) {
        PushAgent pushAgent = PushAgent.getInstance(context);

        //设置通知栏显示通知的最大个数（0～10），0：不限制个数
        pushAgent.setDisplayNotificationNumber(5);

        //推送消息处理
        UmengMessageHandler msgHandler = new UmengMessageHandler() {
            //处理通知栏消息
            @Override
            public void dealWithNotificationMessage(Context context, UMessage msg) {
                super.dealWithNotificationMessage(context, msg);
                Log.i(TAG, "notification receiver:" + msg.getRaw().toString());
            }

            //处理透传消息
            @Override
            public void dealWithCustomMessage(Context context, UMessage msg) {
                super.dealWithCustomMessage(context, msg);
                Log.i(TAG, "custom receiver:" + msg.getRaw().toString());
            }
        };
        pushAgent.setMessageHandler(msgHandler);

        //推送消息点击处理
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void openActivity(Context context, UMessage msg) {
                super.openActivity(context, msg);
                if(sobotMsgClick !=null) {
                    sobotMsgClick.msgClick(msg);
                }
                Log.i(TAG, "click openActivity: " + msg.getRaw().toString());
            }

            @Override
            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
                if(sobotMsgClick !=null) {
                    sobotMsgClick.msgClick(msg);
                }
                Log.i(TAG, "click launchApp: " + msg.getRaw().toString());
            }

            @Override
            public void dismissNotification(Context context, UMessage msg) {
                super.dismissNotification(context, msg);
                if(sobotMsgClick !=null) {
                    sobotMsgClick.msgClick(msg);
                }
                Log.i(TAG, "click dismissNotification: " + msg.getRaw().toString());
            }
        };
        pushAgent.setNotificationClickHandler(notificationClickHandler);
    }
    public static void setMsgClickListener(SobotMsgClick msgClickListener){
        sobotMsgClick = msgClickListener;
    }

    /**
     * 设置别名
     * 别名绑定，将某一类型的别名ID绑定至某设备，老的绑定设备信息被覆盖，别名ID和deviceToken是一对一的映射关系
     * @param context   上下文
     * @param alias     别名 长度限制分别为128，64个字符,仅支持半角大小写字母，数字，下划线
     * @param aliasType 别名类型 默认单个Appkey下同时生效的alias_type数最多10个,长度限制分别为128，64个字符,仅支持半角大小写字母，数字，下划线
     * @param callBack 返回结果
     */
    public static void bindAlias(Context context, String alias, String aliasType, SobotPushCallBack callBack) {
        PushAgent.getInstance(context).setAlias(alias, aliasType, new UPushAliasCallback() {
            @Override
            public void onMessage(boolean isSuccess, String message) {
                if (callBack != null) {
                    callBack.onResultBack(isSuccess, message);
                }
            }
        });
    }

    /**
     * 移除别名
     * @param context   上下文
     * @param alias     别名 长度限制分别为128，64个字符,仅支持半角大小写字母，数字，下划线
     * @param aliasType 别名类型 默认单个Appkey下同时生效的alias_type数最多10个,长度限制分别为128，64个字符,仅支持半角大小写字母，数字，下划线
     * @param callBack 返回结果
     */
    public static void unBindAlias(Context context, String alias, String aliasType, SobotPushCallBack callBack) {
        PushAgent.getInstance(context).deleteAlias(alias, aliasType, new UPushAliasCallback() {
            @Override
            public void onMessage(boolean isSuccess, String message) {
                if (callBack != null) {
                    callBack.onResultBack(isSuccess, message);
                }
            }
        });
    }
}
