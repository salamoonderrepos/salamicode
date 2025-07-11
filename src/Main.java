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
import SalamiPackager.Packages.PackageException;
import SalamiPackager.Packages.Packager;
import SalamiPackager.Packages.SalamiPackage;
import SalamiRuntime.Initializer;
import SalamiRuntime.Interpreter;
import SalamiRuntime.InterpreterException;
import SalamiRuntime.RuntimeData.Environment;
import SalamiRuntime.RuntimeData.ProgramCounter;
import SalamiRuntime.RuntimeData.Value;
import SalamiRuntime.RuntimeData.ValueException;
import Structure.BaseException;
import Structure.Process;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.text.SimpleDateFormat;

import Helper.Logger.Timer;
import SalamiRuntime.RuntimeDisruptedException;

public class Main {
    static final Logger logger = new Logger("Main");
    static final String version = "1.8.0";
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
            "DUDE WHAT EVEN IS THIS",
            "PACK IT UP",
            "IT'S BETTER THAN JAVASCRIPT...",
            "10% NEW FEATURES, 90% REFACTORING",
            "DEBUG PRINT TO THE RESCUE",
            "DID YOU MEAN FOR THAT TO HAPPEN?",
            "THAT WAS INTENTIONAL RIGHT?",
            "MHM. YEP. MY NOTES HERE SAY THIS IS UNEXPECTED. SORRY.",
            "YOU MISSED SOMETHING ON LINE 18"
    };

    public static void main(String[] args) {
        Timer maintimer = new Timer("MainTimer");

        HashMap<Object, String> argMap = new HashMap<>();
        List<String> flags = new ArrayList<>();
        String currentKey = null;

        // Parse args into a map: --key value OR --key (boolean)
        for (String arg : args) {
            if (arg.startsWith("--")) {
                currentKey = arg;
                flags.add(arg);
            } else if (currentKey != null) {
                argMap.put(currentKey, arg); // override with value if given
                currentKey = null;
            }
        }

        boolean silent = flags.contains("--silent");
        Logger.doColor = !flags.contains("--monochrome");
        boolean doRepl = flags.contains("--repl");
        boolean debugger = flags.contains("--debug");;
        if (flags.contains("--nopretty")) {logger.prettify();} else {logger.deprettify();};

        if (silent) {
            logger.silence();
            Parser.logger.silence();
            Lexer.logger.silence();
            Packager.logger.silence();

            Initializer.logger.silence();
            Interpreter.logger.silence();
        }

        if (!argMap.containsKey("--file")){
            String javaVersion = System.getProperty("java.version");
            String javaVendor = System.getProperty("java.vendor");

            // OS Info
            String osName = System.getProperty("os.name");
            String osVersion = System.getProperty("os.version");
            String osArch = System.getProperty("os.arch");

            // Date & Time
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            String dateTime = format.format(now);


            System.out.println("SalamiCode V"+version+" ("+osArch+"-"+osName+" V"+osVersion+" )");
            System.out.println("Java "+javaVersion+" with "+javaVendor+" on "+dateTime);
            System.out.println("\nUsage: salamicode [flags]\n");
            System.out.println("""
            Where flags include:
                --file <path-to-file>
                    File to process
                --silent
                    Silence whisper logs and timers
                --monochrome
                    Run without ANSI color coding
                --repl
                    Disregards `--file` and opts into the REPL
                --debug
                    Runs the file with the debug stepper
                --nopretty
                    Objects are not automatically formatted when logged
            """);
            return;
        }

        String fileName = argMap.get("--file");

        if (fileName==null) { // if no filename is provided
            System.out.println("Provide a file to process.");
            return;
        }

        File file = new File(fileName);

        if (!fileIsValid(file)){
            throw new MainException("Given file is not valid.");
        }






        Environment env = Initializer.initialize_global_environment();

        if (!true == true){
            SalamiPackage test = Packager.unzipPackage(Packager.findFileToLoad("math"));
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
            } catch (ParserException | LexerException | FileNotFoundException | InterpreterException |
                     StackOverflowError |
                     PackageException e) {
                handleError(e, file.getAbsolutePath());
                logger.yell("END OF PROGRAM (took "+(maintimer.time())+" miliseconds.) (Interrupted) (Debugger)");
                return;
            }
        }
        try {

            runFile(file, silent);
            logger.yell("END OF PROGRAM (took "+(maintimer.time())+" miliseconds.)");
        } catch (ParserException | LexerException | FileNotFoundException | InterpreterException | StackOverflowError |
                 PackageException e) {
            handleError(e, file.getAbsolutePath());
            logger.yell("END OF PROGRAM (took "+(maintimer.time())+" miliseconds.) (Interrupted)");
        }

    }
    public static void handleError(Throwable e, String file){
        Logger errorLogger = new Logger("ErrorLog");
        errorLogger.print(randomMessage()+"\n",Logger.RED);
        errorLogger.print("AN ERROR HAS OCCURRED WITHIN FILE '"+file+'\'',Logger.GRAY);
        if (e instanceof BaseException) {

            BaseException runtimeexcp = (BaseException) e;
            if (runtimeexcp.getLocation()!= null){
                System.out.println(Logger.colorize(Logger.GRAY, "ON LINE "+runtimeexcp.getLocationLineNumber()+" COLUMN "+ runtimeexcp.getLocationColumnNumber()));
            }
        }

        errorLogger.print(e.toString()+'\n',Logger.RED);


        //System.out.println(e.getClass().getName());
    }
    public static void runFile(File file, boolean silent) throws ParserException, LexerException, InterpreterException, FileNotFoundException, ValueException, StackOverflowError, RuntimeDisruptedException{
        ProgramNode p = Parser.parseFile(file);
        ProgramCounter counter = new ProgramCounter(0);
        Process fileProcess = new Process("Main", p, true, silent);
        //logger.log(p);
        Runtime runtime = Runtime.getRuntime();
        //long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        //logger.log(usedMemory / (1024 * 1024));
        logger.log(fileProcess.begin());

        //logger.log(fileProcess.getLocalEnvironment());
        // passes in an ast node tree and the initialized env variable
    }
    public static void runDebugger(File file, Environment env) throws ParserException, LexerException, InterpreterException, FileNotFoundException, ValueException, StackOverflowError, RuntimeDisruptedException{
        ProgramNode p = Parser.parseFile(file);
        ProgramCounter counter = new ProgramCounter(0);
        Debugger.run(p, env, "DebuggerMain");
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
            } catch (ParserException | LexerException | InterpreterException e) {
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