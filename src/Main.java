// recursion wont work
// the environment passed into a subroutine has a null methods because it is never declared
// dont know why
// fix this please
// @noah


import Helper.Debugger.Debugger;
import Helper.Logger.Logger;
import SalamiPreEvaluator.Lexer;
import SalamiPreEvaluator.LexerException;
import SalamiPreEvaluator.Parser;
import SalamiPreEvaluator.ParserException;
import SalamiPreEvaluator.types.ast.*;
import SalamiPackager.PackageException;
import SalamiPackager.Packager;
import SalamiPackager.Packages.SalamiPackage;
import SalamiRuntime.Initializer;
import SalamiRuntime.Interpreter;
import SalamiRuntime.InterpreterException;
import SalamiRuntime.RuntimeData.Environment;
import SalamiRuntime.RuntimeData.ProgramCounter;
import SalamiRuntime.RuntimeData.Value;
import SalamiRuntime.RuntimeData.ValueException;
import SalamiRuntime.Structure.Process;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import Helper.Logger.Timer;
import SalamiRuntime.RuntimeDisruptedException;

import java.util.Scanner;

public class Main {
    static final Logger logger = new Logger("Main");
    static final String version = "1.7.2";
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
            "I THINK SOMETHING HAPPENED...",
            "DONT WORRY. EMPATHY BANANA IS HERE FOR YOU.",
            "THERES A WIKI YKNOW...",
            "YOU WROTE THAT? AND YOU THOUGHT IT WOULD WORK?",
            "RUN IT AGAIN THEN IT WILL WORK TRUST ME",
            "SO, UH, YOU COME HERE OFTEN?",
            "LOOK AT ME! LA DI DA DI DA!",
            "YEAH MAN YOU BETTER CUT YOUR LOSSES...",
            "NOT YOUR CUP OF TEA EH?",
            "EVEN I CANT READ YOUR SPAGHETTI CODE",
    };

    public static void main(String[] args) {
        Timer maintimer = new Timer("MainTimer");
        boolean silent;
        boolean debugger;
        boolean doRepl;

        if (args.length<1){
            System.out.println("SalamiCode V"+version+" \nUsage: (filename) {--silent} {--repl} {--monochrome} {--nolint}");
            return;
        }

        String fileName = null;
        fileName = args[0];

        if (fileName==null) { // if no filename is provided
            System.out.println("Provide a file to process.");
            return;
        }

        File file = new File(fileName);
//        System.out.println(System.getProperty("user.dir"));
//        if (!file.isAbsolute()){
//            file = new File(System.getProperty("user.dir"), fileName);
//        }


        if (!fileIsValid(file)){
            throw new MainException("Given file is not valid.");
        }


        Logger.doColor = true;
        doRepl = false;
        debugger = false;
        silent = false;
        logger.prettify();
        for (String arg : args) {
            if (arg.equals("--debug")) {
                debugger = true;
            } else if (arg.equals("--silent")) {
                silent = true;
            }else if (arg.equals("--repl")) {
                doRepl = true;
            }else if (arg.equals("--monochrome")) {
                Logger.doColor = false;
            }else if (arg.equals("--nolint")){
                logger.deprettify();
            }

        }

        if (silent) {
            logger.silence();
            Parser.logger.silence();
            Lexer.logger.silence();
            Packager.logger.silence();
            Initializer.logger.silence();
            Interpreter.logger.silence();
        }

        Environment env = Initializer.initialize_global_environment();

        if (!true == true){
            SalamiPackage test = Packager.unzipPackage(Packager.findPackage("math"));
            //Environment packagedenv = Packager.loadPackage(test, env, new Interpreter());
            logger.log("Boilerplate stuff");

            return;
        }

        if (doRepl) {
            runRepl(file, env);
            return;
        }
        if (debugger){
            try {
                runDebugger(file, env);
                logger.yell("END OF PROGRAM (took "+(maintimer.time())+" miliseconds.) (Debugger)");
                return;
            } catch (ParserException | LexerException | FileNotFoundException | InterpreterException | ValueException | StackOverflowError | RuntimeDisruptedException |
                     PackageException e) {
                handleError(e, file.getAbsolutePath());
                logger.yell("END OF PROGRAM (took "+(maintimer.time())+" miliseconds.) (Interrupted) (Debugger)");
                return;
            }
        }
        try {

            runFile(file);
            logger.yell("END OF PROGRAM (took "+(maintimer.time())+" miliseconds.)");
        } catch (ParserException | LexerException | FileNotFoundException | InterpreterException | ValueException | StackOverflowError | RuntimeDisruptedException |
                 PackageException e) {
            handleError(e, file.getAbsolutePath());
            logger.yell("END OF PROGRAM (took "+(maintimer.time())+" miliseconds.) (Interrupted)");
        }

    }
    public static void handleError(Throwable e, String file){
        System.out.println(Logger.colorize(Logger.RED, randomMessage()+"\n"));
        System.out.println(Logger.colorize(Logger.GRAY, "ERROR HAS OCCURRED WITH LOCATION '"+file+'\''));
        System.out.println(Logger.colorize(Logger.RED, e)+'\n');
        //System.out.println(e.getClass().getName());
    }
    public static void runFile(File file) throws ParserException, LexerException, InterpreterException, FileNotFoundException, ValueException, StackOverflowError, RuntimeDisruptedException{
        ProgramNode p = Parser.parseFile(file);
        ProgramCounter counter = new ProgramCounter(0);
        Process fileProcess = new Process("Main", p, true);
        logger.log(p);
        logger.log(fileProcess.begin());
        logger.log(fileProcess.getLocalEnvironment());
        // passes in an ast node tree and the initialized env variable
    }
    public static void runDebugger(File file, Environment env) throws ParserException, LexerException, InterpreterException, FileNotFoundException, ValueException, StackOverflowError, RuntimeDisruptedException{
        ProgramNode p = Parser.parseFile(file);
        ProgramCounter counter = new ProgramCounter(0);
        Debugger.run(p, env);
    }

    public static void runRepl(File file, Environment env) {

        // initialize scanner
        Scanner scan = new Scanner(System.in);
        String next;
        System.out.println("SalamiCode REPL v0.1");


        do {

            System.out.println(">>> ");
            next = scan.nextLine();
            if (next.equals("exit()")) break;
            try {
                runLine(next);
            } catch (ParserException | LexerException | InterpreterException | ValueException e) {
                scan.close();
                handleError(e, "repl");
            }
        } while (true);
        scan.close();
    }
    public static void runLine(String line) throws ParserException, LexerException, InterpreterException{

        ProgramCounter counter = new ProgramCounter(0);
        ProgramNode ast = Parser.parseLine(line);
        Environment jasonmylittlebabyboy = Initializer.initialize_global_environment();
        logger.log(ast);
        Value result = Interpreter.evaluate(ast, jasonmylittlebabyboy, counter, null, "MainLine");
        logger.log(result);
    }

    public static boolean fileIsValid(File file){
        if (!file.exists()){
            System.out.println("File does not exist.");
            return false;
        }
        if (!(file.getName().endsWith(".salami") || file.getName().endsWith(".sal"))){
            System.out.println("File must end with `.salami` or `.sal`");
            return false;
        }
        return true;
    }

    public static String randomMessage(){
        Random random = new Random();
        int index = random.nextInt(responses.length);
        return responses[index];
    }
}