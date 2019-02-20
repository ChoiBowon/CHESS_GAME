package com.example.dkdk6.blackpinkchess.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.dkdk6.blackpinkchess.R;

public class FinishGameActivity extends AppCompatActivity {
    TextView resultText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_game);
        //Intent에서 결과 받아온 것을 띄어주면 됨.
        String result = getIntent().getStringExtra("Result");
        resultText = (TextView)findViewById(R.id.GameResult);
        resultText.setText(result);
    }
}
