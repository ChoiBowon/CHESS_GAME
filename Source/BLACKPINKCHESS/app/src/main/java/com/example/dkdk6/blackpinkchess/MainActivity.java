package com.example.dkdk6.blackpinkchess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.dkdk6.blackpinkchess.Activity.FindRoomActivity;
import com.example.dkdk6.blackpinkchess.Activity.MakeRoomActivity;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    EditText nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nickname = (EditText)findViewById(R.id.userNickname);
    }

    public void mainClick(View view){
        switch (view.getId()){
            case R.id.make_room_btn:
                Intent intent = new Intent(MainActivity.this, MakeRoomActivity.class);
                intent.putExtra("player_name", nickname.getText().toString());
                startActivity(intent);
                break;
            case R.id.find_room_btn:
                Intent intent2 = new Intent(MainActivity.this, FindRoomActivity.class);
                intent2.putExtra("player_name", nickname.getText().toString());
                startActivity(intent2);
                break;
        }
    }

}
