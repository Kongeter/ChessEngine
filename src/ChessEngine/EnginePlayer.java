package ChessEngine;

import ChessEngine.OpeningBook.OpeningBook;

public class EnginePlayer {
    ZobristHashMap hash = new ZobristHashMap();
    OpeningBook openingBook = new OpeningBook();
    TreeSearchEngine treeSearchEngine;
    int age = 0;
    int openingBookDepth;
    int maxSearchDepth = -1;
    int maxTime = -1;


    public EnginePlayer(int openingBookDepth, int maxSearchDepth) {
        this.openingBookDepth = openingBookDepth;
        this.maxSearchDepth = maxSearchDepth;
    }

    public EnginePlayer(int openingBookDepth, int maxTime, boolean maxTimeSearch) {
        this.openingBookDepth = openingBookDepth;
        this.maxTime = maxTime;
    }

    public Move getMove(Board board, Boolean whiteHasMove) {
        if(treeSearchEngine==null){
            treeSearchEngine = new TreeSearchEngine(board, whiteHasMove);
        }

        if (age <= openingBookDepth) {
            String openingBookMove = openingBook.getMoveFromOpeningBook(hash.getDataFromBoard(board, whiteHasMove).currentKey);
            if (openingBookMove != null) {
                return BoardHelper.pgnToMove(openingBookMove, board, whiteHasMove);
            }
        }


        Move bestMove;
        if (maxTime == -1) {
            bestMove = treeSearchEngine.findBestMoveDepth(board, whiteHasMove, maxSearchDepth, age);
        } else {
            bestMove = treeSearchEngine.findBestMoveTime(board, whiteHasMove, maxTime, age);
        }

        age += 2;
        return bestMove;
    }


}
