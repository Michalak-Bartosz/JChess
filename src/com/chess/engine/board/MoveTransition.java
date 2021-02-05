package com.chess.engine.board;

import com.chess.engine.board.Move.MoveStatus;

/**
 * Klasa ktora przechowuje ruch ktory bedzie przeniesiony na nowa plansze
 */
public final class MoveTransition {

    private final Board fromBoard;
    private final Board toBoard;
    private final Move transitionMove;
    private final MoveStatus moveStatus;

    public MoveTransition(final Board fromBoard,
                          final Board toBoard,
                          final Move transitionMove,
                          final MoveStatus moveStatus) {
        this.fromBoard = fromBoard;
        this.toBoard = toBoard;
        this.transitionMove = transitionMove;
        this.moveStatus = moveStatus;
    }

    /**
     * Metoda ktora okresla z jakiej planszy przeniesiono ruch
     * @return zwraca plansze z ktorej pobrano ruch
     */
    public Board getFromBoard() {
        return this.fromBoard;
    }

    /**
     * Metoda ktora okresla na jaka plansze zostanie przeniesiony ruch
     * @return zwraca plansze na ktora ma przeniesc ruch
     */
    public Board getToBoard() {
         return this.toBoard;
    }

    /**
     * Metoda zwracajaca ruch ktory ma byc wykonany
     * @return zwraca ruch, ktory ma byc wykonany
     */
    public Move getTransitionMove() {
        return this.transitionMove;
    }

    /**
     * Metoda zwracajaca status ruchu, ktory ma byc wykonany
     * @return zwraca status ruchu, ktory ma byc wykonany
     */
    public MoveStatus getMoveStatus() {
        return this.moveStatus;
    }
}
