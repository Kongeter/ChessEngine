package ChessEngine.OpeningBook;

import ChessEngine.Board;
import ChessEngine.Move;

import javax.imageio.IIOException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class OpeningBook {

    HashMap<Long, List<HashEntryOB>> OpeningBookHashMap;
    public OpeningBook() {
        String inputFilePath = "./src/ChessEngine/OpeningBook/OpeningBook.ser";
        try (FileInputStream fileIn = new FileInputStream(inputFilePath);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            OpeningBookHashMap = (HashMap<Long, List<HashEntryOB>>) objectIn.readObject();
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Opening Book konnte nicht geladen werden");
        }
    }

    /**
     * @param key zobrist Key des aktuellen Brettes
     * @return möglicher zug aus Opening-book in algebraic notation, null wenn keiner gefunden
     */
    public String getMoveFromOpeningBook(long key){
        List<HashEntryOB> hashedMoves = OpeningBookHashMap.get(key);
        if(hashedMoves == null){
            return null;
        }

//        Random rand = new Random();
//        return hashedMoves.get(rand.nextInt(hashedMoves.size())).move;

        String mostPlayedMove = null;
        int hightestCount = 0;
        for(HashEntryOB x :hashedMoves){
            if(x.counter > hightestCount){
                hightestCount = x.counter;
                mostPlayedMove = x.move;
            }
        }
        return mostPlayedMove;
    }
}

