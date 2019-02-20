package com.example.dkdk6.blackpinkchess;

public class Coordinate {
    public final int x, y;
    public int direction;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //방향성
    public Coordinate(int x, int y, int direction) {

        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    //범위안에 들어오는지 체크
    public boolean isValid() {
        return (x >= 0 && y >= 0 && x <= 13 && y <= 13) &&
                !(x <= 2 && y <= 2) && // upper left
                !(x >= 11 && y <= 2) && // upper right
                !(x <= 2 && y >= 11) && // bottom left
                !(x >= 11 && y >= 11); // bottom right

    }


}
