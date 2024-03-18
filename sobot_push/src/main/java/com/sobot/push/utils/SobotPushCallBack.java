package com.sobot.push.utils;

public interface SobotPushCallBack {
    /**
     * 返回的接口
     * @param isSuccess 是否成功
     * @param message 返回的结果
     */
    void onResultBack(boolean isSuccess,String message);
}
