package com.example.youplayiguess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.JsonWriter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.youplayiguess.constants.NetWorkConstant;
import com.example.youplayiguess.global.GlobalValues;
import com.example.youplayiguess.model.Account;
import com.example.youplayiguess.model.ServerResponse;
import com.example.youplayiguess.utils.GsonUtil;
import com.example.youplayiguess.utils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huawei.game.common.utils.StringUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import okhttp3.internal.http.HttpMethod;
import okhttp3.internal.http2.ErrorCode;

public class MainActivity extends AppCompatActivity {

    private EditText accountText;
    private EditText passwordText;
    private Button loginButton;
    private Button registerButton;

    private Gson gson = GsonUtil.getGsonInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidget();

    }

    private void initWidget() {
        accountText = findViewById(R.id.edit_text_account);
        passwordText = findViewById(R.id.edit_text_password);
        loginButton = findViewById(R.id.button_login);
        registerButton = findViewById(R.id.button_register);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = accountText.getText().toString();
                String password = passwordText.getText().toString();
                if (StringUtils.isBlank(username)) {
                    Toast.makeText(MainActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (StringUtils.isBlank(password)) {
                    Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 开启一个子线程。进行网络操作。等待有返回结果，使用handler通知UI
                new Thread(() -> {
                    String result = null;
                    try {
                        result = HttpUtil.post(NetWorkConstant.LOGIN_URL, gson.toJson(new Account(username, password)));

                        System.out.println(result);
                        ServerResponse serverResponse = gson.fromJson(result, ServerResponse.class);
                        if (serverResponse.getCode() != ErrorCode.NO_ERROR.httpCode) {
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "用户名或者密码不正确", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        } else {
                            GlobalValues.username = username;
                            Intent intent = new Intent(MainActivity.this, GameLayoutActivity.class);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        Looper.prepare();
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = accountText.getText().toString();
                String password = passwordText.getText().toString();
                if (StringUtils.isBlank(username)) {
                    Toast.makeText(MainActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (StringUtils.isBlank(password)) {
                    Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                new Thread(() -> {
                    try {
                        String result = HttpUtil.post(NetWorkConstant.REGISTER_URL, gson.toJson(new Account(username, password)));
                        System.out.println(result);
                        ServerResponse serverResponse = gson.fromJson(result, ServerResponse.class);
                        if (serverResponse.getCode() != ErrorCode.NO_ERROR.httpCode) {
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        } else {
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    } catch (Exception e) {
                        Looper.prepare();
                        Toast.makeText(MainActivity.this, "注册失败，请稍后再试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();
            }
        });
    }
}