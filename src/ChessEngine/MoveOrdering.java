package ChessEngine;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;



public class MoveOrdering {

    /* Sortiert die Moves einer Liste, nach ihren in früherer iteration berechneten Values.
    * Moves mit hohem Value bei Suchtiefe x sind öfter auch besser bei Suchtiefe >x und werden somit früher betrachte,
    * somit können mehr Branches früh pruned werden.
    * Speichert bereits berechnete Values in memoizedValues um möglichst selten ineffiziente HashTable.getValueFromBoardAndMove Funktion aufzurufen.
    * */
    static void orderWithHash(List<Move> moves, ZobristHashMap hash, Board board, boolean whiteHasMove) {

        HashMap<Move, Integer> memoizedValues = new HashMap<>();

        Comparator<Move> moveComparator = (move1, move2) -> {
            int valueMove1 = memoizedValues.computeIfAbsent(move1, move -> ZobristHashMap.getValueFromBoardAndMove(move, hash, board, whiteHasMove));

            int valueMove2 = memoizedValues.computeIfAbsent(move2, move -> ZobristHashMap.getValueFromBoardAndMove(move, hash, board, whiteHasMove));

            return (!whiteHasMove) ? Integer.compare(valueMove1, valueMove2) : Integer.compare(valueMove2, valueMove1);
        };

        moves.sort(moveComparator);
    }




}