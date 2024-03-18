package com.sobot.push.receiver;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.aaid.HmsInstanceId;
import com.sobot.push.utils.SPUtil;
import com.taobao.accs.common.ThreadPoolExecutorFactory;
import com.taobao.accs.utl.ALog;

import org.android.agoo.huawei.HuaWeiRegister;

/**
 * @author: Sobot
 * 获取华为token，不是通过receiver
 * 2021/10/12
 */
public class HWMessageReceiver extends HuaWeiRegister {

    public static void getToken(final Context context) {
        ThreadPoolExecutorFactory.execute(new Runnable() {
            public void run() {
                try {
                    ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                    String value = appInfo.metaData.getString("com.huawei.hms.client.appid");
                    String appId = "";
                    if (!TextUtils.isEmpty(value)) {
                        appId = value.replace("appid=", "");
                    }

                    ALog.i("HuaWeiRegister", "onToken", new Object[]{"appId", appId});
                    String token;
                    if (TextUtils.isEmpty(appId)) {
                        token = HmsInstanceId.getInstance(context).getToken();
                    } else {
                        token = HmsInstanceId.getInstance(context).getToken(appId, "HCM");
                    }
                    Log.d("==推送==", "==推送==华为token="+token);
                    SPUtil.saveStringData(context, "deviceToken", token);
                    SPUtil.saveStringData(context, "device", "华为");
                } catch (Exception var6) {
                    Log.e("HuaWeiRegister", "getToken failed.", var6);
                }
            }
        });
    }
}
