package com.epoint.mqttopts.mqtt;

/**
 * @author liyc
 * @date 2020/3/18 6:44 PM
 * @copyright Jiangsu Guotai Epoint Software co., Ltd
 * @description
 */
public interface IMqttChannelHandler {

    public void connectSuccess();

    public void connectFailure();

    public void onMessage(String topic,String msg);

    public void notifyConnectionStatus(boolean status);
}
