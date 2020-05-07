package com.epoint.mqttopts.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 * @author liyc
 * @date 2020/3/18 4:53 PM
 * @copyright Jiangsu Guotai Epoint Software co., Ltd
 * @description
 */
public class MqttClientExtend extends MqttClient implements MqttCallbackExtended {

    MqttChannelConfig config;
    int msgCount;

    /*private static MqttClientExtend instance;

    private static final Object mLock = new Object();

    public static MqttClientExtend getInstance(MqttChannelConfig config) throws MqttException {
        if (instance == null) {
            synchronized (mLock) {
                if (instance == null) {
                    instance = new MqttClientExtend(config);
                }
            }
        }
        return instance;
    }*/

    public MqttClientExtend(MqttChannelConfig config) throws MqttException {
        super(config.getHostUri(),config.getCliendid(),new MqttDefaultFilePersistence(config.getPersistDir()));
        this.config = config;
    }

    @Override
    public void connectionLost(Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        msgCount ++;
        config.getHandler().onMessage(topic,message.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        config.getHandler().connectSuccess();
    }

    public void connectToServer() {
        try {
            setCallback(this);
            MqttConnectOptions opts = new MqttConnectOptions();
            opts.setUserName(config.getUsername());
            opts.setPassword(config.getPassword().toCharArray());
            opts.setAutomaticReconnect(config.isAutoConnect());
            connect(opts);
        } catch (Exception e) {
            e.printStackTrace();
            config.getHandler().connectFailure();
        }
    }

    public int getMsgCount(){
        return msgCount;
    }
}
