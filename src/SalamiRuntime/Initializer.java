package SalamiRuntime;

import Helper.Logger.Logger;
import Helper.Parameters.EnvironmentParameters;
import SalamiEvaluator.*;
import SalamiEvaluator.types.ast.*;
import SalamiRuntime.Manager.InterpreterFactory;
import SalamiRuntime.Runtime.*;

import java.util.List;
import java.util.Random;
import Helper.Logger.Timer;

/**
 * Handles pre-evaluating a program for labels and subroutines.
 * @see Interpreter
 */
public class Initializer {
    public static Logger logger = new Logger("Initializer");
    public static Interpreter initializer_interpreter = InterpreterFactory.createMainInterpreter("Initializer");

    /**
     * <p>
     *     Pre-evaluates a program node by predefining all labels and subroutines. This ensures someone can jump to a label defined later in the file.
     *     Because we are using the {@link Interpreter}'s <code>evaluate</code> statements, and the program we are given is the exact same as the interpreter,
     *     no label will be defined outside the maximum {@link ProgramCounter} range.
     * </p>
     *
     * @param p The program to be initialized.
     * @param startingpc A starting program counter in case we need to start from a different location.
     * @return the initialized environment.
     * @throws InterpreterException Throws an interpreter exception in case the pre-evaluation fails. (Label declaration is mangled, etc...)
     */
    public static Interpreter initialize_program(ProgramNode p, Interpreter interpreter, ProgramCounter startingpc) throws InterpreterException {
        Timer inittimer = new Timer("InitializerTimer");
        Environment env = interpreter.getLocalEnvironment();
        ProgramCounter initpc = startingpc;
        while (initpc.get()<p.statements.size()){
            StatementNode statement = p.statements.get(initpc.get());
            evaluate_init(statement, interpreter, initpc);

            initpc.increment();
        }
        logger.whisper("Took "+inittimer.time()+" milliseconds");
        return interpreter;
    }



    /**
     * @param stat A statement node to be pre-evaled.
     * @param pc A program counter to tell label declarations where they are located.
     * @return the environment.
     * @throws InterpreterException Throws an interpreter exception if a statement is not evaluated correctly.
     */
    public static Interpreter evaluate_init(StatementNode stat, Interpreter interpreter, ProgramCounter pc) throws InterpreterException{
        switch (stat.type){
            case LABELDECLARATIONSTATEMENT:
                interpreter.evaluate_label_statement((LabelDeclarationStatement) stat, pc);
                return interpreter;
            case SUBROUTINEDECLARATIONSTATEMENT:
                SubroutineDeclarationStatement substat = (SubroutineDeclarationStatement) stat;
                SubroutineValue sub = interpreter.evaluate_subroutine_declaration_statement(substat, pc);
                sub.env = initialize_program(sub.code, interpreter, new ProgramCounter(0)).getLocalEnvironment();
                return interpreter;
            case PORTSTATEMENT:
                interpreter.evaluate_port_statement((PortStatement) stat, pc);
                return interpreter;
        }
        return interpreter;
    }
}

