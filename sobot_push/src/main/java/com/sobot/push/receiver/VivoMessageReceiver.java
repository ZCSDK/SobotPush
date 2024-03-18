package com.sobot.push.receiver;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sobot.push.utils.SPUtil;
import com.taobao.accs.utl.ALog;
import com.taobao.accs.utl.UtilityImpl;
import com.taobao.agoo.BaseNotifyClickActivity;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;

import org.android.agoo.vivo.VivoMsgParseImpl;
import org.android.agoo.vivo.VivoRegister;

/**
 * @author: Sobot
 * 2021/10/12
 */
public class VivoMessageReceiver extends VivoRegister {
    public static void register(final Context context) {
        try {
            if (context == null) {
                return;
            }
            if (!UtilityImpl.isMainProcess(context)) {
                ALog.i("VivoRegister", "not in main process, return", new Object[0]);
                return;
            }
            if (PushClient.getInstance(context).isSupport()) {
                ALog.d("VivoRegister", "register start", new Object[0]);
                BaseNotifyClickActivity.addNotifyListener(new VivoMsgParseImpl());
                PushClient.getInstance(context).initialize();
                PushClient.getInstance(context).turnOnPush(new IPushActionListener() {
                    public void onStateChanged(int state) {
                        ALog.d("VivoRegister", "turnOnPush", new Object[]{"state", state});
                        if (state == 0) {
                            String regId = PushClient.getInstance(context).getRegId();
                            if (!TextUtils.isEmpty(regId)) {
                                ALog.d("==推送==","==VivoToken=="+regId);
                                SPUtil.saveStringData(context, "deviceToken", regId);
                                SPUtil.saveStringData(context, "device", "VIVO");
                                Log.d("==推送==", "==推送==VivoToken="+regId);
                            }
                        }

                    }
                });
            } else {
                ALog.e("VivoRegister", "this device is not support vivo push", new Object[0]);
            }
        } catch (Throwable var2) {
            ALog.e("VivoRegister", "register", var2, new Object[0]);
        }

    }
}
