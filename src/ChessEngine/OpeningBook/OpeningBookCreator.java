package ChessEngine.OpeningBook;

import ChessEngine.Board;
import ChessEngine.ZobristHashMap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class OpeningBookCreator {

    /**
     * Erstellt neue Textdateien aus PGN files welche pro Zeile ein Spiel in Algebraischer schreibweise enthält, entfernt metadaten etc.
     * wird in pureGameNotation abgespeichert
     * @param filepath path zu ordner wo sich pgn Dateien befinden
     */
    public void pgnToPureAlgebraic(String filepath) throws IOException {
        List<String> filenames = Stream.of(new File(filepath).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .toList();
        for(String filename : filenames) {
            System.out.println(filename);
            String content = Files.readString(Path.of(filepath + filename));
            content = content.replaceAll("\\[.*?\\]", ""); //Metadaten Löschen
            content = content.replaceAll("\\{.*?\\}", ""); //Kommentare Löschen

            content = content.replaceAll("(1/2-1/2)|(1-0)|(0-1)", "gameEnd"); //Spielenden Löschen und markieren
            content = content.replaceAll("\r\n", " "); //Alle Zeilenumbrüche löschen
            content = content.replaceAll("gameEnd", "\r\n"); //Neue Zeilenumbrüche bei Spielenden

            content = content.replaceAll("( +)", " "); //Mehrfach Leerzeichen Löschen


            try (PrintWriter out = new PrintWriter("./src/ChessEngine/OpeningBook/pureGameNotation/" + filename)) {
                out.println(content);
            }
        }
    }

    /**
     * Erstellt Opening-book (Datei: OpeningBook.ser) aus einer liste von pureGameNotationFiles.
     * @param filepath path zu Ordner mit pureGameNotation Files von Spielen
     * @param depth max opening depth in half-moves
     */
    public void createOpeningBook(String filepath, int depth) throws IOException {
        List<String> filenames = Stream.of(new File(filepath).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .toList();

        Map<Long, List<HashEntryOB>> data = new HashMap<Long, List<HashEntryOB>>();

        for (String filename : filenames) {
            String content = Files.readString(Path.of(filepath+filename));
            String[] games = content.split("\r\n");

            System.out.println("\r\n"+filename);
            for (int gameIndex = 0; gameIndex < games.length - 1; gameIndex++) {
                String game = games[gameIndex];
                System.out.print(" " + gameIndex);
                try {
                    openingBookAddSingleGame(game, depth, data);
                } catch (Exception e) {
                    System.out.print(":konnte nicht gelesen werden ");
                }

            }
        }

        saveData(data);
    }

    private void openingBookAddSingleGame(String game, int depth, Map<Long, List<HashEntryOB>> data) {
        ZobristHashMap hashMap = new ZobristHashMap();
        Board board = new Board();
        String[] moveArray = game.split("(\\d\\.)|( )"); //splittet pgn bei "[Nummer]."/"[Leerzeichen]", also jeder Halbzug einzeln
        moveArray = Arrays.stream(moveArray).filter(str -> !str.isEmpty()).toArray(String[]::new);//leere züge löschen
        for (int moveIndex = 0; moveIndex < moveArray.length && moveIndex < depth; moveIndex++) {
            boolean whiteHasMove = (moveIndex % 2) == 0;
            String move = moveArray[moveIndex];
            addMoveToData(hashMap.getDataFromBoard(board, whiteHasMove).currentKey, move, data);
            board.makeMovePgn(move, whiteHasMove);
        }
    }

    private void addMoveToData(long key, String move, Map<Long, List<HashEntryOB>> data) {
        List<HashEntryOB> OldEntryList = data.computeIfAbsent(key, k -> new LinkedList<>());

        for (HashEntryOB oldEntrySingleMove : OldEntryList) {
            if (Objects.equals(oldEntrySingleMove.move, move)) {
                oldEntrySingleMove.incrementCounter();
                return;
            }
        }
        OldEntryList.add(new HashEntryOB(move));
    }

    private void saveData(Map<Long, List<HashEntryOB>> data) throws IOException{
        /*HashMap<String, HashEntryOB> data = new HashMap<>();

        data.put("Eins", new HashEntryOB("t"));
        data.put("Zwei", new HashEntryOB("s"));
        //data.put("Drei", new HashEntryOB[]{new HashEntryOB("tr")});*/


        String outputFilePath = "./src/ChessEngine/OpeningBook/OpeningBook.ser";
        try (FileOutputStream fileOut = new FileOutputStream(outputFilePath);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(data);
            System.out.println("HashMap in Datei gespeichert.");
        }



    }



}
