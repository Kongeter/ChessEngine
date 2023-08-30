package ChessEngine;

import ChessEngine.MoveGeneratorF.MoveGenerator;

import static ChessEngine.BinaryHelper.*;

public class PostionEvaluation {

    private static final int[] pawnTable = {
            0, 0, 0, 0, 0, 0, 0, 0,
            50, 50, 50, 50, 50, 50, 50, 50,
            10, 10, 20, 30, 30, 20, 10, 10,
            5, 5, 10, 25, 25, 10, 5, 5,
            0, 0, 0, 20, 20, 0, 0, 0,
            5, -5, -10, 0, 0, -10, -5, 5,
            5, 10, 10, -20, -20, 10, 10, 5,
            0, 0, 0, 0, 0, 0, 0, 0};

    private static final int[] knightTable = {
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50,
    };

    private static final int[] bishopTable = {
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -20,-10,-10,-10,-10,-10,-10,-20,
    };
    private static final int[] rookTable = {
            0,  0,  0,  0,  0,  0,  0,  0,
            5, 10, 10, 10, 10, 10, 10,  5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            0,  0,  0,  5,  5,  0,  0,  0
    };
    private static final int[] queenTable = {
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -5,  0,  5,  5,  5,  5,  0, -5,
            0,  0,  5,  5,  5,  5,  0, -5,
            -10,  5,  5,  5,  5,  5,  0,-10,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20
    };
    private static final int[] kingTable = {
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -20,-30,-30,-40,-40,-30,-30,-20,
            -10,-20,-20,-20,-20,-20,-20,-10,
            20, 20,  0,  0,  0,  0, 20, 20,
            20, 30, 10,  0,  0, 10, 30, 20
    };




    PostionEvaluation() {
    }

    byte FARBE; // FARBE die den nächsten zug macht

    byte[] Feld; // gegnerische farbe
    byte[] currentBoard; // ganzes Spielfeld

    //List<Integer> Figuren; //alle Positionen wo Figuren stehen
    int numberOfMovesEvaluated = 0;

    public int numberOfNewMovesEvaluated(){
        int res = numberOfMovesEvaluated;
        numberOfMovesEvaluated = 0;
        return res;
    }


    private int getPieceTableIndex(int Index_12x12, boolean whiteHasMove){
        int Index_64 = (Index_12x12 % 12 - 2) + 8 * (Index_12x12 / 12 - 2);
        if(whiteHasMove){
            return  Index_64;
        }
        else {
            return 63-Index_64;
        }
    }
    public int Eval(Board board, boolean whiteHasMove) {
        numberOfMovesEvaluated ++;
        //TODO schachmatt?

        int EvaluationSum = 0;
        this.Feld = board.getCurrentBoard();
        //this.Figuren = board.getFigureIndexes();
        //for (int singlePos : Figuren) {
        for (int singlePos = 26; singlePos < 118; singlePos++) {
            if ((Feld[singlePos] & 0b10000) != 0b10000) {
                continue;
            }
            int singeValue = 0;
            if (WHITE == (Feld[singlePos] & 0b11000)) { //weiß
                switch (Feld[singlePos] & 0b00111) {
                    case PAWN -> singeValue = 100+pawnTable[getPieceTableIndex(singlePos,false)];
                    case KNIGHT -> singeValue = 320+knightTable[getPieceTableIndex(singlePos,false)];
                    case BISHOP -> singeValue = 330+ bishopTable[getPieceTableIndex(singlePos,false)];
                    case ROOK -> singeValue = 500+rookTable[getPieceTableIndex(singlePos,false)];
                    case QUEEN -> singeValue = 900+queenTable[getPieceTableIndex(singlePos,false)];
                    case KING -> singeValue = 20000+kingTable[getPieceTableIndex(singlePos,false)];
                }
                EvaluationSum += singeValue;
            }
            else { //schwarz
                switch (Feld[singlePos] & 0b00111) {
                    case PAWN -> singeValue = 100+pawnTable[getPieceTableIndex(singlePos,true)];
                    case KNIGHT -> singeValue = 320+knightTable[getPieceTableIndex(singlePos,true)];
                    case BISHOP -> singeValue = 330+ bishopTable[getPieceTableIndex(singlePos,true)];
                    case ROOK -> singeValue = 500+rookTable[getPieceTableIndex(singlePos,true)];
                    case QUEEN -> singeValue = 900+queenTable[getPieceTableIndex(singlePos,true)];
                    case KING -> singeValue = 20000+kingTable[getPieceTableIndex(singlePos,true)];
                }
                EvaluationSum -= singeValue;
            }


        }
        return EvaluationSum;
    }
    //White
    public int GameFinishedEval(Board board, boolean whiteHasMove) {

        int kingPos = MoveGenerator.currentKingPos(board.currentBoard, whiteHasMove);
        boolean[] inCheckBoard = new boolean[144];
        inCheckBoard = MoveGenerator.calculateInCheckBoard(board, !whiteHasMove , inCheckBoard);
        if(inCheckBoard[kingPos]){
            return whiteHasMove ? -1000001 : 1000001;
        }
        else {
            return 0;
        }

    }

}

