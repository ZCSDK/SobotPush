package com.sobot.custom;

import android.app.Application;

import com.sobot.push.SobotPushConstants;
import com.sobot.push.SobotPushHelper;
import com.sobot.custom.util.ConstantUtils;
import com.sobot.custom.util.SharedPreferencesUtil;
import com.umeng.commonsdk.utils.UMUtils;

public class MyApplication extends Application {
    /**
     * 应用实例
     **/
    private static MyApplication instance;
    /**
     * 获得实例
     *
     * @return MyApplication
     */
    public static MyApplication getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initUmeng();
    }
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

}

