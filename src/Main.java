// ok ive researched. thigns i need todo:
// create a lexer
// create a tree format system
// create a parser
// create a evaluater or something like that

// for the lexer:
// make tokens that actually do stuff
// implement custom function writing for a more modular system

import SalamiEvaluator.Lexer;

public class Main {
    public static void main(String[] args) {
        Lexer l = new Lexer();
        l.lex();

    }
}