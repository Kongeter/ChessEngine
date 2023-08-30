import ChessEngine.Board;
import ChessEngine.Engine;
import ChessEngine.OpeningBook.OpeningBook;
import ChessEngine.OpeningBook.OpeningBookCreator;
import ChessEngine.TreeSearchEngine;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
//       OpeningBookCreator opc = new OpeningBookCreator();

//       opc.pgnToPureAlgebraic("/home/tk/OneDrive/Projekte/SchachEngine/pgnLibary/");

//      opc.createOpeningBook("./src/ChessEngine/OpeningBook/pureGameNotation/", 10);

        Engine e = new Engine();
        e.playOneGameHumanVsEngine(null, true, true, 10);
    }
}
