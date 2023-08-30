package ChessEngine;

public class ZobristHashEntry {
    public int age;
    public int depth;
    public int value;
    public long zobristHash;
    ZobristHashEntry(int age, int value, int depth, long zobristHash) {
        this.age = age;
        this.value = value;
        this.depth = depth;
        this.zobristHash = zobristHash;
    }
}
