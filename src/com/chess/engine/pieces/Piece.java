package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;

/**
 * Klasa abstrakcyjna po ktorej dziedzicza wszystkie rodzaje pionkow
 * @see Bishop
 * @see King
 * @see Knight
 * @see Pawn
 * @see Queen
 * @see Rook
 */
public abstract class Piece {

    final PieceType pieceType;
    final Alliance pieceAlliance;
    final int piecePosition;
    private final boolean isFirstMove;
    private final int cachedHashCode;

    Piece(final PieceType type,
          final Alliance alliance,
          final int piecePosition,
          final boolean isFirstMove) {
        this.pieceType = type;
        this.piecePosition = piecePosition;
        this.pieceAlliance = alliance;
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }

    /**
     * Metoda zwracajaca typ pionka
     * @return zwraca typ pionka
     */
    public PieceType getPieceType() {
        return this.pieceType;
    }

    /**
     * Metoda zwracajaca kolor pionka
     * @return zwraca kolor pionka
     */
    public Alliance getPieceAllegiance() {
        return this.pieceAlliance;
    }

    /**
     * Metoda zwracajaca pozycje pionka na planszy
     * @return zwraca numer pola na ktorym znajduje sie pionek
     */
    public int getPiecePosition() {
        return this.piecePosition;
    }

    /**
     * Metoda okreslajaca czy ruch pionka jest pierwszym ruchem
     * @return zwraca prawde jesli jest to pierwszy ruch pionka lub falsz gdy nie jest to pierwszy ruch
     */
    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    /**
     * Metoda ktora zwraca wartosciowosc pionka, ktora sluzy do sortowania zbitych pionkow
     * @return zwraca wartosciowosc pionka
     */
    public int getPieceValue() {
        return this.pieceType.getPieceValue();
    }

    /**
     * Metoda abstrakcyjna, za pomoca ktorej wykonywane sa ruchy dla pionkow
     * @param move ruch ktory ma byc wykonany
     * @return zwraca pionek ktory wykonal ruch
     */
    public abstract Piece movePiece(Move move);

    /**
     * Metoda abstrakcyjna, ktora tworzy liste dostepnych ruchow dla pionka
     * @param board aktualna plansza
     * @return zwraca liste mozliwych ruchow dla pionka
     */
    public abstract Collection<Move> calculateLegalMoves(final Board board);


    /**
     * Metoda sprawdzajaca czy podany pionek jest rowny pionkowi
     * @param other sprawdzany pionek
     * @return zwraca prawde gdy pionki sa takie same lub falsz w przeciwnym wypadku
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Piece)) {
            return false;
        }
        final Piece otherPiece = (Piece) other;
        return this.piecePosition == otherPiece.piecePosition && this.pieceType == otherPiece.pieceType &&
               this.pieceAlliance == otherPiece.pieceAlliance && this.isFirstMove == otherPiece.isFirstMove;
    }

    /**
     * Metoda zwracajaca hashCode dla pionka
     * @return zwraca hashCode pionka
     */
    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    /**
     * Metoda obliczajaca hashCode pionka w zaleznosci od koloru pionka oraz czy jest to pierwszy ruch pionka
     * @return zwraca obliczony hashCode
     */
    private int computeHashCode() {
        int result = this.pieceType.hashCode();
        result = 31 * result + this.pieceAlliance.hashCode();
        result = 31 * result + this.piecePosition;
        result = 31 * result + (this.isFirstMove ? 1 : 0);
        return result;
    }

    /**
     * Enum przechowujace nazwy wszystkich pionkow
     */
    public enum PieceType {

        PAWN(100, "P") {
            @Override
            public boolean isPawn() {
                return true;
            }

            @Override
            public boolean isBishop() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isKing() {
                return false;
            }
        },
        KNIGHT(320, "N") {
            @Override
            public boolean isPawn() {
                return false;
            }

            @Override
            public boolean isBishop() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isKing() {
                return false;
            }
        },
        BISHOP(350, "B") {
            @Override
            public boolean isPawn() {
                return false;
            }

            @Override
            public boolean isBishop() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isKing() {
                return false;
            }
        },
        ROOK(500, "R") {
            @Override
            public boolean isPawn() {
                return false;
            }

            @Override
            public boolean isBishop() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }

            @Override
            public boolean isKing() {
                return false;
            }
        },
        QUEEN(900, "Q") {
            @Override
            public boolean isPawn() {
                return false;
            }

            @Override
            public boolean isBishop() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isKing() {
                return false;
            }
        },
        KING(20000, "K") {
            @Override
            public boolean isPawn() {
                return false;
            }

            @Override
            public boolean isBishop() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isKing() {
                return true;
            }
        };

        private final int value;
        private final String pieceName;

        public int getPieceValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        PieceType(final int val,
                  final String pieceName) {
            this.value = val;
            this.pieceName = pieceName;
        }

        /**
         * Metoda abstrakcyjna, ktora sprawdza czy pionek jest pionkiem
         * @return zwraca prawde dla pionka lub falsz w przeciwnym przypadku
         */
        public abstract boolean isPawn();

        /**
         * Metoda abstrakcyjna, ktora sprawdza czy pionek jest goncem
         * @return zwraca prawde dla gonca lub falsz w przeciwnym przypadku
         */
        public abstract boolean isBishop();

        /**
         * Metoda abstrakcyjna, ktora sprawdza czy pionek jest wieza
         * @return zwraca prawde dla wiezy lub falsz w przeciwnym przypadku
         */
        public abstract boolean isRook();

        /**
         * Metoda abstrakcyjna, ktora sprawdza czy pionek jest krolem
         * @return zwraca prawde dla krola lub falsz w przeciwnym przypadku
         */
        public abstract boolean isKing();

    }

}