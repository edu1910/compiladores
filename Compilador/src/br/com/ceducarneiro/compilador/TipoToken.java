package br.com.ceducarneiro.compilador;

public enum TipoToken {
    VIRGULA(","),
    ID("identificador"),
    TIPO_VAR("tipo de variável"),
    DOIS_PONTOS(":");

    String nick;

    TipoToken(String nick) {
        this.nick = nick;
    }

    @Override
    public String toString() {
        return nick;
    }
}
