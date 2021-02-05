package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MajorAttackMove;
import com.chess.engine.board.Move.MajorMove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Klasa ktora przechowuje strukture pionka krolowa
 */
public final class Queen extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATES = { -9, -8, -7, -1, 1,
        7, 8, 9 };

    public Queen(final Alliance alliance, final int piecePosition) {
        super(PieceType.QUEEN, alliance, piecePosition, true);
    }

    public Queen(final Alliance alliance,
                 final int piecePosition,
                 final boolean isFirstMove) {
        super(PieceType.QUEEN, alliance, piecePosition, isFirstMove);
    }

    /**
     * Metoda tworzaca liste dostepnych ruchow dla krolowej
     * @param board aktualna plansza
     * @return zwraca liste mozliwych ruchow dla krolowej
     */
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
            int candidateDestinationCoordinate = this.piecePosition;
            while (true) {
                if (isFirstColumnExclusion(currentCandidateOffset, candidateDestinationCoordinate) ||
                    isEightColumnExclusion(currentCandidateOffset, candidateDestinationCoordinate)) {
                    break;
                }
                candidateDestinationCoordinate += currentCandidateOffset;
                if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                    break;
                } else {
                    final Piece pieceAtDestination = board.getPiece(candidateDestinationCoordinate);
                    if (pieceAtDestination == null) {
                        legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                    } else {
                        final Alliance pieceAtDestinationAllegiance = pieceAtDestination.getPieceAllegiance();
                        if (this.pieceAlliance != pieceAtDestinationAllegiance) {
                            legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate,
                                    pieceAtDestination));
                        }
                        break;
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    /**
     * Metoda, za pomoca ktorej wykonywane sa ruchy dla krolowej
     * @param move aktualny ruch
     * @return zwraca nowa wieze po wykonaniu ruchu
     */
    @Override
    public Queen movePiece(final Move move) {
        return PieceUtils.INSTANCE.getMovedQueen(move.getMovedPiece().getPieceAllegiance(), move.getDestinationCoordinate());
    }

    /**
     * Metoda zwracajaca wartosc funkcji toString dla krolowej
     * @return zwraca "Q"
     */
    @Override
    public String toString() {
        return this.pieceType.toString();
    }

    private static boolean isFirstColumnExclusion(final int currentPosition,
                                                  final int candidatePosition) {
        return BoardUtils.INSTANCE.FIRST_COLUMN.get(candidatePosition) && ((currentPosition == -9)
                || (currentPosition == -1) || (currentPosition == 7));
    }

    private static boolean isEightColumnExclusion(final int currentPosition,
                                                  final int candidatePosition) {
        return BoardUtils.INSTANCE.EIGHTH_COLUMN.get(candidatePosition) && ((currentPosition == -7)
                || (currentPosition == 1) || (currentPosition == 9));
    }

}