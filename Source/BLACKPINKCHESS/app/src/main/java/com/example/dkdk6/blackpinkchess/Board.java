package com.example.dkdk6.blackpinkchess;


import android.util.Log;

import com.example.dkdk6.blackpinkchess.pieces.Bishop;
import com.example.dkdk6.blackpinkchess.pieces.King;
import com.example.dkdk6.blackpinkchess.pieces.Knight;
import com.example.dkdk6.blackpinkchess.pieces.Pawn;
import com.example.dkdk6.blackpinkchess.pieces.Piece;
import com.example.dkdk6.blackpinkchess.pieces.Queen;
import com.example.dkdk6.blackpinkchess.pieces.Rook;
import com.example.dkdk6.blackpinkchess.pieces.Temp;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;


public class Board {

    static Piece[][] BOARD;

    public Board() {
        this.BOARD = new Piece[14][14];

    }


    public static Piece getPiece(Coordinate c) {
        /*Return this Location's Piece information*/
        return BOARD[c.x][c.y];
    }

    /*
     * Created by Dokyeong Kwon
     * load->initialBoard
     * */
    public Piece[][] initialBoard() {
        Log.d("mover","I'ma new!");
        /*
         * To-Do
         * 1. Map 생성 후 초기 Map Return (Piece Array Return)
         * Piece : coordinate, color, piece_id
         * */
        int piece_id[][] = {
                {-1,-1,-1,23,21,22,25,24,22,21,23,-1,-1,-1}, //1
                {-1,-1,-1,20,20,20,20,20,20,20,20,-1,-1,-1},//2
                {-1,-1,-1,6,6,6,6,6,6,6,6,-1,-1,-1},//3
                {13,10,6,6,6,6,6,6,6,6,6,6,30,33},//4
                {11,10,6,6,6,6,6,6,6,6,6,6,30,31},//5
                {12,10,6,6,6,6,6,6,6,6,6,6,30,32},//6
                {14,10,6,6,6,6,6,6,6,6,6,6,30,35},//7
                {15,10,6,6,6,6,6,6,6,6,6,6,30,34},//8
                {12,10,6,6,6,6,6,6,6,6,6,6,30,32},//9
                {11,10,6,6,6,6,6,6,6,6,6,6,30,31},//10
                {13,10,6,6,6,6,6,6,6,6,6,6,30,33},//11
                {-1,-1,-1,6,6,6,6,6,6,6,6,-1,-1,-1},//12
                {-1,-1,-1,0,0,0,0,0,0,0,0,-1,-1,-1},//13
                {-1,-1,-1,3,1,2,4,5,2,1,3,-1,-1,-1}//14

        };

        for(int i=0; i<14; i++){
            for(int j=0; j<14; j++) {
                Coordinate c;
                if (piece_id[i][j] == 6||piece_id[i][j]==-1){
                    if(piece_id[i][j]==-1){
                        c = new Coordinate(i, j, 0);
                        BOARD[i][j] = new Temp(c, "T", piece_id[i][j],  -1);

                    }else{
                        c = new Coordinate(i, j, piece_id[i][j] / 10);
                        BOARD[i][j] = new Temp(c, "T", piece_id[i][j],  -1);
                    }
                }else{
                    c = new Coordinate(i, j, piece_id[i][j] / 10);
                    switch (piece_id[i][j] % 10) {
                        case 0:
                            /*Team Check해서 넣어주기*/
                            if(piece_id[i][j]/10==0){
                                BOARD[i][j] = new Pawn(c, "W", piece_id[i][j], R.drawable.w0);
                            }else if(piece_id[i][j]/10==1){
                                BOARD[i][j] = new Pawn(c, "R", piece_id[i][j], R.drawable.r0);
                            }else if(piece_id[i][j]/10==2){
                                BOARD[i][j] = new Pawn(c, "B", piece_id[i][j], R.drawable.b0);
                            }else if(piece_id[i][j]/10==3){
                                BOARD[i][j] = new Pawn(c, "G", piece_id[i][j], R.drawable.g0);
                            }
                            break;
                        case 1:
                            if(piece_id[i][j]/10==0){
                                BOARD[i][j] = new Knight(c, "W", piece_id[i][j], R.drawable.w1);
                            }else if(piece_id[i][j]/10==1){
                                BOARD[i][j] = new Knight(c, "R", piece_id[i][j], R.drawable.r1);
                            }else if(piece_id[i][j]/10==2){
                                BOARD[i][j] = new Knight(c, "B", piece_id[i][j], R.drawable.b1);
                            }else if(piece_id[i][j]/10==3){
                                BOARD[i][j]= new Knight(c, "G", piece_id[i][j], R.drawable.g1);
                            }
                            break;
                        case 2:

                            if(piece_id[i][j]/10==0){
                                BOARD[i][j]= new Bishop(c, "W", piece_id[i][j], R.drawable.w2);
                            }else if(piece_id[i][j]/10==1){
                                BOARD[i][j] = new Bishop(c, "R", piece_id[i][j], R.drawable.r2);
                            }else if(piece_id[i][j]/10==2){
                                BOARD[i][j] = new Bishop(c, "B", piece_id[i][j], R.drawable.b2);
                            }else if(piece_id[i][j]/10==3){
                                BOARD[i][j] = new Bishop(c, "G", piece_id[i][j], R.drawable.g2);
                            }

                            break;

                        case 3:
                            if(piece_id[i][j]/10==0){
                                BOARD[i][j] = new Rook(c, "W", piece_id[i][j], R.drawable.w3);
                            }else if(piece_id[i][j]/10==1){
                                BOARD[i][j] = new Rook(c, "R", piece_id[i][j], R.drawable.r3);
                            }else if(piece_id[i][j]/10==2){
                                BOARD[i][j] = new Rook(c, "B", piece_id[i][j], R.drawable.b3);
                            }else if(piece_id[i][j]/10==3){
                                BOARD[i][j] = new Rook(c, "G", piece_id[i][j], R.drawable.g3);
                            }

                            break;

                        case 4:

                            if(piece_id[i][j]/10==0){
                                BOARD[i][j] = new Queen(c, "W", piece_id[i][j], R.drawable.w4);
                            }else if(piece_id[i][j]/10==1){
                                BOARD[i][j] = new Queen(c, "R", piece_id[i][j], R.drawable.r4);
                            }else if(piece_id[i][j]/10==2){
                                BOARD[i][j] = new Queen(c, "B", piece_id[i][j], R.drawable.b4);
                            }else if(piece_id[i][j]/10==3){
                                BOARD[i][j] = new Queen(c, "G", piece_id[i][j], R.drawable.g4);
                            }

                            break;
                        case 5:

                            if(piece_id[i][j]/10==0){
                                BOARD[i][j] = new King(c, "W", piece_id[i][j], R.drawable.w5);
                            }else if(piece_id[i][j]/10==1){
                                BOARD[i][j] = new King(c, "R", piece_id[i][j], R.drawable.r5);
                            }else if(piece_id[i][j]/10==2){
                                BOARD[i][j] = new King(c, "B", piece_id[i][j], R.drawable.b5);
                            }else if(piece_id[i][j]/10==3){
                                BOARD[i][j] = new King(c, "G", piece_id[i][j], R.drawable.g5);
                            }

                            break;
                    }
                }
            }
        }
        return BOARD;
    }

    public Piece[][] getBOARD() {
        return BOARD;
    }

    public static void setBOARD(Piece[][] BOARD) {
        Board.BOARD = BOARD;
    }

    public Piece[][] move(Coordinate old_pos, Coordinate new_pos) {
        Piece p;
        p=BOARD[old_pos.x][old_pos.y];
        for(int i=0;i<p.getPossiblePositions().size();i++)
        {
            if (p.getPossiblePositions().get(i).x==new_pos.x&&p.getPossiblePositions().get(i).y==new_pos.y) // not possible to move there
            {
                if(BOARD[old_pos.x][old_pos.y].piece_id%10==0)
                    BOARD[new_pos.x][new_pos.y]=new Pawn(new Coordinate(new_pos.x, new_pos.y, old_pos.direction),BOARD[old_pos.x][old_pos.y].color,
                            BOARD[old_pos.x][old_pos.y].piece_id,BOARD[old_pos.x][old_pos.y].piece_image);
                else if(BOARD[old_pos.x][old_pos.y].piece_id%10==1)
                    BOARD[new_pos.x][new_pos.y]=new Knight(new Coordinate(new_pos.x, new_pos.y, old_pos.direction),BOARD[old_pos.x][old_pos.y].color,
                            BOARD[old_pos.x][old_pos.y].piece_id,BOARD[old_pos.x][old_pos.y].piece_image);
                else if(BOARD[old_pos.x][old_pos.y].piece_id%10==2)
                    BOARD[new_pos.x][new_pos.y]=new Bishop(new Coordinate(new_pos.x, new_pos.y, old_pos.direction),BOARD[old_pos.x][old_pos.y].color,
                            BOARD[old_pos.x][old_pos.y].piece_id,BOARD[old_pos.x][old_pos.y].piece_image);
                else if(BOARD[old_pos.x][old_pos.y].piece_id%10==3)
                    BOARD[new_pos.x][new_pos.y]=new Rook(new Coordinate(new_pos.x, new_pos.y, old_pos.direction),BOARD[old_pos.x][old_pos.y].color,
                            BOARD[old_pos.x][old_pos.y].piece_id,BOARD[old_pos.x][old_pos.y].piece_image);
                else if(BOARD[old_pos.x][old_pos.y].piece_id%10==4)
                    BOARD[new_pos.x][new_pos.y]=new Queen(new Coordinate(new_pos.x, new_pos.y, old_pos.direction),BOARD[old_pos.x][old_pos.y].color,
                            BOARD[old_pos.x][old_pos.y].piece_id,BOARD[old_pos.x][old_pos.y].piece_image);
                else if(BOARD[old_pos.x][old_pos.y].piece_id%10==5)
                    BOARD[new_pos.x][new_pos.y]=new King(new Coordinate(new_pos.x, new_pos.y, old_pos.direction),BOARD[old_pos.x][old_pos.y].color,
                            BOARD[old_pos.x][old_pos.y].piece_id,BOARD[old_pos.x][old_pos.y].piece_image);
                Coordinate c = new Coordinate(old_pos.x, old_pos.y, 0);
                BOARD[old_pos.x][old_pos.y] = new Temp(c, "T", 6, -1);
                break;
            }
        }
        return BOARD;
    }


    public Piece[][] remove(int color)
    {
        for(int j=0;j<14;j++) {
            for (int k = 0; k < 14; k++) {
                if (BOARD[j][k].piece_id / 10 == color&&BOARD[j][k].piece_id!=-1) {
                    BOARD[j][k] = new Temp(new Coordinate(j, k, 0), "T", 6, -1);
                }
            }
        }
        return BOARD;
    }

    public static boolean isChecked(String myColor)
    {
        ArrayList<String> enemy=new ArrayList<String>();
        List<Coordinate> temp;
        List<Coordinate> allPossiblePositions=new  LinkedList<Coordinate>();
        if(myColor.equals("W")||myColor.equals("B"))
        {
            enemy.add("G");
            enemy.add("R");
        }
        if(myColor.equals("G")||myColor.equals("R"))
        {
            enemy.add("W");
            enemy.add("B");
        }
        int king_x=-1;
        int king_y=-1;
        for(int i=0;i<14;i++) {
            for (int j = 0; j < 14; j++)
            {
                if(BOARD[i][j].color.equals(myColor)&&BOARD[i][j].piece_id%10==5)
                {
                    king_x=i;
                    king_y=j;
                }
                if(BOARD[i][j].color.equals(enemy.get(0))||BOARD[i][j].color==enemy.get(1))
                {
                    temp=BOARD[i][j].getPossiblePositions();
                    for(int k=0;k<temp.size();k++)
                    {
                        allPossiblePositions.add(temp.get(k));
                    }
                }
            }
        }
        for(int i=0;i<allPossiblePositions.size();i++)
        {
            if(allPossiblePositions.get(i).x==king_x&&allPossiblePositions.get(i).y==king_y)
                return true;
        }
        return false;
    }

    public static List<Coordinate> check_kingMove(String myColor)
    {
        ArrayList<String> enemy = new ArrayList<String>();
        List<Coordinate> temp;
        List<Coordinate> allPossiblePositions = new LinkedList<Coordinate>();
        List<Coordinate> newPossiblePositions = new LinkedList<Coordinate>();
        List<Coordinate> kingPossiblePositions = new LinkedList<Coordinate>();
        int king_x=-1;
        int king_y=-1;

        if (myColor.equals("W") || myColor.equals("B"))
        {
            enemy.add("G");
            enemy.add("R");
        }
        if (myColor.equals("G") || myColor.equals("R"))
        {
            enemy.add("W");
            enemy.add("B");
        }

        for(int i=0;i<14;i++) {
            for (int j = 0; j < 14; j++)
            {
                if(BOARD[i][j].color.equals(myColor)&&BOARD[i][j].piece_id%10==5)
                {
                    king_x=i;
                    king_y=j;
                }
                else if(BOARD[i][j].color.equals(enemy.get(0))||BOARD[i][j].color==enemy.get(1))
                {
                    temp=BOARD[i][j].getPossiblePositions();
                    for(int k=0;k<temp.size();k++)
                    {
                        allPossiblePositions.add(temp.get(k));
                    }
                }
            }
        }

        kingPossiblePositions=BOARD[king_x][king_y].getPossiblePositions();

        for(int i=kingPossiblePositions.size()-1;i>=0;i--)
        {
            for(int j=0;j<allPossiblePositions.size();j++)
            {
                if(kingPossiblePositions.get(i).x==allPossiblePositions.get(j).x&&kingPossiblePositions.get(i).y==allPossiblePositions.get(j).y)
                {
                    kingPossiblePositions.remove(i);
                    break;
                }

            }
        }
        // 남은 기물이 있는곳
        for(int m=kingPossiblePositions.size()-1;m>=0;m--)
        {
            int tmp_x=kingPossiblePositions.get(m).x;
            int tmp_y=kingPossiblePositions.get(m).y;
            String tmp_color=BOARD[kingPossiblePositions.get(m).x][kingPossiblePositions.get(m).y].color;
            BOARD[kingPossiblePositions.get(m).x][kingPossiblePositions.get(m).y].color=myColor;
            for(int i=0;i<14;i++) {
                for (int j = 0; j < 14; j++) {
                    if (BOARD[i][j].color.equals(enemy.get(0))|| BOARD[i][j].color.equals(enemy.get(1))) {
                        temp = BOARD[i][j].getPossiblePositions();
                        for (int k = 0; k < temp.size(); k++) {
                            newPossiblePositions.add(temp.get(k));
                        }
                    }
                }
            }
            Log.d("kings", "new"+newPossiblePositions.size()+"");
            for(int j=0;j<newPossiblePositions.size();j++)
            {
                if(kingPossiblePositions.get(m).x==newPossiblePositions.get(j).x&&kingPossiblePositions.get(m).y==newPossiblePositions.get(j).y) {
                    kingPossiblePositions.remove(m);
                    break;
                }

            }
            BOARD[tmp_x][tmp_y].color=tmp_color;
        }

        Log.d("kings", "last"+kingPossiblePositions.size()+"");

        return kingPossiblePositions;
    }

    public static List<Coordinate> check_protectKing(String myColor,int x,int y) {
        ArrayList<String> enemy = new ArrayList<String>();
        List<Coordinate> temp;
        List<Coordinate> allPossiblePositions = new LinkedList<Coordinate>();

        List<Coordinate> answer = new LinkedList<Coordinate>();

        int attack_king_x = -1;
        int attack_king_y = -1;

        int king_x = -1;
        int king_y = -1;

        if (myColor.equals("W") || myColor.equals("B")) {
            enemy.add("G");
            enemy.add("R");
        }
        if (myColor.equals("G") || myColor.equals("R")) {
            enemy.add("W");
            enemy.add("B");
        }


        //왕 위치 파악 king
        for (int i = 0; i < 14; i++) {
            for (int j = 0; j < 14; j++) {
                if (BOARD[i][j].color.equals(myColor) && BOARD[i][j].piece_id % 10 == 5) {
                    king_x = i;
                    king_y = j;
                }
            }
        }

        //왕을 공격하는 말 위치 파악 attack_king
        for (int i = 0; i < 14; i++) {
            for (int j = 0; j < 14; j++) {
                if (BOARD[i][j].color.equals(enemy.get(0)) || BOARD[i][j].color.equals(enemy.get(1))) {
                    temp = BOARD[i][j].getPossiblePositions();
                    for (int k = 0; k < temp.size(); k++) {
                        if (temp.get(k).x == king_x && temp.get(k).y == king_y) {
                            attack_king_x = i;
                            attack_king_y = j;
                        }
                    }
                }
            }

        }

        Log.d("kings", attack_king_x + " ," + attack_king_y);

        //다른 piece가 막을 수 있는지 없는지
        temp = BOARD[x][y].getPossiblePositions();
        for (int k = 0; k < temp.size(); k++) {
            if (temp.get(k).x == attack_king_x && temp.get(k).y == attack_king_y) {
                Coordinate tmp = new Coordinate(attack_king_x, attack_king_y, -1);
                answer.add(tmp);
            }
        }
        return answer;
    }


    public static List<Coordinate> isCheckmate(String myColor)
    {
        ArrayList<String> enemy = new ArrayList<String>();
        List<Coordinate> answer=new LinkedList<Coordinate>();
        List<Coordinate> temp;
        List<Coordinate> allPossiblePositions=new  LinkedList<Coordinate>();
        List<Coordinate> protectKing=new  LinkedList<Coordinate>();
        List<Coordinate> newPossiblePositions=new  LinkedList<Coordinate>();
        if (myColor.equals("W") || myColor.equals("B")) {
            enemy.add("G");
            enemy.add("R");
        }
        if (myColor.equals("G") || myColor.equals("R")) {
            enemy.add("W");
            enemy.add("B");
        }

        int attack_king_x=-1;
        int attack_king_y=-1;

        List<Coordinate> kingPossiblePositions=new  LinkedList<Coordinate>();
        int king_x=-1;
        int king_y=-1;

        //왕 위치 파악 king
        //왕을 보호할 수 있는 구역 파악 protectKing
        for(int i=0;i<14;i++) {
            for (int j = 0; j < 14; j++) {
                if (BOARD[i][j].color.equals(myColor) && BOARD[i][j].piece_id % 10 == 5) {
                    king_x = i;
                    king_y = j;
                } else if (BOARD[i][j].color.equals(myColor) && BOARD[i][j].piece_id % 10 != 5) {
                    temp = BOARD[i][j].getPossiblePositions();
                    for (int k = 0; k < temp.size(); k++) {
                        protectKing.add(temp.get(k));
                    }
                }
            }
        }

        //상대방 갈 수 있는 위치 파악 allPossiblePositions
        //왕을 공격하는 말 위치 파악 attack_king
        for(int i=0;i<14;i++) {
            for (int j = 0; j < 14; j++) {
                if (BOARD[i][j].color.equals(enemy.get(0)) || BOARD[i][j].color.equals(enemy.get(1))) {
                    temp = BOARD[i][j].getPossiblePositions();
                    for (int k = 0; k < temp.size(); k++) {
                        if(temp.get(k).x==king_x&&temp.get(k).y==king_y)
                        {
                            attack_king_x=i;
                            attack_king_y=j;
                        }
                        allPossiblePositions.add(temp.get(k));
                    }
                }
            }

        }

        kingPossiblePositions=BOARD[king_x][king_y].getPossiblePositions();

        //왕이 피할 수 있는지 파악
        for(int i=kingPossiblePositions.size()-1;i>=0;i--)
        {
            for(int j=0;j<allPossiblePositions.size();j++)
            {
                if(kingPossiblePositions.get(i).x==allPossiblePositions.get(j).x&&kingPossiblePositions.get(i).y==allPossiblePositions.get(j).y)
                {
                    kingPossiblePositions.remove(i);
                    break;
                }

            }
        }

        // 남은 기물이 있는곳
        for(int m=kingPossiblePositions.size()-1;m>=0;m--)
        {
            int tmp_x=kingPossiblePositions.get(m).x;
            int tmp_y=kingPossiblePositions.get(m).y;
            String tmp_color=BOARD[kingPossiblePositions.get(m).x][kingPossiblePositions.get(m).y].color;
            BOARD[kingPossiblePositions.get(m).x][kingPossiblePositions.get(m).y].color=myColor;
            for(int i=0;i<14;i++) {
                for (int j = 0; j < 14; j++) {
                    if (BOARD[i][j].color.equals(enemy.get(0)) || BOARD[i][j].color.equals(enemy.get(1))) {
                        temp = BOARD[i][j].getPossiblePositions();
                        for (int k = 0; k < temp.size(); k++) {

                            newPossiblePositions.add(temp.get(k));
                        }
                    }
                }
            }

            for(int j=0;j<newPossiblePositions.size();j++)
            {
                if(kingPossiblePositions.get(m).x==newPossiblePositions.get(j).x&&kingPossiblePositions.get(m).y==newPossiblePositions.get(j).y) {
                    kingPossiblePositions.remove(m);
                    break;
                }

            }
            BOARD[tmp_x][tmp_y].color=tmp_color;
        }
        Log.d("kings", kingPossiblePositions.size()+"");
        if(kingPossiblePositions.size()!=0)
        {
            for(int i=0;i<kingPossiblePositions.size();i++)
            {
                Coordinate tmp=new Coordinate(kingPossiblePositions.get(i).x,kingPossiblePositions.get(i).y,-1);
                answer.add(tmp);
            }
        }

        Log.d("kings", attack_king_x+" ,"+attack_king_y);

        //다른 piece가 왕을 체크하고 있는 말을 잡을 수 있는지
        for(int i=0;i<14;i++) {
            for (int j = 0; j < 14; j++) {
                if (BOARD[i][j].color == myColor && BOARD[i][j].piece_id % 10 != 5) {
                    temp = BOARD[i][j].getPossiblePositions();
                    for (int k = 0; k < temp.size(); k++) {
                        if(temp.get(k).x==attack_king_x&&temp.get(k).y==attack_king_y)
                        {
                            Coordinate tmp=new Coordinate(i,j,-1);
                            answer.add(tmp);
                        }
                    }
                }
            }
        }
//        //다른 piece가 왕을 체크하고 있는 말의 길을 방해할 수 있는지
//        String tmp_Color;
//        for(int i=0;i<protectKing.size();i++){
//            tmp_Color=BOARD[protectKing.get(i).x][kingPossiblePositions.get(i).y].color;
//            BOARD[protectKing.get(i).x][protectKing.get(i).y].color=myColor;
//
//            for(int j=0;j<BOARD[attack_king_x][attack_king_y].getPossiblePositions().size();j++)
//            {
//                if(BOARD[attack_king_x][attack_king_y].getPossiblePositions().get(j).x==king_x&&BOARD[attack_king_x][attack_king_y].getPossiblePositions().get(j).y==king_y)
//                {
//                    Coordinate tmp=new Coordinate(protectKing.get(i).x,protectKing.get(i).y);
//                    answer.add(tmp);
//                    break;
//                }
//            }
//            BOARD[protectKing.get(i).x][protectKing.get(i).y].color=tmp_Color;
//        }
        return answer;
    }

    public static boolean cannotMove(String myColor)
    {
        ArrayList<String> enemyColor=new ArrayList<String>();
        if(myColor.equals("W")||myColor.equals("B"))
        {
            enemyColor.add("G");
            enemyColor.add("R");
        }
        if(myColor.equals("G")||myColor.equals("R"))
        {
            enemyColor.add("B");
            enemyColor.add("W");
        }
        List<Coordinate> temp;
        List<Coordinate> allPossiblePositions=new  LinkedList<Coordinate>();
        List<Coordinate> newPossiblePositions=new  LinkedList<Coordinate>();
        List<Coordinate> kingPossiblePositions=new  LinkedList<Coordinate>();

        int king_x=-1;
        int king_y=-1;
        for(int i=0;i<14;i++) {
            for (int j = 0; j < 14; j++)
            {
                if(BOARD[i][j].color.equals(myColor)&&BOARD[i][j].piece_id%10==5)
                {
                    king_x=i;
                    king_y=j;
                }
                else if(BOARD[i][j].color.equals(myColor)&&BOARD[i][j].getPossiblePositions().size()!=0)
                {
                    return false;
                }

                if(BOARD[i][j].color.equals(enemyColor.get(0))||BOARD[i][j].color.equals(enemyColor.get(1)))
                {
                    temp=BOARD[i][j].getPossiblePositions();
                    for(int k=0;k<temp.size();k++)
                    {
                        allPossiblePositions.add(temp.get(k));
                    }
                }
            }
        }
        Log.d("kings error", king_x+" "+king_y);
        Log.d("kings error", BOARD[king_x][king_y].getPossiblePositions().size()+"");

        kingPossiblePositions=BOARD[king_x][king_y].getPossiblePositions();


        for(int i=kingPossiblePositions.size()-1;i>=0;i--)
        {
            for(int j=0;j<allPossiblePositions.size();j++)
            {
                if(kingPossiblePositions.get(i).x==allPossiblePositions.get(j).x&&kingPossiblePositions.get(i).y==allPossiblePositions.get(j).y)
                {
                    kingPossiblePositions.remove(i);
                    break;
                }

            }
        }
        // 남은 기물이 있는곳
        for(int m=kingPossiblePositions.size()-1;m>=0;m--)
        {
            int tmp_x=kingPossiblePositions.get(m).x;
            int tmp_y=kingPossiblePositions.get(m).y;
            String tmp_color=BOARD[kingPossiblePositions.get(m).x][kingPossiblePositions.get(m).y].color;
            BOARD[kingPossiblePositions.get(m).x][kingPossiblePositions.get(m).y].color=myColor;
            for(int i=0;i<14;i++) {
                for (int j = 0; j < 14; j++) {
                    if (BOARD[i][j].color.equals(enemyColor.get(0)) || BOARD[i][j].color.equals(enemyColor.get(1))) {
                        temp = BOARD[i][j].getPossiblePositions();
                        for (int k = 0; k < temp.size(); k++) {
                            newPossiblePositions.add(temp.get(k));
                        }
                    }
                }
            }
            Log.d("kings", "new 갯수"+newPossiblePositions.size()+"");
            for(int j=0;j<newPossiblePositions.size();j++)
            {
                //Log.d("kings", kingPossiblePositions.size()+"");
                if(kingPossiblePositions.get(m).x==newPossiblePositions.get(j).x&&kingPossiblePositions.get(m).y==newPossiblePositions.get(j).y) {
                    kingPossiblePositions.remove(m);
                    break;
                }

            }
            BOARD[tmp_x][tmp_y].color=tmp_color;
        }

        Log.d("kings", kingPossiblePositions.size()+"");
        if(kingPossiblePositions.size()==0)
            return true;
        else
            return false;
    }


}