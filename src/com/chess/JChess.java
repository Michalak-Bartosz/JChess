package com.chess;

import com.chess.gui.Table;

/**
 * Gra w szachy
 * @author Michalak Bartosz
 * @version 1.0
 * @since 08.06.2020
 */

public class JChess extends Thread{

    public static void main(final String[] args) throws Exception {
        new Thread(new JChess()).start();
    }

    /**
     * Metoda run() watku, ktora najpierw wykonuje funkcje get(), a nastepnie show() klasy Table
     * @see Table
     */
    @Override
    public void run(){
        Table.get().show();
    }
}
