package ChessEngine;

import ChessEngine.MoveGeneratorF.MoveGenerator;

import java.util.List;

public class PerformanceTest {

    public static void searchPerformanceSuit(){
        long time = System.currentTimeMillis();

        String[] FEN = {
                "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -",
                "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ",
                "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",
                "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8",
        };
        Move[] Results = {
                new Move( 102 , 50 ,0),
                new Move( 75 , 79 ,0),
                new Move( 116 , 117 ,0),
                new Move( 41 , 28 ,0)
        };

        Integer[] depth = {6,9,7,8};

        boolean testFailedFlag = false;
        for (int i = 0; i < FEN.length; i++) {
            long startTimeSingleTest = System.currentTimeMillis();

            Board board = new Board(FEN[i]);
            Move bestMove = (new TreeSearchEngine(board, true).findBestMoveDepth(board,true, depth[i], 0));

            if (bestMove.start != Results[i].start && bestMove.end != Results[i].end) {
                System.out.println("Error bei Test " + i + "  Gefundener Move: " + bestMove.getMoveAsString() +"   Erwarteter Move: " + Results[i].getMoveAsString());
                testFailedFlag = true;
            } else {
                System.out.println("Test " + i +" passed in: " + (System.currentTimeMillis() - startTimeSingleTest)/1000 + "s");
            }
        }
        System.out.println("Zeit für alle Tests: " +((System.currentTimeMillis() - time)/60000) + "min , "  + ((System.currentTimeMillis() - time)/1000%60) + "s");
        if (testFailedFlag){
            System.out.println("At least one Test Failed");
        }
    }

    public static Board performanceTestDebugger() {

        int depth;
        Board board;
        boolean whiteHasMove;

        depth = 8;
        board = new Board("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -");
        whiteHasMove = true;

        MoveGenerator moveGen = new MoveGenerator();

        List<Move> moves = moveGen.generate(board, whiteHasMove);

        long sum = 0;

        for (Move move : moves) {

            if(depth == 1){
                System.out.print(Helper.getChessNotationIndexFromFieldIndex(move.start));
                System.out.print(Helper.getChessNotationIndexFromFieldIndex(move.end));
                System.out.println(": "+ 1);
            }
            else {

                System.out.print(Helper.getChessNotationIndexFromFieldIndex(move.start));
                System.out.print(Helper.getChessNotationIndexFromFieldIndex(move.end));

                board.makeMove(move);
                long moveNumber = performanceTestRec(depth - 1, board, moveGen, !whiteHasMove);
                sum += moveNumber;
                System.out.println(": " + moveNumber);
                board.undoMove(move);
            }

        }
        System.out.println(sum);
        System.out.println(moves.size());



        return board;
    }


    public static long performanceTest(int depth, String FEN) {
        Board board = new Board(FEN);
        MoveGenerator MoveGen = new MoveGenerator();
        return performanceTestRec(depth, board, MoveGen, true);
    }

    public static void performanceTestSuit() {
        long time = System.currentTimeMillis();
        String[] FEN = {
                "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -",
                "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ",
                "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",
                "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8",
                "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ",
                "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - "
        };
        Long[] Results = {
                193690690L,
                178633661L,
                706045033L,
                89941194L,
                8031647685L,
                3009794393L
        };
        Integer[] depth = {5, 7, 6, 5, 6, 8};

        for (int i = 0; i < FEN.length; i++) {
            long myResult = performanceTest(depth[i], FEN[i]) ;
            if (myResult != Results[i]) {
                System.out.println("ERROR BEI " + i + ", depth " + depth[i] + "  my result is " + myResult);
            } else {
                System.out.println("pass");
            }
        }
        System.out.println("Zeit für alle Tests: " +((System.currentTimeMillis() - time)/60000) + ":"  + ((System.currentTimeMillis() - time)%60000));
    }

    public static void performanceTestPrint(int maxDepth, String FEN) {
        Board board = new Board(FEN);

        MoveGenerator MoveGen = new MoveGenerator();
        for (int depth = 1; depth < maxDepth + 1; depth++) {
            long startTime = System.currentTimeMillis();
            long moveNumber = performanceTestRec(depth, board, MoveGen, true);
            long time = System.currentTimeMillis() - startTime;

            System.out.println("Time: " + time + "ms   Depth = " + depth + "     MoveNumber = " + moveNumber);
        }

    }


    private static long performanceTestRec(int depth, Board board, MoveGenerator moveGen, boolean whiteHasMove) {

        List<Move> moves = moveGen.generate(board, whiteHasMove);
        long sum = 0;

        if (depth == 1 || moves.isEmpty()) {
            return moves.size();
        }

        for (Move move : moves) {
            board.makeMove(move);
            sum += performanceTestRec(depth - 1, board, moveGen, !whiteHasMove);
            board.undoMove(move);
        }
        return sum;
    }

}
