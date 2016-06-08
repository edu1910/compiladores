package br.com.ceducarneiro.compilador;

public enum TipoToken {
    VIRGULA(","),
    FIM_CMD(";"),
    ID("identificador"),
    TIPO_VAR("tipo de variável"),
    DOIS_PONTOS(":"),
    NUMERO_INT("inteiro"),
    NUMERO_REAL("real"),
    ATRIBUICAO("atribuição"),
    OP_SOMA("+"),
    OP_SUB("-");

    String nick;

    TipoToken(String nick) {
        this.nick = nick;
    }

    @Override
    public String toString() {
        return nick;
    }
}
