package com.example.youplayiguess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.youplayiguess.constants.AppAccessConstants;
import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.gmme.handler.IGameMMEEventHandler;
import com.huawei.game.gmme.model.EngineCreateParams;

import java.util.List;

public class MediaActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final String[] mPermissions =
            {android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.READ_PHONE_STATE};


    private TextView roomIdTextView;
    private TextView openIdTextView;
    private TextView messageTextView;

    private Button joinBtn;

    /**
     * 申请权限请求码
     */
    private static final int REQUEST_PERMISSIONS_CODE = 0X1001;

    /**
     * 权限是否已获取标志
     */
    private boolean mPermissionGranted = false;

    private GameMediaEngine mHwRtcEngine;

    private IGameMMEEventHandler eventHandler = new IGameMMEEventHandler() {
        @Override
        public void onCreate(int code, String msg) {
            LogUtil.i(TAG, "onCreate : code=" + code + ", msg=" + msg);
            // 创建实例完成后的数据处理
        }

        @Override
        public void onMutePlayer(String s, String s1, boolean b, int i, String s2) {

        }

        @Override
        public void onMuteAllPlayers(String s, List<String> list, boolean b, int i, String s1) {

        }

        @Override
        public void onJoinTeamRoom(String roomId, int code, String msg) {
            StringBuilder sb = new StringBuilder("onJoinTeamRoom : ").append("roomId=")
                    .append(roomId)
                    .append(", code=")
                    .append(code)
                    .append(", msg=")
                    .append(msg);
            LogUtil.i(TAG+"_join", sb.toString());
            // 创建小队房间成功或失败数据处理
            // roomId：房间ID; interval:当前发言玩家列表回调的时间间隔,有效值范围为[100, 10000],单位: 毫秒,当传入0时,即关闭音量回调
            mHwRtcEngine.enableSpeakersDetection(roomId, 100);
            runOnUiThread( () -> messageTextView.setText(sb.toString()));
        }

        @Override
        public void onJoinNationalRoom(String s, int i, String s1) {

        }

        @Override
        public void onSwitchRoom(String s, int i, String s1) {

        }

        @Override
        public void onLeaveRoom(String s, int i, String s1) {

        }

        @Override
        public void onSpeakersDetection(List<String> openIds) {
            LogUtil.i(TAG+"_speak", "onSpeakersDetection: openIds=" + openIds);
            // 可根据需求将数据进行处理
        }

        @Override
        public void onForbidAllPlayers(String s, List<String> list, boolean b, int i, String s1) {

        }

        @Override
        public void onForbidPlayer(String s, String s1, boolean b, int i, String s2) {

        }

        @Override
        public void onForbiddenByOwner(String s, List<String> list, boolean b) {

        }

        @Override
        public void onVoiceToText(String s, int i, String s1) {

        }

        @Override
        public void onPlayerOnline(String s, String s1) {

        }

        @Override
        public void onPlayerOffline(String s, String s1) {

        }

        @Override
        public void onTransferOwner(String s, int i, String s1) {

        }

        @Override
        public void onDestroy(int i, String s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        roomIdTextView = findViewById(R.id.roomIdText);
        openIdTextView = findViewById(R.id.openIdText);
        messageTextView = findViewById(R.id.messageText);

        joinBtn = findViewById(R.id.joinRoomBtn);


        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHwRtcEngine == null) {
                    createEngineInstance(MediaActivity.this);
                }
                createRoom();
            }
        });

        // 检查权限
        checkPermissions();

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


    private void createEngineInstance(Context context) {
        EngineCreateParams params = new EngineCreateParams();
        params.setOpenId(openIdTextView.getText().toString().trim()); // 玩家ID
        params.setContext(context); // 应用的上下文
        params.setLogEnable(true); // 开启SDK日志记录
        //params.setLogPath(logPath); // 日志路径
        params.setLogSize(1024 * 10); // 日志存储大小
        params.setCountryCode("CN"); // 国家码，用于网关路由，不设置默认CN
        params.setAgcAppId(AppAccessConstants.APP_ID); // 游戏应用在AGC上注册的APP ID
        params.setClientId(AppAccessConstants.CLIENT_ID); // 客户端ID
        params.setClientSecret(AppAccessConstants.CLIENT_SECRET); // 客户端ID对应的秘钥
        //params.setCpAccessToken(cpAccessToken); // AGC接入凭证（推荐）
        params.setApiKey(AppAccessConstants.API_SECRET); // API秘钥（凭据）

        LogUtil.i(eventHandler.toString());
        mHwRtcEngine = GameMediaEngine.create(params, eventHandler);
        System.out.println(mHwRtcEngine);
    }

    private void destroyEngineInstance() {
        GameMediaEngine.destroy();
    }


    private void createRoom() {
        String roomId = roomIdTextView.getText().toString().trim();
        if (roomId == null  || roomId.isEmpty()) {
            roomId = null;
        }
        mHwRtcEngine.joinTeamRoom(roomId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GameMediaEngine.destroy();
    }
}