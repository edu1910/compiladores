package br.com.ceducarneiro;

public class Main {

    public static void main(String[] args) throws LexException {
        Token token;

	    while ((token = Lexico.nextToken()) != null) {
            System.out.print(token);
        }
    }
}
