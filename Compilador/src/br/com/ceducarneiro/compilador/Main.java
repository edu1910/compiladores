package br.com.ceducarneiro.compilador;

import java.io.File;
import java.io.IOException;

public class Main {

    private static Scanner scanner;

    public static void main(String[] args) {
        /* Verifica se os caminhos dos arquivos de entra
           e saída foram passados como argumentos */
        if (args.length == 2) {
            String inputPath = args[0];
            String outputPath = args[1];

            File inputFile = new File(inputPath);

            /* Verifica se o caminho entrada é arquivo */
            if (inputFile.isFile()) {
                try {
                    /* Criando o analisador léxico */
                    scanner = new Scanner(inputFile);

                    /* Chamando a análise sintática pelo
                       símbolo inicial */
                    PROGRAMA();

                    /*Token token;
                    while ((token = scanner.nextToken()) != null) {
                        System.out.print(token);
                    }*/
                    System.out.println("\n\n-- COMPILADO COM SUCESSO --");
                } catch (SinException sin) {
                    System.out.println("\n"+sin);
                } catch (LexException lex) {
                    System.out.println("\n"+lex);
                } catch (Exception other) {
                    showHelp();
                }
            } else {
                showHelp();
            }
        } else {
            showHelp();
        }
    }

    private static void PROGRAMA() throws IOException, LexException, SinException {
        DECL();
        LISTA_DECL();
    }

    private static void LISTA_DECL() throws IOException, LexException, SinException {
        Token token = scanner.nextToken();

        if (token != null) {
            scanner.rollbackToken(token);
            PROGRAMA();
        }
    }

    private static void DECL() throws IOException, LexException, SinException {
        Token token = scanner.nextToken();
        if (checkTipo(token, TipoToken.ID)) {
            LISTA_IDS();
        } else {
            throw new SinException(token, new TipoToken[]{TipoToken.ID},
                    scanner.getLine(), scanner.getColumn());
        }
    }

    private static void LISTA_IDS() throws SinException, IOException, LexException {
        Token token = scanner.nextToken();
        if (checkTipo(token, TipoToken.VIRGULA)) {
            DECL();
        } else if (checkTipo(token, TipoToken.DOIS_PONTOS)) {
            token = scanner.nextToken();
            if (checkTipo(token, TipoToken.TIPO_VAR)) {
                //TODO: Hora bacana de gerar um código
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
}
