package ChessEngine;


import java.util.HashMap;
import java.util.Random;

import static ChessEngine.BinaryHelper.*;

public class ZobristHashMap {

    /*
     * HashTable, um bereits berechnete Values effektiv abspeichern zu können.
     * Wird für MoveOrdering und als TranspositionTable genutzt.
     * Zur Berechnung des Keys wird Zobrist Hashing genutzt.
     * */

    public HashMap<Long, ZobristHashEntry> table = new HashMap<>();
    static long[][] zobristTablePieces = new long[12][64];
    static long[][] zobristPieceHasNotMoved = new long[4][64]; //unbewegter rook / unbewegter king: [0]K [1]R [2]k [2]r
    static long zobristTableWhiteToMove;
    static long[] zobristTableEnPassant = new long[8];

    static {
        Random random = new Random(-1061214964488769724L);

        zobristTableWhiteToMove = 1L;

        for (long[] pieceArray : zobristTablePieces) {
            for (int i = 0; i < 64; i++) {
                pieceArray[i] = random.nextLong();
            }
        }
        for (long[] hasMoved : zobristPieceHasNotMoved) {
            for (int i = 0; i < 64; i++) {
                hasMoved[i] = random.nextLong();
            }
        }
        for (int i = 0; i < 8; i++) {
            zobristTableEnPassant[i] = random.nextLong();
        }
    }

    public static class KeyData {
        KeyData(long currentKey, boolean[] enPassantPossible) {
            this.currentKey = currentKey;
            this.enPassantPossible = enPassantPossible;
        }
        public long currentKey;
        boolean[] enPassantPossible;
        public KeyData copy() {
            return new KeyData(this.currentKey, enPassantPossible.clone());
        }
    }

    public long getKeyFromBoardAndMove(Board board, Move move, boolean whiteHasMove) {
        board.makeMove(move);
        long res = this.getDataFromBoard(board, whiteHasMove).currentKey;
        board.undoMove(move);
        return res;
    }

    public static int getValueFromBoardAndMove(Move move, ZobristHashMap hash, Board board, boolean whiteHasMove) {
        long keyMove = hash.getKeyFromBoardAndMove(board, move, whiteHasMove);
        ZobristHashEntry hashEntryMove = hash.table.get(keyMove);
        int badValue = whiteHasMove ? -100000000 : 100000000;
        return (hashEntryMove != null) ? hashEntryMove.value : badValue;
    }


    public KeyData getDataFromBoard(Board board, boolean whiteHasMove) {

        boolean[] enPassantPossible = new boolean[8];
        long key = 0;


        for (int singlePos = 26; singlePos < 118; singlePos++) {
            byte figure = board.getCurrentBoard()[singlePos];
            int filedNumber = (singlePos % 12 - 2) + 8 * (singlePos / 12 - 2);
            if ((figure & 0b11000) == 0b00000 || (figure & 0b11000) == 0b01000) {
                continue;
            }

            if (type(figure) == PAWN) {
                if ((figure & 0b100000) == 0b100000) {//en passant möglich
                    if ((figure & 0b1000000) == 0b1000000) {//en passant rechts
                        enPassantPossible[(singlePos % 12 - 2) - 1] = true;
                    } else {//en passant links
                        enPassantPossible[(singlePos % 12 - 2) + 1] = true;
                    }
                }
            }

            key ^= getZobristTableIndex(figure)[filedNumber];
        }

        if (whiteHasMove) {
            key ^= zobristTableWhiteToMove;
        }

        //en Passant auf Key dazu
        for (int i = 0; i < 8; i++) {
            if (enPassantPossible[i]) {
                key ^= zobristTableEnPassant[i];
            }
        }

        return new KeyData(key, enPassantPossible);
    }

    private long[] getZobristTableIndex(byte figure) {
        int zobristTablePiecesOffset = (color(figure) == WHITE) ? 0 : 6;
        int zobristTablePiecesOffsetHasMoved = (color(figure) == WHITE) ? 0 : 2;
        long[] returnArray = null;


        switch (type(figure)) {
            case PAWN -> returnArray = zobristTablePieces[zobristTablePiecesOffset];
            case KNIGHT -> returnArray = zobristTablePieces[zobristTablePiecesOffset + 1];
            case BISHOP -> returnArray = zobristTablePieces[zobristTablePiecesOffset + 2];
            case ROOK -> {
                if ((figure & 0b100000) == 0) {
                    returnArray = zobristPieceHasNotMoved[zobristTablePiecesOffsetHasMoved + 1];

                } else {
                    returnArray = zobristTablePieces[zobristTablePiecesOffset + 3];
                }

            }
            case QUEEN -> returnArray = zobristTablePieces[zobristTablePiecesOffset + 4];
            case KING -> {
                if ((figure & 0b100000) == 0) {
                    returnArray = zobristPieceHasNotMoved[zobristTablePiecesOffsetHasMoved];
                } else {
                    returnArray = zobristTablePieces[zobristTablePiecesOffset + 5];

                }
            }
        }
        return returnArray;
    }
}
