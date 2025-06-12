package SalamiRuntime.Manager;

import Helper.Logger.Logger;
import Helper.Parameters.EnvironmentParameters;
import SalamiEvaluator.LexerException;
import SalamiEvaluator.Parser;
import SalamiEvaluator.ParserException;
import SalamiEvaluator.types.ast.ProgramNode;
import SalamiRuntime.Interpreter;
import SalamiRuntime.InterpreterException;
import SalamiRuntime.Runtime.*;
import SalamiRuntime.RuntimeDisruptedException;

import java.util.List;
import java.util.Random;

public class EnvironmentFactory {
    /**
     * Returns a new environment made for the global scope.
     * @return A new environment with global variables
     */
    public static final Environment createGlobalEnvironment(){
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
            Interpreter interpreter = InterpreterFactory.createMainInterpreter();
            StringValue s = (StringValue) params.get(0);
            logger.log(s.value, Logger.GREEN);
            System.out.print(">>> ");
            String v = interpreter.reader.nextLine();
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
                Environment modenv = EnvironmentFactory.createGovernedEnvironment(new EnvironmentParameters(
                        ((BooleanValue) array.getArrayValue(0)).value,
                        ((BooleanValue) array.getArrayValue(1)).value,
                        ((BooleanValue) array.getArrayValue(2)).value,
                        ((BooleanValue) array.getArrayValue(3)).value,
                        ((BooleanValue) array.getArrayValue(4)).value,
                        ((BooleanValue) array.getArrayValue(5)).value,
                        ((BooleanValue) array.getArrayValue(6)).value
                ));
                Interpreter interpreter = InterpreterFactory.createRootedInterpreter(modenv);
                return interpreter.evaluate(p, new ProgramCounter(0), p);

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
                Interpreter interpreter = InterpreterFactory.createMainInterpreter();
                return interpreter.evaluate(p, new ProgramCounter(0), p);
            } catch (Error e){
                throw new ValueException("String parsed with an error. `"+e.getMessage()+"`");
            } catch (ParserException | LexerException | InterpreterException e) {
                throw new RuntimeDisruptedException(e.getMessage());
            }
        });



        return env;
    }

    private static Environment createGovernedEnvironment(EnvironmentParameters environmentParameters) {
        Environment global = createGlobalEnvironment();
        return new Environment(global, environmentParameters);
    }
}
