package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.KingSideCastleMove;
import com.chess.engine.board.Move.QueenSideCastleMove;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Klasa odpowiadajaca za mechaniki gracza czarnego
 */
public final class BlackPlayer extends Player {

    /**
     * Konstruktor klasy WhitePlayer
     * @param board aktualna plansza
     * @param whiteStandardLegals aktualne ruchy dla gracza bialego
     * @param blackStandardLegals aktuane ruchy dla gracza czarnego
     */
    public BlackPlayer(final Board board,
                       final Collection<Move> whiteStandardLegals,
                       final Collection<Move> blackStandardLegals) {
        super(board, blackStandardLegals, whiteStandardLegals);
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                    final Collection<Move> opponentLegals) {

        if (this.isInCheck() || this.isCastled() || !(this.isKingSideCastleCapable() || this.isQueenSideCastleCapable())) {
            return Collections.emptyList();
        }

        final List<Move> kingCastles = new ArrayList<>();

        if (this.playerKing.isFirstMove() && this.playerKing.getPiecePosition() == 4 && !this.isInCheck) {
            //blacks king side castle
            if (this.board.getPiece(5) == null && this.board.getPiece(6) == null) {
                final Piece kingSideRook = this.board.getPiece(7);
                if (kingSideRook != null && kingSideRook.isFirstMove() &&
                        Player.calculateAttacksOnTile(5, opponentLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(6, opponentLegals).isEmpty() &&
                        kingSideRook.getPieceType().isRook()) {
                    if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 12)) {
                        kingCastles.add(
                                new KingSideCastleMove(this.board, this.playerKing, 6, (Rook) kingSideRook, kingSideRook.getPiecePosition(), 5));

                    }
                }
            }
            //blacks queen side castle
            if (this.board.getPiece(1) == null && this.board.getPiece(2) == null &&
                    this.board.getPiece(3) == null) {
                final Piece queenSideRook = this.board.getPiece(0);
                if (queenSideRook != null && queenSideRook.isFirstMove() &&
                        Player.calculateAttacksOnTile(2, opponentLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(3, opponentLegals).isEmpty() &&
                        queenSideRook.getPieceType().isRook()) {
                    if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 12)) {
                        kingCastles.add(
                                new QueenSideCastleMove(this.board, this.playerKing, 2, (Rook) queenSideRook, queenSideRook.getPiecePosition(), 3));
                    }
                }
            }
        }
        return Collections.unmodifiableList(kingCastles);
    }

    /**
     * Funkcja zwracajaca rywala gracza czarnego
     * @return zwraca referencje na gracza bialego
     */
    @Override
    public WhitePlayer getOpponent() {
        return this.board.whitePlayer();
    }

    /**
     * Funkcja zwracajaca aktywne pionki gracza czarnego
     * @return zwraca aktywne pionki gracza czarnego
     */
    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    /**
     * Funkcja zwracajaca kolor gracza
     * @return zwraca "BLACK" dla gracza czarnego
     */
    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    /**
     * Funkcja zwracajaca reprezentacje znakowa dla gracza czarnego
     * @return zwraca wartosc funkcji toString, czyli "Black"
     */
    @Override
    public String toString() {
        return Alliance.BLACK.toString();
    }

}
