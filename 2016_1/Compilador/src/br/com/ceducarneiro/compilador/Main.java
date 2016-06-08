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
                } catch (SinException sin) {
                    System.out.println("\n"+sin);
                } catch (LexException lex) {
                    System.out.println("\n" + lex);
                } catch (SymbolExistsException sex) {
                    System.out.println("\n" + sex);
                } catch (SymbolNotFoundException snf) {
                    System.out.println("\n" + snf);
                } catch (InvalidTypeException itp) {
                    System.out.println("\n" + itp);
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
        CMD();
        LISTA_CMD();
    }

    private static void LISTA_CMD() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (token != null) {
            scanner.rollbackToken(token);
            PROGRAMA();
        }
    }

    private static void CMD() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        pct = new Stack<>();
        exp = "";

        if (checkTipo(token, TipoToken.ID)) {
            tokens.add(token);
            CMD_COMP();
            token = scanner.nextToken();
            if (checkTipo(token, TipoToken.FIM_CMD)) {
                //TODO: Local para talvez gerar um código
            } else {
                throw new SinException(token, new TipoToken[]{TipoToken.FIM_CMD},
                        scanner.getLine(), scanner.getColumn());
            }
        } else {
            throw new SinException(token, new TipoToken[]{TipoToken.ID},
                    scanner.getLine(), scanner.getColumn());
        }
    }

    private static void CMD_COMP() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (checkTipo(token, TipoToken.ATRIBUICAO)) {
            EXP();

            Symbol sym = symbolTable.search(tokens.get(0).getLexema());

            if (sym != null) {
                String tipo_exp = pct.pop();
                if (sym.getType().equals("int") && tipo_exp.equals("inteiro")
                        || sym.getType().equals("float") || sym.getType().equals("double")) {
                    atrb.add(sym.getId() + " <- " + exp + ";");
                } else {
                    throw new InvalidTypeException(sym, tipo_exp);
                }
            } else {
                throw new SymbolNotFoundException(new Symbol(tokens.get(0).getLexema(), scanner.getLine()));
            }
            tokens.clear();
        } else {
            scanner.rollbackToken(token);
            LISTA_IDS();
        }
    }

    private static void EXP() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        CMD_COMP_LINHA();
        if (pct.size() > 1) {
            String operando1 = pct.pop();
            String operando2 = pct.pop();

            if (operando1.equals("real") || operando2.equals("real"))
                pct.push("real");
            else
                pct.push("inteiro");
        }
        FIM_EXP();
    }

    private static void FIM_EXP() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (token != null) {
            boolean keepRunning = true;
            scanner.rollbackToken(token);

            try {
                OP();
            } catch (SinException ex) {
                scanner.rollbackToken(ex.getToken());
                keepRunning = false;
            }

            if (keepRunning) {
                EXP();
            }
        }
    }

    private static void OP() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();

        if (checkTipo(token, TipoToken.OP_SOMA)
                || checkTipo(token, TipoToken.OP_SUB)) {
            //TODO: mais um belo local para gerar um código
            exp += token.getTipo();
        } else {
            throw new SinException(token, new TipoToken[]{TipoToken.OP_SOMA, TipoToken.OP_SUB},
                    scanner.getLine(), scanner.getColumn());
        }
    }

    private static void CMD_COMP_LINHA() throws IOException, LexException, SinException, SymbolExistsException, SymbolNotFoundException, InvalidTypeException {
        Token token = scanner.nextToken();
        if (checkTipo(token, TipoToken.NUMERO_INT)
                || checkTipo(token, TipoToken.NUMERO_REAL)) {
            exp += token.getLexema();
            pct.push(token.getTipo().toString());
        } else if (checkTipo(token, TipoToken.ID)) {
            Symbol sym = symbolTable.search(token.getLexema());

            if (sym != null) {
                exp += token.getLexema();
                pct.push(sym.getType().equals("int") ? "inteiro" : "real");
            } else {
                throw new SymbolNotFoundException(new Symbol(token.getLexema(), scanner.getLine()));
            }
        } else {
            throw new SinException(token, new TipoToken[]{TipoToken.NUMERO_INT, TipoToken.NUMERO_REAL},
                    scanner.getLine(), scanner.getColumn());
        }
    }

    private static void LISTA_IDS() throws SinException, IOException, LexException, SymbolExistsException {
        Token token = scanner.nextToken();
        if (checkTipo(token, TipoToken.VIRGULA)) {
            token = scanner.nextToken();
            if (checkTipo(token, TipoToken.ID)) {
                tokens.add(token);
                LISTA_IDS();
            }
        } else if (checkTipo(token, TipoToken.DOIS_PONTOS)) {
            token = scanner.nextToken();
            if (checkTipo(token, TipoToken.TIPO_VAR)) {
                for (Token t : tokens) {
                    symbolTable.insert(new Symbol(t.getLexema(), token.getLexema(), scanner.getLine()));
                    log(symbolTable);
                }

                String tipoFinal = token.getLexema().equals("int") ? "INTEGER"
                        : token.getLexema().equals("float") ? "FLOAT" : "DOUBLE";

                for (Token t : tokens) {
                    decl.add(tipoFinal + " " + t.getLexema() + ";");
                }
                tokens.clear();
            } else {
                throw new SinException(token, new TipoToken[]{TipoToken.TIPO_VAR},
                        scanner.getLine(), scanner.getColumn());
            }
        } else {
            throw new SinException(token, new TipoToken[]{TipoToken.VIRGULA,TipoToken.DOIS_PONTOS},
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
