// ok ive researched. thigns i need todo:
// create a lexer
// create a tree format system
// create a parser
// create a evaluater or something like that

// for the lexer:
// make tokens that actually do stuff
// implement custom function writing for a more modular system

import SalamiEvaluator.Lexer;
import SalamiEvaluator.Parser;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Main <filename>");
            return;
        }

        String fileName = args[0];


        Parser p = new Parser(new Lexer(fileName));
        p.parse();

    }
}