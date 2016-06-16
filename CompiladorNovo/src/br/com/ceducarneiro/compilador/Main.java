package br.com.ceducarneiro.compilador;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Main {

    private static int numLog = 1;

    private static BufferedWriter output;

    private static List<Token> tokens;
    private static SymbolTable symbolTable;
    private static Scanner scanner;
    private static String exp;

    private static Stack<String> pct;

    private static List<String> decl = new ArrayList<>();
    private static List<String> atrb = new ArrayList<>();

    public static void main(String[] args) {
        /* Verifica se os caminhos dos arquivos de entra
           e saída foram passados como argumentos */
        if (args.length == 2) {
            String inputPath = args[0];
            String outputPath = args[1];

            File inputFile = new File(inputPath);
            File outputFile = new File(outputPath);

            /* Verifica se o caminho entrada é arquivo */
            if (inputFile.isFile()) {
                try {
                    output = new BufferedWriter(new FileWriter(outputFile));

                    /* Criando o analisador léxico */
                    scanner = new Scanner(inputFile);
                    symbolTable = new SymbolTable();
                    tokens = new ArrayList<>();

                    /* Chamando a análise sintática pelo
                       símbolo inicial */
                    symbolTable.initScope();
                    PROGRAMA();
                    symbolTable.finishScope();

                    /*Token token;
                    while ((token = scanner.nextToken()) != null) {
                        System.out.print(token);
                    }*/

                    for (String line : decl) {
                        output.write(line);
                        output.newLine();
                    }

                    output.newLine();

                    for (String line : atrb) {
                        output.write(line);
                        output.newLine();
                    }

                    output.close();

                    System.out.println("\n\n-- COMPILADO COM SUCESSO --");
                } catch (SinException | InvalidTypeException | LexException
                        | SymbolNotFoundException| SymbolExistsException ex) {
                    System.out.println("\n"+ex);
                } catch (Exception other) {
                    other.printStackTrace();
                    showHelp();
                }
            } else {
                showHelp();
            }
        } else {
            showHelp();
        }
    }

    private static void PROGRAMA() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        LISTA_DECL();
        LISTA_CMD();
    }

    private static void LISTA_DECL() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (token != null) {
            boolean keepRunning = true;

            scanner.rollbackToken(token);

            try {
                DECL();
            } catch (SinException ex) {
                if (ex.getExpected()[0] != TipoToken.TIPO_VAR)
                    throw ex;

                keepRunning = false;
                scanner.rollbackToken(ex.getToken());
            }

            if (keepRunning) {
                token = scanner.nextToken();
                if (checkTipo(token ,TipoToken.FIM_CMD)) {
                    LISTA_DECL();
                } else {
                    throw new SinException(token, new TipoToken[]{TipoToken.FIM_CMD},
                            scanner.getLine(), scanner.getColumn());
                }
            }
        }
    }

    private static void DECL() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (checkTipo(token, TipoToken.TIPO_VAR)) {
            LISTA_ID();
        } else {
            throw new SinException(token, new TipoToken[]{TipoToken.TIPO_VAR},
                    scanner.getLine(), scanner.getColumn());
        }
    }

    private static void LISTA_ID() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        ID();
        FIM_LISTA_ID();
    }

    private static void FIM_LISTA_ID() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (token != null) {
            if (checkTipo(token, TipoToken.VIRGULA)) {
                LISTA_ID();
            } else {
                scanner.rollbackToken(token);
            }
        }
    }

    private static void ID() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (checkTipo(token, TipoToken.ID)) {
            ATR_ID();
        } else {
            throw new SinException(token, new TipoToken[]{TipoToken.ID},
                    scanner.getLine(), scanner.getColumn());
        }
    }

    private static void ATR_ID() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (token != null) {
            if (checkTipo(token, TipoToken.ATRIBUICAO)) {
                EXP();
            } else {
                scanner.rollbackToken(token);
            }
        }
    }

    private static void EXP() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        OP();
        OPERACAO();
    }

    private static void OPERACAO() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (token != null) {
            if (checkTipo(token, TipoToken.OP_SOMA)
                    || checkTipo(token, TipoToken.OP_SUB)) {
                EXP();
            } else {
                scanner.rollbackToken(token);
            }
        }
    }

    private static void OP() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (checkTipo(token, TipoToken.NUMERO_INT)
                || checkTipo(token, TipoToken.NUMERO_REAL)
                || checkTipo(token, TipoToken.ID)) {
            //TODO: Hora massa de gerar um código
        } else {
            throw new SinException(token, new TipoToken[]{TipoToken.ID, TipoToken.NUMERO_INT, TipoToken.NUMERO_REAL},
                    scanner.getLine(), scanner.getColumn());
        }
    }

    private static void LISTA_CMD() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (token != null) {
            boolean keepRunning = true;

            scanner.rollbackToken(token);

            try {
                CMD();
            } catch (SinException ex) {
                if (ex.getExpected()[0] != TipoToken.ID
                        && ex.getExpected()[0] != TipoToken.PRINT
                        && ex.getExpected()[0] != TipoToken.SCAN)
                    throw ex;

                keepRunning = false;
                scanner.rollbackToken(ex.getToken());
            }

            if (keepRunning) {
                token = scanner.nextToken();
                if (checkTipo(token ,TipoToken.FIM_CMD)) {
                    LISTA_CMD();
                } else {
                    throw new SinException(token, new TipoToken[]{TipoToken.FIM_CMD},
                            scanner.getLine(), scanner.getColumn());
                }
            }
        }
    }

    private static void CMD() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        try {
            ATR();
        } catch (SinException sex1) {
            if (sex1.getExpected()[0] != TipoToken.ID)
                throw sex1;

            scanner.rollbackToken(sex1.getToken());
            try {
                PRINT();
            } catch (SinException sex2) {
                if (sex1.getExpected()[0] != TipoToken.PRINT)
                    throw sex1;

                scanner.rollbackToken(sex2.getToken());
                try {
                    SCAN();
                } catch (SinException sex3) {
                    throw new SinException(sex3.getToken(), new TipoToken[]{TipoToken.ID, TipoToken.PRINT, TipoToken.SCAN},
                            scanner.getLine(), scanner.getColumn());
                }
            }
        }
    }

    private static void SCAN() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (checkTipo(token, TipoToken.SCAN)) {
            token = scanner.nextToken();

            if (checkTipo(token, TipoToken.ABRE_PAR)) {
                F_SCAN();
                token = scanner.nextToken();
                if (checkTipo(token, TipoToken.FECHA_PAR)) {
                    //TODO: Bela manhã para gerar um código aproveitando a vista
                } else {
                    throw new SinException(token, new TipoToken[]{TipoToken.FECHA_PAR},
                            scanner.getLine(), scanner.getColumn());
                }
            } else {
                throw new SinException(token, new TipoToken[]{TipoToken.ABRE_PAR},
                        scanner.getLine(), scanner.getColumn());
            }
        } else {
            throw new SinException(token, new TipoToken[]{TipoToken.PRINT},
                    scanner.getLine(), scanner.getColumn());
        }
    }

    private static void F_SCAN() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (checkTipo(token, TipoToken.FMT_INT)) {
            token = scanner.nextToken();
            if (checkTipo(token, TipoToken.VIRGULA)) {
                token = scanner.nextToken();
                if (checkTipo(token, TipoToken.ENDERECO)) {
                    token = scanner.nextToken();
                    if (checkTipo(token, TipoToken.ID)) {
                        //TODO:
                    } else {
                        throw new SinException(token, new TipoToken[]{TipoToken.ID},
                                scanner.getLine(), scanner.getColumn());
                    }
                } else {
                    throw new SinException(token, new TipoToken[]{TipoToken.ENDERECO},
                            scanner.getLine(), scanner.getColumn());
                }
            } else {
                throw new SinException(token, new TipoToken[]{TipoToken.VIRGULA},
                        scanner.getLine(), scanner.getColumn());
            }
        } else if (checkTipo(token, TipoToken.FMT_FLOAT)) {
            token = scanner.nextToken();
            if (checkTipo(token, TipoToken.VIRGULA)) {
                token = scanner.nextToken();
                if (checkTipo(token, TipoToken.ENDERECO)) {
                    token = scanner.nextToken();
                    if (checkTipo(token, TipoToken.ID)) {
                        //TODO:
                    } else {
                        throw new SinException(token, new TipoToken[]{TipoToken.ID},
                                scanner.getLine(), scanner.getColumn());
                    }
                } else {
                    throw new SinException(token, new TipoToken[]{TipoToken.ENDERECO},
                            scanner.getLine(), scanner.getColumn());
                }
            } else {
                throw new SinException(token, new TipoToken[]{TipoToken.VIRGULA},
                        scanner.getLine(), scanner.getColumn());
            }
        } else {
            throw new SinException(token, new TipoToken[]{TipoToken.STRING, TipoToken.FMT_INT, TipoToken.FMT_FLOAT},
                    scanner.getLine(), scanner.getColumn());
        }
    }

    private static void PRINT() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (checkTipo(token, TipoToken.PRINT)) {
            token = scanner.nextToken();

            if (checkTipo(token, TipoToken.ABRE_PAR)) {
                P_PRINT();
                token = scanner.nextToken();
                if (checkTipo(token, TipoToken.FECHA_PAR)) {
                    //TODO: Bela manhã para gerar um código aproveitando a vista
                } else {
                    throw new SinException(token, new TipoToken[]{TipoToken.FECHA_PAR},
                            scanner.getLine(), scanner.getColumn());
                }
            } else {
                throw new SinException(token, new TipoToken[]{TipoToken.ABRE_PAR},
                        scanner.getLine(), scanner.getColumn());
            }
        } else {
            throw new SinException(token, new TipoToken[]{TipoToken.PRINT},
                    scanner.getLine(), scanner.getColumn());
        }
    }

    private static void P_PRINT() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (checkTipo(token, TipoToken.STRING)) {
            //TODO:
        } else if (checkTipo(token, TipoToken.FMT_INT)) {
            token = scanner.nextToken();
            if (checkTipo(token, TipoToken.VIRGULA)) {
                F_INT();
            } else {
                throw new SinException(token, new TipoToken[]{TipoToken.VIRGULA},
                        scanner.getLine(), scanner.getColumn());
            }
        } else if (checkTipo(token, TipoToken.FMT_FLOAT)) {
            token = scanner.nextToken();
            if (checkTipo(token, TipoToken.VIRGULA)) {
                F_FLOAT();
            } else {
                throw new SinException(token, new TipoToken[]{TipoToken.VIRGULA},
                        scanner.getLine(), scanner.getColumn());
            }
        } else {
            throw new SinException(token, new TipoToken[]{TipoToken.STRING, TipoToken.FMT_INT, TipoToken.FMT_FLOAT},
                    scanner.getLine(), scanner.getColumn());
        }
    }

    private static void F_INT() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (checkTipo(token, TipoToken.ID)
                || checkTipo(token, TipoToken.NUMERO_INT)) {
            //TODO:
        } else {
            throw new SinException(token, new TipoToken[]{TipoToken.ID, TipoToken.NUMERO_INT},
                    scanner.getLine(), scanner.getColumn());
        }
    }

    private static void F_FLOAT() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (checkTipo(token, TipoToken.ID)
                || checkTipo(token, TipoToken.NUMERO_REAL)) {
            //TODO:
        } else {
            throw new SinException(token, new TipoToken[]{TipoToken.ID, TipoToken.NUMERO_REAL},
                    scanner.getLine(), scanner.getColumn());
        }
    }

    private static void ATR() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (checkTipo(token, TipoToken.ID)) {
            token = scanner.nextToken();

            if (checkTipo(token, TipoToken.ATRIBUICAO)) {
                EXP();
            } else {
                throw new SinException(token, new TipoToken[]{TipoToken.ATRIBUICAO},
                        scanner.getLine(), scanner.getColumn());
            }
        } else {
            throw new SinException(token, new TipoToken[]{TipoToken.ID},
                    scanner.getLine(), scanner.getColumn());
        }
    }

    private static boolean checkTipo(Token token, TipoToken tipo) {
        return token != null && token.getTipo() == tipo;
    }

    private static void showHelp() {
        System.out.println("TODO: Help");
    }

    public static void log(SymbolTable sym) {
        //System.out.println(String.format("[%d]: %s", numLog++, sym));
    }
}
