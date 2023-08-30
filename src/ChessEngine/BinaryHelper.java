package ChessEngine;

public class BinaryHelper {
    /*binary Helper Class*/
    public static final int[] possiblePositions = {26, 27, 28, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 43, 44, 45, 50, 51, 52, 53, 54, 55, 56, 57, 62, 63, 64, 65, 66, 67, 68, 69, 74, 75, 76, 77, 78, 79, 80, 81, 86, 87, 88, 89, 90, 91, 92, 93, 98, 99, 100, 101, 102, 103, 104, 105, 110, 111, 112, 113, 114, 115, 116, 117};
    public static final byte BLACK = 0b11000;
    public static final byte WHITE = 0b10000;
    public static final byte PAWN = 0b001;
    public static final byte KNIGHT = 0b010;
    public static final byte BISHOP = 0b011;
    public static final byte ROOK = 0b100;
    public static final byte QUEEN = 0b101;
    public static final byte KING = 0b110;
    public static final byte BLOCK = 0b01000;

    public static final byte pieceFlags = 0b00111;
    public static final byte colorFlags = 0b11000;

    public static byte color(byte i) {
        return (byte) (i & colorFlags);
    }

    public static byte type(byte i) {
        return (byte) (i & pieceFlags);
    }


    public static void setMovedFlag(int position, byte[] board) {
        board[position] = (byte) (board[position] | 0b100000);
    }

    public static void removeMovedFlag(int position, byte[] board) {
        board[position] = (byte) (board[position] & 0b11011111);
    }

    public static boolean hasMoved(int position, byte[] board) {
        return (board[position] & 0b100000) == 0b100000;
    }

    public static boolean FieldBlocked(int fieldNumber, byte[] field, byte farbe) {
        return (((field[fieldNumber] & 0b11000) == BLOCK) || ((field[fieldNumber] & 0b11000) == farbe));
    }

    public static boolean FieldIsEnemy(int fieldNumber, byte[] field, byte feindfarbe) {
        return ((field[fieldNumber] & 0b11000) == feindfarbe);
    }

    public static void removeEnPassantFlags(int fieldNumber, byte[] field) {
        if (type(field[fieldNumber]) == PAWN) {
            field[fieldNumber] = (byte) (field[fieldNumber] & 0b0011111);
        }
    }
    public static String binToStr(byte input) {
        String result = " ";
        input &= 0b00011111;
        switch (input) {
            case  0b11001-> result = "p";
            case  0b11010-> result = "n";
            case  0b11011-> result = "b";
            case  0b11100-> result = "r";
            case  0b11101-> result = "q";
            case  0b11110-> result = "k";

            case  0b10001-> result = "P";
            case  0b10010-> result = "N";
            case  0b10011-> result = "B";
            case  0b10100-> result = "R";
            case  0b10101-> result = "Q";
            case  0b10110-> result = "K";
        }
        return result;
    }

    public static Character binToChar(byte input){
        return binToStr(input).charAt(0);
    }

    public static byte CharToBin(char input) {
        byte result = 0b00000;
        switch (input) {
            case 'p' -> result = (byte) 0b11001;
            case 'n' -> result = (byte) 0b11010;
            case 'b' -> result = (byte) 0b11011;
            case 'r' -> result = (byte) 0b111100;
            case 'q' -> result = (byte) 0b11101;
            case 'k' -> result = (byte) 0b111110;

            case 'P' -> result = (byte) 0b10001;
            case 'N' -> result = (byte) 0b10010;
            case 'B' -> result = (byte) 0b10011;
            case 'R' -> result = (byte) 0b110100;
            case 'Q' -> result = (byte) 0b10101;
            case 'K' -> result = (byte) 0b110110;
        }
        return result;
    }



}
