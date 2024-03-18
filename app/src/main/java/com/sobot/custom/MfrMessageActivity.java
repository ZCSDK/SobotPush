package com.sobot.custom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.sobot.push.SobotPushHelper;
import com.umeng.message.UmengNotifyClick;
import com.umeng.message.entity.UMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 杀进程--收到消息处理
 */
public class MfrMessageActivity extends Activity {
    private static final String TAG = "MfrMessageActivity";

    private final UmengNotifyClick mNotificationClick = new UmengNotifyClick() {
        @Override
        public void onMessage(UMessage msg) {
            final String body = msg.getRaw().toString();
            Log.d(TAG, "body: " + body);
            if (!TextUtils.isEmpty(body)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.tv)).setText(body);
                    }
                });
                if(SobotPushHelper.sobotMsgClick !=null) {
                    SobotPushHelper.sobotMsgClick.msgClick(msg);
                }

            }

        }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.mfr_message_layout);
        mNotificationClick.onCreate(this, getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mNotificationClick.onNewIntent(intent);
    }
}