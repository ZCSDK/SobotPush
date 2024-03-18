package com.sobot.push.receiver;

import android.content.Context;
import android.util.Log;

import com.sobot.push.utils.SPUtil;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;

import org.android.agoo.xiaomi.MiPushBroadcastReceiver;

import java.util.List;

/**
 * @author: Sobot
 * 2021/10/12
 */
public class MiMessageReceiver extends MiPushBroadcastReceiver {
    public MiMessageReceiver(){
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        super.onReceiveRegisterResult(context,message);
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = arguments != null && arguments.size() > 0 ? (String)arguments.get(0) : null;

        String regId = null;
        if ("register".equals(command) && message.getResultCode() == 0L) {
            regId = cmdArg1;
        }

        Log.d("==推送==", "==推送==小米token="+regId);
        SPUtil.saveStringData(context, "deviceToken", regId);
        SPUtil.saveStringData(context, "device", "小米");
    }

}
