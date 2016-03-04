package com.company;

public class Lexico {

    private static String entrada = "   ab c _asjdkj2134 q_ a !=    a";
    private static int idx = 0;

    public static Token proximoToken() throws LexException {
        int estado = 0;
        Token token = null;

        boolean sair = false;
        String lexema = "";
        Character ch;

        while (token == null && !sair) {
            ch = lerCaractere();

            switch (estado) {
                case 0:
                    if (ch == '!') {
                        estado = 2;
                    } else if (Character.isLetter(ch) || ch == '_') {
                        estado = 1;
                        lexema += ch;
                    } else if (ch == 0) {
                        sair = true;
                    } else if (ch != ' ') {
                        throw new LexException(idx);
                    }
                    break;
                case 1:
                    if (Character.isLetterOrDigit(ch) || ch == '_') {
                        lexema += ch;
                    } else {
                        token = new Token(TipoToken.ID, lexema);

                        if (ch != 0) {
                            idx--;
                        }
                    }
                    break;
                case 2:
                    if (ch == '=') {
                        estado = 3;
                    } else {
                        token = new Token(TipoToken.NOT, null);

                        if (ch != 0) {
                            idx--;
                        }
                    }
                    break;
                case 3:
                    token = new Token(TipoToken.DIFF, null);

                    if (ch != 0) {
                        idx--;
                    }
                    break;
            }
        }

        return token;
    }

    private static Character lerCaractere() {
        Character read = 0;

        if (idx < entrada.length()) {
            read = entrada.charAt(idx++);
        }

        return read;
    }

}
