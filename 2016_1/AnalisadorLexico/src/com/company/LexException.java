package com.company;

/**
 * Created by eduardo on 03/03/16.
 */
public class LexException extends Throwable {
    private int position;

    public LexException(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return String.format("Erro na Análise Léxica, posição=%d", position);
    }
}
