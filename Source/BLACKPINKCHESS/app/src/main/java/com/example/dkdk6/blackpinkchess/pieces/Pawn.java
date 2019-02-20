package com.example.dkdk6.blackpinkchess.pieces;

import com.example.dkdk6.blackpinkchess.Board;
import com.example.dkdk6.blackpinkchess.Coordinate;


import java.util.LinkedList;
import java.util.List;

public class Pawn extends  Piece {

    public Pawn(final Coordinate p, final String color, int id, int piece_image)
    {
        super(p,color, id, piece_image);
    }

    /*
     * It has each direction
     * Down direction 2
     * Up direction 0
     * Right direction 1
     * Left direction 3
     * */

    public List<Coordinate> getPossiblePositions()
    {
        List<Coordinate> re = new LinkedList<Coordinate>();
        int x = position.x;
        int y = position.y;
        Coordinate c;

        //Move forward one square if if that square is empty
        // Whether the square is empty is up to Board.getPiece(c) instaceof Temp
        if(this.position.direction==2)
            c = new Coordinate(x+1, y);
        else if(this.position.direction==0)
            c = new Coordinate(x-1, y);
        else if(this.position.direction==1)
            c = new Coordinate(x, y+1);
        else
            c = new Coordinate(x, y-1);
        if (c.isValid()&&Board.getPiece(c) instanceof Temp) {
            re.add(c);
        }

        //Move forward two squares if pawn is in initial position
        if (this.position.direction==2&&x == 1 && Board.getPiece(c) instanceof Temp) {
            c = new Coordinate(x + 2, y);
            if (c.isValid() && Board.getPiece(c) instanceof Temp) {
                re.add(c);
            }
        }
        else if(this.position.direction==0&&x == 12 && Board.getPiece(c) instanceof Temp) {
            c = new Coordinate(x - 2, y);
            if (c.isValid() && Board.getPiece(c) instanceof Temp) {
                re.add(c);
            }
        }
        else if(this.position.direction==1&&y == 1 && Board.getPiece(c) instanceof Temp) {
            c = new Coordinate(x, y+2);
            if (c.isValid() && Board.getPiece(c) instanceof Temp) {
                re.add(c);
            }
        }
        else if(this.position.direction==3&&y == 12 && Board.getPiece(c) instanceof Temp) {
            c = new Coordinate(x, y-2);
            if (c.isValid() && Board.getPiece(c) instanceof Temp) {
                re.add(c);
            }
        }

        //can attack diagonally forward one square away
        if(this.position.direction==2)
        {
            c = new Coordinate(x + 1, y - 1);
            if (c.isValid() && !(Board.getPiece(c) instanceof Temp) && !sameTeam(c)) {
                re.add(c);
            }
            c = new Coordinate(x + 1, y + 1);
            if (c.isValid() &&!(Board.getPiece(c) instanceof Temp) && !sameTeam(c)) {
                re.add(c);
            }
        }
        else if(this.position.direction==0)
        {
            c = new Coordinate(x - 1, y - 1);
            if (c.isValid() && !(Board.getPiece(c) instanceof Temp) && !sameTeam(c)) {
                re.add(c);
            }
            c = new Coordinate(x - 1, y + 1);
            if (c.isValid() &&!(Board.getPiece(c) instanceof Temp) && !sameTeam(c)) {
                re.add(c);
            }
        }
        else if(this.position.direction==1)
        {
            c = new Coordinate(x + 1, y + 1);
            if (c.isValid() && !(Board.getPiece(c) instanceof Temp) && !sameTeam(c)) {
                re.add(c);
            }
            c = new Coordinate(x - 1, y + 1);
            if (c.isValid() &&!(Board.getPiece(c) instanceof Temp) && !sameTeam(c)) {
                re.add(c);
            }

        }
        else if(this.position.direction==3)
        {
            c = new Coordinate(x + 1, y - 1);
            if (c.isValid() && !(Board.getPiece(c) instanceof Temp) && !sameTeam(c)) {
                re.add(c);
            }
            c = new Coordinate(x - 1, y - 1);
            if (c.isValid() &&!(Board.getPiece(c) instanceof Temp) && !sameTeam(c)) {
                re.add(c);
            }

        }

        return re;

    }




}
