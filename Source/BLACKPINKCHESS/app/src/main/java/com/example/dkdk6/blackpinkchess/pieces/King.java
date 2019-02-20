package com.example.dkdk6.blackpinkchess.pieces;

import com.example.dkdk6.blackpinkchess.Coordinate;

import java.util.LinkedList;
import java.util.List;

public class King extends  Piece {

    public King(final Coordinate p, final String color, int id, int piece_image)
    {
        super(p,color, id, piece_image);
    }

    /*
     * Move one square diagonally or horizontally or vertically
     * */
    public List<Coordinate> getPossiblePositions() {
        List<Coordinate> re = new LinkedList<Coordinate>();
        int x = position.x;
        int y = position.y;
        Coordinate c;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                c = new Coordinate(x + i, y + j);
                if (c.isValid() && !sameTeam(c)) {
                    re.add(c);
                }
            }
        }
        return re;
    }
}
