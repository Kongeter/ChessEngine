package ChessEngine;

import ChessEngine.MoveGeneratorF.MoveGenerator;


import java.util.List;
import java.util.concurrent.*;


public class TreeSearchEngine {
    PostionEvaluation postionEvaluator = new PostionEvaluation();
    MoveGenerator moveGenerator = new MoveGenerator();
    ZobristHashMap hash;
    int age;
    static final boolean debug = true;

    public TreeSearchEngine(Board board, Boolean whiteHasMove){
        hash = new ZobristHashMap();
    }

    public Move findBestMoveTime(Board board, Boolean whiteHasMove, int time, int age) {
        this.age = age;
        Move bestMove = null;
        long startTime = System.currentTimeMillis();
        long timeLeft = time * 1000L;
        int currentDepth = 1;
        while (timeLeft>0) {
            try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
                Callable<Move> task = new alphaBetaTask(board, whiteHasMove, currentDepth, age);
                Future<Move> future = executor.submit(task);
                try {
                    bestMove = future.get(timeLeft, TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    if(debug)System.out.println("Berechnete Tiefe:" + (currentDepth-1));
                    future.cancel(true);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    executor.shutdownNow();
                }
            }
            currentDepth ++;
            timeLeft -= System.currentTimeMillis()-startTime;
        }
        return bestMove;
    }
    private class alphaBetaTask implements Callable<Move> {
        Board board;
        boolean whiteHasMove;
        private int depth;
        int age;
        public alphaBetaTask(Board board, boolean whiteHasMove, int depth, int age) {
            this.depth = depth;
            this.whiteHasMove = whiteHasMove;
            this.board = board;
            this.age = age;
        }
        @Override
        public Move call() throws Exception {
            return alphaBetaSearch(board, whiteHasMove, depth);
        }
    }

    public Move findBestMoveDepth(Board board, Boolean whiteHasMove, int maxDepth, int age) {
        this.age = age;

        Move bestMove = null;

        for (int depth = 1; depth <= maxDepth; depth += 1) {
            bestMove = alphaBetaSearch(board, whiteHasMove, depth);
        }

        if (debug) System.out.println();

        return bestMove;
    }

    public Move alphaBetaSearch(Board board, Boolean whiteHasMove, int depth) {

        List<Move> moves = moveGenerator.generate(board, whiteHasMove);

        if (moves.isEmpty()) {
            return null;
        }

        Move bestMove = moves.get(0);

        int alpha = -1000000;
        int beta = 1000000;

        int bestValue = whiteHasMove ? -1000000 : 1000000;

        MoveOrdering.orderWithHash(moves, hash, board, whiteHasMove);


        for (Move move : moves) {
            board.makeMove(move);
            //--------------------
            int result;
            long key = hash.getDataFromBoard(board, whiteHasMove).currentKey;
            ZobristHashEntry oldEntry = hash.table.get(key);
            if (oldEntry != null && oldEntry.age + 8 > age && oldEntry.depth >= depth && oldEntry.zobristHash == key) {
                result = oldEntry.value;
            } else {
                result = alphaBetaRec(board, depth - 1, alpha, beta, !whiteHasMove);
            }
            //---------------------
            board.undoMove(move);

            if (whiteHasMove) {
                //maximizing players turn
                alpha = Math.max(alpha, result);
                if (bestValue < result) {
                    bestValue = result;
                    bestMove = move;
                }
            } else {
                //minimizing players turn
                beta = Math.min(beta, result);
                if (bestValue > result) {
                    bestValue = result;
                    bestMove = move;
                }
            }
        }

        if (debug) System.out.print(postionEvaluator.numberOfNewMovesEvaluated() + "   ");

        return bestMove;
    }


    public int alphaBetaRec(Board board, int depth, int alpha, int beta, Boolean maxPlayer) {
        if (depth == 0) {
            return postionEvaluator.Eval(board, maxPlayer);
        }
        int bestValue = maxPlayer ? -1000000 : 1000000;

        List<Move> moves = moveGenerator.generate(board, maxPlayer);
        MoveOrdering.orderWithHash(moves, hash, board, maxPlayer);

        if (moves.isEmpty()) {
            return postionEvaluator.GameFinishedEval(board, maxPlayer);
        }

        for (Move move : moves) {
            board.makeMove(move);
            //--------------------
            int result;

            long key = hash.getDataFromBoard(board, maxPlayer).currentKey;

            ZobristHashEntry oldEntry = hash.table.get(key);
            if (oldEntry != null && oldEntry.age + 8 > age && oldEntry.depth >= depth && oldEntry.zobristHash == key) {
                result = oldEntry.value;
            } else {
                result = alphaBetaRec(board, depth - 1, alpha, beta, !maxPlayer);
                ZobristHashEntry entry = new ZobristHashEntry(age, result, depth, key);
                hash.table.put(key, entry);
            }
            //---------------------
            board.undoMove(move);

            if (maxPlayer) {
                //maximizing players turn
                alpha = Math.max(alpha, result);
                bestValue = Math.max(bestValue, result);
            } else {
                //minimizing players turn
                beta = Math.min(beta, result);
                bestValue = Math.min(bestValue, result);
            }
            if (beta <= alpha) {
                break;
            }
        }
        return bestValue;
    }


    public int minMax(Board board, int depth, Boolean maxPlayer) {
        if (depth == 0 || Helper.isFinished(board)) {
            return postionEvaluator.Eval(board, maxPlayer);
        }
        if (maxPlayer) {
            int maxEval = -10000000;

            List<Move> moves = moveGenerator.generate(board, true);

            for (Move move : moves) {
                board.makeMove(move);
                int r = minMax(board, depth - 1, false);
                maxEval = Math.max(maxEval, r);
                board.undoMove(move);
            }
            return maxEval;
        } else {
            int minEval = 10000000;
            List<Move> moves = moveGenerator.generate(board, false);
            for (Move move : moves) {
                board.makeMove(move);
                int r = minMax(board, depth - 1, true);
                minEval = Math.min(minEval, r);
                board.undoMove(move);
            }
            return minEval;
        }
    }
}


