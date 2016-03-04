package com.company;

public class Token {

    private TipoToken tipo;
    private String lexema = null;

    public Token(TipoToken tipo, String lexema) {
        setTipo(tipo);
        setLexema(lexema);
    }

    public TipoToken getTipo() {
        return tipo;
    }

    public void setTipo(TipoToken tipo) {
        this.tipo = tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    @Override
    public String toString() {
        return lexema != null ?
                String.format("<%s,\"%s\">", tipo.name(), lexema)
                : String.format("<%s>", tipo.name());
    }
}
