package com.example.youplayiguess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.youplayiguess.constants.AppAccessConstants;
import com.example.youplayiguess.constants.ClientMessageTypeConstant;
import com.example.youplayiguess.constants.ServerMessageTypeConstant;
import com.example.youplayiguess.constants.NormalConstant;
import com.example.youplayiguess.global.GlobalValues;
import com.example.youplayiguess.model.ClientMessage;
import com.example.youplayiguess.model.WebSocketMessage;
import com.example.youplayiguess.utils.GsonUtil;
import com.google.gson.Gson;
import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.gmme.handler.IGameMMEEventHandler;
import com.huawei.game.gmme.model.EngineCreateParams;
import com.huawei.game.gmme.model.VoiceParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GameActivity extends AppCompatActivity implements Observer {

    private static final String TAG = "GameActivity";
    private JWebSocketClient client;
    private Gson gson = GsonUtil.getGsonInstance();

    private TextView performCountdownTextView;
    private TextView showText;
    private TextView showDescriptionText;
    private TextView firstUserTextView;
    private TextView firstUserScoreTextView;
    private TextView secondUserTextView;
    private TextView secondUserScoreTextView;
    private TextView thirdUserTextView;
    private TextView thirdUserScoreTextView;

    private Button changeWordButton;

    private EditText inputWordEditText;
    private Button sendMessageButton;

    private ImageButton convertTextButton;


    private List<UserTextView> userTextViews = new ArrayList<>();

    private String roomNo;


    private AssetManager aManager;
    private SoundPool mSoundPool = null;
    private HashMap<Integer, Integer> soundID = new HashMap<Integer, Integer>();


    private GameMediaEngine mHwRtcEngine;
    VoiceParam voiceParam ;

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
            LogUtil.i(TAG + "_join", sb.toString());
            // 创建小队房间成功或失败数据处理
            // roomId：房间ID; interval:当前发言玩家列表回调的时间间隔,有效值范围为[100, 10000],单位: 毫秒,当传入0时,即关闭音量回调
            mHwRtcEngine.enableSpeakersDetection(roomId, 100);
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
            LogUtil.i(TAG + "_speak", "onSpeakersDetection: openIds=" + openIds);
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
        public void onVoiceToText(String text, int status, String message) {
            Log.i(TAG, "onVoiceToText: " + "text:" + text + "status:" + status + "message:" + message);
            runOnUiThread(() -> {
                inputWordEditText.setText(text);
            });
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

        roomNo = getIntent().getStringExtra(NormalConstant.ROOM_NO);

        client = JWebSocketClient.getClient(roomNo, GlobalValues.username);


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        aManager = getAssets();
        try {
            initSP();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mHwRtcEngine == null) {
            createEngineInstance(GameActivity.this);
        }


        // 加入房间
        joinRoom(roomNo);

        // 语音转文字
        voiceParam = new VoiceParam();
        voiceParam.languageCodeSet("zh"); // 语言编码，只支持zh和en_US两种


        setContentView(R.layout.activity_game);

        initWidget();

        subscribe(client);
    }

    private void initSP() throws IOException {
        //设置最多可容纳5个音频流，音频的品质为5
        mSoundPool = new SoundPool(6, AudioManager.STREAM_SYSTEM, 5);
        soundID.put(1, mSoundPool.load(getAssets().openFd("start_game_count_down.mp3"), 1));  //需要捕获IO异常
        soundID.put(2, mSoundPool.load(getAssets().openFd("start.wav"), 1));  //需要捕获IO异常
        soundID.put(3, mSoundPool.load(getAssets().openFd("good.mp3"), 1));  //需要捕获IO异常
        soundID.put(4, mSoundPool.load(getAssets().openFd("bad.mp3"), 1));  //需要捕获IO异常
        soundID.put(5, mSoundPool.load(getAssets().openFd("ten_count_down.mp3"), 1));  //需要捕获IO异常
        soundID.put(6, mSoundPool.load(getAssets().openFd("game_over.mp3"), 1));  //需要捕获IO异常

    }

    private void initWidget() {
        performCountdownTextView = findViewById(R.id.perform_countdown_text_view);
        showText = findViewById(R.id.show_text_view);
        showDescriptionText = findViewById(R.id.show_description_text_view);
        firstUserTextView = findViewById(R.id.first_user_in_game_text_view);
        firstUserScoreTextView = findViewById(R.id.first_user_score_text_view);
        secondUserTextView = findViewById(R.id.second_user_in_game_text_view);
        secondUserScoreTextView = findViewById(R.id.second_user_score_text_view);
        thirdUserTextView = findViewById(R.id.third_user_in_game_text_view);
        thirdUserScoreTextView = findViewById(R.id.third_user_score_in_game_text_view);


        userTextViews.add(new UserTextView(firstUserTextView, firstUserScoreTextView));
        userTextViews.add(new UserTextView(secondUserTextView, secondUserScoreTextView));
        userTextViews.add(new UserTextView(thirdUserTextView, thirdUserScoreTextView));

        inputWordEditText = findViewById(R.id.input_word_edit_text);
        sendMessageButton = findViewById(R.id.send_message_button);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = inputWordEditText.getText().toString();
                ClientMessage message = new ClientMessage(ClientMessageTypeConstant.GUESS_WORD, roomNo, GlobalValues.username, word);
                client.send(gson.toJson(message));
            }
        });

        changeWordButton = findViewById(R.id.change_word_button);
        changeWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientMessage message = new ClientMessage(ClientMessageTypeConstant.CHANGE_WORD, roomNo, GlobalValues.username, "");
                client.send(gson.toJson(message));
            }
        });
    }


    @Override
    public <T> void subscribe(T subject) {
        client.addObserver(this);
    }

    @Override
    public void update(String message) {
        WebSocketMessage msg = gson.fromJson(message, WebSocketMessage.class);
        Log.i(TAG, msg.toString());
        try {
            switch (msg.getType()) {
                case ServerMessageTypeConstant.GAME_INIT:
                    initGame(msg);
                    break;
                case ServerMessageTypeConstant.READY_TO_PERFORM:
                    // 这里防止进入游戏的时候服务端的消息已经发送，但是客户端还没接收到补救措施
                    initGame(msg);
                    readyToPerform(msg);
                    break;
                case ServerMessageTypeConstant.START_PERFORMER:
                    startPerform(msg);
                    break;
                case ServerMessageTypeConstant.PERFORMER_COUNTDOWN:
                    performCountdown(msg);
                    break;
                case ServerMessageTypeConstant.CHANGE_WORD:
                    changeWord(msg);
                    break;
                case ServerMessageTypeConstant.GUESS_RIGHT:
                    guessRightWord(msg);
                    break;
                case ServerMessageTypeConstant.WORD_TIME_OUT:
                    wordTimeOut(msg);
                    break;
                case ServerMessageTypeConstant.USER_PERFORMER_OVER:
                    userShowOver(msg);
                    break;
                case ServerMessageTypeConstant.GAME_OVER:
                    gameOver(msg);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e("GameActivity_error", e.getMessage() + msg);
        }
    }


    private void initGame(WebSocketMessage msg) {
        runOnUiThread(() -> {
            List<String> users = msg.getUsers();
            int len = users.size() > userTextViews.size() ? userTextViews.size() : users.size();
            for (int i = 0; i < len; i++) {
                userTextViews.get(i).usernameTextView.setText(users.get(i));
            }
        });
    }

    private void readyToPerform(WebSocketMessage msg) {
        Log.d(TAG, "readyToPerform: " + msg.getCountdown() + ", " + msg.getPerformer());
        if (msg.getCountdown() == 3) {
            mSoundPool.play(soundID.get(1), 1, 1, 0, 0, 1);
        }
        runOnUiThread(() -> {
            showText.setText(msg.getCountdown() + "");
            showDescriptionText.setText(msg.getPerformer() + "    " + "准备上台表演");
            if (Objects.equals(msg.getPerformer(), GlobalValues.username)) {
                changeWordButton.setVisibility(View.VISIBLE);

                sendMessageButton.setVisibility(View.INVISIBLE);
                inputWordEditText.setVisibility(View.INVISIBLE);
            } else {
                changeWordButton.setVisibility(View.INVISIBLE);

                sendMessageButton.setVisibility(View.VISIBLE);
                inputWordEditText.setVisibility(View.VISIBLE);
            }
        });
        // 有人开始表演时，把其他所有的玩家屏蔽掉，放开表演者
        mHwRtcEngine.muteAllPlayers(roomNo, true);
        mHwRtcEngine.mutePlayer(roomNo, msg.getPerformer(), false);
    }

    private void startPerform(WebSocketMessage msg) {
        runOnUiThread(() -> {
            showText.setText("");
            if (Objects.equals(GlobalValues.username, msg.getPerformer())) {
                showDescriptionText.setText("题目：   " + msg.getCurrentWord());
            } else {
                showDescriptionText.setText("");
            }
        });
        mSoundPool.play(soundID.get(2), 1, 1, 0, 0, 1);
    }


    private void performCountdown(WebSocketMessage msg) {
        runOnUiThread(() -> {
            performCountdownTextView.setText(msg.getCountdown() + "s");
        });
    }

    private void changeWord(WebSocketMessage msg) {
        runOnUiThread(() -> {
            if (!Objects.equals(GlobalValues.username, msg.getPerformer())) {
                showDescriptionText.setText("玩家换词了，原词是：" + msg.getCurrentWord());
            } else {
                showDescriptionText.setText("题目：   " + msg.getNextWord());
            }
        });
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                if (!Objects.equals(GlobalValues.username, msg.getPerformer())) {
                    runOnUiThread(() -> {
                        showDescriptionText.setText("");
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void guessRightWord(WebSocketMessage msg) {
        /**
         * 三种情况：
         * 1. 你是表演者，有人答对了，提示：Ta答对了，播放胜利音效，你和猜对的人加分
         * 2. 你是猜词者，你答对了，提示：你答对了，播放胜利音效，你和表演者加分
         * 3. 你是猜词者，别人答对了，提示：被抢先了，播放失败音效，别人和表演者加分
         */
        if (Objects.equals(GlobalValues.username, msg.getPerformer())) { // 你是表演者
            mSoundPool.play(soundID.get(3), 1, 1, 0, 0, 1);
            runOnUiThread(() -> {
                showText.setText(msg.getGuesser() + "猜对了 +10 \n" + "表演者 +5");
                showDescriptionText.setText("题目：   " + msg.getNextWord() + "");
            });
        } else if (Objects.equals(GlobalValues.username, msg.getGuesser())) { // 你猜对了
            mSoundPool.play(soundID.get(3), 1, 1, 0, 0, 1);
            runOnUiThread(() -> {
                showText.setText("你答对了");
            });
        } else { // 别人猜对了
            mSoundPool.play(soundID.get(4), 1, 1, 0, 0, 1);
            runOnUiThread(() -> {
                showText.setText("被抢答了\n" + msg.getGuesser() + "猜对了");
            });
        }

        Map<String, Integer> scoreMap = msg.getScoreMap();
        //Log.i(TAG, msg.toString());
        Log.i(TAG, scoreMap.toString());
        // 表演者和猜词者得分
        runOnUiThread(() -> {
            for (UserTextView userTextView : userTextViews) {
                String username = userTextView.usernameTextView.getText().toString();
                if (scoreMap.containsKey(username)) {
                    Log.i(TAG, "更新分数了");
                    userTextView.scoreTextView.setText(scoreMap.get(username) + "");
                }
            }
        });
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                runOnUiThread(() -> {
                    showText.setText("");
                });
            } catch (InterruptedException e) {

            }

        }).start();
    }

    private void wordTimeOut(WebSocketMessage msg) {
        // 弹出窗口显示时间到，播放音效，显示当前词语， 这个先不搞了，有时间再说吧
    }


    private void userShowOver(WebSocketMessage msg) {
        // 当前用户时间到，播放音效，显示当前词语
        mSoundPool.play(soundID.get(6), 1, 1, 0, 0, 1);
    }

    private void gameOver(WebSocketMessage msg) {
        mSoundPool.play(soundID.get(6), 1, 1, 0, 0, 1);
        // 跳到结果页，显示当前排名
        Map<String, String> scoreStrMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : msg.getScoreMap().entrySet()) {
            scoreStrMap.put(entry.getKey(), entry.getValue().toString());
        }
        leaveRoom(roomNo);
        if (client != null && !client.isClosed()) {
            client.close();
        }
        destroyEngineInstance();
        Intent intent = new Intent(GameActivity.this, ResultActivity.class);
        intent.putExtra(NormalConstant.SCORE_MAP, gson.toJson(scoreStrMap));
        startActivity(intent);
    }


    private class UserTextView {
        public TextView usernameTextView;
        public TextView scoreTextView;

        public UserTextView(TextView usernameTextView, TextView scoreTextView) {
            this.usernameTextView = usernameTextView;
            this.scoreTextView = scoreTextView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null) {
            client.close();
        }
        destroyEngineInstance();
        leaveRoom(roomNo);
        mSoundPool.release();
    }

    private void createEngineInstance(Context context) {
        EngineCreateParams params = new EngineCreateParams();
        params.setOpenId(GlobalValues.username); // 玩家ID
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


    private void joinRoom(String roomNo) {
        // 加入房间后先把其他所有人屏蔽，等到轮到谁讲话了，把谁放开
        mHwRtcEngine.joinTeamRoom(roomNo);
        mHwRtcEngine.muteAllPlayers(roomNo, true);
    }

    private void leaveRoom(String roomNo) {
        mHwRtcEngine.leaveRoom(roomNo, "");
    }
}