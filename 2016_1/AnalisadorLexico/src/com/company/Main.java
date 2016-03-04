package com.company;

public class Main {

    public static void main(String[] args) throws LexException {

        Token token ;

        while ((token = Lexico.proximoToken()) != null) {
            System.out.print(token);
        }


    }
}
