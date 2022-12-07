package com.example.youplayiguess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youplayiguess.constants.NetWorkConstant;
import com.example.youplayiguess.constants.NormalConstant;
import com.example.youplayiguess.global.GlobalValues;
import com.example.youplayiguess.model.UserSummary;
import com.example.youplayiguess.utils.GsonUtil;
import com.example.youplayiguess.utils.HttpUtil;
import com.google.gson.Gson;

import java.io.IOException;

public class GameLayoutActivity extends AppCompatActivity {
    private TextView usernameTextView;

    private ImageButton rankButton;
    private ImageButton mySelfButton;

    private ImageButton youPlayIGuessBtn;
    private ImageButton langrenshaBtn;
    private ImageButton ktvBtn;
    private ImageButton haiguitangBtn;
    private ImageButton chaidanmaoBtn;
    private ImageButton youDrawIGuessBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_layout);

        initWidget();
    }

    private void initWidget() {
        mySelfButton = findViewById(R.id.myself_button);
        rankButton = findViewById(R.id.rank_button);


        rankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(() -> {
                    try {
                        String top3Url = NetWorkConstant.GET_RANK_URL.replace("{topN}", "3");
                        String result = HttpUtil.get(top3Url);
                        Intent intent = new Intent(GameLayoutActivity.this, RankActivity.class);
                        intent.putExtra(NormalConstant.RANK, result);
                        startActivity(intent);
                    } catch (Exception e) {
                        Looper.prepare();
                        Toast.makeText(GameLayoutActivity.this, "获取排行榜失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();
            }
        });


        usernameTextView = findViewById(R.id.username_text_view);


        youPlayIGuessBtn = findViewById(R.id.youplayiguess_btn);
        langrenshaBtn = findViewById(R.id.lanrensha_btn);
        ktvBtn = findViewById(R.id.ktv_btn);
        haiguitangBtn = findViewById(R.id.haiguitang_btn);
        chaidanmaoBtn = findViewById(R.id.chaidanmao_btn);
        youDrawIGuessBtn = findViewById(R.id.youdrawiguess_btn);

        usernameTextView.setText(GlobalValues.username);
        youPlayIGuessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameLayoutActivity.this, RoomActivity.class);
                startActivity(intent);
            }
        });

        langrenshaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GameLayoutActivity.this, "敬请期待",
                        Toast.LENGTH_SHORT).show();
            }
        });


        ktvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GameLayoutActivity.this, "敬请期待",
                        Toast.LENGTH_SHORT).show();
            }
        });

        haiguitangBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GameLayoutActivity.this, "敬请期待",
                        Toast.LENGTH_SHORT).show();
            }
        });

        chaidanmaoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GameLayoutActivity.this, "敬请期待",
                        Toast.LENGTH_SHORT).show();
            }
        });


        youDrawIGuessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GameLayoutActivity.this, "敬请期待",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}