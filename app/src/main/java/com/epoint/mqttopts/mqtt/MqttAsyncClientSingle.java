package com.epoint.mqttopts.mqtt;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPingSender;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author liyc
 * @date 2020/3/30 9:03 AM
 * @copyright Jiangsu Guotai Epoint Software co., Ltd
 * @description
 */
public class MqttAsyncClientSingle extends MqttAsyncClient {

    private static MqttAsyncClientSingle instance;

    private static final Object mLock = new Object();

    public static MqttAsyncClientSingle getInstance(String serverURI, String clientId, MqttClientPersistence persistence) throws MqttException {
        if (instance == null) {
            synchronized (mLock) {
                if (instance == null) {
                    instance = new MqttAsyncClientSingle(serverURI, clientId, persistence);
                }
            }
        }
        return instance;
    }

    public MqttAsyncClientSingle(String serverURI, String clientId) throws MqttException {
        super(serverURI, clientId);
    }

    public MqttAsyncClientSingle(String serverURI, String clientId, MqttClientPersistence persistence) throws MqttException {
        super(serverURI, clientId, persistence);
    }

    public MqttAsyncClientSingle(String serverURI, String clientId, MqttClientPersistence persistence, MqttPingSender pingSender) throws MqttException {
        super(serverURI, clientId, persistence, pingSender);
    }

    private MqttAsyncClientSingle(String serverURI, String clientId, MqttClientPersistence persistence, MqttPingSender pingSender, ScheduledExecutorService executorService) throws MqttException {
        super(serverURI, clientId, persistence, pingSender, executorService);
    }

    public void setNull() {
        instance = null;
    }
}
