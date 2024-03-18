package com.sobot.push.receiver;

import android.content.Context;
import android.util.Log;

import com.heytap.msp.push.HeytapPushManager;
import com.heytap.msp.push.callback.ICallBackResultService;
import com.sobot.push.utils.SPUtil;
import com.taobao.accs.utl.ALog;
import com.taobao.accs.utl.UtilityImpl;
import com.taobao.agoo.BaseNotifyClickActivity;

import org.android.agoo.oppo.OppoMsgParseImpl;
import org.android.agoo.oppo.OppoRegister;

/**
 * @author: Sobot
 * 2021/10/12
 */
public class OppoMessageReceiver extends OppoRegister {
    public static void register(final Context context, String appKey, String appSecret) {
        try {
            if (!UtilityImpl.isMainProcess(context)) {
                ALog.i("OppoPush", "not in main process, return", new Object[0]);
                return;
            }

            HeytapPushManager.init(context, (context.getApplicationInfo().flags & 2) != 0);
            if (HeytapPushManager.isSupportPush(context)) {
                BaseNotifyClickActivity.addNotifyListener(new OppoMsgParseImpl());
                ALog.i("OppoPush", "register oppo begin ", new Object[0]);
                HeytapPushManager.register(context, appKey, appSecret, new ICallBackResultService() {
                    public void onRegister(int i, String s) {
                        Log.d("==推送==","==推送=oppoToken="+s);
                        SPUtil.saveStringData(context, "deviceToken", s);
                        SPUtil.saveStringData(context, "device", "OPPO");
                        ALog.i("OppoPush", "onRegister regid=" + s, new Object[0]);
                    }

                    public void onUnRegister(int i) {
                        ALog.e("OppoPush", "onUnRegister code=" + i, new Object[0]);
                    }

                    public void onSetPushTime(int i, String s) {
                        ALog.i("OppoPush", "onSetPushTime", new Object[0]);
                    }

                    public void onGetPushStatus(int i, int i1) {
                        ALog.i("OppoPush", "onGetPushStatus", new Object[0]);
                    }

                    public void onGetNotificationStatus(int i, int i1) {
                        ALog.i("OppoPush", "onGetNotificationStatus", new Object[0]);
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            } else {
                ALog.i("OppoPush", "not support oppo push", new Object[0]);
            }
        } catch (Throwable var4) {
            ALog.e("OppoPush", "register error", var4, new Object[0]);
        }

    }

}
