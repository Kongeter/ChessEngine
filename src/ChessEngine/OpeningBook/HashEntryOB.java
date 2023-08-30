package ChessEngine.OpeningBook;

import java.io.Serializable;

public class HashEntryOB implements Serializable {
    String move;
    int counter = 1;
    HashEntryOB(String move) {
        this.move = move;
    }
    public void incrementCounter() {
        this.counter++;
    }
}
