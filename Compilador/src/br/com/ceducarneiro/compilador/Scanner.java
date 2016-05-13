package br.com.ceducarneiro.compilador;

import java.io.*;

public class Scanner {
    private static final char EOF = 0;
    private static final int STATE_ID = 17;

    private BufferedReader input;
    private String line = null;
    private int lineIdx = 0;
    private int columnIdx = 0;

    private Token rollback = null;

    public Scanner(File input) throws FileNotFoundException {
        this.input = new BufferedReader(new FileReader(input));
    }

    public int getLine() {
        return lineIdx;
    }

    public int getColumn() {
        return columnIdx;
    }

    public Token nextToken() throws IOException, LexException {
        ScannerSt st = new ScannerSt();

        if (rollback != null) {
            st.token = rollback;
            rollback = null;
        } else {

            while (st.token == null && !st.exit) {
                st.ch = nextChar();

                switch (st.state) {
                    case 0: {
                        if (st.ch == ':') { //state 1
                            st.token = new Token(TipoToken.DOIS_PONTOS);
                        } else if (st.ch == ',') { //state 2
                            st.token = new Token(TipoToken.VIRGULA);
                        } else if (st.ch == 'i') {
                            st.lexema += st.ch;
                            st.state = 3;
                        } else if (st.ch == 'd') {
                            st.lexema += st.ch;
                            st.state = 6;
                        } else if (st.ch == 'f') {
                            st.lexema += st.ch;
                            st.state = 12;
                        } else if (isLetterOrUnderline(st.ch)) {
                            st.lexema += st.ch;
                            st.state = STATE_ID;
                        } else if (st.ch == EOF) {
                            st.exit = true;
                        } else if (st.ch != ' ' && st.ch != '\t' && st.ch != '\n') {
                            throw new LexException(st.ch, lineIdx, columnIdx);
                        }
                        break;
                    }
                    case 3:
                        treatTipoVar(st, 'n', 4);
                        break;
                    case 4:
                        treatTipoVar(st, 't', 5);
                        break;
                    case 5: {
                        if (isLetterOrNumberOrUnderline(st.ch)) {
                            st.state = STATE_ID;
                            st.lexema += st.ch;
                        } else {
                            st.token = new Token(TipoToken.TIPO_VAR, st.lexema);
                            rollback(st.ch);
                        }
                        break;
                    }
                    case 6:
                        treatTipoVar(st, 'o', 7);
                        break;
                    case 7:
                        treatTipoVar(st, 'u', 8);
                        break;
                    case 8:
                        treatTipoVar(st, 'b', 9);
                        break;
                    case 9:
                        treatTipoVar(st, 'l', 10);
                        break;
                    case 10:
                        treatTipoVar(st, 'e', 5);
                        break;

                    case 12:
                        treatTipoVar(st, 'l', 13);
                        break;
                    case 13:
                        treatTipoVar(st, 'o', 14);
                        break;
                    case 14:
                        treatTipoVar(st, 'a', 15);
                        break;
                    case 15:
                        treatTipoVar(st, 't', 5);
                        break;

                    case STATE_ID: {
                        if (isLetterOrNumberOrUnderline(st.ch)) {
                            st.lexema += st.ch;
                        } else {
                            st.token = new Token(TipoToken.ID, st.lexema);
                            rollback(st.ch);
                        }
                        break;
                    }
                }
            }
        }

        System.out.println("LOG: " + (st.token != null ?
            st.token.getTipo().name() : "EOF"));

        return st.token;
    }

    private void treatTipoVar(ScannerSt state, char want, int nextState) {
        if (state.ch == want) {
            state.state = nextState;
            state.lexema += state.ch;
        } else if (isLetterOrNumberOrUnderline(state.ch)) {
            state.state = STATE_ID;
            state.lexema += state.ch;
        } else {
            state.token = new Token(TipoToken.ID, state.lexema);
            rollback(state.ch);
        }
    }

    private boolean isLetterOrUnderline(char ch) {
        return Character.isLetter(ch) || ch == '_';
    }

    private boolean isLetterOrNumberOrUnderline(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_';
    }

    private void rollback(char ch) {
        if (ch != EOF) {
            columnIdx--;
        }
    }

    private char nextChar() throws IOException {
        char next;

        if (line == null || columnIdx >= line.length()) {
            line = input.readLine();
            columnIdx = 0;

            if (line != null) {
                lineIdx++;
                line += '\n';
            }
        }

        if (line == null) { //Fim do arquivo
            next = EOF;
        } else {
            next = line.charAt(columnIdx++);
        }

        return next;
    }

    public void rollbackToken(Token token) {
        rollback = token;
    }

    class ScannerSt {
        Token token = null;
        boolean exit = false;
        String lexema = "";
        int state = 0;
        char ch;
    }

}
