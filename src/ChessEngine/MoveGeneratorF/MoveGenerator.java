package ChessEngine.MoveGeneratorF;

import static ChessEngine.BinaryHelper.*;


import ChessEngine.Board;
import ChessEngine.Move;

import java.util.ArrayList;
import java.util.List;


public class MoveGenerator{

    static final byte[] MoveDirection = new byte[]{-12, -11, 1, 13, 12, 11, -1, -13};//MoveDirection[0] is up, then 45deg clockwise
    //List<Integer> FigPos; //alle Positionen wo Figuren stehen
    List<Move> Moves = new ArrayList<Move>();
    byte FARBE; // FARBE die den nächsten zug macht
    byte FEINDFARBE; // gegnerische farbe
    byte[] Feld; // ganzes Spielfeld
    boolean[] inCheckBoard; //enthält alle felder auf die der gegner im nächsten zug kommen kann
    int kingPosition = 0;
    boolean whiteHasMove;
    List<Integer> pinned;


    public List<Move> generate(Board board, boolean whiteHasMove) {

        kingAttacker1 = -1;
        directionAttacker1 = -1;
        kingAttacker2 = -1;
        directionAttacker2 = -1;

        FARBE = whiteHasMove ? WHITE : BLACK;
        FEINDFARBE = whiteHasMove ? BLACK : WHITE;

        Moves = new ArrayList<Move>();
        this.whiteHasMove = whiteHasMove;
        Feld = board.getCurrentBoard();
        kingPosition = currentKingPos(Feld, whiteHasMove);
        calculateInCheckBoardOther();
        pinned = new ArrayList<>();


        if (inCheckBoard[kingPosition]) {
            kingInChessMoves(kingPosition);
        }
        else {
            new PinnedMovesClass( Moves,Feld, inCheckBoard, kingPosition, whiteHasMove, pinned).pinnedMoves();
            new NonPinnedMovesClass( Moves,Feld, inCheckBoard, kingPosition, whiteHasMove, pinned).nonPinnedMoves();
        }

        return Moves;
    }

    int kingAttacker1;
    int directionAttacker1;
    int kingAttacker2;
    int directionAttacker2;

    private void kingInChessMoves(int kingPosition) {
        if(kingAttacker2 == -1){
            //nur von einer seite im schach
            new ProtectKingWithOtherPieceClass( Moves,Feld, inCheckBoard, kingPosition, whiteHasMove, pinned).protectKingWithOtherPiece(kingAttacker1, directionAttacker1);
        }
        OnlyKingMovesClass.OnlyKingMoves(Moves, inCheckBoard, kingPosition, Feld ,FARBE, directionAttacker1, directionAttacker2);
    }



    private void setInCheckBoard(int position, int attackerPosition, int direction) {
        inCheckBoard[position] = true;
        if(position == kingPosition){
            if(kingAttacker1 == -1){
                kingAttacker1 = attackerPosition;
                directionAttacker1 = direction;
            }
            else if(kingAttacker2 == -1){
                kingAttacker2 = attackerPosition;
                directionAttacker2 = direction;
            }
            else {
                System.out.println("Fehler bei setInCheckBoard");
            }
        }
    }

    public  void calculateInCheckBoardOther() { //
        inCheckBoard = new boolean[144];
        byte farbe = !whiteHasMove ? WHITE : BLACK;
        for (int singlePos : possiblePositions) {

            if ((farbe != (Feld[singlePos] & 0b11000))) {
                continue;
            }
            switch (Feld[singlePos] & 0b00111) {
                case PAWN -> {
                    int normalChange = (farbe == WHITE) ? -12 : 12;
                    setInCheckBoard(singlePos + normalChange + 1, singlePos, -1);
                    setInCheckBoard(singlePos + normalChange - 1, singlePos, -1);
                }
                case KNIGHT -> {
                    int[] Changes = new int[]{-23, -10, 14, 25, 23, 10, -14, -25};
                    for (int Change : Changes) {
                        setInCheckBoard(singlePos + Change, singlePos, -1);
                    }
                }
                case KING -> {
                    for (int Change : MoveDirection) {
                        setInCheckBoard(singlePos + Change, singlePos, -1);

                    }
                }
                default -> {

                    for (int i = 0; i < 8; i++) {

                        int Change = MoveDirection[i];

                        if (((Feld[singlePos] & 0b00111) == ROOK) && (i % 2 == 1)) {
                            continue;
                        }
                        if (((Feld[singlePos] & 0b00111) == BISHOP) && (i % 2 == 0)) {
                            continue;
                        }

                        int NeuesFeld = singlePos;

                        while (true) {
                            NeuesFeld += Change;
                            setInCheckBoard(NeuesFeld, singlePos, i);
                            if (Feld[NeuesFeld] != 0) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean[] calculateInCheckBoard(Board board, boolean whiteHasMove, boolean[] inCheckBoard) { //
        byte[] field = board.getCurrentBoard();
        inCheckBoard = new boolean[144];
        byte farbe = whiteHasMove ? WHITE : BLACK;
        for (int singlePos : possiblePositions) {

            if ((farbe != (field[singlePos] & 0b11000))) {
                continue;
            }
            switch (field[singlePos] & 0b00111) {
                case PAWN -> {
                    int normalChange = (farbe == WHITE) ? -12 : 12;
                    inCheckBoard[singlePos + normalChange + 1] = true;
                    inCheckBoard[singlePos + normalChange - 1] = true;
                }
                case KNIGHT -> {
                    int[] Changes = new int[]{-23, -10, 14, 25, 23, 10, -14, -25};
                    for (int Change : Changes) {
                        inCheckBoard[singlePos + Change] = true;
                    }
                }
                case KING -> {
                    for (int Change : MoveDirection) {
                        inCheckBoard[singlePos + Change] = true;
                    }
                }
                default -> {

                    for (int i = 0; i < 8; i++) {

                        int Change = MoveDirection[i];

                        if (((field[singlePos] & 0b00111) == ROOK) && (i % 2 == 1)) {
                            continue;
                        }
                        if (((field[singlePos] & 0b00111) == BISHOP) && (i % 2 == 0)) {
                            continue;
                        }

                        int NeuesFeld = singlePos;

                        while (true) {
                            NeuesFeld += Change;
                            inCheckBoard[NeuesFeld] = true;
                            if (field[NeuesFeld] != 0) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return inCheckBoard;
    }
    public static int currentKingPos(byte[] currentPos, boolean whiteHasMove) {
        byte color = whiteHasMove ? WHITE : BLACK;

        for (int singlePos : possiblePositions) {
            if ((color == (currentPos[singlePos] & 0b11000)) && (currentPos[singlePos] & 0b00111) == KING) {
                return singlePos;
            }
        }
        return 0;
    }


}
