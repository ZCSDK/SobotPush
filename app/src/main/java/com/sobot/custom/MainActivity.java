package com.sobot.custom;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sobot.custom.util.ConstantUtils;
import com.sobot.custom.util.LogUtils;
import com.sobot.custom.util.SharedPreferencesUtil;
import com.sobot.push.SobotMsgClick;
import com.sobot.push.SobotPushConstants;
import com.sobot.push.SobotPushHelper;
import com.sobot.push.utils.SobotPushCallBack;
import com.umeng.message.PushAgent;
import com.umeng.message.entity.UMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * demo界面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_appkey;
    private EditText et_alias, et_alias_type;
    private Button btn_setAlias, btn_removeAlias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PushAgent.getInstance(this).onAppStart();
        tv_appkey = findViewById(R.id.tv_appkey);
        tv_appkey.setText(SobotPushConstants.APP_KEY);
        tv_appkey.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyStr(tv_appkey.getText().toString());
                return false;
            }
        });
        et_alias = findViewById(R.id.et_alias);
        et_alias.setText(SharedPreferencesUtil.getStringData(MainActivity.this, "alias", ""));
        et_alias_type = findViewById(R.id.et_alias_type);
        et_alias_type.setText(SharedPreferencesUtil.getStringData(MainActivity.this, "aliasType", ""));
        btn_setAlias = findViewById(R.id.btn_set_alias);
        btn_removeAlias = findViewById(R.id.btn_remove_alias);
        btn_setAlias.setOnClickListener(this);
        btn_removeAlias.setOnClickListener(this);
        SharedPreferencesUtil.saveBooleanData(this, ConstantUtils.SOBOT_PRIVACY_AGREEMENT, true);
        setMsgClick();
    }

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
    @Override
    public void onClick(View v) {
        String alias = et_alias.getText().toString();
        String aliasType = et_alias_type.getText().toString();
        if (TextUtils.isEmpty(alias)) {
            Toast.makeText(MainActivity.this, "请输入别名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(aliasType)) {
            Toast.makeText(MainActivity.this, "请输入别名类型", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferencesUtil.saveStringData(MainActivity.this, "alias", alias);
        SharedPreferencesUtil.saveStringData(MainActivity.this, "aliasType", aliasType);
        if (v == btn_setAlias) {
            //设置别名
            SobotPushHelper.bindAlias(MainActivity.this, alias, aliasType, new SobotPushCallBack() {
                @Override
                public void onResultBack(boolean isSuccess, String message) {
                    LogUtils.d("==addAlias==isSuccess=" + isSuccess + "====message=" + message);
                }
            });

        } else if (v == btn_removeAlias) {
            SobotPushHelper.unBindAlias(MainActivity.this, alias, aliasType, new SobotPushCallBack() {
                @Override
                public void onResultBack(boolean isSuccess, String message) {
                    LogUtils.d("==deleteAlias==isSuccess=" + isSuccess + "====message=" + message);
                }
            });
        }
    }

    private void copyStr(String copyString) {
        if (Build.VERSION.SDK_INT >= 11) {
            android.content.ClipboardManager cmb = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(copyString);
            cmb.getText();
        } else {
            android.text.ClipboardManager cmb = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(copyString);
            cmb.getText();
        }

        Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
    }
}