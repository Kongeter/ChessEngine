package ChessEngine.MoveGeneratorF;

import ChessEngine.Board;
import ChessEngine.Move;
import static ChessEngine.BinaryHelper.*;


import java.util.ArrayList;
import java.util.List;


public class PinnedMovesClass{
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
    PinnedMovesClass(List<Move> Moves,byte[] Feld,boolean[] inCheckBoard,int kingPosition,boolean whiteHasMove,List<Integer> pinned){
        FARBE = whiteHasMove ? WHITE : BLACK;
        FEINDFARBE = whiteHasMove ? BLACK : WHITE;
        this.Moves = Moves;
        this.whiteHasMove = whiteHasMove;
        this.Feld = Feld;
        this.inCheckBoard = inCheckBoard;
        this.kingPosition = kingPosition;
        this.pinned =pinned;
    }
    //returns true if there is a piece with the given color on the given fieldnumber
    public void pinnedMoves() {
        for (int i = 0; i < 8; i++) {
            int CheckPos = kingPosition;
            int Change = MoveDirection[i];
            int thePinnedOne = -1;

            int Row = whiteHasMove ? 3 : 4;
            if (kingPosition / 12 - 2 == Row) {
                pinnedEnPassantHidden(kingPosition);
            }

            while (true) {
                CheckPos += Change;
                if (thePinnedOne == -1) { //noch keine eigene figur / fremder bauer gefunden

                    if ((Feld[CheckPos] & 0b11000) == FARBE) {
                        thePinnedOne = CheckPos;
                    } else if ((Feld[CheckPos] & 0b111) == 0b001) { //es ist ein bauer
                        thePinnedOne = CheckPos;
                    } else if (!(Feld[CheckPos] == 0)) {
                        break;
                    }
                } else { //eigene figur / bauer im weg schon gefunden

                    if ((Feld[CheckPos] & 0b11000) == FEINDFARBE) {
                        if ((Feld[CheckPos] & 0b111) == QUEEN) {
                            if ((Feld[thePinnedOne] & 0b11000) == FARBE) {
                                pinnedMoves2(thePinnedOne, i);
                            } else {
                                pinnedEnPassant(thePinnedOne);
                                break;
                            }
                        }
                        if ((Feld[CheckPos] & 0b111) == ROOK) {
                            if (i % 2 == 1) {
                                break;
                            }
                            if ((Feld[thePinnedOne] & 0b11000) == FARBE) {
                                pinnedMoves2(thePinnedOne, i);
                            } else {
                                pinnedEnPassant(thePinnedOne);
                                break;
                            }
                        }
                        if ((Feld[CheckPos] & 0b111) == BISHOP) {
                            if (i % 2 == 0) {
                                break;
                            }
                            if ((Feld[thePinnedOne] & 0b11000) == FARBE) {
                                pinnedMoves2(thePinnedOne, i);
                            } else {
                                pinnedEnPassant(thePinnedOne);
                                break;
                            }
                        }
                        break;
                    } else if (!(Feld[CheckPos] == 0)) {
                        break;
                    }
                }
            }
        }
    }

    private void pinnedEnPassantHidden(int Position) {
        int[] richtungen = new int[] {-1, +1};
        for(int Change : richtungen){
            int CheckPos = Position;
            int pin1 = -1;
            int pin2 = -1;
            while (true){
                CheckPos += Change;
                if(pin1 == -1){
                    if ((Feld[CheckPos] & 0b111) == PAWN){
                        pin1 = CheckPos;
                    }
                    else if (Feld[CheckPos] != 0){
                        break;
                    }

                }
                else if(pin2 == -1){
                    if ((Feld[CheckPos] & 0b111) == PAWN){
                        pin2 = CheckPos;
                    }
                    else if (Feld[CheckPos] != 0){
                        break;
                    }

                }
                else {
                    if ((Feld[CheckPos] & 0b11000) == FEINDFARBE) {
                        if ((Feld[CheckPos] & 0b111) == QUEEN || (Feld[CheckPos] & 0b111) == ROOK) {
                            Feld[pin1] = (byte) (Feld[pin1] & 0b0011111);
                            Feld[pin2] = (byte) (Feld[pin2] & 0b0011111);
                        }
                        else {
                            break;
                        }

                    }
                    else if(Feld[CheckPos]  != 0){
                        break;
                    }
                }

            }
        }

    }

    private void pinnedEnPassant(int Position) {
        int Row = whiteHasMove ? 4 : 3;
        if (Position / 12 - 2 == Row) {
            if ((Feld[Position - 1] & 0b00111) == 0b00001 && (Feld[Position - 1] & 0b11000) == FARBE) { //links steht bauer
                Feld[Position - 1] = (byte) (Feld[Position - 1] & 0b0011111);
            }
            if ((Feld[Position + 1] & 0b00111) == 0b00001 && (Feld[Position + 1] & 0b11000) == FARBE) { //rechts steht bauer
                Feld[Position + 1] = (byte) (Feld[Position + 1] & 0b0011111);
            }
        }
    }

    private void pinnedMoves2(int Position, int Pinndirection) {

        switch (Feld[Position] & 0b00111) {
            case PAWN -> {
                pinnedMovesPawn(Position, Pinndirection);
                pinned.add(Position);
            }
            case KNIGHT -> {
                pinned.add(Position);
            }
            default -> {
                pinnedMovesOther(Position, Pinndirection);
                pinned.add(Position);
            }
        }
    }

    private void pinnedMovesPawn(int FeldNummer, int Pinndirection) {
        int normalChange = (FARBE == WHITE) ? -12 : 12;
        int startingRow = (FARBE == WHITE) ? 6 : 1;
        int finalRow = (FARBE == WHITE) ? 0 : 7;
        int x = whiteHasMove ? -1 : 1;
        int y = whiteHasMove ? 1 : -1;
        int[] MoveDirectionPinned;

        Feld[FeldNummer] =(byte) (Feld[FeldNummer] & 0b0011111);

        switch (Pinndirection) {
            case 0, 4 -> {
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
            }
            case 1, 5 -> {

                if (FieldIsEnemy(FeldNummer + normalChange + y, Feld, FEINDFARBE)) {
                    if (((FeldNummer + normalChange + 1) / 12 - 2) == finalRow) { //bauer landet auf letztem feld
                        Moves.add(new Move(FeldNummer, FeldNummer + normalChange + y, Feld[FeldNummer + normalChange + y], QUEEN));
                        Moves.add(new Move(FeldNummer, FeldNummer + normalChange + y, Feld[FeldNummer + normalChange + y], KNIGHT));
                        Moves.add(new Move(FeldNummer, FeldNummer + normalChange + y, Feld[FeldNummer + normalChange + y], (byte) 0b100100));
                        Moves.add(new Move(FeldNummer, FeldNummer + normalChange + y, Feld[FeldNummer + normalChange + y], BISHOP));
                    } else {
                        Moves.add(new Move(FeldNummer, FeldNummer + normalChange + y, Feld[FeldNummer + normalChange + y]));
                    }
                }
            }
            case 3, 7 -> {

                if (FieldIsEnemy(FeldNummer + normalChange + x, Feld, FEINDFARBE)) {
                    if (((FeldNummer + normalChange + x) / 12 - 2) == finalRow) { //bauer landet auf letztem feld
                        Moves.add(new Move(FeldNummer, FeldNummer + normalChange + x, Feld[FeldNummer + normalChange + x], QUEEN));
                        Moves.add(new Move(FeldNummer, FeldNummer + normalChange + x, Feld[FeldNummer + normalChange + x], KNIGHT));
                        Moves.add(new Move(FeldNummer, FeldNummer + normalChange + x, Feld[FeldNummer + normalChange + x], (byte) 0b100100));
                        Moves.add(new Move(FeldNummer, FeldNummer + normalChange + x, Feld[FeldNummer + normalChange + x], BISHOP));
                    } else {
                        Moves.add(new Move(FeldNummer, FeldNummer + normalChange + x, Feld[FeldNummer + normalChange + x]));
                    }
                }

            }
            default -> {
                //kann nix machen wenn die von der seite kommen
            }
        }

        Feld[FeldNummer] = (byte) (Feld[FeldNummer] & 0b0011111);

    }


    private void pinnedMovesOther(int Position, int Pinndirection) {
        int[] MoveDirectionPinned;
        switch (Pinndirection) {
            case 0, 4 -> {
                MoveDirectionPinned = new int[]{0, 4};
            }
            case 1, 5 -> {
                MoveDirectionPinned = new int[]{1, 5};
            }
            case 2, 6 -> {
                MoveDirectionPinned = new int[]{2, 6};
            }
            case 3, 7 -> {
                MoveDirectionPinned = new int[]{3, 7};
            }
            default -> {
                MoveDirectionPinned = new int[]{0};
                System.out.println(" bug bei Movegen pinnedMovesOther");
            }
        }

        for (int i : MoveDirectionPinned) {

            int Change = MoveDirection[i];

            if (((Feld[Position] & 0b00111) == ROOK) && (i % 2 == 1)) {
                continue;
            }
            if (((Feld[Position] & 0b00111) == BISHOP) && (i % 2 == 0)) {
                continue;
            }

            int NeuesFeld = Position;

            while (true) {
                NeuesFeld += Change;
                if (!FieldBlocked(NeuesFeld, Feld, FARBE)) {
                    Moves.add(new Move(Position, NeuesFeld, Feld[NeuesFeld]));
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
