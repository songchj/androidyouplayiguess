package com.example.youplayiguess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.youplayiguess.constants.NormalConstant;
import com.example.youplayiguess.model.WebSocketMessage;
import com.example.youplayiguess.utils.GsonUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {
    private Gson gson = GsonUtil.getGsonInstance();

    private TextView firstUserTextView;
    private TextView firstUserScoreTextView;
    private TextView secondUserTextView;
    private TextView secondUserScoreTextView;
    private TextView thirdUserTextView;
    private TextView thirdUserScoreTextView;

    private Button backRoomButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initWidget();

        updateUI();
    }

    private void updateUI() {
        runOnUiThread(() -> {
            Map<String, String> scoreMap = gson.fromJson(getIntent().getStringExtra(NormalConstant.SCORE_MAP), Map.class);
            List<String> sortedUser = getSortedUser(scoreMap);
            for (int i = 0; i < sortedUser.size(); i++) {
                String username = sortedUser.get(i);
                if (i == 0) {
                    firstUserTextView.setText(username);
                    firstUserScoreTextView.setText(scoreMap.get(username) + "");
                }
                if (i == 1) {
                    secondUserTextView.setText(username);
                    secondUserScoreTextView.setText(scoreMap.get(username) + "");
                }
                if (i == 2) {
                    thirdUserTextView.setText(username);
                    thirdUserScoreTextView.setText(scoreMap.get(username) + "");
                }
            }
        });
    }

    private List<String> getSortedUser(Map<String, String> scoreMap) {
        List<Map.Entry<String, String>> entries = new ArrayList<>(scoreMap.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return Integer.valueOf(o2.getValue()) - Integer.valueOf(o1.getValue());
            }
        });
        List<String> soredUser = new ArrayList<>();
        for (Map.Entry<String, String> entry : entries) {
            soredUser.add(entry.getKey());
        }
        return soredUser;
    }

    private void initWidget() {
        firstUserTextView = findViewById(R.id.first_user_in_result_text_view);
        firstUserScoreTextView = findViewById(R.id.first_user_score_in_result_text_view);
        secondUserTextView = findViewById(R.id.second_user_in_result_text_view);
        secondUserScoreTextView = findViewById(R.id.second_user_score_in_result_text_view);
        thirdUserTextView = findViewById(R.id.third_user_in_result_text_view);
        thirdUserScoreTextView = findViewById(R.id.third_user_score_in_result_text_view);

        backRoomButton = findViewById(R.id.back_room_button);
        backRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, RoomActivity.class);
                startActivity(intent);
            }
        });
    }
}