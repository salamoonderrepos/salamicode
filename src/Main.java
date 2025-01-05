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
import SalamiEvaluator.types.ast.*;
import SalamiRuntime.Interpreter;
import SalamiRuntime.InterpreterException;
import SalamiRuntime.Runtime.Environment;
import SalamiRuntime.Runtime.Value;
import SalamiRuntime.Runtime.ValueException;

import java.io.File;
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
            "5183 FATAL ERRORS OCCURED. DELETING LINKEDIN PROFILE :(",
    };
    public static void main(String[] args) {
        if (args.length == 0) { // if no filename is provided
            System.out.println("Provide a file to process.");
            return;
        }


        boolean doRepl = Boolean.parseBoolean(args[1]);
        System.out.println(doRepl);
        Environment env = Interpreter.initialize();
        // create a specially made environment
        // this is for predefined variables like "pi" and stuff
        if (!doRepl) {
            String fileName = args[0];
            File file = new File(fileName);

            if (!file.exists()){
                System.out.println("File does not exist.");
                return;
            }
            //System.out.println(fileName);
            //if (!fileName.endsWith(".salami") | !fileName.endsWith(".sal")){
            //    System.out.println("File must end with `.salami` or `.sal`");
            //    return;
            //}
            try {
                ProgramNode p = Parser.parseFile(file);
                System.out.println(p);
                System.out.println(Interpreter.evaluate(p, env));
                // passes in a ast node tree and the initialized env variable
            } catch (ParserException | LexerException | FileNotFoundException | InterpreterException | ValueException e) {
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
                    Value result = Interpreter.evaluate(ast, env);
                    System.out.println(result);
                } catch (ParserException | LexerException | InterpreterException | FileNotFoundException | ValueException e) {
                    System.out.println(randomMessage());
                    System.out.println("ERROR HAS OCCURRED INTERPRETING REPL\n");
                    System.out.println(e.getMessage());
                }
            } while (true);
            scan.close();
        }
    }
    public static String randomMessage(){
        Random random = new Random();
        int index = random.nextInt(responses.length);
        return responses[index];
    }
}