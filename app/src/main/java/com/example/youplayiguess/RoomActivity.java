package com.example.youplayiguess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youplayiguess.constants.AppAccessConstants;
import com.example.youplayiguess.constants.NetWorkConstant;
import com.example.youplayiguess.constants.NormalConstant;
import com.example.youplayiguess.global.GlobalValues;
import com.example.youplayiguess.model.PlayerMatchReq;
import com.example.youplayiguess.model.PlayerMatchRsp;
import com.example.youplayiguess.utils.GsonUtil;
import com.example.youplayiguess.utils.HttpUtil;
import com.google.gson.Gson;
import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.gmme.handler.IGameMMEEventHandler;
import com.huawei.game.gmme.model.EngineCreateParams;
import com.huawei.game.gmme.model.Player;

import java.io.IOException;
import java.util.List;

public class RoomActivity extends AppCompatActivity {
    private static final String TAG = "RoomActivity";

    private final String[] mPermissions =
            {android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.READ_PHONE_STATE};

    /**
     * 申请权限请求码
     */
    private static final int REQUEST_PERMISSIONS_CODE = 0X1001;

    /**
     * 权限是否已获取标志
     */
    private boolean mPermissionGranted = false;


    private TextView usernameTextView;
    private Button startMatch;

    PopupWindow matchWindow;

    Gson gson = GsonUtil.getGsonInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        // 检查麦克风等权限。
        checkPermissions();

        initWidget();
    }

    private void initWidget() {
        usernameTextView = findViewById(R.id.username_in_room_text_view);
        usernameTextView.setText(GlobalValues.username);

        startMatch = findViewById(R.id.start_match);
        startMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread( () -> {
                    PlayerMatchReq req = new PlayerMatchReq();
                    req.setUsername(GlobalValues.username);
                    try {
                        String matchUrl = NetWorkConstant.PLAYER_MATCH_URL.replace("{username}", GlobalValues.username);
                        runOnUiThread(() -> showMatchWindow());
                        String result = HttpUtil.post(matchUrl, gson.toJson(req));
                        Log.i(TAG, result);
                        PlayerMatchRsp playerMatchRsp = gson.fromJson(result, PlayerMatchRsp.class);
                        Intent intent = new Intent(RoomActivity.this, GameActivity.class);
                        intent.putExtra(NormalConstant.ROOM_NO, playerMatchRsp.getRoomNo());
                        startActivity(intent);
                        runOnUiThread(() -> {
                            if (matchWindow != null) {
                                matchWindow.dismiss();
                            }
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            if (matchWindow != null) {
                                matchWindow.dismiss();
                            }
                        });
                        Looper.prepare();
                        Toast.makeText(RoomActivity.this, "匹配失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();
            }
        });
    }

    private void showMatchWindow() {
        View contentView = LayoutInflater.from(RoomActivity.this).inflate(R.layout.match_window, null, false);
        matchWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        matchWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        //显示PopupWindow
        View rootview = LayoutInflater.from(RoomActivity.this).inflate(R.layout.activity_room, null);
        matchWindow.showAtLocation(rootview, Gravity.CENTER, 0, 0);
    }


    /**
     * 检查权限
     */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.RECORD_AUDIO)
                    || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_PHONE_STATE)) {
                // 如果权限未获取，则申请权限
                requestPermissions(mPermissions, REQUEST_PERMISSIONS_CODE);
                return;
            }
        }
        mPermissionGranted = true;
    }

}