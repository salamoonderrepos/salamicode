package SalamiRuntime;

import Logger.Logger;
import SalamiEvaluator.types.ast.*;
import SalamiRuntime.Runtime.*;
import SalamiRuntime.Runtime.Method.Function;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import Logger.Timer;
import java.util.Scanner;

/**
 * Handles pre-evaluating a program for labels and subroutines.
 * @see Interpreter
 */
public class Initializer {
    public static Logger logger = new Logger("Initializer");

    /**
     * <p>
     *     Pre-evaluates a program node by predefining all labels and subroutines. This ensures someone can jump to a label defined later in the file.
     *     Because we are using the {@link Interpreter}'s <code>evaluate</code> statements, and the program we are given is the exact same as the interpreter,
     *     no label will be defined outside the maximum {@link ProgramCounter} range.
     * </p>
     *
     * @param p The program to be initialized.
     * @param environment The environment to be initialized.
     * @param startingpc A starting program counter in case we need to start from a different location.
     * @return the initialized environment.
     * @throws InterpreterException Throws an interpreter exception in case the pre-evaluation fails. (Label declaration is mangled, etc...)
     */
    public static Environment initialize_program(ProgramNode p, Environment environment, ProgramCounter startingpc) throws InterpreterException {
        Timer inittimer = new Timer("InitializerTimer");
        Environment env = environment;
        ProgramCounter initpc = startingpc;
        while (initpc.get()<p.statements.size()){
            StatementNode statement = p.statements.get(initpc.get());
            evaluate_init(statement, environment, initpc);

            initpc.increment();
        }
        logger.whisperImportant("Took "+inittimer.time()+" milliseconds");
        return env;
    }

    /**
     * Returns a new environment made for the global scope.
     * @return A new environment with global variables
     */
    public static final Environment initialize_global_environment(){
        Environment env = new Environment();
        env.declareVariable("pi", new FloatingValue(3.14159F), true);
        env.declareVariable("mathE", new FloatingValue(2.71828F), true);
        env.declareVariable("goldRatio", new FloatingValue(1.618F), true);
        env.declareVariable("true", new BooleanValue(true), true);
        env.declareVariable("false", new BooleanValue(false), true);
        env.declareLabel("start", 0);

        env.declareMethod("pow", List.of(Value.class, Value.class), (params, logger) -> {
            FloatingValue floatarg1 = FloatingValue.parseFloatingValue(params.get(0));
            FloatingValue floatarg2 = FloatingValue.parseFloatingValue(params.get(1));
            return new FloatingValue((float) Math.pow(floatarg1.value,floatarg2.value));
        });
        env.declareMethod("toString", List.of(Value.class), (params, logger) -> StringValue.parseStringValue(params.get(0)));
        env.declareMethod("toNumber", List.of(Value.class), (params, logger) -> NumberValue.parseNumberValue(params.get(0)));
        env.declareMethod("toFloat", List.of(Value.class), (params, logger) -> FloatingValue.parseFloatingValue(params.get(0)));

        env.declareMethod("rand", List.of(NumberValue.class, NumberValue.class), (params, logger) -> {
            NumberValue numarg1 = (NumberValue) params.get(0);
            NumberValue numarg2 = (NumberValue) params.get(1);
            Random numgen = new Random();
            return new NumberValue(numgen.nextDouble(numarg2.value - numarg1.value + 1) + numarg1.value);
        });
        env.declareMethod("get", List.of(StringValue.class), (params, logger) -> {
            StringValue s = (StringValue) params.get(0);
            logger.log(s.value, Logger.GREEN);
            System.out.print(">>> ");
            String v = Interpreter.reader.nextLine();
            return new StringValue(v);
        });
        env.declareMethod("winter", List.of(), (params, logger) -> {
            String poem =
            """
            Glory to the marching pines along the roadside.
            Frigid air churns each particle with sighs of desperation.
            The fire warms them up with a scarf knitted quilt.
            The petals fall with their last abscission.
            
            True, they love me.
            False, they love me not.
            The globe continued spinning.
            """;
            return new StringValue(poem);
        });



        return env;
    }

    /**
     * @param stat A statement node to be pre-evaled.
     * @param env The environment to be changed.
     * @param pc A program counter to tell label declarations where they are located.
     * @return the environment.
     * @throws InterpreterException Throws an interpreter exception if a statement is not evaluated correctly.
     */
    public static Environment evaluate_init(StatementNode stat, Environment env, ProgramCounter pc) throws InterpreterException{
        switch (stat.type){
            case LABELDECLARATIONSTATEMENT:
                Interpreter.evaluate_label_statement((LabelDeclarationStatement) stat, env, pc);
                return env;
            case SUBROUTINEDECLARATIONSTATEMENT:
                SubroutineDeclarationStatement substat = (SubroutineDeclarationStatement) stat;
                SubroutineValue sub = Interpreter.evaluate_subroutine_declaration_statement(substat, env, pc);
                sub.env = initialize_program(sub.code, sub.env, new ProgramCounter(0));
                return env;
        }
        return env;
    }
}

