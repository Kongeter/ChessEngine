package ChessEngine.MoveGeneratorF;

import ChessEngine.Move;

import java.util.List;

import static ChessEngine.BinaryHelper.*;

public class NonPinnedMovesClass {

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
    NonPinnedMovesClass(List<Move> Moves,byte[] Feld,boolean[] inCheckBoard,int kingPosition,boolean whiteHasMove,List<Integer> pinned){
        FARBE = whiteHasMove ? WHITE : BLACK;
        FEINDFARBE = whiteHasMove ? BLACK : WHITE;
        this.Moves = Moves;
        this.whiteHasMove = whiteHasMove;
        this.Feld = Feld;
        this.inCheckBoard = inCheckBoard;
        this.kingPosition = kingPosition;
        this.pinned =pinned;
    }

    public void nonPinnedMoves(){
        //nicht pinned Figuren berechnen
        for (int singlePos : possiblePositions) {
            if (pinned.stream().anyMatch(x -> x == singlePos)) {
                continue;
            }
            if ((FARBE != (Feld[singlePos] & 0b11000))) {
                continue;
            }
            switch (Feld[singlePos] & 0b00111) {
                case PAWN -> {
                    PawnMoves(singlePos);
                }
                case KNIGHT -> {
                    KnightMoves(singlePos);
                }
                case KING -> {
                    KingMoves(singlePos);
                }
                default -> {
                    OtherMoves(singlePos);
                }
            }
        }

    }
    private void PawnMoves(int FeldNummer) {
        int normalChange = (FARBE == WHITE) ? -12 : 12;
        int startingRow = (FARBE == WHITE) ? 6 : 1;
        int finalRow = (FARBE == WHITE) ? 0 : 7;
        byte Piece = Feld[FeldNummer];

        /*en Passont*/
        if ((Piece & 0b100000) == 0b100000) {//en passant möglich
            if ((Piece & 0b1000000) == 0b1000000) {//en passant rechts
                Moves.add(new Move(FeldNummer, FeldNummer - 1 + normalChange, Feld[FeldNummer - 1], true));
            } else {//en passant links
                Moves.add(new Move(FeldNummer, FeldNummer + 1 + normalChange, Feld[FeldNummer + 1], true));
            }
        }
        Feld[FeldNummer] = (byte) (Feld[FeldNummer] & 0b0011111); //entferne en passant flags

        /*nach vorne Laufen*/
        if ((FeldNummer / 12 - 2) == startingRow) { //doppelter zug noch möglich

            int NeuesFeld = FeldNummer + normalChange;
            if (!FieldBlocked(NeuesFeld, Feld, FARBE) && !FieldIsEnemy(NeuesFeld, Feld, FEINDFARBE)) {
                Moves.add(new Move(FeldNummer, NeuesFeld, Feld[NeuesFeld]));
                NeuesFeld += normalChange;
                if (!FieldBlocked(NeuesFeld, Feld, FARBE) && !FieldIsEnemy(NeuesFeld, Feld, FEINDFARBE)) {
                    //SpecialMove der EnPassant Ermöglichen könnte
                    Moves.add(new Move(FeldNummer, NeuesFeld, Feld[NeuesFeld], true));
                }
            }
        } else {//nur einfacher zug
            int NeuesFeld = FeldNummer + normalChange;
            if (!FieldBlocked(NeuesFeld, Feld, FARBE) && !FieldIsEnemy(NeuesFeld, Feld, FEINDFARBE)) {

                if ((NeuesFeld / 12 - 2) == finalRow) { //bauer landet auf letztem feld
                    Moves.add(new Move(FeldNummer, NeuesFeld, Feld[NeuesFeld], QUEEN));
                    Moves.add(new Move(FeldNummer, NeuesFeld, Feld[NeuesFeld], KNIGHT));
                    Moves.add(new Move(FeldNummer, NeuesFeld, Feld[NeuesFeld], (byte) 0b100100));
                    Moves.add(new Move(FeldNummer, NeuesFeld, Feld[NeuesFeld], BISHOP));
                } else {
                    Moves.add(new Move(FeldNummer, NeuesFeld, Feld[NeuesFeld]));
                }


            }


        }

        /*schlagen*/
        if (FieldIsEnemy(FeldNummer + normalChange + 1, Feld, FEINDFARBE)) {
            if (((FeldNummer + normalChange + 1) / 12 - 2) == finalRow) { //bauer landet auf letztem feld
                Moves.add(new Move(FeldNummer, FeldNummer + normalChange + 1, Feld[FeldNummer + normalChange + 1], QUEEN));
                Moves.add(new Move(FeldNummer, FeldNummer + normalChange + 1, Feld[FeldNummer + normalChange + 1], KNIGHT));
                Moves.add(new Move(FeldNummer, FeldNummer + normalChange + 1, Feld[FeldNummer + normalChange + 1], (byte) 0b100100));
                Moves.add(new Move(FeldNummer, FeldNummer + normalChange + 1, Feld[FeldNummer + normalChange + 1], BISHOP));
            } else {
                Moves.add(new Move(FeldNummer, FeldNummer + normalChange + 1, Feld[FeldNummer + normalChange + 1]));
            }
        }

        if (FieldIsEnemy(FeldNummer + normalChange - 1, Feld, FEINDFARBE)) {
            if (((FeldNummer + normalChange - 1) / 12 - 2) == finalRow) { //bauer landet auf letztem feld
                Moves.add(new Move(FeldNummer, FeldNummer + normalChange - 1, Feld[FeldNummer + normalChange - 1], QUEEN));
                Moves.add(new Move(FeldNummer, FeldNummer + normalChange - 1, Feld[FeldNummer + normalChange - 1], KNIGHT));
                Moves.add(new Move(FeldNummer, FeldNummer + normalChange - 1, Feld[FeldNummer + normalChange - 1], (byte) 0b100100));
                Moves.add(new Move(FeldNummer, FeldNummer + normalChange - 1, Feld[FeldNummer + normalChange - 1], BISHOP));
            } else {
                Moves.add(new Move(FeldNummer, FeldNummer + normalChange - 1, Feld[FeldNummer + normalChange - 1]));
            }
        }
    }

    private void KnightMoves(int fieldNumber) {
        int[] Changes = new int[]{-23, -10, 14, 25, 23, 10, -14, -25};
        for (int Change : Changes) {
            int NeuesFeld = fieldNumber + Change;
            if (!FieldBlocked(NeuesFeld, Feld, FARBE)) {
                Moves.add(new Move(fieldNumber, NeuesFeld, Feld[NeuesFeld]));
                //System.out.println("n: " + FeldNummer + " " + NeuesFeld);
            }
        }
    }

    private void KingMoves(int fieldNumber) {
        if ((Feld[fieldNumber] & 0b100000) == 0b000000) { //King wurde noch nicht bewegt
            //Normale moves
            for (int Change : MoveDirection) {
                int NeuesFeld = fieldNumber + Change;
                if (!FieldBlocked(NeuesFeld, Feld, FARBE) && !inCheckBoard[NeuesFeld]) {
                    Moves.add(new Move(fieldNumber, NeuesFeld, Feld[NeuesFeld], true));
                    //System.out.println("k: " + fieldNumber + " " + NeuesFeld);
                }
            }

            //Rochade links (a)
            if ((Feld[fieldNumber - 4] & (0b00000111)) == ROOK && (Feld[fieldNumber - 4] & 0b100000) == 0b000000) { // Rook ist da und unbewegt
                if (!FieldIsEnemy(fieldNumber - 3, Feld, FEINDFARBE) && !FieldBlocked(fieldNumber - 3, Feld, FARBE)) {
                    if (!FieldIsEnemy(fieldNumber - 2, Feld, FEINDFARBE) && !FieldBlocked(fieldNumber - 2, Feld, FARBE) && !inCheckBoard[fieldNumber - 2]) {
                        if (!FieldIsEnemy(fieldNumber - 1, Feld, FEINDFARBE) && !FieldBlocked(fieldNumber - 1, Feld, FARBE) && !inCheckBoard[fieldNumber - 1]) {
                            if (!inCheckBoard[fieldNumber]) {
                                Moves.add(new Move(fieldNumber, fieldNumber - 2, Feld[fieldNumber - 1], true));
                            }
                        }

                    }
                }
            }
            //Rochade rechts (f)
            if ((Feld[fieldNumber + 3] & (0b00000111)) == ROOK && (Feld[fieldNumber + 3] & 0b100000) == 0b000000) { // Rook ist da und unbewegt
                if (!FieldIsEnemy(fieldNumber + 2, Feld, FEINDFARBE) && !FieldBlocked(fieldNumber + 2, Feld, FARBE) && !inCheckBoard[fieldNumber + 2]) {
                    if (!FieldIsEnemy(fieldNumber + 1, Feld, FEINDFARBE) && !FieldBlocked(fieldNumber + 1, Feld, FARBE) && !inCheckBoard[fieldNumber + 1]) {
                        if (!inCheckBoard[fieldNumber]) {
                            Moves.add(new Move(fieldNumber, fieldNumber + 2, Feld[fieldNumber + 1], true));
                        }
                    }

                }
            }


        } else {
            for (int Change : MoveDirection) { //King wurde schon bewegt
                int NeuesFeld = fieldNumber + Change;
                if (!FieldBlocked(NeuesFeld, Feld, FARBE) && !inCheckBoard[NeuesFeld]) {
                    Moves.add(new Move(fieldNumber, NeuesFeld, Feld[NeuesFeld]));
                    //System.out.println("k: " + fieldNumber + " " + NeuesFeld);
                }
            }
        }


    }

    private void OtherMoves(int fieldNumber) {
        for (int i = 0; i < 8; i++) {

            int Change = MoveDirection[i];

            if (((Feld[fieldNumber] & 0b00111) == ROOK) && (i % 2 == 1)) {
                continue;
            }
            if (((Feld[fieldNumber] & 0b00111) == BISHOP) && (i % 2 == 0)) {
                continue;
            }

            int NeuesFeld = fieldNumber;

            while (true) {
                NeuesFeld += Change;
                if (!FieldBlocked(NeuesFeld, Feld, FARBE)) {
                    if (((Feld[fieldNumber] & 0b00111) == ROOK) && !hasMoved(fieldNumber, Feld)) {//Es ist unbewegter Rook
                        Moves.add(new Move(fieldNumber, NeuesFeld, Feld[NeuesFeld], true));
                    } else {
                        Moves.add(new Move(fieldNumber, NeuesFeld, Feld[NeuesFeld]));
                    }
                    if (FieldIsEnemy(NeuesFeld, Feld, FEINDFARBE)) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }

    }
}
