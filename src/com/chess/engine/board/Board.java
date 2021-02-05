package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Klasa opisujaca plansze gry oraz posiadajaca builder planszy
 */
public final class Board {

    private final Int2ObjectMap<Piece> boardConfig;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassantPawn;

    private static final Board STANDARD_BOARD = createStandardBoardImpl();

    private Board(final Builder builder) {
        this.boardConfig = Int2ObjectMaps.unmodifiable(builder.boardConfig);
        this.whitePieces = calculateActivePieces(builder, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(builder, Alliance.BLACK);
        this.enPassantPawn = builder.enPassantPawn;
        final Collection<Move> whiteStandardMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardMoves = calculateLegalMoves(this.blackPieces);
        this.whitePlayer = new WhitePlayer(this, whiteStandardMoves, blackStandardMoves);
        this.blackPlayer = new BlackPlayer(this, whiteStandardMoves, blackStandardMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayerByAlliance(this.whitePlayer, this.blackPlayer);
    }

    /**
     * Metoda zwracajaca czarne pionki
     * @return zwraca liste czarnych pionkow
     */
    public Collection<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    /**
     * Metoda zwracajaca biale pionki
     * @return zwraca liste bialych pionkow
     */
    public Collection<Piece> getWhitePieces() {
        return this.whitePieces;
    }

    /**
     * Metoda zwracajaca wszystkie pionki
     * @return zwraca liste wszystkich pionkow
     */
    public Collection<Piece> getAllPieces() {
        return Stream.concat(this.whitePieces.stream(),
                             this.blackPieces.stream()).collect(Collectors.toList());
    }

    /**
     * Metoda zwracajaca wszystkie mozliwe ruchy
     * @return zwraca liste wszystkich mozliwych ruchow
     */
    public Collection<Move> getAllLegalMoves() {
        return Stream.concat(this.whitePlayer.getLegalMoves().stream(),
                             this.blackPlayer.getLegalMoves().stream()).collect(Collectors.toList());
    }

    /**
     * Metoda zwracajaca instancje bialego gracza
     * @return zwraca instancje bialego gracza
     */
    public WhitePlayer whitePlayer() {
        return this.whitePlayer;
    }

    /**
     * Metoda zwracajaca instancje czarnego gracza
     * @return zwraca instancje czarnego gracza
     */
    public BlackPlayer blackPlayer() {
        return this.blackPlayer;
    }

    /**
     * Metoda zwracajaca instancje aktualnie wykonywujacego ruch gracza
     * @return zwraca instancje aktualnego gracza
     */
    public Player currentPlayer() {
        return this.currentPlayer;
    }

    /**
     * Metoda zwracajaca pionek znajdujacy sie na podanym polu
     * @param coordinate pole, ktore jest sprawdzane
     * @return zwraca pionek znajdujacy sie na podanym polu
     */
    public Piece getPiece(final int coordinate) {
        return this.boardConfig.get(coordinate);
    }

    /**
     * Metoda zwracajaca pionek ktory ma mozliwosc ruchu EnPassant
     * @return zwraca pionek z mozliwoscia ruchu EnPassant
     */
    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    /**
     * Metoda tworzaca standardowa plansze - poczatkowa
     * @return zwraca instancje standardowej planszy
     */
    public static Board createStandardBoard() {
        return STANDARD_BOARD;
    }

    /**
     * Metoda tworzaca poczatkowa plansze gry
     * @return zwraca instancje poczatkowej planszy gry
     */
    private static Board createStandardBoardImpl() {
        final Builder builder = new Builder();

        builder.setPiece(new Rook(Alliance.BLACK, 0));
        builder.setPiece(new Knight(Alliance.BLACK, 1));
        builder.setPiece(new Bishop(Alliance.BLACK, 2));
        builder.setPiece(new Queen(Alliance.BLACK, 3));
        builder.setPiece(new King(Alliance.BLACK, 4, true, true));
        builder.setPiece(new Bishop(Alliance.BLACK, 5));
        builder.setPiece(new Knight(Alliance.BLACK, 6));
        builder.setPiece(new Rook(Alliance.BLACK, 7));
        builder.setPiece(new Pawn(Alliance.BLACK, 8));
        builder.setPiece(new Pawn(Alliance.BLACK, 9));
        builder.setPiece(new Pawn(Alliance.BLACK, 10));
        builder.setPiece(new Pawn(Alliance.BLACK, 11));
        builder.setPiece(new Pawn(Alliance.BLACK, 12));
        builder.setPiece(new Pawn(Alliance.BLACK, 13));
        builder.setPiece(new Pawn(Alliance.BLACK, 14));
        builder.setPiece(new Pawn(Alliance.BLACK, 15));

        builder.setPiece(new Pawn(Alliance.WHITE, 48));
        builder.setPiece(new Pawn(Alliance.WHITE, 49));
        builder.setPiece(new Pawn(Alliance.WHITE, 50));
        builder.setPiece(new Pawn(Alliance.WHITE, 51));
        builder.setPiece(new Pawn(Alliance.WHITE, 52));
        builder.setPiece(new Pawn(Alliance.WHITE, 53));
        builder.setPiece(new Pawn(Alliance.WHITE, 54));
        builder.setPiece(new Pawn(Alliance.WHITE, 55));
        builder.setPiece(new Rook(Alliance.WHITE, 56));
        builder.setPiece(new Knight(Alliance.WHITE, 57));
        builder.setPiece(new Bishop(Alliance.WHITE, 58));
        builder.setPiece(new Queen(Alliance.WHITE, 59));
        builder.setPiece(new King(Alliance.WHITE, 60, true, true));
        builder.setPiece(new Bishop(Alliance.WHITE, 61));
        builder.setPiece(new Knight(Alliance.WHITE, 62));
        builder.setPiece(new Rook(Alliance.WHITE, 63));

        builder.setMoveMaker(Alliance.WHITE);

        return builder.build();
    }

    private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {
        return pieces.stream().flatMap(piece -> piece.calculateLegalMoves(this).stream())
                      .collect(Collectors.toList());
    }

    private static Collection<Piece> calculateActivePieces(final Builder builder,
                                                           final Alliance alliance) {
        return builder.boardConfig.values().stream()
               .filter(piece -> piece.getPieceAllegiance() == alliance)
               .collect(Collectors.toList());
    }

    /**
     * Klasa buildera planszy, ktora jest wykorzystywana przy tworzeniu planszy
     */
    public static class Builder {
        Int2ObjectMap<Piece> boardConfig;
        Alliance nextMoveMaker;
        Pawn enPassantPawn;
        Move transitionMove;

        /**
         * Metoda, ktora w boardConfig zapisuje mape w postaci hashCode
         */
        public Builder() {
            this.boardConfig = new Int2ObjectOpenHashMap<>(32, 1.0f);
        }

        /**
         * Metoda ustawiajaca pionek na pozycji
         * @param piece pionek, ktory nalezy ustawic
         * @return zwraca instancje buildera z ustawionym pionkiem
         */
        public Builder setPiece(final Piece piece) {
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        /**
         * Metoda ustawiajaca odpowiedni kolor gracza, ktory bedzie ruszal sie jako nastepny
         * @param nextMoveMaker kolor ruszajacego sie gracza
         * @return zwraca instancje buildera z ustawionym kolorem ruszajacego sie gracza
         */
        public Builder setMoveMaker(final Alliance nextMoveMaker) {
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        /**
         * Metoda ustawiajaca ruch EnPassant na planszy
         * @param enPassantPawn pionek wykonujacy ruch EnPassant
         * @return zwraca instancje buildera z ustawionym ruchem EnPassant
         */
        public Builder setEnPassantPawn(final Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
            return this;
        }

        /**
         * Metoda ustawiajaca ruchy ktore maja byc wykonane na planszy
         * @param transitionMove ruch, ktory ma byc wykonany
         * @return zwraca instancje buildera z ustawionym ruchem, ktory ma byc wykonany
         */
        public Builder setMoveTransition(final Move transitionMove) {
            this.transitionMove = transitionMove;
            return this;
        }

        /**
         * Metoda tworzaca nowa instancje buildera
         * @return zwraca stworzona instancje buildera
         */
        public Board build() {
            return new Board(this);
        }

    }

}
