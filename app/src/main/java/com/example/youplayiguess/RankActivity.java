package com.example.youplayiguess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youplayiguess.constants.NormalConstant;
import com.example.youplayiguess.model.UserSummary;
import com.example.youplayiguess.utils.GsonUtil;
import com.google.gson.Gson;

import org.w3c.dom.Text;

public class RankActivity extends AppCompatActivity {

    private TextView top1UsernameTextView;
    private TextView top1TotalScoreTextView;
    private TextView top1GameAmount;
    private TextView top1PerformAmount;
    private TextView top1GuessAmount;

    private TextView top2UsernameTextView;
    private TextView top2TotalScoreTextView;
    private TextView top2GameAmount;
    private TextView top2PerformAmount;
    private TextView top2GuessAmount;

    private TextView top3UsernameTextView;
    private TextView top3TotalScoreTextView;
    private TextView top3GameAmount;
    private TextView top3PerformAmount;
    private TextView top3GuessAmount;

    Gson gson = GsonUtil.getGsonInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        top1UsernameTextView = findViewById(R.id.top1_text_username);
        top1TotalScoreTextView = findViewById(R.id.top1_total_score);
        top1GameAmount = findViewById(R.id.top1_game_amount);
        top1PerformAmount = findViewById(R.id.top1_perform_amount);
        top1GuessAmount = findViewById(R.id.top1_guess_amount);

        top2UsernameTextView = findViewById(R.id.top2_text_username);
        top2TotalScoreTextView = findViewById(R.id.top2_total_score);
        top2GameAmount = findViewById(R.id.top2_game_amount);
        top2PerformAmount = findViewById(R.id.top2_perform_amount);
        top2GuessAmount = findViewById(R.id.top2_guess_amount);

        top3UsernameTextView = findViewById(R.id.top3_text_username);
        top3TotalScoreTextView = findViewById(R.id.top3_total_score);
        top3GameAmount = findViewById(R.id.top3_game_amount);
        top3PerformAmount = findViewById(R.id.top3_perform_amount);
        top3GuessAmount = findViewById(R.id.top3_guess_amount);


        String result = getIntent().getStringExtra(NormalConstant.RANK);
        System.out.println("rank" +result);
        try {
            UserSummary[] userSummaries = gson.fromJson(result,UserSummary[].class);
            runOnUiThread(() -> {
                for (int i = 0; i < userSummaries.length; i++) {
                    if (i == 0) {
                        top1UsernameTextView.setText(userSummaries[i].getUsername() +"");
                        top1TotalScoreTextView.setText(userSummaries[i].getTotalScore()+"");
                        top1GameAmount.setText(userSummaries[i].getTotalGameAmount()+"");
                        top1PerformAmount.setText(userSummaries[i].getPerformCorrectAmount()+"");
                        top1GuessAmount.setText(userSummaries[i].getGuessCorrectAmount()+"");
                    }
                    if (i == 1) {
                        top2UsernameTextView.setText(userSummaries[i].getUsername() +"");
                        top2TotalScoreTextView.setText(userSummaries[i].getTotalScore()+"");
                        top2GameAmount.setText(userSummaries[i].getTotalGameAmount()+"");
                        top2PerformAmount.setText(userSummaries[i].getPerformCorrectAmount()+"");
                        top2GuessAmount.setText(userSummaries[i].getGuessCorrectAmount()+"");
                    }
                    if (i == 2) {
                        top3UsernameTextView.setText(userSummaries[i].getUsername() +"");
                        top3TotalScoreTextView.setText(userSummaries[i].getTotalScore()+"");
                        top3GameAmount.setText(userSummaries[i].getTotalGameAmount()+"");
                        top3PerformAmount.setText(userSummaries[i].getPerformCorrectAmount()+"");
                        top3GuessAmount.setText(userSummaries[i].getGuessCorrectAmount()+"");
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(RankActivity.this, "获取排行榜失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }

    }
}