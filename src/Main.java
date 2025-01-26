// hey heres what i have planned
// add subroutines
// remove semicolons


import Logger.Logger;
import SalamiEvaluator.Lexer;
import SalamiEvaluator.LexerException;
import SalamiEvaluator.Parser;
import SalamiEvaluator.ParserException;
import SalamiEvaluator.types.ast.*;
import SalamiRuntime.Initializer;
import SalamiRuntime.Interpreter;
import SalamiRuntime.InterpreterException;
import SalamiRuntime.Runtime.Environment;
import SalamiRuntime.Runtime.ProgramCounter;
import SalamiRuntime.Runtime.Value;
import SalamiRuntime.Runtime.ValueException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static final Logger logger = new Logger("Main");

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
        boolean silent;
        try {silent = Boolean.parseBoolean(args[2]);} catch (IndexOutOfBoundsException e ){silent = false;}
        //System.out.println(doRepl);
        Environment env = Initializer.initialize_global_environment();
        // create the global scope
        // things like PI and stuff are there

        if (!doRepl) {
            String fileName = args[0];
            File file = new File(fileName);

            if (fileIsValid(file)){
                runFile(file, env, silent);
            }


        } else {
            // initialize scanner
            Scanner scan = new Scanner(System.in);
            String next;
            System.out.println("SalamiCode REPL v0.1");


            do {

                System.out.println(">>> ");
                next = scan.nextLine();
                if (next.equals("exit()")) break;
                try {
                    runLine(next, env, silent);
                } catch (ParserException | LexerException | InterpreterException | ValueException e) {
                    handleError(e,"repl");
                }
            } while (true);
            scan.close();
        }

        logger.yell("END OF PROGRAM");
    }
    public static void handleError(Throwable e, String file){
        System.out.println(randomMessage()+"\n\n");
        System.out.println("ERROR HAS OCCURRED WITH LOCATION '"+file+'\''+'\n');
        System.out.println(e);
        //System.out.println(e.getClass().getName());
    }
    public static void runFile(File file, Environment env, boolean silent){
        try {
            if (silent) {
                logger.silence();
                Parser.logger.silence();
                Lexer.logger.silence();
            }
            ProgramNode p = Parser.parseFile(file);
            ProgramCounter counter = new ProgramCounter(0);
            logger.log(p);
            logger.log(Interpreter.evaluate(p, env, counter, null));
            logger.log(env);
            // passes in an ast node tree and the initialized env variable
        } catch (ParserException | LexerException | FileNotFoundException | InterpreterException | ValueException e) {
            handleError(e, file.getAbsolutePath());
        }
    }

    public static void runLine(String line, Environment env, boolean silent) throws ParserException, LexerException, InterpreterException{
        if (silent) {
            logger.silence();
            Parser.logger.silence();
            Lexer.logger.silence();
        }
        ProgramCounter counter = new ProgramCounter(0);
        ProgramNode ast = Parser.parseLine(line);
        logger.log(ast);
        Value result = Interpreter.evaluate(ast, env, counter, null);
        logger.log(result);
    }

    public static boolean fileIsValid(File file){
        if (!file.exists()){
            System.out.println("File does not exist.");
            return false;
        }

//        if (!file.getAbsolutePath().endsWith(".salami") | !file.getAbsolutePath().endsWith(".sal")){
//            System.out.println("File must end with `.salami` or `.sal`");
//            return false;
//        }
        return true;
    }

    public static String randomMessage(){
        Random random = new Random();
        int index = random.nextInt(responses.length);
        return responses[index];
    }
}