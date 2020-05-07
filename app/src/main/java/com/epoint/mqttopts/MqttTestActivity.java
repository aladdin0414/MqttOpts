package com.epoint.mqttopts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author liyc
 * @date 2020/3/27 2:05 PM
 * @copyright Jiangsu Guotai Epoint Software co., Ltd
 * @description
 */
public class MqttTestActivity extends AppCompatActivity {
    EditText etConfig;
    Button btStartService, btStopService;
    TextView tvNetStatus;
    MqttConfigSp sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerNetStatusNotifyReceiver();

        sp = new MqttConfigSp(this);
        setContentView(R.layout.mqtttest_activity);
        etConfig = findViewById(R.id.etConfig);
        btStartService = findViewById(R.id.btStartService);
        btStopService = findViewById(R.id.btStopService);
        tvNetStatus = findViewById(R.id.tvNetStatus);

        etConfig.setText(sp.getConfig());

        etConfig.setText(getSampleConfig().toString());
        btStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMqttService();
            }
        });
        btStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMqttService();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNetStatusNotifyReceiver);
    }

    private void startMqttService() {
        sp.setConfig(etConfig.getText().toString());
        Intent service = new Intent(this, MqttService.class);
        startService(service);
    }

    private void stopMqttService() {
        Intent intent = new Intent(MqttService.STOP_MQTT_SERVICE_RECEIVER_ACTION);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private JSONObject getSampleConfig() {
        JSONObject obj = new JSONObject();
        obj.put("hosturi", "tcp://www.liyachun.com:1883");
        obj.put("username", "admin");
        obj.put("password", "11111");
        obj.put("clientid", getIntent().getStringExtra("loginid"));
        obj.put("cleansession", true);
        JSONArray subArr = new JSONArray();
        subArr.add(getSubObj("test", 1));
        subArr.add(getSubObj("test2", 1));
        obj.put("subarr", subArr);
        return obj;
    }

    private JSONObject getSubObj(String topic, int qos) {
        if (qos < 0 || qos > 2) {
            throw new IllegalArgumentException("qos should be 0, 1 or 2");
        }
        JSONObject sub1 = new JSONObject();
        sub1.put("topic", topic);
        sub1.put("qos", qos);
        return sub1;
    }

    public static final String NET_STATUS_NOTIFY_RECEIVER_ACTION = "NetStatusNotifyReceiver";

    private void registerNetStatusNotifyReceiver() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NET_STATUS_NOTIFY_RECEIVER_ACTION);
        broadcastManager.registerReceiver(onNetStatusNotifyReceiver, intentFilter);
    }

    private BroadcastReceiver onNetStatusNotifyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int netStatus = intent.getIntExtra("netStatus", 2);
            if (netStatus == 0) {
                tvNetStatus.setVisibility(View.VISIBLE);
                tvNetStatus.setText("正在连接...");
                tvNetStatus.setBackground(getDrawable(R.color.red));
            } else if (netStatus == 1) {
                tvNetStatus.setVisibility(View.VISIBLE);
                tvNetStatus.setText("已连接...");
                tvNetStatus.setBackground(getDrawable(R.color.MediumSpringGreen));
            } else if (netStatus == 2) {
                finish();
            } else if (netStatus == 3) {
                Toast.makeText(getActivity(), "暂时无法退出", Toast.LENGTH_SHORT).show();
            } else if (netStatus == 4) {
                Toast.makeText(getActivity(), "消息服务器连接失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public Activity getActivity() {
        return this;
    }

}
