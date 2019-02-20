package com.example.dkdk6.blackpinkchess.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dkdk6.blackpinkchess.Player;
import com.example.dkdk6.blackpinkchess.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

public class MakeRoomActivity extends AppCompatActivity {

    static final int PORT = 10001;
    int GAMEPLAYER = 3;
    int NOWPLAYER = 0;
    Player myPlayer;

    TextView IPinformation;
    TextView information_text;
    TextView member_number;
    TextView text_msg; //클라이언트로부터 받을 메세지를 표시하는 TextView
    Button gameStartBtn;
    String msg = " ";
    String name;

    private ServerService serverService;
    private boolean isBind;

    ServiceConnection sconn = new ServiceConnection() {
        @Override // 서비스가 실행될 때 호출
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ServerService.MyBinder myBinder = (ServerService.MyBinder)iBinder;
            serverService = myBinder.getService();
            isBind = true;
            Log.e("LOG", "onServiceConnected()");
            //콜백 등록
            serverService.registerCallback(mCallback);
        }

        @Override //서비스가 종료될 때 호출
        public void onServiceDisconnected(ComponentName componentName) {
            serverService = null;
            isBind = false;
            Log.e("LOG", "onServiceDisconnected()");
        }
    };

    private ServerService.ICallback mCallback = new ServerService.ICallback(){
        public void recvData(String msg){
            text_msg.setText(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeroom);

        name = getIntent().getStringExtra("player_name");
        text_msg = (TextView) findViewById(R.id.text_massage_from_client);
        member_number = (TextView) findViewById(R.id.number_member);
        IPinformation = (TextView) findViewById(R.id.socket_information_text);
        information_text = (TextView) findViewById(R.id.socket_information);
        gameStartBtn = (Button)findViewById(R.id.start_game_btn);
        name = getIntent().getStringExtra("player_name");
        myPlayer = new Player("W","B",0); //Server -> Game Activity에 같이 넘겨주기

    }

    public void mOnClick(View v) {
        Intent intent = new Intent(MakeRoomActivity.this, ServerService.class); // 다음넘어갈 컴퍼넌트

        switch (v.getId()) {
            case R.id.btn_start_server: //방만들기 (IP broadcast)
                information_text.setText("방이 만들어졌습니다");
                intent.putExtra("Tag","MakeRoom");
                intent.putExtra("Name", name);
                bindService(intent, sconn, Context.BIND_AUTO_CREATE);
                break;
            case R.id.start_game_btn:
                //브로드캐스팅
                //unbindService(sconn);
                if(NOWPLAYER<GAMEPLAYER){
                    Toast.makeText(getApplicationContext(), "아직 준비가 되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }else if(NOWPLAYER==GAMEPLAYER){
                    if(isBind==true){
                        Intent startIntent = new Intent(MakeRoomActivity.this, PlayGameActivity.class);
                        startIntent.putExtra("OBJECT", myPlayer);
                        startIntent.putExtra("MYTYPE",1);
                        startIntent.putExtra("IPADDRESS", IPinformation.getText().toString());
                        startActivity(startIntent);
                    }else{
                        Toast.makeText(getApplicationContext(), "서비스가 연결 안됐습니당..", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("custom-event-name"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            String[] temp = message.split("&");
            if(temp[0].equals("SERVER")){
                IPinformation.setText(temp[1]);
            }else if(temp[0].equals("PNUMBER")){
                member_number.setText("게임 참여 인원 : "+ temp[1]);
                NOWPLAYER = Integer.parseInt(temp[1]);
            }
        }
    };
}
