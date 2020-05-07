package com.epoint.mqttopts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author liyc
 * @date 2020/3/20 3:03 PM
 * @copyright Jiangsu Guotai Epoint Software co., Ltd
 * @description
 */
public class LoginActivity extends AppCompatActivity {
    EditText etLoginId,etPassword;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        etLoginId = findViewById(R.id.etLoginId);
        etPassword = findViewById(R.id.etPassword);
        findViewById(R.id.btLoginSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MqttTestActivity.class);
                intent.putExtra("loginid",etLoginId.getText().toString());
                startActivity(intent);
            }
        });

        /*Intent intent = new Intent(this,BackService2.class);
        startService(intent);*/
    }
}
