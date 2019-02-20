package com.example.dkdk6.blackpinkchess.pieces;

import com.example.dkdk6.blackpinkchess.Board;
import com.example.dkdk6.blackpinkchess.Coordinate;

import java.util.List;

public abstract class Piece {

    public Coordinate position;
    public String color;
    public int piece_id;
    public int piece_image;

    Piece(final Coordinate p, final String color, final int id, final int image)
    {
        this.position=p;
        this.color=color;
        this.piece_id=id;
        this.piece_image = image;
    }


    public abstract List<Coordinate> getPossiblePositions();

    public String getColor() {
        return color;
    }

    /*
     * W is White, B is Black, W and B is the same team.
     * R is White, G is Black, R and G is the same team.
     * */
    boolean sameTeam(final Coordinate destination) {
        Piece p = Board.getPiece(destination);
        if((p.color=="W"||p.color=="B")&&(this.color=="W"||this.color=="B"))
            return true;
        if((p.color=="R"||p.color=="G")&&(this.color=="R"||this.color=="G"))
            return true;
        else
            return false;

    }

}
