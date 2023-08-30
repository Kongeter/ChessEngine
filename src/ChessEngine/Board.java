package ChessEngine;

import static ChessEngine.BinaryHelper.*;
import static ChessEngine.BoardHelper.*;

import java.util.HashMap;
import java.util.Objects;

public class Board {
    HashMap<Long, Short> recurringPositionCounter = new HashMap<Long, Short>(); //Hashmap um doppelte boards zu zählen, für unentschieden nach 3x derselben Position
    byte[] currentBoard; //speichert welche spielsteine auf brett stehen in 12x12 = 144 byte matrix wobei nur die innere 8x8 matrix bespielt wird

    public Board() {
        this.setPosition(Helper.FENtoArray("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }

    public Board(String FEN) {
        if(FEN == null){
            this.setPosition(Helper.FENtoArray("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
        }
        else{
            currentBoard = fenToArray(FEN);
        }

    }

    //erstellt Board Kopie
    public Board copy() {
        Board res = new Board();
        res.currentBoard = this.currentBoard.clone();
        return res;
    }

    //setter für board.currentBoard
    public void setPosition(byte[] newBoard) {
        this.currentBoard = newBoard;
    }

    public byte[] getCurrentBoard() {
        return currentBoard;
    }

    public void makeMovePgn(String pgn, boolean whiteHasMove){
        Move move = BoardHelper.pgnToMove(pgn,this, whiteHasMove);
        if(move == null){
            throw new RuntimeException();
        }
        this.makeMove(move);
    }


    //debug Funktion für make und unmake Move
    public void makeMoveDebug(Move move) {
        Board clone = this.copy();
        clone.makeMove(move);
        clone.undoMove(move);

        if (Helper.printDiff(currentBoard, clone.currentBoard)) {
            System.out.println("----------------------------------------------start---------------------------------------------------");
            System.out.println("Fehler bei Figur: " + Integer.toBinaryString(currentBoard[move.start]));
            System.out.println("Von Feld : " + move.start + "   Zu Feld: " + move.end + "     special move:" + move.specialMove + "     Piece Taken: " + move.pieceTaken + "      Promo Choice: " + move.promotionChoice);
            System.out.println("Feld Davor:");
            Helper.printBoard(currentBoard);
            this.makeMove(move);
            System.out.println("Feld Danach:");
            Helper.printBoard(currentBoard);
            System.out.println("------------------------------------------------end-------------------------------------------------");
        } else {
            this.makeMove(move);
        }
    }

    //führt move auf currentBoard aus (in place)
    public void makeMove(Move move) {
        int start = move.start;
        int end = move.end;

        if (move.specialMove) {
            switch (type(currentBoard[start])) {
                case PAWN -> {
                    //promotion
                    if (move.promotionChoice != 0) {
                        currentBoard[end] = currentBoard[start];
                        currentBoard[end] = (byte) ((currentBoard[start] & 0b11000) | move.promotionChoice);
                        currentBoard[start] = 0;
                        break;
                    }


                    //doppelter pawn schritt
                    if ((start - end) % 12 == 0) {
                        byte Color = (start - end) > 0 ? BLACK : WHITE;
                        if ((currentBoard[end - 1] & 0b00111) == 0b00001 && color(currentBoard[end - 1]) == Color) { //links steht bauer
                            currentBoard[end - 1] = (byte) (currentBoard[end - 1] | 0b0100000);
                        }

                        if ((currentBoard[end + 1] & 0b00111) == 0b00001 && color(currentBoard[end + 1]) == Color) { //rechts steht bauer
                            currentBoard[end + 1] = (byte) (currentBoard[end + 1] | 0b1100000);
                        }
                        basicMakeMove(start, end);
                        break;
                    }

                    //en passant
                    int direction = start - end > 0 ? 12 : -12;
                    basicMakeMove(start, end);
                    currentBoard[end + direction] = 0;


                }
                case ROOK -> {
                    setMovedFlag(start, currentBoard);
                    basicMakeMove(start, end);

                }
                case KING -> {
                    setMovedFlag(start, currentBoard);

                    if (start - end == -2) {  //rochade rechts
                        currentBoard[end] = currentBoard[start];             //könig aufs richtige feld
                        currentBoard[start] = 0;                                  // alter könig löschen
                        currentBoard[start + 1] = currentBoard[start + 3];       //knight bewegen
                        currentBoard[start + 3] = 0;                                //knight löschen
                        setMovedFlag(start + 1, currentBoard);
                    } else if (start - end == 2) {//rochade links
                        currentBoard[end] = currentBoard[start];             //könig aufs richtige feld
                        currentBoard[start] = 0;                                  // alter könig löschen
                        currentBoard[start - 1] = currentBoard[start - 4];       //knight bewegen
                        currentBoard[start - 4] = 0;                                //knight löschen
                        setMovedFlag(start - 1, currentBoard);
                    } else {   //normaler Königsschritt
                        basicMakeMove(start, end);
                    }

                }
                default -> System.out.println("error bei Board.getPlayerMove");

            }
        } else {
            basicMakeMove(start, end);
        }
    }

    private void basicMakeMove(int start, int end) {
        currentBoard[end] = currentBoard[start];
        currentBoard[start] = 0;
    }

    //macht move rückgängig currentBoard (in place)
    public void undoMove(Move move) {
        int start = move.start;
        int end = move.end;
        byte pieceTaken = move.pieceTaken;
        if (move.specialMove) {
            if (move.promotionChoice != 0) { //promotion
                currentBoard[end] = (byte) (color(currentBoard[end]) | PAWN);
                basicUndoMove(start,end,pieceTaken);
                return;
            }
            switch (currentBoard[end] & 0b00111) {
                case PAWN -> {

                    //doppelter pawn schritt
                    if ((start - end) % 12 == 0) {
                        byte enemyColor = (start - end) > 0 ? BLACK : WHITE;
                        if ((currentBoard[end - 1] & 0b00111) == 0b00001 && (currentBoard[end - 1] & 0b11000) == enemyColor) { //links steht bauer
                            currentBoard[end - 1] = (byte) (currentBoard[end - 1] & 0b0011111);
                        }

                        if ((currentBoard[end + 1] & 0b00111) == 0b00001 && (currentBoard[end + 1] & 0b11000) == enemyColor) { //rechts steht bauer
                            currentBoard[end + 1] = (byte) (currentBoard[end + 1] & 0b0011111);
                        }
                        basicUndoMove(start, end, pieceTaken);
                        break;
                    }

                    //en passant
                    int direction = start - end > 0 ? 12 : -12;
                    currentBoard[start] = currentBoard[end];
                    currentBoard[end + direction] = pieceTaken;
                    currentBoard[end] = 0;
                }
                case ROOK -> {//erster rook schritt
                    removeMovedFlag(end, currentBoard);
                    basicUndoMove(start,end, pieceTaken);
                }
                case KING -> {
                    removeMovedFlag(end, currentBoard);

                    if (start - end == -2) {  //rochade rechts
                        currentBoard[start] = currentBoard[end];//könig aufs richtige feld
                        currentBoard[end] = pieceTaken;// alter könig löschen
                        currentBoard[start + 3] = currentBoard[start + 1]; //knight bewegen
                        currentBoard[start + 1] = 0; //knight löschen
                        removeMovedFlag(start + 3, currentBoard);
                    } else if (start - end == 2) {//rochade links
                        currentBoard[start] = currentBoard[end];//könig aufs richtige feld
                        currentBoard[end] = pieceTaken;// alter könig löschen
                        currentBoard[start - 4] = currentBoard[start - 1]; //knight bewegen
                        currentBoard[start - 1] = 0; //knight löschen
                        removeMovedFlag(start - 4, currentBoard);

                    } else {   //normaler Königsschritt
                        basicUndoMove(start, end, pieceTaken);
                    }

                }
                default -> System.out.println("error bei Board.undoMove");

            }
        } else {
            basicUndoMove(start, end, pieceTaken);
        }
    }

    private void basicUndoMove(int start, int end, byte pieceTaken) {
        currentBoard[start] = currentBoard[end];
        currentBoard[end] = pieceTaken;
    }

}
