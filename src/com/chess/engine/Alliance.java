package com.chess.engine;

import com.chess.engine.board.BoardUtils;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;

/**
 * Enum przechowujace dwa rodzaje pionkow - biale oraz czarne, wraz z metodami pozwalajacymi okreslic ktory gracz wykonuje aktualnie ruch oraz kierunek ruchu zalezny od ustawienia planszy.
 */
public enum Alliance {

    WHITE() {

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public int getDirection() {
            return UP_DIRECTION;
        }

        @Override
        public int getOppositeDirection() {
            return DOWN_DIRECTION;
        }

        @Override
        public boolean isPawnPromotionSquare(final int position) {
            return BoardUtils.INSTANCE.FIRST_ROW.get(position);
        }

        @Override
        public Player choosePlayerByAlliance(final WhitePlayer whitePlayer,
                                             final BlackPlayer blackPlayer) {
            return whitePlayer;
        }

        @Override
        public String toString() {
            return "White";
        }
    },
    BLACK() {

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public int getDirection() {
            return DOWN_DIRECTION;
        }

        @Override
        public int getOppositeDirection() {
            return UP_DIRECTION;
        }

        @Override
        public boolean isPawnPromotionSquare(final int position) {
            return BoardUtils.INSTANCE.EIGHTH_ROW.get(position);
        }

        @Override
        public Player choosePlayerByAlliance(final WhitePlayer whitePlayer,
                                             final BlackPlayer blackPlayer) {
            return blackPlayer;
        }

        @Override
        public String toString() {
            return "Black";
        }
    };

    /**
     * Metoda zwracajaca kierunek ruchu.
     * @return wartosc reprezentujaca kierunek ruchu (do gory -1, do dolu 1)
     */
    public abstract int getDirection();

    /**
     * Metoda zwracajaca przeciwny kierunek ruchu.
     * @return wartosc reprezentujaca kierunek ruchu (do gory -1, do dolu 1)
     */
    public abstract int getOppositeDirection();

    /**
     * Metoda zwracajaca wartosc logiczna czy gracz jest bialy
     * @return zwraca prawde lub falsz
     */
    public abstract boolean isWhite();

    /**
     * Metoda zwracajaca wartosc logiczna czy gracz jest czarny
     * @return zwraca prawde lub falsz
     */
    public abstract boolean isBlack();

    /**
     * Metoda zwracajaca wartosc logiczna czy pole na ktorym jest pionek jest polem promocji (ostatnie pole planszy)
     * @param position akturalna pozycja pionka
     * @return zwraca prawde lub falsz
     */
    public abstract boolean isPawnPromotionSquare(int position);

    /**
     * Metoda wracajaca gracza po kolorze.
     * @param whitePlayer referencja na gracza bialego
     * @param blackPlayer referencja na gracza czarnego
     * @return zwraca gracza bialego dla ruchu gracza bialego, a gracza czarnego dla ruchu gracza czarnego
     */
    public abstract Player choosePlayerByAlliance(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer);

    private static final int UP_DIRECTION = -1;

    private static final int DOWN_DIRECTION = 1;

}