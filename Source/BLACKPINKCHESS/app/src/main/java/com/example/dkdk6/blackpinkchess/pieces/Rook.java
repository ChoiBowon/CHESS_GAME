package com.example.dkdk6.blackpinkchess.pieces;

import com.example.dkdk6.blackpinkchess.Board;
import com.example.dkdk6.blackpinkchess.Coordinate;
import java.util.LinkedList;
import java.util.List;

public class Rook extends  Piece {

    public Rook(final Coordinate p, final String color, int id, int piece_image)
    {
        super(p,color, id, piece_image);
    }

    public List<Coordinate> getPossiblePositions() {
        return moveStraight(this);
    }

    /*
     * Move or attack any number of squares horizontally or vertically
     * */
    public static List<Coordinate> moveStraight(final Piece p) {
        List<Coordinate> re = new LinkedList<Coordinate>();

        // move to top
        int x = p.position.x;
        int y = p.position.y + 1;
        Coordinate c = new Coordinate(x, y);
        while (c.isValid() && (Board.getPiece(c) instanceof Temp)) {
            re.add(c);
            y++;
            c = new Coordinate(x, y);
        }
        if (c.isValid() && !p.sameTeam(c)) {
            re.add(c);
        }

        // move to bottom
        y = p.position.y - 1;
        c = new Coordinate(x, y);
        while (c.isValid() && (Board.getPiece(c) instanceof Temp)) {
            re.add(c);
            y--;
            c = new Coordinate(x, y);
        }
        if (c.isValid() && !p.sameTeam(c)) {
            re.add(c);
        }

        // move right
        y = p.position.y;
        x = p.position.x + 1;
        c = new Coordinate(x, y);
        while (c.isValid() &&(Board.getPiece(c) instanceof Temp)) {
            re.add(c);
            x++;
            c = new Coordinate(x, y);
        }
        if (c.isValid() && !p.sameTeam(c)) {
            re.add(c);
        }

        // move left
        x = p.position.x - 1;
        c = new Coordinate(x, y);
        while (c.isValid() && (Board.getPiece(c) instanceof Temp)) {
            re.add(c);
            x--;
            c = new Coordinate(x, y);
        }
        if (c.isValid() && !p.sameTeam(c)) {
            re.add(c);
        }

        return re;
    }


}
