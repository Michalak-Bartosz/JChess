package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MoveStatus;
import com.chess.engine.board.MoveTransition;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;

/**
 * Klasa abstrakcyjna po ktorej dziedzicza klasy BlackPlayer oraz WhitePlayer
 * @see BlackPlayer
 * @see WhitePlayer
 */
public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    protected final boolean isInCheck;

    Player(final Board board,
           final Collection<Move> playerLegals,
           final Collection<Move> opponentLegals) {
        this.board = board;
        this.playerKing = establishKing();
        this.isInCheck = !calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentLegals).isEmpty();
        playerLegals.addAll(calculateKingCastles(playerLegals, opponentLegals));
        this.legalMoves = Collections.unmodifiableCollection(playerLegals);
    }

    /**
     * Metoda zwracajaca wartosc prawda lub falsz dla mozliwosci zbicia krola (Szach)
     * @return aktualna wartosc parametru isInCheck
     */
    public boolean isInCheck() {
        return this.isInCheck;
    }

    /**
     * Metoda zwracajaca wartosc prawda lub falsz dla mozliwosci zbicia krola oraz gdy nie ma ucieczki (Szach-Mat)
     * @return aktualna wartosc parametru isInCheck oraz czy sa ruchy ucieczki krola
     */
    public boolean isInCheckMate() {
       return this.isInCheck && !hasEscapeMoves();
    }

    /**
     * Metoda zwracajaca wartosc prawda lub falsz gdy krol nie jest zbijany, ale nie ma rowniez ucieczki i jest ostatnim pionkiem
     * @return aktualna wartosc parametru isInCheck oraz czy sa ruchy ucieczki krola
     */
    public boolean isInStaleMate() {
        return !this.isInCheck && !hasEscapeMoves();
    }

    /**
     * Metoda zwracajaca wartosc prawda lub falsz gdy mozliwa jest zamiana krola z wieza
     * @return zwraca wartosc prawda lub falsz dla mozliwosci zmiany krola z wieza
     */
    public boolean isCastled() {
        return this.playerKing.isCastled();
    }

    /**
     * Metoda zwracajaca wartosc prawda lub falsz gdy mozliwa jest zamiana krola z wieza po stronie krola
     * @return zwraca wartosc prawda lub falsz dla mozliwosci zmiany krola z wieza
     */
    public boolean isKingSideCastleCapable() {
        return this.playerKing.isKingSideCastleCapable();
    }

    /**
     * Metoda zwracajaca wartosc prawda lub falsz gdy mozliwa jest zamiana krola z wieza po stronie krolowej
     * @return zwraca wartosc prawda lub falsz dla mozliwosci zmiany krola z wieza
     */
    public boolean isQueenSideCastleCapable() {
        return this.playerKing.isQueenSideCastleCapable();
    }

    /**
     * Metoda ustalajaca ktory pionek jest krolem dla gracza
     * @return zwraca pionek ktory jest krolem
     */
    private King establishKing() {
        return (King) getActivePieces().stream()
                                       .filter(piece -> piece.getPieceType().isKing())
                                       .findAny()
                                       .orElseThrow(RuntimeException::new);
    }

    /**
     * Metoda sprawdzajaca czy sa dostepne ruchy ucieczki dla krola
     * @return zwraca liste dostepnych ruchow ucieczki dla krola
     */
    private boolean hasEscapeMoves() {
        return this.legalMoves.stream()
                              .anyMatch(move -> makeMove(move)
                              .getMoveStatus().isDone());
    }

    /**
     * Metoda zwracajaca liste dostepnych ruchow
     * @return zwraca liste dostepnych ruchow
     */
    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }

    /**
     * Metoda ustalajaca ktore ruchy z mozliwych sa atakujace
     * @param tile aktualne pole, na ktorym znajduje sie pionek
     * @param moves lista mozliwych ruchow dla pionka
     * @return zwraca liste ruchow atakujacych inne pionki
     */
    static Collection<Move> calculateAttacksOnTile(final int tile,
                                                   final Collection<Move> moves) {
        return moves.stream()
                    .filter(move -> move.getDestinationCoordinate() == tile)
                    .collect(collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    /**
     * Metoda tworzaca nowy obiekt klasy MoveTransition, ktory jest kolejnym zestawem ruchow pomiedzy aktualna plansza gry, a nastepna
     * @param move aktualne ruchy dla gracza
     * @return zwraca nowy obiekt klasy MoveTransition z odpowiednim statusem
     * @see MoveTransition
     * */
    public MoveTransition makeMove(final Move move) {
        if (!this.legalMoves.contains(move)) {
            return new MoveTransition(this.board, this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionedBoard = move.execute();
        if(transitionedBoard.currentPlayer().getOpponent().isInCheck()) {
            return new MoveTransition(this.board, this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        return transitionedBoard.currentPlayer().getOpponent().isInCheck() ?
                new MoveTransition(this.board, this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK) :
                new MoveTransition(this.board, transitionedBoard, move, MoveStatus.DONE);
    }

    /**
     * Metoda wykonujaca cofniecie ruchu gracza
     * @param move aktualne ruchy gracza
     * @return zwraca nowy obiekt klasy MoveTransition, ktory posiada cofniete ruchy gracza
     * @see MoveTransition
     */
    public MoveTransition unMakeMove(final Move move) {
        return new MoveTransition(this.board, move.undo(), move, MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
                                                             Collection<Move> opponentLegals);

}
