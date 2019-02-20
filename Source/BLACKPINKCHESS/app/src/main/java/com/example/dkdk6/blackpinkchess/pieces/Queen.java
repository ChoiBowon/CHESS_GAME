package com.example.dkdk6.blackpinkchess.pieces;

import com.example.dkdk6.blackpinkchess.Coordinate;


import java.util.List;


public class Queen extends  Piece {

    public Queen(final Coordinate p, final String color, int id, int piece_image)
    {
        super(p,color, id, piece_image);
    }


    /*
     * Move or attack any number of squares diagonally (Bishop)
     * or horizontally or vertically (Rook)
     *
     * */

    public List<Coordinate> getPossiblePositions() {
        List<Coordinate> re = Rook.moveStraight(this);
        re.addAll(Bishop.moveDiagonal(this));
        return re;
    }

}
