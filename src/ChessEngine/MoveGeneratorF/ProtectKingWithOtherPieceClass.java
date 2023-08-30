package ChessEngine.MoveGeneratorF;

import ChessEngine.Move;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static ChessEngine.BinaryHelper.*;

public class ProtectKingWithOtherPieceClass {
    static final byte[] MoveDirection = new byte[]{-12, -11, 1, 13, 12, 11, -1, -13};//MoveDirection[0] is up, then 45deg clockwise
    //List<Integer> FigPos; //alle Positionen wo Figuren stehen
    List<Move> Moves;
    byte FARBE; // FARBE die den nächsten zug macht
    byte FEINDFARBE; // gegnerische farbe
    byte[] Feld; // ganzes Spielfeld
    boolean[] inCheckBoard; //enthält alle felder auf die der gegner im nächsten zug kommen kann
    int kingPosition;
    boolean whiteHasMove;
    List<Integer> pinned;

    ProtectKingWithOtherPieceClass(List<Move> Moves, byte[] Feld, boolean[] inCheckBoard, int kingPosition, boolean whiteHasMove, List<Integer> pinned) {
        FARBE = whiteHasMove ? WHITE : BLACK;
        FEINDFARBE = whiteHasMove ? BLACK : WHITE;
        this.Moves = Moves;
        this.whiteHasMove = whiteHasMove;
        this.Feld = Feld;
        this.inCheckBoard = inCheckBoard;
        this.kingPosition = kingPosition;
        this.pinned = pinned;
    }

    public void protectKingWithOtherPiece(int attackerPosition, int direction) {


        pinnedPositions();
        new NonPinnedMovesClass(Moves, Feld, inCheckBoard, kingPosition, whiteHasMove, pinned).nonPinnedMoves();

        if (type(Feld[attackerPosition]) == PAWN) {
            int behindAttackerField = (color(Feld[attackerPosition]) == WHITE) ? attackerPosition + 12 : attackerPosition - 12;
            int possiblePawnLeftPos = type(Feld[attackerPosition - 1]) == PAWN ? attackerPosition - 1 : 1;
            int possiblePawnRightPos = type(Feld[attackerPosition + 1]) == PAWN ? attackerPosition + 1 : 1;


            Moves.removeIf(x -> x.start == kingPosition);
            Moves.removeIf(x ->
                    !( // ! beachten
                            (x.end == attackerPosition) ||
                                    ((x.start == possiblePawnLeftPos && x.end == behindAttackerField && x.specialMove)) ||
                                    ((x.start == possiblePawnRightPos && x.end == behindAttackerField && x.specialMove))
                    )
            );

        } else if (type(Feld[attackerPosition]) == KNIGHT || fieldNextToKingField(attackerPosition)) {
            Moves.removeIf(x -> x.end != attackerPosition || x.start == kingPosition);

        } else {

            List<Integer> BlockingPositions = getBlockingPositions(direction);
            Moves.removeIf(x -> !BlockingPositions.contains(x.end));
        }

    }



    private boolean fieldNextToKingField(int position) {
        final byte[] MoveDirection = new byte[]{-12, -11, 1, 13, 12, 11, -1, -13};
        for (byte direction : MoveDirection) {
            if (position + direction == kingPosition) {
                return true;
            }
        }
        return false;
    }

    private List<Integer> getBlockingPositions(int direction) {
        final byte[] MoveDirectionNegative = new byte[]{12, 11, -1, -13, -12, -11, 1, 13};

        List<Integer> result = new ArrayList<>();
        int NeuesFeld = kingPosition;
        while (true) {
            NeuesFeld += MoveDirectionNegative[direction];
            if (!FieldBlocked(NeuesFeld, Feld, FARBE)) {
                result.add(NeuesFeld);
                if (color(Feld[NeuesFeld]) == FEINDFARBE) {
                    break;
                }
            } else {
                break;
            }

        }
        return result;
    }


    public void pinnedPositions() {
        for (int i = 0; i < 8; i++) {
            int CheckPos = kingPosition;
            int Change = MoveDirection[i];
            int thePinnedOne = -1;
            while (true) {
                CheckPos += Change;
                if (thePinnedOne == -1) { //noch keine eigene figur gefunden
                    if (color(Feld[CheckPos]) == FARBE) {
                        thePinnedOne = CheckPos;
                    } else if (Feld[CheckPos] != 0) {
                        break;
                    }
                } else { //eigene figur schon gefunden

                    if (color(Feld[CheckPos]) == FEINDFARBE) {
                        if (type(Feld[CheckPos]) == QUEEN) {
                            pinned.add(thePinnedOne);
                            removeEnPassantFlags(thePinnedOne, Feld);

                        }
                        if (type(Feld[CheckPos]) == ROOK) {
                            if (i % 2 == 1) {
                                break;
                            }
                            pinned.add(thePinnedOne);
                            removeEnPassantFlags(thePinnedOne, Feld);
                        }
                        if (type(Feld[CheckPos]) == BISHOP) {
                            if (i % 2 == 0) {
                                break;
                            }
                            pinned.add(thePinnedOne);
                            removeEnPassantFlags(thePinnedOne, Feld);

                        }
                        break;
                    } else if ((Feld[CheckPos] != 0)) {
                        break;
                    }
                }
            }
        }
    }

}
