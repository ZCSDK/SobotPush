package com.sobot.push.receiver;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.meizu.cloud.pushsdk.platform.message.RegisterStatus;
import com.sobot.push.utils.SPUtil;
import com.taobao.accs.utl.ALog;

import org.android.agoo.mezu.MeizuPushReceiver;

/**
 * @author: Sobot
 * 2021/10/12
 */
public class MZMessageReceiver extends MeizuPushReceiver {
    @Override
    public void onRegisterStatus(Context context, RegisterStatus registerStatus) {
        if (registerStatus != null && !TextUtils.isEmpty(registerStatus.getPushId())) {
            ALog.i("MeizuPushReceiver", "onRegister", new Object[]{"status", registerStatus.toString()});
            Log.d("==推送==", "==推送==魅族token="+registerStatus.getPushId());
            SPUtil.saveStringData(context, "deviceToken", registerStatus.getPushId());
            SPUtil.saveStringData(context, "device", "魅族");
        } else {
            ALog.e("MeizuPushReceiver", "onRegisterStatus", new Object[]{"status", registerStatus == null ? "" : registerStatus.toString()});
        }
    }
}
