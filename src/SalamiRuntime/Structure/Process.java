package SalamiRuntime.Structure;

import Helper.Logger.Logger;
import Helper.Logger.Timer;
import SalamiPackager.Packages.SalamiPackage;
import SalamiPreEvaluator.types.ast.ProgramNode;
import SalamiPreEvaluator.types.ast.StatementNode;
import SalamiRuntime.Initializer;
import SalamiRuntime.Interpreter;
import SalamiRuntime.InterpreterException;
import SalamiRuntime.RuntimeData.*;

public class Process {
    public Logger logger;
    private boolean preInitilization;
    public Environment localEnvironment;
    public ProgramNode localProgram;
    public String location;
    public Logger localProcessLogger;
    public SalamiPackage sourcePackage;
    public Process(String _location, ProgramNode salamiprogram, boolean _preInitilization){
        location = "Process/"+_location;
        logger =  new Logger(location);
        preInitilization = _preInitilization;
        localEnvironment = Initializer.initialize_global_environment();
        localProgram = salamiprogram;
        localProcessLogger = new Logger(location);
    }
    public Process(String _location, ProgramNode salamiprogram, boolean _preInitilization, Environment givenEnvironment){
        location = "Process/"+_location;
        logger =  new Logger(location);
        preInitilization = _preInitilization;
        localEnvironment = givenEnvironment;
        localProgram = salamiprogram;
        localProcessLogger = new Logger(location);
    }

    public Value begin() throws InterpreterException {
        return evaluate_program(new ProgramCounter(0));
    }

    /**
     * <p>
     *     Takes in a {@link ProgramNode} and evaluates each {@link StatementNode} in the program.
     *     Pass in an {@link Environment} and a {@link ProgramCounter} so it can properly modify variables and use <code>jump</code> statements.
     * </p>
     * @param startingpc The starting program counter, in case we wanted to start from a different program counter for any reason.
     * @return the evaluation of the last statement.
     * @throws InterpreterException If a statement fails to be evaluated, an InterpreterException will occur.
     *
     * @see Initializer
     */
    public Value evaluate_program(ProgramCounter startingpc) throws InterpreterException, ValueException, StackOverflowError{
        Timer evaltimer = new Timer(location+"ProcessTimer");

        Value eval = new VoidValue(); // initialize the eval variables
        ProgramCounter pc = startingpc;
        if (preInitilization) Initializer.initialize_program(localProgram, localEnvironment, new ProgramCounter(startingpc.get()),sourcePackage); // declare labels ahead of time
        //
        while (pc.get()<localProgram.statements.size()){
            StatementNode statement = localProgram.statements.get(pc.get());
            // here the statement will go through checks to see if this process permits it to run
            eval = Interpreter.evaluate(statement, localEnvironment, pc, localProgram, location);
            pc.increment();
        }
        localProcessLogger.whisper("Took "+evaltimer.time()+" milliseconds");

        return eval;
    }

    public Environment getLocalEnvironment() {
        return localEnvironment;
    }

    public ProgramNode getLocalProgram() {
        return localProgram;
    }

    public void setSourcePackage(SalamiPackage pack) {
        sourcePackage = pack;
    }
}
