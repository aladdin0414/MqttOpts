package com.epoint.mqttopts;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * @author liyc
 * @date 2020/3/23 4:45 PM
 * @copyright Jiangsu Guotai Epoint Software co., Ltd
 * @description
 */
public class MqttConfigSp {

    SharedPreferences sp;

    public MqttConfigSp(Context context) {
        sp = context.getSharedPreferences("mqtt_config_sp", context.MODE_PRIVATE);
    }

    public void write(String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String read(String key) {
        return sp.getString(key, "");
    }

    public String getConfig() {
        return read("mqtt_config");
    }

    public void setConfig(String config) {
        write("mqtt_config", config);
    }
}
