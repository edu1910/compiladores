package br.com.ceducarneiro.compilador;

public enum TipoToken {
    VIRGULA(","),
    FIM_CMD(";"),
    ID("identificador"),
    TIPO_VAR("tipo de variável"),
    NUMERO_INT("inteiro"),
    NUMERO_REAL("real"),
    ATRIBUICAO("atribuição"),
    PRINT("printf"),
    ABRE_PAR("("),
    FECHA_PAR(")"),
    STRING("string"),
    FMT_INT("%d"),
    FMT_FLOAT("%f"),
    ENDERECO("&"),
    SCAN("scanf"),
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
