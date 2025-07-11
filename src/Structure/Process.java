package Structure;

import Helper.Logger.Logger;
import Helper.Logger.Timer;
import SalamiPackager.Packages.SalamiPackage;
import SalamiPreEvaluator.types.ast.ProgramNode;
import SalamiPreEvaluator.types.ast.StatementNode;
import SalamiRuntime.Initializer;
import SalamiRuntime.Interpreter;
import SalamiRuntime.InterpreterException;
import SalamiRuntime.RuntimeData.*;
import SalamiRuntime.RuntimeDisruptedException;

import java.util.Arrays;

public class Process {
    private boolean preInitilization;
    public Environment localEnvironment;
    public ProgramNode localProgram;
    public String location;
    public Logger localProcessLogger;
    public SalamiPackage sourcePackage;
    public Process(String _location, ProgramNode salamiprogram, boolean _preInitilization, boolean silent){
        location = "Process/"+_location;
        preInitilization = _preInitilization;
        localEnvironment = Initializer.initialize_global_environment();
        localProgram = salamiprogram;
        localProcessLogger = new Logger(location);
        if (silent){
            localProcessLogger.silence();
        }
    }
    public Process(String _location, ProgramNode salamiprogram, boolean _preInitilization, Environment givenEnvironment, boolean silent){
        location = "Process/"+_location;
        preInitilization = _preInitilization;
        localEnvironment = givenEnvironment;
        localProgram = salamiprogram;
        localProcessLogger = new Logger(location);
        if (silent){
            localProcessLogger.silence();
        }
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

            // catch an error and rethrow with location
            try {
                eval = Interpreter.evaluate(statement, localEnvironment, pc, localProgram, location);
            } catch (InterpreterException e){
                localProcessLogger.print("Runtime stopped abruptly and we don't really know where :(", Logger.GRAY);
                localProcessLogger.print("!! Error caught in running process "+location, Logger.PURPLE);
                localProcessLogger.print("The last statement evaluated in "+location+": " + Arrays.toString(statement.getLocationFromFile()), Logger.GREEN);
                throw e;
            }
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
