package com.example.dkdk6.blackpinkchess.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dkdk6.blackpinkchess.Board;
import com.example.dkdk6.blackpinkchess.Coordinate;
import com.example.dkdk6.blackpinkchess.Player;
import com.example.dkdk6.blackpinkchess.R;
import com.example.dkdk6.blackpinkchess.pieces.Piece;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static android.os.SystemClock.sleep;

public class PlayGameActivity extends AppCompatActivity {
    private static final int PORT = 10001; //서버에서 설정한 PORT 번호
    private ServerService serverService;
    private boolean isBind;

    static {
        System.loadLibrary("7segment"); //"native-lib");
    }

    /*For Hardward Variable*/

    public native int SSegmentWrite(int data);
    public native int DotWrite(int data);
    public native String LcdWrite(String data1, String data2);
    public native int MotorWrite(int data);
    public native int ButtonRead();
    int hardware, t, result=0, timer=8, num=0, count=1;
    String[] macro = new String[9];
    String data1 = "Current:";
    String data2 = "Next:";
    boolean flag=true, timerFlag=true;

    /*For Network*/
    ServiceConnection conn = new ServiceConnection() {
        @Override // 서비스가 실행될 때 호출
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ServerService.MyBinder myBinder = (ServerService.MyBinder)iBinder;
            serverService = myBinder.getService();
            isBind = true;
            myName = serverService.getServerNickname();

            Log.e("LOG", "onServiceConnected() in PlayerGame"+myName);
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

        }
    };

    /*For Game*/
    ImageButton btn[][] = new ImageButton[14][14];
    List<Coordinate> availablePosition;
    ArrayList<String> Turn=new ArrayList<String>();
    Board play_board =  new Board();
    TextView myturn_txt, nextturn_txt, chatting;
    int i=0, j=0, origin_x, origin_y, new_x, new_y, MYTYPE;
    String request = "empty";
    boolean moveOfPiece = false;
    boolean canClick = false;
    boolean isUpdated = false;
    String Nowturn = "W";
    String Nextturn = "R";
    Socket socket;
    DataInputStream is;
    DataOutputStream os;
    String ip = "0";

    String text;

    int turnCount = 0;

    Player myPlayer;
    String myName;
    Intent serverIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playgame);
        chatting = (TextView)findViewById(R.id.chatting);
        macro[0] = "hi";
        macro[1] = "hello~";
        macro[2] = "Cheer Up!";
        macro[3] = "kkkkkk";
        macro[4] = "Good!";
        macro[5] = "Sorry T.T";
        macro[6] = "I'm angry!!";
        macro[7] = "Happy";
        macro[8] = "Fun Chess!";
        Turn.add("W");
        Turn.add("R");
        Turn.add("B");
        Turn.add("G");
        data1 = "Current: White";
        data2 = "Next: Red";
        LcdWrite(data1,data2);

        /*Get Data*/
        MYTYPE = getIntent().getIntExtra("MYTYPE",0);
        ip = getIntent().getStringExtra("IPADDRESS");
        myturn_txt = (TextView)findViewById(R.id.myColor);
        nextturn_txt = (TextView)findViewById(R.id.nextColor);

        new Thread(new Runnable() {
            @Override
            public void run() {
                        while(true) {
                            num=ButtonRead();
                            sleep(5);
                            if(num!=0) {
                                for (i = 0; i < 9; i++) {
                                    if((num % 10)==1) {result=8-i; break;}
                                    num = num / 10;
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendChatting("CHATTING&"+macro[result]);
                                        chatting.setText(macro[result]);
                                    }
                                });
                                LcdWrite("result : "+result,macro[result]);
                                count--;
                            }
                        }
                    }

        }).start();


        play_board.initialBoard();
        initialDraw();


        if(MYTYPE==1){
            /*For Server*/
            serverIntent = new Intent(PlayGameActivity.this, ServerService.class);
            serverIntent.putExtra("Tag","GAMESTART");
            bindService(serverIntent, conn, Context.BIND_AUTO_CREATE);

            myPlayer = (Player) getIntent().getSerializableExtra("OBJECT");
            myturn_txt.setText(myPlayer.getMyColor());
            nextturn_txt.setText(Nowturn);
            DotWrite(myPlayer.getMyID());//하드웨어에 자신의 아이디 넣어줌
            if(myPlayer.getMyColor().equals(Nowturn)){
                canClick = true;
            }
            timer = 8;
            TimeThread timeThread = new TimeThread();
            Thread timeCheck = new Thread(timeThread);
            timeCheck.start();
            }

            else{
            myName = getIntent().getStringExtra("CLIENTNICKNAME");
            nextturn_txt.setText(Nowturn);
            timer = 8;
            TimeThread timeThread = new TimeThread();
            Thread timeCheck = new Thread(timeThread);
            timeCheck.start();
            Toast.makeText(getApplicationContext(),"I'm Client!"+ip,Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = new Socket(InetAddress.getByName(ip), PORT);
                        Log.d("client 서버_PlayGme", "connected");
                        is = new DataInputStream(socket.getInputStream());
                        os = new DataOutputStream(socket.getOutputStream());
                        os.writeUTF(myName);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try{
                        while (true) {
                            String str2 = is.readUTF();
                            Log.e("readUTF결과", str2);
                            if (str2.equals("MSG")) {
                                Log.d("ServerService","Clienct OK");
                            }else{
                                final String[] temp = str2.split("&");
                                if(temp[0].equals(myName)){
                                    if (temp[1].equals("PLAYER")) {
                                        myPlayer = new Player(temp[2], temp[3], Integer.parseInt(temp[4]));
                                        if (myPlayer.getMyColor().equals(Nowturn)) {
                                            canClick = true;
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                myturn_txt.setText(myPlayer.getMyColor());
                                                DotWrite(myPlayer.getMyID());
                                            }
                                        });
                                    }
                                }else if(temp[0].equals("MOVING")){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),"통신을 받았습니다.", Toast.LENGTH_SHORT).show();
                                            if(temp[1].equals("REMOVE")){
                                                Toast.makeText(getApplicationContext(),"죽은 말이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                Nowturn= temp[3];
                                                Nextturn = temp[4];
                                                if(Nowturn.equals("W")){
                                                    data1 = "Current: White ";
                                                }else if(Nowturn.equals("R")){
                                                    data1 = "Current: Red ";
                                                }else if(Nowturn.equals("G")){
                                                    data1 = "Current: Green";
                                                }if(Nowturn.equals("B")){
                                                    data1 = "Current: Black";
                                                }
                                                if(Nextturn.equals("W")){
                                                    data2 = "Next: White";
                                                }else if(Nextturn.equals("R")){
                                                    data2 = "Next: Red";
                                                }else if(Nextturn.equals("G")){
                                                    data2 = "Next: Green";
                                                }if(Nextturn.equals("B")){
                                                    data2 = "Next: Black";
                                                }
                                                LcdWrite(data1,data2);
                                                nextturn_txt.setText(temp[3]); //-> 말이 움직이는 방향으로 바꿔야함..
                                                REMOVE(temp[2]+"&"+temp[5]+"&"+temp[6]);
                                                if(temp[3].equals(myPlayer.getMyColor())){
                                                    Toast.makeText(getApplicationContext(),"나의 턴 입니다!", Toast.LENGTH_SHORT).show();
                                                    canClick = true;
                                                }else{
                                                    canClick = false;
                                                }
                                                turnCount = Integer.parseInt(temp[7]);
                                            }else {
                                                Nowturn= temp[1];
                                                Nextturn = temp[2];
                                                if(Nowturn.equals("W")){
                                                    data1 = "Current: White ";
                                                }else if(Nowturn.equals("R")){
                                                    data1 = "Current: Red ";
                                                }else if(Nowturn.equals("G")){
                                                    data1 = "Current: Green";
                                                }if(Nowturn.equals("B")){
                                                    data1 = "Current: Black";
                                                }
                                                if(Nextturn.equals("W")){
                                                    data2 = "Next: White";
                                                }else if(Nextturn.equals("R")){
                                                    data2 = "Next: Red";
                                                }else if(Nextturn.equals("G")){
                                                    data2 = "Next: Green";
                                                }if(Nextturn.equals("B")){
                                                    data2 = "Next: Black";
                                                }
                                                LcdWrite(data1,data2);
                                                nextturn_txt.setText(temp[1]); //-> 말이 움직이는 방향으로 바꿔야함..
                                                if(temp[1].equals(myPlayer.getMyColor())){
                                                    canClick = true;
                                                    UPDATE(temp[3]+"&"+temp[4]);
                                                }else{
                                                    canClick = false;
                                                    UPDATE(temp[3]+"&"+temp[4]);
                                                }
                                                turnCount = Integer.parseInt(temp[5]);
                                            }
                                        }
                                    });
                                }else if(temp[0].equals("CHATTING")){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                           chatting.setText(temp[1]);
                                        }
                                    });
                                }else if(temp[0].equals("GAMEOVER")){
                                    Intent gameover = new Intent(PlayGameActivity.this, FinishGameActivity.class);
                                    gameover.putExtra("Result",temp[1]);
                                    startActivity(gameover);
                                    finish();
                                }
                            }
                        }
                    }catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void initialDraw(){
        Piece[][] BOARD = play_board.getBOARD();
        GridLayout chess_background = (GridLayout) findViewById(R.id.chess_background);
        View layoutMainView = (View) this.findViewById(R.id.chess_background);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(36, 36);

        for(i=0; i<14; i++){
            for(j=0; j<14; j++){
                final ImageButton button1 = new ImageButton(this);
                btn[i][j]= button1;
                int text = (i*14)+j;
                button1.setId((i*14)+j);
                button1.setLayoutParams(lp);
                button1.setTag(i+"&"+j);
                if(i%2==0){
                    if(BOARD[i][j].piece_id==-1){
                        button1.setBackgroundColor(Color.WHITE);
                    }else if(j%2==0){
                        button1.setBackgroundColor(Color.GRAY);
                        if(BOARD[i][j].piece_image!=-1){
                            button1.setImageDrawable(getResources().getDrawable(BOARD[i][j].piece_image));
                        }
                    }else{
                        button1.setBackgroundColor(Color.LTGRAY);
                        if(BOARD[i][j].piece_image!=-1){
                            button1.setImageDrawable(getResources().getDrawable(BOARD[i][j].piece_image));
                        }
                    }
                }else{
                    if(BOARD[i][j].piece_id==-1){
                        button1.setBackgroundColor(Color.WHITE);
                    }else if(j%2==0){
                        button1.setBackgroundColor(Color.LTGRAY);
                        if(BOARD[i][j].piece_image!=-1){
                            button1.setImageDrawable(getResources().getDrawable(BOARD[i][j].piece_image));
                        }
                    }else{
                        button1.setBackgroundColor(Color.GRAY);
                        if(BOARD[i][j].piece_image!=-1){
                            button1.setImageDrawable(getResources().getDrawable(BOARD[i][j].piece_image));
                        }
                    }
                }
                if(BOARD[i][j].piece_id!=-1){
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //        Turn : W->R->B->G : 0 1 2 3
                            if(myPlayer.getMyColor().equals(Turn.get(turnCount))){
                                if(moveOfPiece==false){
                                    /*다음 동작으로 움직일 말을 선택한 것.*/
                                    String nowLocation = button1.getTag().toString();
                                    String[] splitData = nowLocation.split("&");
                                    int x = Integer.parseInt(splitData[0]);
                                    int y = Integer.parseInt(splitData[1]);
                                    origin_x = x;
                                    origin_y = y;
                                    Piece[][] GETBOARD = play_board.getBOARD();

                                    //체크이면서 왕 선택 되었을 때
                                    if (play_board.isChecked(Turn.get(turnCount))&&GETBOARD[x][y].piece_id%10==5&&play_board.check_kingMove(Turn.get(turnCount)).size()!=0) {
                                        List<Coordinate> kingMove=play_board.check_kingMove(Turn.get(turnCount));

//                                        turnCount++; //다음 턴으로 넘겨라.
//
//                                        if(turnCount==Turn.size())
//                                            turnCount=0;
                                        moveOfPiece=true;
                                        availablePosition=kingMove;
                                        /*
                                         * 가능한 위치 표시
                                         */
                                        DrawPossiblePositions(GETBOARD,kingMove);
                                    }
                                    //체크이면서 왕이 보호 될 수 있을 때
                                    else if(play_board.isChecked(Turn.get(turnCount))&&play_board.check_protectKing(Turn.get(turnCount),x,y).size()!=0){
                                        List<Coordinate> protectKing=play_board.check_protectKing(Turn.get(turnCount),x,y);

                                        moveOfPiece=true;
                                        availablePosition=protectKing;
                                        /*
                                         * 가능한 위치 표시
                                         */
                                        DrawPossiblePositions(GETBOARD,protectKing);

                                    } else if(play_board.isChecked(Turn.get(turnCount))){
                                        moveOfPiece=false;

                                    }else if(GETBOARD[origin_x][origin_y].getPossiblePositions().size()==0) {
                                        moveOfPiece=false;
                                    }
                                    else if( Turn.get(turnCount).equals(GETBOARD[origin_x][origin_y].color)&&GETBOARD[origin_x][origin_y].piece_id!=6){
                                        Toast.makeText(getApplicationContext(),"다음 위치 선택하세요.", Toast.LENGTH_SHORT).show();
                                        moveOfPiece = true;
                                        availablePosition=GETBOARD[origin_x][origin_y].getPossiblePositions();
                                        DrawPossiblePositions(GETBOARD,availablePosition);
                                    }else if(GETBOARD[origin_x][origin_y].piece_id==6){
                                        Toast.makeText(getApplicationContext(),"잘못된 위치를 선택하셨습니다.", Toast.LENGTH_SHORT).show();
                                        moveOfPiece = false;
                                    }else{
                                        Toast.makeText(getApplicationContext(),"턴이 넘어갔습니다.", Toast.LENGTH_SHORT).show();
                                        moveOfPiece = false;
                                    }
                                }else if(moveOfPiece == true) {
                                    String nowLocation = button1.getTag().toString();
                                    String[] splitData = nowLocation.split("&");
                                    int x = Integer.parseInt(splitData[0]);
                                    int y = Integer.parseInt(splitData[1]);
                                    new_x = x;
                                    new_y = y;
                                    /*다음 동작으로 어디로 가는지 확인할 것.*/
                                    Piece[][] GETBOARD = play_board.getBOARD();
                                    for (int q = 0; q < availablePosition.size(); q++) {
                                        if (availablePosition.get(q).x == new_x && availablePosition.get(q).y == new_y) {

                                            turnCount++;
                                            if (turnCount == Turn.size())
                                                turnCount = 0;

                                            Nowturn = Turn.get(turnCount);
                                            if (turnCount == 3)
                                                Nextturn = Turn.get(0);
                                            else
                                                Nextturn = Turn.get(turnCount + 1);
                                            GETBOARD = play_board.move(GETBOARD[origin_x][origin_y].position, GETBOARD[new_x][new_y].position);
                                            Draw(GETBOARD);
                                            String moveResult = "MOVING&" + Nowturn + "&" + Nextturn + "&" + origin_x + ":" + origin_y + "&" + new_x + ":" + new_y + "&" + turnCount;
                                            sendUpdateCall(moveResult);
                                            moveOfPiece = false;
                                        }
                                    }
                                    if(play_board.isChecked("W")==true&&play_board.isCheckmate("W").size()==0) {
                                        Toast.makeText(getApplicationContext(),"White checkmate", Toast.LENGTH_SHORT).show();
                                        if(!Turn.contains("B"))
                                            sendGameOverCall("Team A is Win");
                                            Toast.makeText(getApplicationContext(),"Black & White Lose", Toast.LENGTH_SHORT).show();
                                        Turn.set(0,"B");
                                        GETBOARD=play_board.remove(0); //
                                        Draw(GETBOARD);
                                        isUpdated = true;
                                        sendRemoveCall("MOVING&REMOVE&"+0+"&"+Nowturn+"&"+Nextturn+"&"+origin_x+":"+origin_y+"&"+new_x+":"+new_y+"&"+turnCount);
                                    }
                                    if(play_board.isChecked("B")==true&&play_board.isCheckmate("B").size()==0) {
                                        Toast.makeText(getApplicationContext(),"Black checkmate", Toast.LENGTH_SHORT).show();

                                        if(!Turn.contains("W"))
                                            sendGameOverCall("Team A is Win");
                                            Toast.makeText(getApplicationContext(),"Black & White Lose", Toast.LENGTH_SHORT).show();

                                        Log.d("PlayerTurnC",turnCount+"");
                                        Turn.set(2,"W");

                                        GETBOARD=play_board.remove(2);
                                        Draw(GETBOARD);
                                        isUpdated = true;
                                        sendRemoveCall("MOVING&REMOVE"+2+"&"+Nowturn+"&"+Nextturn+"&"+origin_x+":"+origin_y+"&"+new_x+":"+new_y+"&"+turnCount);
                                    }

                                    if(play_board.isChecked("R")==true&&play_board.isCheckmate("R").size()==0) {
                                        Toast.makeText(getApplicationContext(),"Red checkmate", Toast.LENGTH_SHORT).show();
                                        if(!Turn.contains("G"))
                                            sendGameOverCall("Team B is Win");
                                            Toast.makeText(getApplicationContext(),"Green & Red Lose", Toast.LENGTH_SHORT).show();
                                        Turn.set(1,"G");

                                        GETBOARD=play_board.remove(1);
                                        Draw(GETBOARD);
                                        isUpdated = true;

                                        sendRemoveCall("MOVING&REMOVE"+1+"&"+Nowturn+"&"+Nextturn+"&"+origin_x+":"+origin_y+"&"+new_x+":"+new_y+"&"+turnCount);
                                    }
                                    if(play_board.isChecked("G")==true&&play_board.isCheckmate("G").size()==0) {
                                        Toast.makeText(getApplicationContext(),"Green checkmate", Toast.LENGTH_SHORT).show();

                                        if(!Turn.contains("R"))
                                            sendGameOverCall("Team B is Win");
                                            Toast.makeText(getApplicationContext(),"Green & Red Lose", Toast.LENGTH_SHORT).show();

                                        Turn.set(3,"R"); //TurnSet도 뿌려줘야됨..
                                        GETBOARD=play_board.remove(3);

                                        Draw(GETBOARD);
                                        isUpdated = true;
                                        sendRemoveCall("MOVING&REMOVE"+3+"&"+Nowturn+"&"+Nextturn+"&"+origin_x+":"+origin_y+"&"+new_x+":"+new_y+"&"+turnCount);                                    }

                                    //stalemate 이후에 끝내기 //서버한테 전송
                                    if(Turn.contains("W")&&Turn.get(turnCount).equals("W")&&!play_board.isChecked("W")&&play_board.cannotMove("W")) {
                                        sendGameOverCall("Draw!");
                                        Toast.makeText(getApplicationContext(),"Stalemate", Toast.LENGTH_SHORT).show();
                                        MotorWrite(1);
                                        try {
                                            Thread.sleep(3000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        MotorWrite(0);
                                    }
                                    if(Turn.contains("G")&&Turn.get(turnCount).equals("G")&&!play_board.isChecked("G")&&play_board.cannotMove("G")) {
                                        sendGameOverCall("Draw!");
                                        Toast.makeText(getApplicationContext(),"Stalemate", Toast.LENGTH_SHORT).show();
                                        MotorWrite(1);
                                        try {
                                            Thread.sleep(3000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        MotorWrite(0);
                                    }
                                    if(Turn.contains("B")&&Turn.get(turnCount).equals("B")&&!play_board.isChecked("B")&&play_board.cannotMove("B")) {
                                        sendGameOverCall("Draw!");
                                        Toast.makeText(getApplicationContext(),"Stalemate", Toast.LENGTH_SHORT).show();
                                        MotorWrite(1);
                                        try {
                                            Thread.sleep(3000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        MotorWrite(0);
                                    }
                                    if(Turn.contains("R")&&Turn.get(turnCount).equals("R")&&!play_board.isChecked("R")&&play_board.cannotMove("R")) {
                                        sendGameOverCall("Draw!");
                                        Toast.makeText(getApplicationContext(),"Stalemate", Toast.LENGTH_SHORT).show();
                                        MotorWrite(1);
                                        try {
                                            Thread.sleep(3000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        MotorWrite(0);
                                    }

                                    //check
                                    if(play_board.isChecked("W")==true&&myPlayer.getMyColor().equals("W")){
                                        Toast.makeText(getApplicationContext(),"White check", Toast.LENGTH_SHORT).show();
                                        MotorWrite(1);
                                        try {
                                            Thread.sleep(3000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        MotorWrite(0);
                                    }
                                    if(play_board.isChecked("B")==true&&myPlayer.getMyColor().equals("B")) {
                                        Toast.makeText(getApplicationContext(),"Black Check", Toast.LENGTH_SHORT).show();
                                        MotorWrite(1);
                                        try {
                                            Thread.sleep(3000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        MotorWrite(0);
                                    }
                                    if(play_board.isChecked("G")==true&&myPlayer.getMyColor().equals("G")) {
                                        Toast.makeText(getApplicationContext(),"Green Check", Toast.LENGTH_SHORT).show();
                                        MotorWrite(1);
                                        try {
                                            Thread.sleep(3000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        MotorWrite(0);
                                    }
                                    if(play_board.isChecked("R")==true&&myPlayer.getMyColor().equals("R")) {
                                        Toast.makeText(getApplicationContext(),"Red Check", Toast.LENGTH_SHORT).show();
                                        MotorWrite(1);
                                        try {
                                            Thread.sleep(3000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        MotorWrite(0);
                                    }
                                    moveOfPiece = false;
                                    canClick = false;
                                    isUpdated = false; //초기화 시점 틀릴 수도 있음.

                                }
                            }else{
                                Toast.makeText(getApplicationContext(),"아직 당신의 턴이 아닙니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                chess_background.addView(button1);
            }
        }

    }

    public void DrawPossiblePositions(Piece[][] BOARD,  List<Coordinate> possiblePoisitions) {

        /*콜하면 화면 업데이트만 함!*/
        GridLayout chess_background = (GridLayout) findViewById(R.id.chess_background);
        View layoutMainView = (View) this.findViewById(R.id.chess_background);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(36, 36);


        for (i = 0; i < 14; i++) {
            for (j = 0; j < 14; j++) {
                if (i % 2 == 0) {
                    if (BOARD[i][j].piece_id == -1) {
                        btn[i][j].setBackgroundColor(Color.WHITE);
                    } else if (j % 2 == 0) {
                        btn[i][j].setBackgroundColor(Color.GRAY);
                        if (BOARD[i][j].piece_image != -1) {
                            btn[i][j].setImageDrawable(getResources().getDrawable(BOARD[i][j].piece_image));
                        } else if (BOARD[i][j].piece_id == 6) {
                            btn[i][j].setImageResource(android.R.color.transparent);
                        }
                    } else {
                        btn[i][j].setBackgroundColor(Color.LTGRAY);
                        if (BOARD[i][j].piece_image != -1) {
                            btn[i][j].setImageDrawable(getResources().getDrawable(BOARD[i][j].piece_image));
                        } else if (BOARD[i][j].piece_id == 6) {
                            btn[i][j].setImageResource(android.R.color.transparent);
                        }
                    }
                } else {
                    if (BOARD[i][j].piece_id == -1) {
                        btn[i][j].setBackgroundColor(Color.WHITE);
                    } else if (j % 2 == 0) {
                        btn[i][j].setBackgroundColor(Color.LTGRAY);
                        if (BOARD[i][j].piece_image != -1) {
                            btn[i][j].setImageDrawable(getResources().getDrawable(BOARD[i][j].piece_image));
                        } else if (BOARD[i][j].piece_id == 6) {
                            btn[i][j].setImageResource(android.R.color.transparent);
                        }
                    } else {
                        btn[i][j].setBackgroundColor(Color.GRAY);
                        if (BOARD[i][j].piece_image != -1) {
                            btn[i][j].setImageDrawable(getResources().getDrawable(BOARD[i][j].piece_image));
                        } else if (BOARD[i][j].piece_id == 6) {
                            btn[i][j].setImageResource(android.R.color.transparent);
                        }
                    }
                }
            }
        }
        for(int i=0;i<possiblePoisitions.size();i++) {
            btn[possiblePoisitions.get(i).x][possiblePoisitions.get(i).y].setBackgroundColor(Color.RED);
        }

        chess_background.invalidate();

    }

    public void UPDATE(String moveInformation){
        Log.d("UPDATE",moveInformation);
        int origin_x, origin_y,new_x,new_y;
        String splitData[] = moveInformation.split("&");
        String origin[] = splitData[0].split(":");
        String newPosition[] = splitData[1].split(":");
        origin_x = Integer.parseInt(origin[0]);
        origin_y = Integer.parseInt(origin[1]);
        new_x = Integer.parseInt(newPosition[0]);
        new_y = Integer.parseInt(newPosition[1]);
        Piece[][] GETBOARD = play_board.getBOARD();
        GETBOARD = play_board.move(GETBOARD[origin_x][origin_y].position,GETBOARD[new_x][new_y].position);
        Draw(GETBOARD);
    }

    public void REMOVE(String moveInformation){ //ID&0:0&1:1
        int removeID = 0;
        int origin_x, origin_y,new_x,new_y;
        String splitData[] = moveInformation.split("&");
        removeID = Integer.parseInt(splitData[0]);
        String origin[] = splitData[1].split(":");
        String newPosition[] = splitData[2].split(":");
        origin_x = Integer.parseInt(origin[0]);
        origin_y = Integer.parseInt(origin[1]);
        new_x = Integer.parseInt(newPosition[0]);
        new_y = Integer.parseInt(newPosition[1]);

        if(removeID==0){
            Turn.set(removeID,"B");
        }else if(removeID==1){
            Turn.set(removeID,"G");
        }else if(removeID==2){
            Turn.set(removeID,"W");
        }else if(removeID==3){
            Turn.set(removeID,"R");
        }

        Piece[][] GETBOARD = play_board.getBOARD();
        GETBOARD=play_board.remove(removeID);
        GETBOARD = play_board.move(GETBOARD[origin_x][origin_y].position,GETBOARD[new_x][new_y].position);
        Draw(GETBOARD);
    }

    public void Draw(Piece[][] BOARD) {
        /*콜하면 화면 업데이트만 함!*/
        GridLayout chess_background = (GridLayout) findViewById(R.id.chess_background);
        View layoutMainView = (View) this.findViewById(R.id.chess_background);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(36, 36);
        for (i = 0; i < 14; i++) {
            for (j = 0; j < 14; j++) {
                if (i % 2 == 0) {
                    if (BOARD[i][j].piece_id == -1) {
                        btn[i][j].setBackgroundColor(Color.WHITE);
                    } else if (j % 2 == 0) {
                        btn[i][j].setBackgroundColor(Color.GRAY);
                        if (BOARD[i][j].piece_image != -1) {
                            btn[i][j].setImageDrawable(getResources().getDrawable(BOARD[i][j].piece_image));
                        } else if (BOARD[i][j].piece_id == 6) {
                            btn[i][j].setImageResource(android.R.color.transparent);
                        }
                    } else {
                        btn[i][j].setBackgroundColor(Color.LTGRAY);
                        if (BOARD[i][j].piece_image != -1) {
                            btn[i][j].setImageDrawable(getResources().getDrawable(BOARD[i][j].piece_image));
                        } else if (BOARD[i][j].piece_id == 6) {
                            btn[i][j].setImageResource(android.R.color.transparent);
                        }
                    }
                } else {
                    if (BOARD[i][j].piece_id == -1) {
                        btn[i][j].setBackgroundColor(Color.WHITE);
                    } else if (j % 2 == 0) {
                        btn[i][j].setBackgroundColor(Color.LTGRAY);
                        if (BOARD[i][j].piece_image != -1) {
                            btn[i][j].setImageDrawable(getResources().getDrawable(BOARD[i][j].piece_image));
                        } else if (BOARD[i][j].piece_id == 6) {
                            btn[i][j].setImageResource(android.R.color.transparent);
                        }
                    } else {
                        btn[i][j].setBackgroundColor(Color.GRAY);
                        if (BOARD[i][j].piece_image != -1) {
                            btn[i][j].setImageDrawable(getResources().getDrawable(BOARD[i][j].piece_image));
                        } else if (BOARD[i][j].piece_id == 6) {
                            btn[i][j].setImageResource(android.R.color.transparent);
                        }
                    }
                }
            }
        }
        chess_background.invalidate();
    }

    public void sendGameOverCall(String msg){
        /*게임이 끝났을 때 서버 전송 내역*/
        String gameReuslt = "GAMEOVER&"+msg;
        if(MYTYPE==1){
            sendBroadCasting(gameReuslt); //Server가 Client들에게 알리는 경우야
        }else{
            sendRequest(gameReuslt); //Client가 Server에게 전체 전송 요청.
        }
    }

    public void sendTurnOutCall(){
        String gameReuslt = "MOVING&"+"Turnout&"+Nowturn+"&"+Nextturn+"&"+origin_x+":"+origin_y+"&"+origin_x+":"+origin_y;
        if(MYTYPE==1){
            sendBroadCasting(gameReuslt); //Server가 Client들에게 알리는 경우야
        }else{
            sendRequest(gameReuslt); //Client가 Server에게 전체 전송 요청.
        }
    }


    public void sendChatting(String msg){
        if(MYTYPE==1){
            sendBroadCasting(msg); //Server가 Client들에게 알리는 경우야
        }else{
            sendRequest(msg); //Client가 Server에게 전체 전송 요청.
        }
    }

    public void sendUpdateCall(String msg){
        if(MYTYPE==1){
            sendBroadCasting(msg); //Server가 Client들에게 알리는 경우야
        }else{
            sendRequest(msg); //Client가 Server에게 전체 전송 요청.
        }
    }
    public void sendRemoveCall(String msg){
        if(MYTYPE==1){
            sendBroadCasting(msg); //Server가 Client들에게 알리는 경우야
        }else{
            sendRequest(msg); //Client가 Server에게 전체 전송 요청.
        }
    }

    public void sendBroadCasting(String msg){
        boolean call = false;
        Log.d("Now Position", msg);
        if(isBind==true){
            Toast.makeText(getApplicationContext(),"통신을 보냈습니다.", Toast.LENGTH_SHORT).show();
            serverService.connectionService(msg); //이게 성공되면 메시지 띠우는거로 가는거야..
            Toast.makeText(getApplicationContext(), "서비스가 연결 중입니다_PlayerActivity", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "서비스가 연결 안됐습니다_PlayerActivity", Toast.LENGTH_SHORT).show();
        }
        Log.d("Check Msg", "OK");
    }

    public void sendRequest(final String requestR){
        request = requestR;
        new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    //서버로 보낼 메세지 EditText로 부터 얻어오기
                    try {
                        os.writeUTF(request);  //서버로 메세지 보내기.UTF 방식으로(한글 전송가능...)
                        os.flush();        //다음 메세지 전송을 위해 연결통로의 버퍼를 지워주는 메소드..
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }//run method..
            }).start(); //Thread 실행..
    }
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("server-event-name"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    //서버로 오는 데이터를 받는다.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Server역할도 들을 수 있게 해주는 부분
            String message = intent.getStringExtra("MOVING");
            Log.d("BROADCAST", "Got message: " + message);
            String[] temp = message.split("&");
            if(temp[0].equals("SERVER")){
                Log.d("receiver", "Got message: " + message);
            }else if(temp[0].equals("PNUMBER")){
                myturn_txt.setText(temp[1]+" ");
            }else if(temp[0].equals("MOVING")){
                Toast.makeText(getApplicationContext(),"통신을 받았습니다.", Toast.LENGTH_SHORT).show();
                Log.d("MOVING","SERVER:"+message);
                if(temp[1].equals("REMOVE")){
                        Toast.makeText(getApplicationContext(),"죽은 말이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        Nowturn= temp[3];
                        Nextturn = temp[4];
                        if(Nowturn.equals("W")){
                            data1 = "Current: White ";
                        }else if(Nowturn.equals("R")){
                            data1 = "Current: Red ";
                        }else if(Nowturn.equals("G")){
                            data1 = "Current: Green";
                        }if(Nowturn.equals("B")){
                            data1 = "Current: Black";
                        }
                        if(Nextturn.equals("W")){
                            data2 = "Next: White";
                        }else if(Nextturn.equals("R")){
                            data2 = "Next: Red";
                        }else if(Nextturn.equals("G")){
                            data2 = "Next: Green";
                        }if(Nextturn.equals("B")){
                            data2 = "Next: Black";
                        }
                        LcdWrite(data1,data2);
                        nextturn_txt.setText(temp[4]); //-> 말이 움직이는 방향으로 바꿔야함..
                        REMOVE(temp[2]+"&"+temp[5]+"&"+temp[6]);
                        if(temp[3].equals(myPlayer.getMyColor())){
                            Toast.makeText(getApplicationContext(),"나의 턴 입니다!", Toast.LENGTH_SHORT).show();
                            canClick = true;
                        }else{canClick = false;}
                        turnCount = Integer.parseInt(temp[7]);
                }else{
                    // 정상적인 턴 입력 왔을 때
                    Nowturn= temp[1];
                    Nextturn = temp[2];
                    if(Nowturn.equals("W")){
                        data1 = "Current: White ";
                    }else if(Nowturn.equals("R")){
                        data1 = "Current: Red ";
                    }else if(Nowturn.equals("G")){
                        data1 = "Current: Green";
                    }if(Nowturn.equals("B")){
                        data1 = "Current: Black";
                    }
                    if(Nextturn.equals("W")){
                        data2 = "Next: White";
                    }else if(Nextturn.equals("R")){
                        data2 = "Next: Red";
                    }else if(Nextturn.equals("G")){
                        data2 = "Next: Green";
                    }if(Nextturn.equals("B")){
                        data2 = "Next: Black";
                    }
                    LcdWrite(data1,data2);
                    nextturn_txt.setText(temp[1]); //-> 말이 움직이는 방향으로 바꿔야함..
                    if(temp[1].equals(myPlayer.getMyColor())){
                        Toast.makeText(getApplicationContext(),"나의 턴 입니다!", Toast.LENGTH_SHORT).show();
                        canClick = true;
                        Log.d("BROADCAST","CAN MOVE");

                        UPDATE(temp[3]+"&"+temp[4]);
                    }else{
                        Log.d("BROADCAST","CANn't MOVE");

                        canClick = false;
                        UPDATE(temp[3]+"&"+temp[4]);
                    }
                    turnCount = Integer.parseInt(temp[5]);
                }

            }else if(temp[0].equals("CHATTING")) {
                chatting.setText(temp[1]);
            }else if(temp[0].equals("GAMEOVER")){
                Intent gameover = new Intent(PlayGameActivity.this, FinishGameActivity.class);
                gameover.putExtra("Result",temp[1]);
                startActivity(gameover);
                finish();
            }
        }
    };

    public class TimeThread implements Runnable {
        public void run() {
            //t=0;
            //timer = 30;
            while (timerFlag) {
                try {
                    for (t = timer; t > 0; t--) {
                        SSegmentWrite(t);
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(t==0) {timerFlag=false;
                LcdWrite("timer thread", "finish");
                sendTurnOutCall();
                }
           }
        }
    }

}
