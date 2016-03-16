package br.com.ceducarneiro;

public class Token {

    private TokenType type;
    private String lexema;

    public Token(TokenType type, String lexema) {
        setType(type);
        setLexema(lexema);
    }

    public Token(TokenType type) {
        this(type, null);
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    @Override
    public String toString() {
        return lexema == null ? String.format("<%s>", type.name())
                : String.format("<%s,\"%s\">", type.name(), lexema);
    }
}
