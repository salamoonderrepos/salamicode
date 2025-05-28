package SalamiRuntime;

import Helper.Logger.Logger;
import Helper.Parameters.EnvironmentParameters;
import SalamiEvaluator.*;
import SalamiEvaluator.types.ast.*;
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
    public static Interpreter initializer_interpreter = new Interpreter("initializer");

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
        logger.whisper("Took "+inittimer.time()+" milliseconds");
        return env;
    }

    /**
     * Returns a new environment made for the global scope.
     * @return A new environment with global variables
     */
    public static final Environment initialize_global_environment(){
        Environment env = new Environment();
        env.declareVariable("true", new BooleanValue(true), true);
        env.declareVariable("false", new BooleanValue(false), true);
        env.declareLabel("start", 0);

        env.declareMethod("pow", List.of(Value.class, Value.class), (params, logger) -> {
            FloatingValue floatarg1 = FloatingValue.parseFloatingValue(params.get(0));
            FloatingValue floatarg2 = FloatingValue.parseFloatingValue(params.get(1));
            return new FloatingValue((float) Math.pow(floatarg1.value,floatarg2.value));
        });
        env.declareMethod("toString", List.of(Value.class), (params, logger) -> StringValue.parseStringValue(params.get(0)));
        env.declareMethod("toNumber", List.of(Value.class), (params, logger) -> {
            return NumberValue.parseNumberValue(params.get(0));
        });
        env.declareMethod("toFloat", List.of(Value.class), (params, logger) -> {
            return FloatingValue.parseFloatingValue(params.get(0));
        });

        env.declareMethod("rand", List.of(NumberValue.class, NumberValue.class), (params, logger) -> {
            NumberValue numarg1 = (NumberValue) params.get(0);
            NumberValue numarg2 = (NumberValue) params.get(1);
            int numarg1value = (int) numarg1.value;
            int numarg2value = (int) numarg2.value;
            Random numgen = new Random();
            return new NumberValue(numgen.nextInt(numarg2value - numarg1value + 1) + numarg1value);
        });
        env.declareMethod("get", List.of(StringValue.class), (params, logger) -> {
            StringValue s = (StringValue) params.get(0);
            logger.log(s.value, Logger.GREEN);
            System.out.print(">>> ");
            String v = initializer_interpreter.reader.nextLine();
            return new StringValue(v);
        });
        //env.declareMethod("throw", List.of(StringValue.class), (params, logger) -> {
        //    StringValue s = (StringValue) params.get(0);
        //    throw new RuntimeDisruptedException(s.value);
        //    //return new VoidValue();
        //});
        env.declareMethod("salami", List.of(), (params, logger) -> {
            String poem =
            """
            That's me!
            """;
            return new StringValue(poem);
        });
        env.declareMethod("version", List.of(), (params, logger) -> {
            String poem =
                    """
                    SalamiCode V1.7.3
                    """;
            return new StringValue(poem);
        });
        env.declareMethod("governedparse", List.of(StringValue.class, ArrayValue.class), (params, logger) -> {
            StringValue s = (StringValue) params.get(0);
            ArrayValue array = (ArrayValue) params.get(1);
            try {
                ProgramNode p = Parser.parseLine(s.value);
                Environment modenv = initialize_global_environment();
                modenv = new Environment(modenv, new EnvironmentParameters(
                        ((BooleanValue) array.getArrayValue(0)).value,
                        ((BooleanValue) array.getArrayValue(1)).value,
                        ((BooleanValue) array.getArrayValue(2)).value,
                        ((BooleanValue) array.getArrayValue(3)).value,
                        ((BooleanValue) array.getArrayValue(4)).value,
                        ((BooleanValue) array.getArrayValue(5)).value,
                        ((BooleanValue) array.getArrayValue(6)).value
                        ));
                return initializer_interpreter.evaluate(p,modenv, new ProgramCounter(0), p);

            } catch (IndexOutOfBoundsException e){
                throw new ValueException("Array given does not match required parameters to parse.");
            } catch (Error e){
                throw new ValueException("String parsed with an error. `"+e.getMessage()+"`");
            } catch (ParserException | LexerException | InterpreterException e) {
                throw new RuntimeDisruptedException(e.getMessage());
            }
        });
        env.declareMethod("parse", List.of(StringValue.class), (params, logger) -> {
            StringValue s = (StringValue) params.get(0);
            try {
                ProgramNode p = Parser.parseLine(s.value);
                Environment modenv = initialize_global_environment();
                return initializer_interpreter.evaluate(p,modenv, new ProgramCounter(0), p);
            } catch (Error e){
                throw new ValueException("String parsed with an error. `"+e.getMessage()+"`");
            } catch (ParserException | LexerException | InterpreterException e) {
                throw new RuntimeDisruptedException(e.getMessage());
            }
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
                initializer_interpreter.evaluate_label_statement((LabelDeclarationStatement) stat, env, pc);
                return env;
            case SUBROUTINEDECLARATIONSTATEMENT:
                SubroutineDeclarationStatement substat = (SubroutineDeclarationStatement) stat;
                SubroutineValue sub = initializer_interpreter.evaluate_subroutine_declaration_statement(substat, env, pc);
                sub.env = initialize_program(sub.code, sub.env, new ProgramCounter(0));
                return env;
            case PORTSTATEMENT:
                initializer_interpreter.evaluate_port_statement((PortStatement) stat, env, pc);
                return env;
        }
        return env;
    }
}

