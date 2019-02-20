package com.example.dkdk6.blackpinkchess.Activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.dkdk6.blackpinkchess.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by choibowon on 2018. 12. 11..
 */

public class ServerService extends Service {
    private IBinder mBinder = new MyBinder();
    ArrayList<String> playInfolist = new ArrayList<>();
    static final int PORT = 10001;
    ServerSocket serversocket;
    User user = new User();
    String ServerNickname;
    Socket socket;
    DataInputStream is;
    DataOutputStream os;
    String ruleMsg;
    public int var = 777;
    int ackcount = 0;
    public String getServerNickname() {
        return ServerNickname;
    }

    public void setServerNickname(String serverNickname) {
        ServerNickname = serverNickname;
    }

    class MyBinder extends Binder {
        ServerService getService() {
            return ServerService.this;
        }
    }

    // 브로드캐스트 ip 구하는 곳
    public InetAddress getBroadcastAddress() {
        InetAddress broadcastAddress = null;
        try {
            Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces();
            while (broadcastAddress == null
                    && networkInterface.hasMoreElements()) {
                NetworkInterface singleInterface = networkInterface
                        .nextElement();
                String interfaceName = singleInterface.getName();
                if (interfaceName.contains("wlan0")
                        || interfaceName.contains("eth0")) {
                    for (InterfaceAddress infaceAddress : singleInterface.getInterfaceAddresses()) {
                        broadcastAddress = infaceAddress.getBroadcast();
                        if (broadcastAddress != null) {
                            break;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return broadcastAddress;
    }

    // 현재 안드로이드의 IP address 를 찾아주는 역할
    public String getLocalIpAddress() {
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
            Log.d("Server IP", LocalIP);
        } catch (SocketException e) {
            Log.e("Server Catch", "getLocalIpAddress Exception:" + e.toString());
        }
        return LocalIP;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //액티비티에서 bindServer()를 실행하면 호출됨
        final boolean wait = false;
        Log.e("LOG", "onBind()");
        String getData = intent.getStringExtra("Tag");
        Log.e("LOG", getData + "");
        if(getData.equals("MakeRoom")){
            setServerNickname(intent.getStringExtra("Name"));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 브로드캐스트로 방장이 ip 날려주는 부분
                    final String messageStr = getLocalIpAddress(); // 방장의 ip
                    int count = 0,tt=100;
                    Thread thread[] = new Thread[10];             //접속하는 각각의 Client로부터 데이터를 읽어들이고 데이터전송
                    int server_port = 9999; //port
                    try {
                        DatagramSocket s = new DatagramSocket();
                        InetAddress local = getBroadcastAddress(); //브로드캐스트 ip
                        Log.d("boradcast local", local.toString());
                        int msg_length = messageStr.length();
                        byte[] message = messageStr.getBytes();
                        DatagramPacket p = new DatagramPacket(message, msg_length, local, server_port);
                        while(tt>0){
                            s.send(p); //같은 네트워크의 단말기들에 방장의 ip 보냄
                            tt--;
                            Log.d("boradcast local", local.toString()+"count "+tt);

                        }
                        sendMessage("SERVER&" + getLocalIpAddress()); // 액티비티로 보내주기 위한 곳
                    } catch (Exception e) {
                        Log.d("제발", "error  " + e.toString());
                    }
                    // Client 들이 방장의 ip 를 받으면 연결되도록 소켓 생성
                    try {
                        serversocket = new ServerSocket(PORT);
                        Log.d("소켓", "생성됌");
                        //Server의 메인쓰레드는 게속해서 사용자의 접속을 받음
                        while (true) {
                            socket = serversocket.accept();
                            is = new DataInputStream(socket.getInputStream()); //클라이언트로 부터 메세지를 받기 위한 통로
                            os = new DataOutputStream(socket.getOutputStream()); //클라이언트로 메세지를 보내기 위한 통로
                            Log.d("소켓", "대기");
                            thread[count] = new Thread(new ServerService.Receiver(user, socket));
                            thread[count].start();
                            count++;
                        }
                    } catch (Exception e) {
                    }
                }//run method...
            }).start(); //Thread 실행..
        }else if (getData.equals("GAMESTART")){

            Log.d("ServerService", getData);
        }else if (getData.equals("SEVERMESSAGE")) {
            Log.d("ServerService", getData);
        }
        return mBinder;//서비스 객체를 리턴
    }

    public void connectionService(String data){ // Server가 Clients들에게 메시지를 보내는 상황.
        ruleMsg = data;
        Log.d("ServerService", "CONNECTION");
        sendToServer(ruleMsg);
        try {
            user.sendAllClientMsg(ruleMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) { //방 만들때만 쓸 애임
        Log.d("messageService", "Broadcasting message");
        Intent intent = new Intent("custom-event-name");
        intent.putExtra("message", msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void sendToServer(String msg) { //PlayGameActivity 에서는 요거 씀 sendToServer
        Log.d("sendToServer", msg);
        Intent qtintent = new Intent("server-event-name");
        qtintent.putExtra("MOVING", msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(qtintent);
    }

    public interface ICallback {
        //String msg = "test data";
        public void recvData(String msg);
    }

    private ICallback mCallback;

    public void registerCallback(ICallback cb) {
        mCallback = cb;
    }

    //메세지 수신
    public class Receiver implements Runnable {
        Socket socket;
        DataInputStream in;
        String name;
        User user = new User();
        String msg;
        public Receiver(ServerService.User user, Socket socket) throws Exception {
                Log.d("SERVER", "Receiver");
                this.user = user;
                this.socket = socket;
                //접속한 Client로부터 데이터를 읽어들이기 위한 DataInputStream 생성
                in = new DataInputStream(socket.getInputStream());
                String rmsg = in.readUTF();
                this.name = rmsg;
                this.user.AddClient(name, socket);
            }
            public void run() {
                try {
                    sendToServer(msg);
                    Log.d("CLIENTS","IN RECEIVER"+msg);
                    user.sendAllClientMsg(msg);
                } catch (Exception e) {
                    //Exception이 발생했다는 건 사용자가 접속을 끊었다는 거. 채팅방에서 사용자를 제거
                    user.RemoveClient(this.name);
                }
            }
    }

    public class User {
        HashMap<String, DataOutputStream> clientmap = new HashMap<String, DataOutputStream>();
        //채팅방의 사용자 관리 위한 Hashmap
        public synchronized void AddClient(String name, Socket socket)            //채팅방 사용자 추가 및
        {                                                                        //채팅방에 남아있는 사용자에게 접속 소식을 알립니다.
            try {
                Log.d("MSG", "in Add client");
                //IPinformation.setText(name + "님이 입장하셨습니다");
                clientmap.put(name, new DataOutputStream(socket.getOutputStream()));
                int count = clientmap.size() - 1;
                sendMessage("PNUMBER&" + clientmap.size()); //Server역할에게 전송
                //sendMessagetq("tqtq&좀가봐");
                sendMsg("PLAYER&" + playInfolist.get(count), name);
            } catch (Exception e) {
            }

        }
        public synchronized void RemoveClient(String name)  //채팅방 사용자 제거 및 채팅방에 존재하는 Client에게 퇴장 소식을 알림
        {
            try {
                clientmap.remove(name);
                sendMsg(name + " 님이 퇴장하셨습니다.", "Server");
                user.sendMsg(name + " 님이 퇴장하셨습니다.", "Server");
                System.out.println("채팅 참여 인원 : " + clientmap.size());
            } catch (Exception e) {
            }
        }
        public synchronized void sendMsg(String msg, String name) throws Exception //채팅방에 있는 사용자에게 메세지를 전송
        {
            Iterator iterator = clientmap.keySet().iterator();
            while (iterator.hasNext()) {
                String clientname = (String) iterator.next();
                clientmap.get(clientname).writeUTF(name + "&" + msg);
            }
        }
        public synchronized void sendAllClientMsg(String msg) throws Exception //채팅방에 있는 사용자에게 메세지를 전송
        {
            Log.d("SERVER ALLCLIENTSMSG", msg);
            Iterator iterator = clientmap.keySet().iterator();
            while (iterator.hasNext()) {
                String clientname = (String) iterator.next();
                clientmap.get(clientname).writeUTF(msg);
            }
        }
    }


    @Override
    public void onCreate() {
        playInfolist.add("R&A&1");
        playInfolist.add("B&B&2");
        playInfolist.add("G&A&3");
        Log.e("LOG", "onCreate()");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("LOG", "onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e("LOG", "onDestroy()");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("LOG", "onUnbind()");
        return super.onUnbind(intent);
    }

}