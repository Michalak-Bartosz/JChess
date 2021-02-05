package com.chess.engine.board;

import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import java.util.*;

/**
 * Enum przechowujace niezbedne mechaniki wykorzystywane przy analizowaniu planszy
 */
public enum  BoardUtils {

    INSTANCE;

    public final List<Boolean> FIRST_COLUMN = initColumn(0);
    public final List<Boolean> SECOND_COLUMN = initColumn(1);
    public final List<Boolean> SEVENTH_COLUMN = initColumn(6);
    public final List<Boolean> EIGHTH_COLUMN = initColumn(7);
    public final List<Boolean> FIRST_ROW = initRow(0);
    public final List<Boolean> SECOND_ROW = initRow(8);
    public final List<Boolean> THIRD_ROW = initRow(16);
    public final List<Boolean> FOURTH_ROW = initRow(24);
    public final List<Boolean> FIFTH_ROW = initRow(32);
    public final List<Boolean> SIXTH_ROW = initRow(40);
    public final List<Boolean> SEVENTH_ROW = initRow(48);
    public final List<Boolean> EIGHTH_ROW = initRow(56);
    public final List<String> ALGEBRAIC_NOTATION = initializeAlgebraicNotation();
    public static final int START_TILE_INDEX = 0;
    public static final int NUM_TILES_PER_ROW = 8;
    public static final int NUM_TILES = 64;

    private static List<Boolean> initColumn(int columnNumber) {
        final Boolean[] column = new Boolean[NUM_TILES];
        for(int i = 0; i < column.length; i++) {
            column[i] = false;
        }
        do {
            column[columnNumber] = true;
            columnNumber += NUM_TILES_PER_ROW;
        } while(columnNumber < NUM_TILES);
        return Collections.unmodifiableList(Arrays.asList((column)));
    }

    private static List<Boolean> initRow(int rowNumber) {
        final Boolean[] row = new Boolean[NUM_TILES];
        for(int i = 0; i < row.length; i++) {
            row[i] = false;
        }
        do {
            row[rowNumber] = true;
            rowNumber++;
        } while(rowNumber % NUM_TILES_PER_ROW != 0);
        return Collections.unmodifiableList(Arrays.asList(row));
    }

    private Map<String, Integer> initializePositionToCoordinateMap() {
        final Map<String, Integer> positionToCoordinate = new HashMap<>();
        for (int i = START_TILE_INDEX; i < NUM_TILES; i++) {
            positionToCoordinate.put(ALGEBRAIC_NOTATION.get(i), i);
        }
        return Collections.unmodifiableMap(positionToCoordinate);
    }

    private static List<String> initializeAlgebraicNotation() {
        return Collections.unmodifiableList(Arrays.asList(
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"));
    }

    /**
     * Metoda ktora okresla czy podane koordynacje mieszcza sie w rozmiarze planszy
     * @param coordinate podane koordynacje do sprawdzenia
     * @return zwraca prawde gdy koordynacje mieszcza sie na planszy lub falsz w przeciwnym przypadku
     */
    public static boolean isValidTileCoordinate(final int coordinate) {
        return coordinate >= START_TILE_INDEX && coordinate < NUM_TILES;
    }

    /**
     * Metoda zwracajac odpowiedni lancuch znakow dla odpowiedniego pola
     * @param coordinate pole dla ktorego jest okreslany lancuch znakow
     * @return zwraca odpowiedni komunikat o polu
     */
    public String getPositionAtCoordinate(final int coordinate) {
        return ALGEBRAIC_NOTATION.get(coordinate);
    }

    /**
     * Metoda spradzajaca czy pionki nie blokuja krola przy probie zamiany miejscami krola z wieza
     * @param board aktualna plansza, ktora jest sprawdzana
     * @param king aktualny krol na sprawdzanej planszy
     * @param frontTile pole, na ktorym stoi krol gracza
     * @return zwraca prawde w przypadku blokowaniu krola przez pionka lub falsz gdy krol nie jest blokowany
     */
    public static boolean isKingPawnTrap(final Board board,
                                         final King king,
                                         final int frontTile) {
        final Piece piece = board.getPiece(frontTile);
        return piece != null &&
               piece.getPieceType().isPawn() &&
               piece.getPieceAllegiance() != king.getPieceAllegiance();
    }
}
