package com.example.dkdk6.blackpinkchess;

import java.io.Serializable;

public class Player implements Serializable {

    String myColor;
    String myTeam;
    int myID;
    boolean myTurn = false;

    public Player(String RColor, String RTeam, int Rid){
        this.myColor = RColor;
        this.myTeam = RTeam;
        this.myID = Rid;
    }


    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }




    public String getMyColor() {
        return myColor;
    }

    public void setMyColor(String myColor) {
        this.myColor = myColor;
    }

    public String getMyTeam() {
        return myTeam;
    }

    public void setMyTeam(String myTeam) {
        this.myTeam = myTeam;
    }

    public int getMyID() {
        return myID;
    }

    public void setMyID(int myID) {
        this.myID = myID;
    }


}
