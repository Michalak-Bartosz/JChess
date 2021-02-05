package com.chess.engine.board;

import com.chess.engine.board.Board.Builder;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

/**
 * Klasa abstrakcyjna zawierajaca opis ruchu oraz kombinacje mozliwych ruchow, ktore moze wykonac pionek
 */
public abstract class Move {

    protected final Board board;
    protected final int destinationCoordinate;
    protected final Piece movedPiece;
    protected final boolean isFirstMove;

    private Move(final Board board,
                 final Piece pieceMoved,
                 final int destinationCoordinate) {
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.movedPiece = pieceMoved;
        this.isFirstMove = pieceMoved.isFirstMove();
    }

    private Move(final Board board,
                 final int destinationCoordinate) {
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.movedPiece = null;
        this.isFirstMove = false;
    }

    /**
     * Metoda zwracajaca ogolny hashCode dla danego ruchu
     * @return zwraca obliczony hashCode
     */
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + this.destinationCoordinate;
        result = 31 * result + this.movedPiece.hashCode();
        result = 31 * result + this.movedPiece.getPiecePosition();
        result = result + (isFirstMove ? 1 : 0);
        return result;
    }

    /**
     * Metoda porownujaca czy podany ruch jest rowny ruchowi
     * @param other ruch, ktory jest sprawdzany
     * @return zwraca prawde gdy ruchy sa rowne lub falsz w przeciwnym przypadku
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Move)) {
            return false;
        }
        final Move otherMove = (Move) other;
        return getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
               getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
               getMovedPiece().equals(otherMove.getMovedPiece());
    }

    /**
     * Metoda zwracajaca plansze dla danego ruchu
     * @return zwraca plansze dla danego ruchu
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Metoda zwracajaca aktualne koordynaty danego ruchu
     * @return zwraca koordynaty ruchu
     */
    public int getCurrentCoordinate() {
        return this.movedPiece.getPiecePosition();
    }

    /**
     * Metoda zwracajaca koordynaty docelowe ruchu
     * @return zwraca koordynaty docelowe ruchu
     */
    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    /**
     * Metoda zwracajaca pionek ktory wykonuje dany ruch
     * @return zwraca pionek wykonujacy ruch
     */
    public Piece getMovedPiece() {
        return this.movedPiece;
    }

    /**
     * Metoda zwracajaca domysla wartosc dla ruchu
     * @return zwraca falsz
     */
    public boolean isAttack() {
        return false;
    }

    /**
     * Metoda zwracajaca domysla wartosc dla ruchu zamiany krola z wieza
     * @return zwraca falsz
     */
    public boolean isCastlingMove() {
        return false;
    }

    /**
     * Metoda zwracajaca domysly pionek dla ruchu ktory ma byc atakiem
     * @return zwraca null
     */
    public Piece getAttackedPiece() {
        return null;
    }

    /**
     * Metoda wykonywana podczas tworzenia nowej planszy, ktora ustawia ruchy pionkow na planszy
     * @return zwraca zbudowana plansze
     */
    public Board execute() {
        final Board.Builder builder = new Builder();
        this.board.currentPlayer().getActivePieces().stream().filter(piece -> !this.movedPiece.equals(piece)).forEach(builder::setPiece);
        this.board.currentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);
        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
        builder.setMoveTransition(this);
        return builder.build();
    }

    /**
     * Metoda wywolywana podczas cofania ruchu
     * @return zwraca poprzednia plansze
     */
    public Board undo() {
        final Board.Builder builder = new Builder();
        this.board.getAllPieces().stream().forEach(builder::setPiece);
        builder.setMoveMaker(this.board.currentPlayer().getAlliance());
        return builder.build();
    }

    /**
     * Metoda ktora zwraca koordynaty ruchu do wypisania
     * @return zwraca lancuch znakow reprezentujacy koordynaty wykonywanego ruchu
     */
    String disambiguationFile() {
        for(final Move move : this.board.currentPlayer().getLegalMoves()) {
            if(move.getDestinationCoordinate() == this.destinationCoordinate && !this.equals(move) &&
               this.movedPiece.getPieceType().equals(move.getMovedPiece().getPieceType())) {
                return BoardUtils.INSTANCE.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0, 1);
            }
        }
        return "";
    }

    /**
     * Enum przechowujace wszystkie mozliwe statusu ruchu: DONE, ILLEGAL_MOVE, LEAVES_PLAYER_IN_CHECK
     */
    public enum MoveStatus {

        DONE {
            @Override
            public boolean isDone() {
                return true;
            }
        },
        ILLEGAL_MOVE {
            @Override
            public boolean isDone() {
                return false;
            }
        },
        LEAVES_PLAYER_IN_CHECK {
            @Override
            public boolean isDone() {
                return false;
            }
        };

        public abstract boolean isDone();

    }

    /**
     * Klasa zawierajaca w sobie wszystkie niezbedne operacje, ktore nalezy wykonac, aby awansowac pionka po dosciu na koniec planszy
     */
    public static class PawnPromotion
            extends PawnMove {

        final Move decoratedMove;
        final Pawn promotedPawn;
        final Piece promotionPiece;

        public PawnPromotion(final Move decoratedMove,
                             final Piece promotionPiece) {
            super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
            this.promotionPiece = promotionPiece;
        }

        /**
         * Metoda ktora oblicza hashCode dla pionka, ktory ma awansowac
         * @return zwraca nowa wartosc hashCode dla awansujacego pionka
         */
        @Override
        public int hashCode() {
            return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        /**
         * Metoda ktora sprawdza czy pionek moze awansowac
         * @param other pionek, ktory jest sprawdzany
         * @return zwraca prawde, gdy pionek moze awansowac lub falsz w przeciwnym wypadku
         */
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnPromotion && (super.equals(other));
        }

        /**
         * Metoda wywolywana przy tworzeniu nowej planszy dla pionka, ktory ma awansowac
         * @return zwraca plansze z awansowanym pionkiem
         */
        @Override
        public Board execute() {
            final Board pawnMovedBoard = this.decoratedMove.execute();
            final Board.Builder builder = new Builder();
            pawnMovedBoard.currentPlayer().getActivePieces().stream().filter(piece -> !this.promotedPawn.equals(piece)).forEach(builder::setPiece);
            pawnMovedBoard.currentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);
            builder.setPiece(this.promotionPiece.movePiece(this));
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());
            builder.setMoveTransition(this);
            return builder.build();
        }

        /**
         * Metoda zwracajaca wartosc czy awansowany pionek atakuje
         * @return zwraca wartosc prawda jesli awansowny pionek atakuje lub falsz w przeciwnym przypadku
         */
        @Override
        public boolean isAttack() {
            return this.decoratedMove.isAttack();
        }

        /**
         * Metoda zwracajaca atakowany pionek
         * @return zwraca atakowany pionek
         */
        @Override
        public Piece getAttackedPiece() {
            return this.decoratedMove.getAttackedPiece();
        }

        /**
         * Metoda zwracajaca odpowiedni lancuch znakow przy awansie pionka
         * @return zwraca komunikat awansu pionka
         */
        @Override
        public String toString() {
            return BoardUtils.INSTANCE.getPositionAtCoordinate(this.movedPiece.getPiecePosition()) + "-" +
                   BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate) + "=" + this.promotionPiece.getPieceType();
        }

    }

    /**
     * Klasa opisujaca zwyczajny ruch pionka
     */
    public static class MajorMove
            extends Move {

        public MajorMove(final Board board,
                         final Piece pieceMoved,
                         final int destinationCoordinate) {
            super(board, pieceMoved, destinationCoordinate);
        }

        /**
         * Metoda sprawdzajaca czy ruch jest ruchem zwyczajnym
         * @param other ruch, ktory jest sprawdzany
         * @return zwraca prawde, gdy ruch jest zwyczajny lub falsz w przeciwnym wypadku
         */
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof MajorMove && super.equals(other);
        }

        /**
         * Metoda zwracajaca odpowiedni lancuch znakow dla zwyczajnego ruchu
         * @return zwraca komunikat zwyczajnego ruchu
         */
        @Override
        public String toString() {
            return movedPiece.getPieceType().toString() + disambiguationFile() +
                   BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    /**
     * Klasa opisujaca atakujacy ruch pionka
     */
    public static class MajorAttackMove
            extends AttackMove {

        public MajorAttackMove(final Board board,
                               final Piece pieceMoved,
                               final int destinationCoordinate,
                               final Piece pieceAttacked) {
            super(board, pieceMoved, destinationCoordinate, pieceAttacked);
        }

        /**
         * Metoda sprawdzajaca czy ruch jest ruchem atakujacym
         * @param other ruch, ktory jest sprawdzany
         * @return zwraca prawde, gdy ruch jest atakiem lub falsz w przeciwnym wypadku
         */
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof MajorAttackMove && super.equals(other);

        }

        /**
         * Metoda zwracajaca odpowiedni lancuch znakow dla atakujacego ruchu
         * @return zwraca komunikat atakujacego ruchu
         */
        @Override
        public String toString() {
            return movedPiece.getPieceType() + disambiguationFile() + "x" +
                   BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    /**
     * Klasa opisujaca ruch pionka
     */
    public static class PawnMove
            extends Move {

        public PawnMove(final Board board,
                        final Piece pieceMoved,
                        final int destinationCoordinate) {
            super(board, pieceMoved, destinationCoordinate);
        }

        /**
         * Metoda sprawdzajaca czy wykonywany ruch jest ruchem pionka
         * @param other ruch, ktory jest sprawdzany
         * @return zwraca prawde, gdy wykonywany ruch jest ruchem pionka lub falsz w przeciwnym wypadku
         */
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnMove && super.equals(other);
        }

        /**
         * Metoda zwracajaca odpowiedni lancuch znakow dla ruchu pionka
         * @return zwraca komunikat ruchu pionka
         */
        @Override
        public String toString() {
            return BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    /**
     * Klasa opisujaca atakujacy ruch pionka
     */
    public static class PawnAttackMove
            extends AttackMove {

        public PawnAttackMove(final Board board,
                              final Piece pieceMoved,
                              final int destinationCoordinate,
                              final Piece pieceAttacked) {
            super(board, pieceMoved, destinationCoordinate, pieceAttacked);
        }

        /**
         * Metoda sprawdzajaca czy wykonywany ruch jest atakujacym ruchem pionka
         * @param other ruch, ktory jest sprawdzany
         * @return zwraca prawde, gdy wykonywany ruch jest atakujacym ruchem pionka lub falsz w przeciwnym wypadku
         */
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        /**
         * Metoda zwracajaca odpowiedni lancuch znakow dla atakujacego ruchu pionka
         * @return zwraca odpowiedni komunikat atakujacego ruchu pionka
         */
        @Override
        public String toString() {
            return BoardUtils.INSTANCE.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0, 1) + "x" +
                   BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    /**
     * Klasa opisujaca mechanike EnPassant w szachach dla pionka
     */
    public static class PawnEnPassantAttack extends PawnAttackMove {

        public PawnEnPassantAttack(final Board board,
                                   final Piece pieceMoved,
                                   final int destinationCoordinate,
                                   final Piece pieceAttacked) {
            super(board, pieceMoved, destinationCoordinate, pieceAttacked);
        }

        /**
         * Metoda sprawdzajca czy wykonywany ruch jest ruchem EnPassant
         * @param other ruch, ktory jest sprawdzany
         * @return zwraca prawde, gdy wykonywany ruch jest ruchem EnPassant lub falsz w przeciwnym przypadku
         */
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnEnPassantAttack && super.equals(other);
        }

        /**
         * Metoda, ktora tworzy nowa plansze dla wykonywujacego sie ruchu EnPassant
         * @return zwraca plansze dla ruchu EnPassant
         */
        @Override
        public Board execute() {
            final Board.Builder builder = new Builder();
            this.board.currentPlayer().getActivePieces().stream().filter(piece -> !this.movedPiece.equals(piece)).forEach(builder::setPiece);
            this.board.currentPlayer().getOpponent().getActivePieces().stream().filter(piece -> !piece.equals(this.getAttackedPiece())).forEach(builder::setPiece);
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            builder.setMoveTransition(this);
            return builder.build();
        }

        /**
         * Metoda cofajaca ruch EnPassant
         * @return zwraca plansze, ktora byla przed wykonaniem ruchu EnPassant
         */
        @Override
        public Board undo() {
            final Board.Builder builder = new Builder();
            this.board.getAllPieces().stream().forEach(builder::setPiece);
            builder.setEnPassantPawn((Pawn)this.getAttackedPiece());
            builder.setMoveMaker(this.board.currentPlayer().getAlliance());
            return builder.build();
        }

    }

    /**
     * Klasa opisujaca pierwszy ruch pionkiem
     */
    public static class PawnJump
            extends Move {

        public PawnJump(final Board board,
                        final Pawn pieceMoved,
                        final int destinationCoordinate) {
            super(board, pieceMoved, destinationCoordinate);
        }

        /**
         * Metoda sprawdzajca czy wykonywany ruch jest pierwszym ruchem pionka
         * @param other ruch, ktory jest sprawdzany
         * @return zwraca prawde, gdy wykonywany ruch jest pierwszym ruchem pionka lub falsz w przeciwnym przypadku
         */
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnJump && super.equals(other);
        }

        /**
         * Metoda, ktora tworzy nowa plansze dla wykonywujacego sie pierwszego ruchu pionka
         * @return zwraca plansze dla pierwszego ruchu pionka
         */
        @Override
        public Board execute() {
            final Board.Builder builder = new Builder();
            this.board.currentPlayer().getActivePieces().stream().filter(piece -> !this.movedPiece.equals(piece)).forEach(builder::setPiece);
            this.board.currentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);
            final Pawn movedPawn = (Pawn)this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            builder.setMoveTransition(this);
            return builder.build();
        }

        /**
         * Metoda zwracajaca odpowiedni lancuch znakow dla pierwszego ruchu pionka
         * @return zwraca odpowiedni komunikat pierwszego ruchu pionka
         */
        @Override
        public String toString() {
            return BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    /**
     * Klasa opisujaca ruch zamiany stronami krola z wieza
     */
    static abstract class CastleMove extends Move {

        final Rook castleRook;
        final int castleRookStart;
        final int castleRookDestination;

        CastleMove(final Board board,
                   final Piece pieceMoved,
                   final int destinationCoordinate,
                   final Rook castleRook,
                   final int castleRookStart,
                   final int castleRookDestination) {
            super(board, pieceMoved, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        /**
         * Metoda zwracajaca wieze ktora bierze udzial w zamianie z krolem
         * @return
         */
        Rook getCastleRook() {
            return this.castleRook;
        }

        /**
         * Metoda zwracajaca prawde dla ruchu zamiany krola z wieza
         * @return zwraca prawde dla tego ruchu zamiany krola z wieza
         */
        @Override
        public boolean isCastlingMove() {
            return true;
        }

        /**
         * Metoda tworzaca plansze dla wykonujacego sie ruchu zamiany krola z wieza
         * @return zwraca plansze utworzona dla ruchu zamiany krola z wieza
         */
        @Override
        public Board execute() {
            final Board.Builder builder = new Builder();
            for (final Piece piece : this.board.getAllPieces()) {
                if (!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRook.getPieceAllegiance(), this.castleRookDestination, false));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            builder.setMoveTransition(this);
            return builder.build();
        }

        /**
         * Metoda zwracajaca odpowiedni hashCode dla ruchu zamiany krola z wieza
         * @return zwraca obliczony hashCode dla ruchu zamiany krola z wieza
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination;
            return result;
        }

        /**
         * Metoda sprawdzajaca, czy wykonywany ruch jest ruchem zamiany krola z wieza
         * @param other ruch, ktory jest sprawdzany
         * @return zwraca prawde, gdy wykonywany ruch jest ruchem zamiany krola z wieza lub falsz w przeciwnym wypadku
         */
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CastleMove)) {
                return false;
            }
            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }

    }

    /**
     * Klasa opisujaca zamiane stronami krola z wieza po stronie krola
     */
    public static class KingSideCastleMove extends CastleMove {

        public KingSideCastleMove(final Board board,
                                  final Piece pieceMoved,
                                  final int destinationCoordinate,
                                  final Rook castleRook,
                                  final int castleRookStart,
                                  final int castleRookDestination) {
            super(board, pieceMoved, destinationCoordinate, castleRook, castleRookStart,
                    castleRookDestination);
        }

        /**
         * Metoda sprawdzajaca, czy wykonywany ruch jest ruchem zamiany krola z wieza po stronie krola
         * @param other ruch, ktory jest sprawdzany
         * @return zwraca prawde, gdy wykonywany ruch jest ruchem zamiany krola z wieza po stronie krola lub falsz w przeciwnym wypadku
         */
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof KingSideCastleMove)) {
                return false;
            }
            final KingSideCastleMove otherKingSideCastleMove = (KingSideCastleMove) other;
            return super.equals(otherKingSideCastleMove) && this.castleRook.equals(otherKingSideCastleMove.getCastleRook());
        }

        /**
         * Metoda zwracajaca odpowiedni lancuch znakow dla ruchu zamiany krola z wieza po stronie krola
         * @return zwraca odpowiedni komunikat dla zamiany krola z wieza po stronie krola
         */
        @Override
        public String toString() {
            return "O-O";
        }

    }

    /**
     * Klasa opisujaca zamiane stronami krola z wieza po stronie krolowej
     */
    public static class QueenSideCastleMove extends CastleMove {

        public QueenSideCastleMove(final Board board,
                                   final Piece pieceMoved,
                                   final int destinationCoordinate,
                                   final Rook castleRook,
                                   final int castleRookStart,
                                   final int rookCastleDestination) {
            super(board, pieceMoved, destinationCoordinate, castleRook, castleRookStart,
                    rookCastleDestination);
        }

        /**
         * Metoda sprawdzajaca, czy wykonywany ruch jest ruchem zamiany krola z wieza po stronie krolowej
         * @param other ruch, ktory jest sprawdzany
         * @return zwraca prawde, gdy wykonywany ruch jest ruchem zamiany krola z wieza po stronie krolowej lub falsz w przeciwnym wypadku
         */
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof QueenSideCastleMove)) {
                return false;
            }
            final QueenSideCastleMove otherQueenSideCastleMove = (QueenSideCastleMove) other;
            return super.equals(otherQueenSideCastleMove) && this.castleRook.equals(otherQueenSideCastleMove.getCastleRook());
        }

        /**
         * Metoda zwracajaca odpowiedni lancuch znakow dla ruchu zamiany krola z wieza po stronie krolowej
         * @return zwraca odpowiedni komunikat dla zamiany krola z wieza po stronie krolowej
         */
        @Override
        public String toString() {
            return "O-O-O";
        }

    }

    /**
     * Klasa opisujaca atakujacy ruch pionkow
     */
    static abstract class AttackMove extends Move {

        private final Piece attackedPiece;

        AttackMove(final Board board,
                   final Piece pieceMoved,
                   final int destinationCoordinate,
                   final Piece pieceAttacked) {
            super(board, pieceMoved, destinationCoordinate);
            this.attackedPiece = pieceAttacked;
        }

        /**
         * Metoda zwracajaca odpowiedni hashCode dla atakujacego ruchu pionka
         * @return zwraca odpowiedni hashCode dla atakujacego ruchu pionka
         */
        @Override
        public int hashCode() {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        /**
         * Metoda sprawdzajaca, czy wykonywany ruch jest atakujacym ruchem
         * @param other ruch, ktory jest sprawdzany
         * @return zwraca prawde, gdy wykonywany ruch jest atakujacym ruchem lub falsz w przeciwnym wypadku
         */
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AttackMove)) {
                return false;
            }
            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        /**
         * Metoda zwracajaca pionek, ktory jest atakowany
         * @return zwraca pionek, ktory jest atakowany
         */
        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }

        /**
         * Metoda zwracajaca prawde dla wszystkich ruchow ktore sa atakujacym ruchem
         * @return zwraca prawde w przypadku ruchu atakujacego
         */
        @Override
        public boolean isAttack() {
            return true;
        }

    }

    /**
     * Klasa opisujaca ruch "null" - niewystepujacego
     */
    private static class NullMove extends Move {

        private NullMove() {
            super(null, -1);
        }

        /**
         * Metoda ktora zwraca aktualne koordynaty pionka w przypadku ruchu null
         * @return zwraca -1 jako aktualne koordynaty pionka
         */
        @Override
        public int getCurrentCoordinate() {
            return -1;
        }

        /**
         * Metoda ktora zwraca docelowe koordynaty pionka w przypadku ruchu null
         * @return zwraca -1 jako docelowe koordynaty pionka
         */
        @Override
        public int getDestinationCoordinate() {
            return -1;
        }

        /**
         * Metoda ktora wyrzuca komunikat o ruchu null
         * @return zwraca komunikat o ruchu null
         */
        @Override
        public Board execute() {
            throw new RuntimeException("cannot execute null move!");
        }

        /**
         * Metoda zwracajaca odpowiedni lancuch znakow dla ruchu null
         * @return zwraca odpowiedni komunikat dla ruchu null
         */
        @Override
        public String toString() {
            return "Null Move";
        }
    }

    /**
     * Klasa opisujaca aktualnie wykonywany ruch
     */
    public static class MoveFactory {

        private static final Move NULL_MOVE = new NullMove();

        private MoveFactory() {
            throw new RuntimeException("Not instantiatable!");
        }

        /**
         * Metoda zwracajaca ruch null
         * @return zwraca instancje ruchu null
         */
        public static Move getNullMove() {
            return NULL_MOVE;
        }

        /**
         * Metoda tworzaca nowy ruch
         * @param board plansza, na ktorej wykonywany jest ruch
         * @param currentCoordinate aktualne koordynacje pionka, ktory wykonuje ruch
         * @param destinationCoordinate docelowe koordynacje pionka, ktory wykonuje ruch
         * @return zwraca instancje klasy Move jako wykonany ruch lub ruch null dla niemozliwego ruchu
         */
        public static Move createMove(final Board board,
                                      final int currentCoordinate,
                                      final int destinationCoordinate) {
            for (final Move move : board.getAllLegalMoves()) {
                if (move.getCurrentCoordinate() == currentCoordinate &&
                    move.getDestinationCoordinate() == destinationCoordinate) {
                    return move;
                }
            }
            return getNullMove();
        }
    }
}