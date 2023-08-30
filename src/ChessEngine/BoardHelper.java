package ChessEngine;

import ChessEngine.MoveGeneratorF.MoveGenerator;

import java.util.List;
import java.util.Objects;

import static ChessEngine.BinaryHelper.*;
import static ChessEngine.BinaryHelper.CharToBin;

public class BoardHelper {


    public static Move pgnToMove(String pgn, Board board, boolean whiteHasMove) {
        List<Move> moves = new MoveGenerator().generate(board, whiteHasMove);
        pgn = pgn.replaceAll("[x+]", "");

        int curr = 0;
        byte type;
        if(pgn.equals("O-O")){ //Rochade rechts
            return moves.stream().filter(move -> KING == type(board.currentBoard[move.start]) && move.start-move.end == -2).findFirst().orElseThrow();
        }
        if(pgn.equals("O-O-O")){ //Rochade links
            return moves.stream().filter(move -> KING == type(board.currentBoard[move.start]) && move.start-move.end == 2).findFirst().orElseThrow();
        }

        switch (pgn.charAt(curr)) {
            case 'P' -> type = PAWN;
            case 'N' -> type = KNIGHT;
            case 'B' -> type = BISHOP;
            case 'R' -> type = ROOK;
            case 'Q' -> type = QUEEN;
            case 'K' -> type = KING;
            default -> {
                type = PAWN;
                pgn = "P" + pgn;
            }

        }

        int endPos = getFieldIndexFromChessNotationIndex(pgn.substring(pgn.length()-2));

        if(pgn.length() == 3){
            return moves.stream().filter(move -> endPos == move.end &&  type == type(board.currentBoard[move.start])).findFirst().orElseThrow();
        }

       if(pgn.length() == 4){
           if(Character.isDigit(pgn.charAt(1))){
               int startRow = Character.getNumericValue(pgn.charAt(1))-1;
               return moves.stream().filter(move -> endPos == move.end &&  type == type(board.currentBoard[move.start])&& move.getStartFieldY() == startRow).findFirst().orElseThrow();
           }
           else{
               int startCol = getColumnNumberFromLetter(pgn.charAt(1));
               return moves.stream().filter(move -> endPos == move.end &&  type == type(board.currentBoard[move.start])&& move.getStartFieldX() == startCol).findFirst().orElseThrow();
           }

       }
       if(pgn.length() == 5){
           int startPos = getFieldIndexFromChessNotationIndex(pgn.substring(1, 3));
           return moves.stream().filter(move -> startPos == move.start && endPos == move.end).findFirst().orElseThrow();
        }

        throw new RuntimeException("pgnToMove(): konnte keinen Move wie in PGN angegeben finden");
    }


    public static String boardToString(Board board) {
        StringBuilder res = new StringBuilder();
        res.append(arrayToFen(board)).append("                                               ---------------------------------\n|");

        int col = 0;
        for (int singlePos : possiblePositions) {
            //int filedNumber = (singlePos % 12 - 2) + 8 * (singlePos / 12 - 2);
            res.append(" ").append(binToStr(board.currentBoard[singlePos])).append(" |");
            col++;
            if (col == 8) {
                col = 0;
                res.append("\n---------------------------------\n|");
            }

        }
        return res.toString();
    }

    public static String arrayToFen(Board board) {
        StringBuilder res = new StringBuilder();
        int col = 0;
        int numOfEmptySpaces = 0;
        for (int singlePos : possiblePositions) {
            byte piece = (byte) (board.currentBoard[singlePos] & 0b11111);

            if (piece != 0 && numOfEmptySpaces != 0) {
                res.append(numOfEmptySpaces);
                numOfEmptySpaces = 0;
            }

            switch (piece) {
                case 0 -> numOfEmptySpaces += 1;
                case (PAWN | BLACK) -> res.append("p");
                case (KNIGHT | BLACK) -> res.append("n");
                case (BISHOP | BLACK) -> res.append("b");
                case (ROOK | BLACK) -> res.append("r");
                case (QUEEN | BLACK) -> res.append("q");
                case (KING | BLACK) -> res.append("k");
                case (PAWN | WHITE) -> res.append("P");
                case (KNIGHT | WHITE) -> res.append("N");
                case (BISHOP | WHITE) -> res.append("B");
                case (ROOK | WHITE) -> res.append("R");
                case (QUEEN | WHITE) -> res.append("Q");
                case (KING | WHITE) -> res.append("K");
            }


            col++;
            if (col == 8) {
                if (numOfEmptySpaces != 0) {
                    res.append(numOfEmptySpaces);
                    numOfEmptySpaces = 0;
                }
                res.append("/");
                col = 0;
            }


        }
        return res.toString();
    }

    public static byte[] fenToArray(String fenCode) {
        byte[] ausgabeFeld = new byte[144];

        //setzt Ränder auf blocked
        for (int i = 0; i < 144; i++) {
            if (i < 24 || i > 119 || i % 12 == 0 || i % 12 == 1 || i % 12 == 10 || i % 12 == 11)
                ausgabeFeld[i] = (byte) 0b01000;
        }

        //berechnet feld
        String[] fenCodeSplitUp = fenCode.split(" ");
        String[] fenCodeFeldZeile = fenCodeSplitUp[0].split("/");
        for (int y = 0; y < 8; y++) {
            int curr = 0;
            for (int x = 0; x < 8; x++) {
                char singeChar = fenCodeFeldZeile[y].charAt(curr);
                if (Character.isDigit(singeChar)) {
                    x += Character.getNumericValue(singeChar) - 1;
                } else {
                    ausgabeFeld[(x + 2) + ((y + 2) * 12)] = CharToBin(singeChar);
                }
                curr++;
            }
        }


        //setzt RochadeFlags
        for (int cursor = 0; cursor < fenCodeSplitUp[2].length(); cursor++) {
            char c = fenCodeSplitUp[2].charAt(cursor);
            {
                switch (c) {
                    case '-' -> {
                    }
                    case 'q' -> {
                        ausgabeFeld[26] = (byte) (ausgabeFeld[26] & 0b011111);
                        ausgabeFeld[30] = (byte) (ausgabeFeld[30] & 0b011111);
                    }
                    case 'k' -> {
                        ausgabeFeld[33] = (byte) (ausgabeFeld[33] & 0b011111);
                        ausgabeFeld[30] = (byte) (ausgabeFeld[30] & 0b011111);
                    }
                    case 'Q' -> {
                        ausgabeFeld[110] = (byte) (ausgabeFeld[110] & 0b011111);
                        ausgabeFeld[114] = (byte) (ausgabeFeld[114] & 0b011111);
                    }
                    case 'K' -> {
                        ausgabeFeld[117] = (byte) (ausgabeFeld[117] & 0b011111);
                        ausgabeFeld[114] = (byte) (ausgabeFeld[114] & 0b011111);
                    }
                }
            }
        }

        //setzt EnPassantFlags
        if (!Objects.equals(fenCodeSplitUp[3], "-")) {
            int position = getFieldIndexFromChessNotationIndex(fenCodeSplitUp[3]);

            if (Objects.equals(fenCodeSplitUp[1], "w")) { //weiß am zug
                if (ausgabeFeld[position + 11] == 0b10001) { //links vom feld
                    ausgabeFeld[position + 11] = 0b0110001;
                }
                if (ausgabeFeld[position + 13] == 0b10001) { //rechts vom feld
                    ausgabeFeld[position + 13] = 0b1110001;
                }


            } else { //schwarz am zug
                if (ausgabeFeld[position - 13] == 0b11001) { //links vom feld
                    ausgabeFeld[position - 13] = 0b0111001;
                }
                if (ausgabeFeld[position - 11] == 0b11001) { //rechts vom feld
                    ausgabeFeld[position - 11] = 0b1111001;
                }
            }

        }


        return ausgabeFeld;
    }

    static private int getFieldIndexFromChessNotationIndex(String position) {

        int xCoordinate = -1;
        switch (position.charAt(0)) {
            case 'a' -> xCoordinate = 0;
            case 'b' -> xCoordinate = 1;
            case 'c' -> xCoordinate = 2;
            case 'd' -> xCoordinate = 3;
            case 'e' -> xCoordinate = 4;
            case 'f' -> xCoordinate = 5;
            case 'g' -> xCoordinate = 6;
            case 'h' -> xCoordinate = 7;
        }
        int yCoordinate = Character.getNumericValue(position.charAt(1));

        return (8 - yCoordinate + 2) * 12 + ((xCoordinate) + 2);
    }

    static private int getColumnNumberFromLetter(char input){
        int output = -1;
        switch (input) {
            case 'a' -> output = 0;
            case 'b' -> output = 1;
            case 'c' -> output = 2;
            case 'd' -> output = 3;
            case 'e' -> output = 4;
            case 'f' -> output = 5;
            case 'g' -> output = 6;
            case 'h' -> output = 7;
        }
        return output;
    }

}
