package com.example.dkdk6.blackpinkchess.pieces;

import com.example.dkdk6.blackpinkchess.Coordinate;

import java.util.LinkedList;
import java.util.List;

public class Temp extends Piece {
    public Temp(final Coordinate p, final String color, int id, int piece_image)
    {
        super(p,color, id, piece_image);

    }
    public List<Coordinate> getPossiblePositions() {
        List<Coordinate> re = new LinkedList<Coordinate>();
        return re;
    }
}
