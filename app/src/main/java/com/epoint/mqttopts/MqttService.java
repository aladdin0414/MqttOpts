package com.epoint.mqttopts;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.epoint.mqttopts.mqtt.MqttAsyncClientSingle;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.util.Timer;
import java.util.TimerTask;


/**
 * @author liyc
 * @date 2020/3/27 2:11 PM
 * @copyright Jiangsu Guotai Epoint Software co., Ltd
 * @description
 */
public class MqttService extends Service {

    MqttAsyncClientSingle mqttAsyncClient;
    MqttConnectOptions opts;
    MqttConfigSp sp;
    Timer timer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerStopMqttServiceReceiver();
        sp = new MqttConfigSp(this);
        startMqtt(sp.getConfig());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onStopMqttServiceReceiver);
    }

    private void startMqtt(String config) {
        final JSONObject cj = JSONObject.parseObject(config);
        try {
            mqttAsyncClient = MqttAsyncClientSingle.getInstance(cj.getString("hosturi"), cj.getString("clientid"), new MqttDefaultFilePersistence(getFilesDir().getAbsolutePath() + "/mqtt"));
            mqttAsyncClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    log("connect success");

                    if (timer == null) {
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                sendNetStatus(mqttAsyncClient.isConnected() ? 1 : 0);
                            }
                        }, 0, 1000);
                    } else {
                        sendNetStatus(1);
                    }

                    JSONArray subArr = cj.getJSONArray("subarr");
                    String[] topicFilters = new String[subArr.size()];
                    int[] qos = new int[subArr.size()];
                    if (subArr != null && subArr.size() > 0) {
                        for (int i = 0; i < subArr.size(); i++) {
                            JSONObject sub = subArr.getJSONObject(i);
                            topicFilters[i] = sub.getString("topic");
                            qos[i] = sub.getInteger("qos");
                        }
                    }
                    try {
                        mqttAsyncClient.subscribe(topicFilters, qos, "subcribe", new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                log("subcribe success");
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {
                    cause.printStackTrace();
                    sendNetStatus(0);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    log("new msg: " + message.toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
            opts = new MqttConnectOptions();
            opts.setAutomaticReconnect(true);
            opts.setCleanSession(cj.getBoolean("cleansession"));
            opts.setUserName(cj.getString("username"));
            opts.setPassword(cj.getString("password").toCharArray());
            mqttAsyncClient.connect(opts, "onCreate", new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    log("connect success init...");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    exception.printStackTrace();
                    log("connect failure");
                    sendNetStatus(4);
                    stopSelf();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            sendNetStatus(4);
            stopSelf();
        }
    }

    private void stopMqtt() {
        if (timer != null) {
            timer.cancel();
        }
        if (mqttAsyncClient != null) {
            try {
                if (mqttAsyncClient.isConnected()) {
                    mqttAsyncClient.disconnect(1, "stop", new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            log("mqtt disconnected");
                            try {
                                mqttAsyncClient.close(true);
                                log("mqtt closed");
                                mqttAsyncClient.setNull();
                                sendNetStatus(2);
                                stopSelf();
                            } catch (MqttException e) {
                                e.printStackTrace();
                                sendNetStatus(3);
                            }
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            exception.printStackTrace();
                            sendNetStatus(3);
                        }
                    });
                }
                else{
                    sendNetStatus(3);
                }
            } catch (MqttException e) {
                e.printStackTrace();
                sendNetStatus(3);
            }
        }
    }

    private void log(String msg) {
        Log.i("mqttservice", msg);
    }

    /**
     * 0 disconnect 1 connect 2 mqttstop
     *
     * @param status
     */
    private void sendNetStatus(int status) {
        Intent intent = new Intent(MqttTestActivity.NET_STATUS_NOTIFY_RECEIVER_ACTION);
        intent.putExtra("netStatus", status);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    public static final String STOP_MQTT_SERVICE_RECEIVER_ACTION = "stopMqttServiceReceiverAction";

    private void registerStopMqttServiceReceiver() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(STOP_MQTT_SERVICE_RECEIVER_ACTION);
        broadcastManager.registerReceiver(onStopMqttServiceReceiver, intentFilter);
    }

    private BroadcastReceiver onStopMqttServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopMqtt();
        }
    };
}
