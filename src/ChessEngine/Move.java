package ChessEngine;

public class Move {



    public int start;
    public int end;
    public byte promotionChoice;
    public byte pieceTaken = 0; //hier wert des geschlagenen eintragen
    public boolean specialMove  = false;

    public Move(int start, int end , int t) {
        this.start = start;
        this.end = end;
    }

    public Move(int start, int end, byte pieceTaken) {
        this.start = start;
        this.end = end;
        this.pieceTaken = pieceTaken;
    }
    public Move(int start, int end, byte pieceTaken, boolean specialMove) {
        this.start = start;
        this.end = end;
        this.pieceTaken = pieceTaken;
        this.specialMove = specialMove;
    }
    public Move(int start, int end, byte pieceTaken,byte promotionChoice) {
        this.start = start;
        this.end = end;
        this.promotionChoice = promotionChoice;
        this.pieceTaken = pieceTaken;
        this.specialMove = true;

    }


    //getter für Feldnummern(x,y) 0-7 statt (z) 0-143
    public int getStartFieldX() {
        return start % 12 - 2;
    }
    public int getStartFieldY() {
        return start / 12 - 2;
    }
    public int getEndFieldX() {
        return end % 12 - 2;
    }
    public int getEndFieldY() {
        return end / 12 - 2;
    }
    public static int getPosX(int positon){return positon % 12 - 2;}
    public static int getPosY(int positon) {return positon / 12 - 2;}

    public String getMoveAsString(){
        String S = specialMove ? " S " : "";
        String result = new String(Helper.getChessNotationIndexFromFieldIndex(this.start) + "-" + Helper.getChessNotationIndexFromFieldIndex(this.end));
        return result;
    }


}
