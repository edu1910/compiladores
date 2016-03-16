package br.com.ceducarneiro;

public class Lexico {

    private static String input = "while abc for foreach fora foreac :=";
    private static int idx = 0;

    public static Token nextToken() throws LexException {
        int state = 0;
        boolean exit = false;

        Token token = null;
        String lexema = "";

        while (token == null && !exit) {
            char ch = nextCh();

            switch (state) {
                case 0: {
                    if (ch == 'f') {
                        lexema += ch;
                        state = 6;
                    } else if (ch == 'w') {
                        lexema += ch;
                        state = 1;
                    } else if (ch == '+') {
                        lexema += ch;
                        state = 14;
                    } else if (ch == '-') {
                        lexema += ch;
                        state = 15;
                    } else if (ch == ':') {
                        lexema += ch;
                        state = 16;
                    } else if (ch == '=') {
                        lexema += ch;
                        state = 18;
                    } else if (ch == '.') {
                        lexema += ch;
                        state = 19;
                    } else if (isNumber(ch)) {
                        lexema += ch;
                        state = 21;
                    } else if ((ch == '_') || isLetter(ch)) {
                        lexema += ch;
                        state = 13;
                    } else if (ch == 0) {
                        exit = true;
                    } else if (ch != ' ' && ch != '\n' && ch != '\t') {
                        throw new LexException(ch, idx);
                    }

                    break;
                }

                case 1: {
                    if (ch == 'h') {
                        lexema += ch;
                        state = 2;
                    } else if (ch == '_' || isLetter(ch) || isNumber(ch)) {
                        lexema += ch;
                        state = 13;
                    } else {
                        token = new Token(TokenType.ID, lexema);
                        rollbackCh(ch);
                    }

                    break;
                }

                case 2: {
                    if (ch == 'i') {
                        lexema += ch;
                        state = 3;
                    } else if (ch == '_' || isLetter(ch) || isNumber(ch)) {
                        lexema += ch;
                        state = 13;
                    } else {
                        token = new Token(TokenType.ID, lexema);
                        rollbackCh(ch);
                    }

                    break;
                }

                case 3: {
                    if (ch == 'l') {
                        lexema += ch;
                        state = 4;
                    } else if (ch == '_' || isLetter(ch) || isNumber(ch)) {
                        lexema += ch;
                        state = 13;
                    } else {
                        token = new Token(TokenType.ID, lexema);
                        rollbackCh(ch);
                    }

                    break;
                }

                case 4: {
                    if (ch == 'e') {
                        lexema += ch;
                        state = 5;
                    } else if (ch == '_' || isLetter(ch) || isNumber(ch)) {
                        lexema += ch;
                        state = 13;
                    } else {
                        token = new Token(TokenType.ID, lexema);
                        rollbackCh(ch);
                    }

                    break;
                }

                case 5: {
                    if (ch == '_' || isLetter(ch) || isNumber(ch)) {
                        lexema += ch;
                        state = 13;
                    } else {
                        token = new Token(TokenType.R_WHILE);
                        rollbackCh(ch);
                    }

                    break;
                }

                case 6: {
                    if (ch == 'o') {
                        lexema += ch;
                        state = 7;
                    } else if (ch == '_' || isLetter(ch) || isNumber(ch)) {
                        lexema += ch;
                        state = 13;
                    } else {
                        token = new Token(TokenType.ID, lexema);
                        rollbackCh(ch);
                    }

                    break;
                }

                case 7: {
                    if (ch == 'r') {
                        lexema += ch;
                        state = 8;
                    } else if (ch == '_' || isLetter(ch) || isNumber(ch)) {
                        lexema += ch;
                        state = 13;
                    } else {
                        token = new Token(TokenType.ID, lexema);
                        rollbackCh(ch);
                    }

                    break;
                }

                case 8: {
                    if (ch == 'e') {
                        lexema += ch;
                        state = 9;
                    } else if (ch == '_' || isLetter(ch) || isNumber(ch)) {
                        lexema += ch;
                        state = 13;
                    } else {
                        token = new Token(TokenType.R_FOR);
                        rollbackCh(ch);
                    }

                    break;
                }

                case 9: {
                    if (ch == 'a') {
                        lexema += ch;
                        state = 10;
                    } else if (ch == '_' || isLetter(ch) || isNumber(ch)) {
                        lexema += ch;
                        state = 13;
                    } else {
                        token = new Token(TokenType.ID, lexema);
                        rollbackCh(ch);
                    }

                    break;
                }

                case 10: {
                    if (ch == 'c') {
                        lexema += ch;
                        state = 11;
                    } else if (ch == '_' || isLetter(ch) || isNumber(ch)) {
                        lexema += ch;
                        state = 13;
                    } else {
                        token = new Token(TokenType.ID, lexema);
                        rollbackCh(ch);
                    }

                    break;
                }

                case 11: {
                    if (ch == 'h') {
                        lexema += ch;
                        state = 12;
                    } else if (ch == '_' || isLetter(ch) || isNumber(ch)) {
                        lexema += ch;
                        state = 13;
                    } else {
                        token = new Token(TokenType.ID, lexema);
                        rollbackCh(ch);
                    }

                    break;
                }

                case 12: {
                    if (ch == '_' || isLetter(ch) || isNumber(ch)) {
                        lexema += ch;
                        state = 13;
                    } else {
                        token = new Token(TokenType.R_FOREACH);
                        rollbackCh(ch);
                    }

                    break;
                }

                case 13: {
                    if (ch == '_' || isLetter(ch) || isNumber(ch)) {
                        lexema += ch;
                    } else {
                        token = new Token(TokenType.ID, lexema);
                        rollbackCh(ch);
                    }

                    break;
                }

                case 14: {
                    if (ch == '.') {
                        lexema += ch;
                        state = 19;
                    } else if (isNumber(ch)) {
                        lexema += ch;
                        state = 21;
                    } else {
                        token = new Token(TokenType.OP_SUM);
                        rollbackCh(ch);
                    }

                    break;
                }

                case 15: {
                    if (ch == '.') {
                        lexema += ch;
                        state = 19;
                    } else if (isNumber(ch)) {
                        lexema += ch;
                        state = 21;
                    } else {
                        token = new Token(TokenType.OP_SUB);
                        rollbackCh(ch);
                    }

                    break;
                }

                case 16: {
                    if (ch == '=') {
                        lexema += ch;
                        state = 17;
                    } else {
                        throw new LexException(ch, '=', idx);
                    }

                    break;
                }

                case 17: {
                    token = new Token(TokenType.OP_ATTR);
                    rollbackCh(ch);

                    break;
                }
            }
        }

        return token;
    }

    private static char nextCh() {
        return (idx < input.length()) ?
                input.charAt(idx++) : 0;
    }

    private static void rollbackCh(char ch) {
        if (ch != 0) {
            idx--;
        }
    }

    private static boolean isLetter(char ch) {
        return Character.isLetter(ch);
    }

    private static boolean isNumber(char ch) {
        return Character.isDigit(ch);
    }

}
