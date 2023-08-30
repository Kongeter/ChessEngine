package ChessEngine;

import ChessEngine.MoveGeneratorF.MoveGenerator;
import ChessUI.MainFrame;

import java.util.List;

public class Engine {
    MainFrame frame;
     static final float scale = 1F;
     static final int openingBookDepth = 10;

    public Engine() {
    }


    public void playOneGameEngineVsEngine(String fen, boolean whiteHasMove, int depth) {
        frame = new MainFrame(scale);
        EnginePlayer enginePlayer1 = new EnginePlayer(openingBookDepth, depth);
        EnginePlayer enginePlayer2 = new EnginePlayer(openingBookDepth, depth);
        Board board = (fen == null) ? new Board() : new Board(fen);

        frame.drawField(board.currentBoard);

        boolean gameFinished = false;
        while (!Helper.isFinished(board) && !gameFinished) {
            if(whiteHasMove){
                gameFinished = engineMove(board, enginePlayer1, true);
            }
            else {
                gameFinished = engineMove(board, enginePlayer2, false);
            }
            whiteHasMove = !whiteHasMove;
        }
    }


    public void playOneGameHumanVsEngine(String fen, boolean whiteHasMove, boolean humanPlaysWhite, int maxTime) {
        frame = new MainFrame(scale);
        EnginePlayer enginePlayer = new EnginePlayer(openingBookDepth, maxTime, true);
        Board board = (fen == null) ? new Board() : new Board(fen);

        frame.drawField(board.currentBoard);

        boolean gameFinished = false;


        if(((humanPlaysWhite&&whiteHasMove) || (!humanPlaysWhite&&!whiteHasMove)) ){
            while (!Helper.isFinished(null)&& !gameFinished) {
                gameFinished = playerMove(board, whiteHasMove);
                if(gameFinished)break;
                gameFinished = engineMove(board, enginePlayer,!whiteHasMove);
            }
        }
        else{
            while (!Helper.isFinished(null)&& !gameFinished) {
                gameFinished = engineMove(board, enginePlayer,whiteHasMove);
                if(gameFinished)break;
                gameFinished = playerMove(board, !whiteHasMove);
            }
        }
    }

    public void PlayOneGameHumanVsHuman(String fen, boolean whiteHasMove) {
        frame = new MainFrame(scale);
        Board board = (fen == null) ? new Board() : new Board(fen);
        frame.drawField(board.currentBoard);
        boolean gameFinished = false;

        while (!gameFinished) {
            gameFinished = playerMove(board, !whiteHasMove);
            whiteHasMove = !whiteHasMove;
        }

    }

    private boolean playerMove(Board board, Boolean isWhiteTurn){
        List<Move> moves = new MoveGenerator().generate(board, isWhiteTurn);
        Move returnMove = frame.getPlayerMove(moves);
        if(!moves.isEmpty()){
            board.makeMove(returnMove);
            frame.drawField(board.getCurrentBoard());
            return false;
        }
        else {
            System.out.println("Game Over");
            return true;
        }

    }

    private boolean engineMove(Board board , EnginePlayer enginePlayer, Boolean isWhiteTurn){
        Move returnMove = enginePlayer.getMove(board, isWhiteTurn);
        if(returnMove != null){
            board.makeMove(returnMove);
            frame.drawField(board.getCurrentBoard());
            return false;
        }
        else {
            System.out.println("Game Over");
            return true;
        }

    }

}
