package com.example.dkdk6.blackpinkchess.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dkdk6.blackpinkchess.Player;
import com.example.dkdk6.blackpinkchess.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by choibowon on 2018. 11. 30..
 */

public class FindRoomActivity extends Activity {
    private static final int PORT = 10001; //서버에서 설정한 PORT 번호

    String ip;
    Player myPlayer;
    Socket socket;
    DataInputStream is;
    DataOutputStream os;
    TextView text_msg;  //서버로 부터 받은 메세지를 보여주는 TextView
    TextView connected_information;

    EditText edit_msg;  //서버로 전송할 메세지를 작성하는 EditText
    EditText edit_ip;   //서버의 IP를 작성할 수 있는 EditText
    String name;
    String text;
    boolean isConnected=true;




    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findroom);

        text_msg=(TextView)findViewById(R.id.text_massage_from_server);
        connected_information = (TextView)findViewById(R.id.connected_information_text);
           edit_msg=(EditText)findViewById(R.id.edit_message_to_server);
        edit_ip=(EditText)findViewById(R.id.edit_addressofserver);
        edit_ip.setText(ip);
        name = getIntent().getStringExtra("player_name");


        new Thread( new Runnable(){
            public void run(){
                Log.d("여기/", "emf");

                int server_port = 9999;
                byte[] message = new byte[1500];

                try{
                    Log.d("여기2222", "emf");
                    DatagramPacket p = new DatagramPacket(message, message.length);
                    DatagramSocket s = new DatagramSocket(server_port);
                    Log.d("여기33333", "데이터 받아오는 통로는 만들어짐");

                    s.receive(p);
                    Log.d("여기33333", "emf");
                    text = new String(message, 0, p.getLength());
                    Log.d("제발","message:" + text);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connected_information.setText("방장의 ip는 " + text + "입니당");  // 브로드 캐스트로 받은 방장 ip
                            edit_ip.setText(text);
                        }
                    });

                    s.close();
                }catch(Exception e){
                    Log.d("제발","error  " + e.toString());
                }
            }
        }).start();


    }

    //
    public void mOnClick(View v) {
//
        switch (v.getId()) {
            case R.id.btn_connectserver://게임 참가 버튼
                ip = edit_ip.getText().toString();// 서버 IP 주소가 작성되어 있는 EditText에서 서버 IP 얻어오기

                Intent intent = new Intent(FindRoomActivity.this, PlayGameActivity.class);
                //intent.putExtra("OBJECT", myPlayer);
                intent.putExtra("MYTYPE",0);
                intent.putExtra("CLIENTNICKNAME",name);
                intent.putExtra("IPADDRESS", ip);
                startActivity(intent);

//                new Thread(new Runnable() {
//                    @Override
//
//                    public void run() {
//                        // 소켓 연결
//                        try {
//                            ip = edit_ip.getText().toString();// 서버 IP 주소가 작성되어 있는 EditText에서 서버 IP 얻어오기
//                            //서버와 연결하는 소켓 생성
//
//                            socket= new Socket(InetAddress.getByName(ip), PORT );
//                            Log.d("client 서버", "connected");
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    connected_information.setText("방장에게 연결 성공"); //소켓 연결 성공
//                                }
//                            });
//
//                            //서버와 메세지를 주고받을 통로 구축
//                            is=new DataInputStream(socket.getInputStream());
//                            os=new DataOutputStream(socket.getOutputStream());
//
//                            os.writeUTF(name);
//
//                            while(true)
//                            {
//                                runOnUiThread(new Runnable() {
//                                    String str2 = is.readUTF();
//                                    @Override
//                                    public void run() {
//                                        Log.d("RunONUIThread","Come IN");
//                                        if(str2.equals("START")){
//                                            Log.d("Click","OK");
//                                            Intent intent = new Intent(FindRoomActivity.this, PlayGameActivity.class);
//                                            intent.putExtra("OBJECT", myPlayer);
//                                            intent.putExtra("MYTYPE",0);
//                                            intent.putExtra("IPADDRESS", ip);
//                                            //Thread종료
//                                            startActivity(intent);
//                                        }else{
//                                            Log.d("Player","OK");
//                                            String[] temp = str2.split("&");
//                                            if(temp[0].equals(name)){
//                                                //나에게 온 메세지..
//                                                if(temp[1].equals("PLAYER")){
//                                                    myPlayer = new Player(temp[2],temp[3],Integer.parseInt(temp[4]), name); //Server -> Game Activity에 같이 넘겨주기
//                                                    text_msg.setText(myPlayer.getMyColor());
//                                                }
//                                            }else if(temp[0].equals("MESSAGE")){
//                                                text_msg.setText(str2);
//                                            }
//                                        }
//                                    }
//                                });
//
//                                //System.out.println(str2);
//
//                            }
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }//run method...
//                }).start();//Thread 실행..
                break;
            case R.id.btn_send_client: //서버로 메세지 전송하기

                if(os==null) return;   //서버와 연결되어 있지 않다면 전송불가
                //네트워크 작업이므로 Thread 생성

                new Thread(new Runnable() {
                    @Override

                    public void run() {
                        //서버로 보낼 메세지 EditText로 부터 얻어오기

                        String msg= edit_msg.getText().toString();
                        try {
                            os.writeUTF(msg);  //서버로 메세지 보내기.UTF 방식으로(한글 전송가능...)
                            os.flush();        //다음 메세지 전송을 위해 연결통로의 버퍼를 지워주는 메소드..
                        } catch (IOException e) {
                            // TODO Auto-generated catch block

                            e.printStackTrace();

                        }
                    }//run method..
                }).start(); //Thread 실행..
                break;
        }
    }

    public String getLocalIpAddress () // 현재 안드로이드의 IP address 를 찾아주는 함
    {
        final String IP_NONE = "N/A";
        final String WIFI_DEVICE_PREFIX = "eth";

        String LocalIP = IP_NONE;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (LocalIP.equals(IP_NONE))
                            LocalIP = inetAddress.getHostAddress().toString();
                        else if (intf.getName().startsWith(WIFI_DEVICE_PREFIX))
                            LocalIP = inetAddress.getHostAddress().toString();
                    }
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        LocalIP = inetAddress.getHostAddress().toString();
                    }

                }
            }


            Log.d("서버 IP", LocalIP);
        } catch (SocketException e) {
            Log.e("뀨", "getLocalIpAddress Exception:" + e.toString());
        }
        return LocalIP;
    }


}