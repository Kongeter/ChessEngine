package ChessEngine;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Helper {
    public static boolean isFinished(Board board) {
        return false; //TODO
    }

    public static byte[] resetGame() {
        return FENtoArray("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    //einmalig Figurenposition bestimmen
    public static List<Integer> getPositionsAfterMove(Move move, List<Integer> Positions){
        List<Integer> result = new ArrayList<>(Positions);
        result.remove(Integer.valueOf(move.start));
        result.remove(Integer.valueOf(move.end));
        result.add(move.end);
        return result;
    }


    public static List<Integer> FigurenGetPos(byte[] board){
        List<Integer> res = new ArrayList<>();
        for(int i = 0; i< 144; i++){
            if((board[i]&0b10000) == 0b10000){
                res.add(i);
            }
        }
        return res;
    }

    static public Move getRandomMove(List<Move> moves){
        Random random = new Random();
        return moves.get(random.nextInt(moves.size()));
    }

    public static byte[] FENtoArray(String FENCode) {
        byte[] AusgabeFeld = new byte[144];
        for (int i = 0; i < 144; i++) {
            if (i < 24 || i > 119 || i % 12 == 0 || i % 12 == 1 || i % 12 == 10 || i % 12 == 11)
                AusgabeFeld[i] = (byte) 0b01000;
        }
        String[] FENCodeNurFeld = FENCode.split(" ");
        String[] FENCodeZeile = FENCodeNurFeld[0].split("/");
        for (int y = 0; y < 8; y++) {
            int curr = 0;
            for (int x = 0; x < 8; x++) {
                char singeChar = FENCodeZeile[y].charAt(curr);
                if (Character.isDigit(singeChar)) {
                    x += Character.getNumericValue(singeChar)-1;
                } else {
                    AusgabeFeld[(x + 2) + ((y + 2) * 12)] = CharToBin(singeChar);
                }
                curr++;
            }
        }
        return AusgabeFeld;
    }

    private static byte CharToBin(char input) {
        byte result = 0b00000;
        switch (input) {
            case 'p' -> result = (byte) 0b11001;
            case 'n' -> result = (byte) 0b11010;
            case 'b' -> result = (byte) 0b11011;
            case 'r' -> result = (byte) 0b11100;
            case 'q' -> result = (byte) 0b11101;
            case 'k' -> result = (byte) 0b11110;

            case 'P' -> result = (byte) 0b10001;
            case 'N' -> result = (byte) 0b10010;
            case 'B' -> result = (byte) 0b10011;
            case 'R' -> result = (byte) 0b10100;
            case 'Q' -> result = (byte) 0b10101;
            case 'K' -> result = (byte) 0b10110;
        }
        return result;
    }

    public static void printBoard(Board board){
        printBoard(board.getCurrentBoard());
    }
    public static void printBoard(byte[] currentBoard){
        System.out.println(" ");
        int x = 0;
        for(byte fig : currentBoard){
            if(fig != 8){
                if (x == 8) {
                    x = 0;
                    System.out.println(" ");
                }
                if(fig == 0){
                    System.out.print(0);
                }
                System.out.print(fig + " ");
                x ++;
            }
        }
        System.out.println(" ");
    }

    public static boolean printDiff(byte[] currentBoard1,byte[] currentBoard2){
        boolean res = false;
        for(int i = 0; i<130; i++){
                if(currentBoard1[i]!=currentBoard2[i]){
                    System.out.println("Difference found bei Index: "+ i + "     Feld1[i] = " + currentBoard1[i] + "     Feld2[i] = " + currentBoard2[i]);
                    res = true;
            }
        }
        return res;

    }

    public static void printArray(boolean[] array){
        System.out.println(" ");
        int x = 0;
        for(boolean fig : array){
                if (x == 12) {
                    x = 0;
                    System.out.println(" ");
                }
                if(fig){
                    System.out.print(1 + " ");
                }
                else {
                    System.out.print(0 + " ");
                }

                x ++;
            }

        System.out.println(" ");
    }


    static private int getFieldIndexFromChessNotationIndex(String position) {
        int xval = -1;
        switch (position.charAt(0)) {
            case 'a' -> xval = 0;
            case 'b' -> xval = 1;
            case 'c' -> xval = 2;
            case 'd' -> xval = 3;
            case 'e' -> xval = 4;
            case 'f' -> xval = 5;
            case 'g' -> xval = 6;
            case 'h' -> xval = 7;
        }
        int yval = Character.getNumericValue(position.charAt(1));

        return (8 - yval + 2) * 12 + ((xval) + 2);
    }
    static public String getChessNotationIndexFromFieldIndex(int position) {
        String res = "";

        int xval =  Move.getPosX(position);
        switch (xval) {
            case 0-> res += "a";
            case 1 -> res += "b";
            case 2 -> res += "c";
            case 3 -> res += "d";
            case 4 -> res += "e";
            case 5 -> res += "f";
            case 6 -> res += "g";
            case 7 -> res += "h";
        }
        res += String.valueOf(8-Move.getPosY(position));
        return res;
    }

}
