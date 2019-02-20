package com.example.dkdk6.blackpinkchess.pieces;


import com.example.dkdk6.blackpinkchess.Coordinate;

import java.util.LinkedList;
import java.util.List;

public class Knight extends  Piece {

    public Knight(final Coordinate p, final String color, int id, int piece_image)
    {
        super(p,color, id, piece_image);
    }

    /*
     * Move two squares horizontal then one square vertical or two squares vertical then one square horizontal
     * It can jump over other pieces
     * */

    public List<Coordinate> getPossiblePositions() {
        List<Coordinate> re = new LinkedList<Coordinate>();
        Coordinate c = new Coordinate(position.x + 2, position.y + 1);
        if (c.isValid() && !sameTeam(c)) re.add(c);

        c = new Coordinate(position.x + 2, position.y - 1);
        if (c.isValid() && !sameTeam(c)) re.add(c);

        c = new Coordinate(position.x - 2, position.y + 1);
        if (c.isValid() && !sameTeam(c)) re.add(c);

        c = new Coordinate(position.x - 2, position.y - 1);
        if (c.isValid() && !sameTeam(c)) re.add(c);

        c = new Coordinate(position.x + 1, position.y + 2);
        if (c.isValid() && !sameTeam(c)) re.add(c);

        c = new Coordinate(position.x - 1, position.y + 2);
        if (c.isValid() && !sameTeam(c)) re.add(c);

        c = new Coordinate(position.x + 1, position.y - 2);
        if (c.isValid() && !sameTeam(c)) re.add(c);

        c = new Coordinate(position.x - 1, position.y - 2);
        if (c.isValid() && !sameTeam(c)) re.add(c);

        return re;
    }

}
