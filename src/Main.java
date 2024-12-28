// ok ive researched. thigns i need todo:
// create a lexer
// create a tree format system
// create a parser
// create a evaluater or something like that

// for the lexer:
// make tokens that actually do stuff
// implement custom function writing for a more modular system

import SalamiEvaluator.Lexer;
import SalamiEvaluator.LexerException;
import SalamiEvaluator.Parser;
import SalamiEvaluator.ParserException;
import SalamiEvaluator.types.ast.ExpressionNode;
import SalamiEvaluator.types.ast.NumericalLiteralNode;
import SalamiEvaluator.types.ast.ProgramNode;
import SalamiEvaluator.types.ast.VoidLiteralNode;
import SalamiRuntime.Interpreter;
import SalamiRuntime.Runtime.Value;

import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static final String[] responses = {
            "WHOOPS. MY BAD.",
            "SORRY...",
            "SOMETHING WENT FATALLY AWRY!",
            "DID I DO THAT?",
            "JUST CODE BETTER NEXT TIME",
            "THE LEXER WANTS TO TALK TO YOU",
            "THE PARSER WANTS TO TALK TO YOU",
            "LOL",
            "OOOF YOU WERE SO CLOSE THAT TIME",
            "TRY RUNNING IT AGAIN",
            "SCRATCH THAT. IT DIDNT WORK.",
            "CTRL-Z MY FRIEND",
            "YOU LOOK LONELY",
            "ISN'T THIS FUN?",
            "I THINK YOU FORGOT SOMETHING THERE",
            "HA-HA IM NOT GOING TO TELL YOU WHAT HAPPENED. JUST KIDDING.",
            "EEK!",
            "DID YOU THINK THAT WOULD WORK??",
            "DELETING SYSTEM32... JUST KIDDING. UNLESS >:)",
            "BUT IT REFUSED!",
            "THIS IS WORSE THAN CYBERPUNK ON RELEASE",
            "OWCH! THAT ONE HURT :(",
            "I THINK IM GONNA... BLEUAHHHHHH... ITS SO.. YOUR CODE IS JUST SO BAD... HEUUGHH..",
            "TOO HARD DADDY", // im sorry
            "5183 FATAL ERRORS OCCURED. DELETING LINKEDIN PROFILE :(",
            "so sad 10 lik for part 2 :>",
            "WAS AT HOUSE CODING GAME WHEN PHONE RING \"YOUR CODE IS KIL\" \"YES\""
    };
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java Main <enableRepl> <filename>");
            return;
        }


        boolean doRepl = Boolean.parseBoolean(args[0]);
        System.out.println(doRepl);

        if (!doRepl) {
            String fileName = args[1];
            try {
                System.out.println(Parser.parseFile(fileName));
            } catch (ParserException | LexerException | FileNotFoundException e) {
                System.out.println(randomMessage());
                System.out.println("ERROR HAS OCCURRED INTERPRETING A FILE\n");
                System.out.println(e.getMessage());
            }
        } else {
            Scanner scan = new Scanner(System.in);
            String next;
            System.out.println("SalamiCode REPL v0.1");
            do {
                System.out.println(">>> ");
                next = scan.nextLine();
                if (next.equals("exit()")) break;
                try {
                    ProgramNode ast = Parser.parseLine(next);
                    System.out.println(ast);
                    //Value result = Interpreter.evaluate(new NumericalLiteralNode(69420f));
                    //System.out.println(result);
                } catch (ParserException | LexerException | FileNotFoundException e) {
                    System.out.println(randomMessage());
                    System.out.println("ERROR HAS OCCURRED INTERPRETING REPL\n");
                    System.out.println(e.getMessage());
                }
            } while (true);
            scan.close();
        }

        System.out.println(Interpreter.evaluate(new VoidLiteralNode()));
    }
    public static String randomMessage(){
        Random random = new Random();
        int index = random.nextInt(responses.length);
        return responses[index];
    }
}