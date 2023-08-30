package ChessEngine.MoveGeneratorF;

import ChessEngine.Move;

import java.util.List;

import static ChessEngine.BinaryHelper.*;

public class OnlyKingMovesClass {
    static final byte[] MoveDirection = new byte[]{-12, -11, 1, 13, 12, 11, -1, -13};//MoveDirection[0] is up, then 45deg clockwise

    public static void OnlyKingMoves(List<Move> Moves, boolean[] inCheckBoard, int kingPosition, byte[] Feld, byte FARBE, int directionAttacker1, int directionAttacker2){
        for (int i= 0; i < 8 ; i++ ) {
            if(i == directionAttacker1 || i == directionAttacker2){
                continue;
            }
            int NeuesFeld = kingPosition + MoveDirection[i];
            if (!FieldBlocked(NeuesFeld, Feld, FARBE) && !inCheckBoard[NeuesFeld]) {
                if(hasMoved(kingPosition, Feld)){
                    Moves.add(new Move(kingPosition, NeuesFeld, Feld[NeuesFeld]));
                }
                else {
                    Moves.add(new Move(kingPosition, NeuesFeld, Feld[NeuesFeld], true));
                }

                //System.out.println("k: " + fieldNumber + " " + NeuesFeld);
            }
        }
    }
}




