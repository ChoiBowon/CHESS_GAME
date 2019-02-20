package com.example.dkdk6.blackpinkchess.pieces;


import com.example.dkdk6.blackpinkchess.Board;
import com.example.dkdk6.blackpinkchess.Coordinate;

import java.util.LinkedList;
import java.util.List;

public class Bishop extends Piece {
    /*
    * Created by Dokyeong Kown
    * */
    public Bishop(final Coordinate p, final String color, int id, int piece_image)
    {
        super(p,color, id, piece_image);
    }

    public List<Coordinate> getPossiblePositions() {
        return moveDiagonal(this);
    }

    /*
     * Move any number of squares diagonally or horizontally or vertically
     * If it meets other piece and the other piece is not same team, then it can attack.
     * */
    public static List<Coordinate> moveDiagonal(final Piece p) {
        List<Coordinate> re = new LinkedList<Coordinate>();
        int x = p.position.x + 1;
        int y = p.position.y + 1;
        Coordinate c = new Coordinate(x, y);


        // move to top right
        while (c.isValid()&&(Board.getPiece(c) instanceof Temp) ) {
            re.add(c);
            y++;
            x++;
            c = new Coordinate(x, y);
        }
        if (c.isValid() && !p.sameTeam(c)) {
            re.add(c);
        }

        // move to bottom right
        x = p.position.x + 1;
        y = p.position.y - 1;
        c = new Coordinate(x, y);
        while (c.isValid() &&(Board.getPiece(c) instanceof Temp)) {
            re.add(c);
            y--;
            x++;
            c = new Coordinate(x, y);

        }
        if (c.isValid() && !p.sameTeam(c)) {
            re.add(c);
        }

        // move top left
        x = p.position.x - 1;
        y = p.position.y + 1;
        c = new Coordinate(x, y);
        while (c.isValid() &&(Board.getPiece(c) instanceof Temp)) {
            re.add(c);
            x--;
            y++;
            c = new Coordinate(x, y);

        }
        if (c.isValid() && !p.sameTeam(c)) {
            re.add(c);
        }

        // move bottom left
        x = p.position.x - 1;
        y = p.position.y - 1;
        c = new Coordinate(x, y);
        while (c.isValid() &&(Board.getPiece(c) instanceof Temp)) {
            re.add(c);
            x--;
            y--;
            c = new Coordinate(x, y);
        }
        if (c.isValid() &&!p.sameTeam(c)) {
            re.add(c);
        }

        return re;
    }
}

