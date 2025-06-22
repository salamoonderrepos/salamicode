package Helper.Debugger;

import SalamiPreEvaluator.LexerException;
import SalamiPreEvaluator.Parser;
import SalamiPreEvaluator.ParserException;
import SalamiPreEvaluator.types.ast.*;
import SalamiRuntime.Initializer;
import SalamiRuntime.Interpreter;
import SalamiRuntime.RuntimeData.*;
import SalamiRuntime.InterpreterException;
import SalamiRuntime.RuntimeDisruptedException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Debugger {

    // breakpoints and watchlist list
    private static final Set<Integer> breakpoints = new HashSet<>();
    private static final Set<String> watchlist = new HashSet<>();

    public static Value run(ProgramNode program, Environment env) throws InterpreterException, ValueException {
        Interpreter debuggerInterpreter = new Interpreter("Debugger");
        ProgramCounter pc = new ProgramCounter(0);
        Scanner debugInput = new Scanner(System.in);
        env = Initializer.initialize_program(program, env, new ProgramCounter(0));
        Value eval = new VoidValue();
        boolean stepping = true;  // initially stepping

        // if program counter is larger than the statements provided
        while (pc.get() < program.statements.size()) {
            StatementNode statement = program.statements.get(pc.get());

            if (!stepping && !breakpoints.contains(pc.get())) {
                // if we outside the step loop and we arent currently on any breakpoints then we evaluate the program like normal
                eval = debuggerInterpreter.evaluate(statement, env, pc, program);
                pc.increment();
                continue;
            }

            // We are in stepping or at a breakpoint
            System.out.println("[PC=" + pc.get() + "] Next: " + statement);
            printWatchedVariables(env);

            System.out.print("(debugger) > ");
            String command = debugInput.nextLine();
            String[] parts = command.split(" ");

            switch (parts[0]) {
                case "step", "" -> {
                    eval = debuggerInterpreter.evaluate(statement, env, pc, program);
                    pc.increment();
                    stepping = true;
                }
                case "continue" -> {
                    eval = debuggerInterpreter.evaluate(statement, env, pc, program);
                    pc.increment();
                    // set stepping to false so it will just continue evaluating without stopping again (unless breakpoints)
                    stepping = false;
                }
                case "break" -> {
                    if (parts.length < 2) {
                        System.out.println("Usage: break {lineNumber}");
                    } else {
                        try {
                            int breakpoint = Integer.parseInt(parts[1]);
                            breakpoints.add(breakpoint);
                            System.out.println("Breakpoint set at PC=" + breakpoint);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid breakpoint number.");
                        }
                    }
                }
                case "watch" -> {
                    if (parts.length < 2) {
                        System.out.println("Usage: watch {variableName}");
                    } else {
                        watchlist.add(parts[1]);
                        System.out.println("Now watching variable '" + parts[1] + "'");
                    }
                }
                case "set" -> {
                    if (parts.length < 3) {
                        System.out.println("Usage: set {variableName} {value}");
                    } else {
                        setVariable(env, parts[1], parts[2]);
                    }
                }
                case "print" -> {
                    if (parts.length < 2) {
                        System.out.println("Usage: print {env|pc}");
                    } else if (parts[1].equals("env")) {
                        System.out.println(env);
                    } else if (parts[1].equals("pc")) {
                        System.out.println("PC = " + pc.get());
                    } else {
                        System.out.println("Unknown print target.");
                    }
                }
                case "run" -> {
                    if (parts.length < 2) {
                        System.out.println("Usage: run {SalamiCode}");
                    } else {
                        String code = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
                        if (code.isBlank()){
                            System.out.println("No code was provided.");
                        } else {
                            try {
                                ProgramNode snippet = Parser.parseLine(code);
                                debuggerInterpreter.evaluate(snippet, env, new ProgramCounter(0), snippet);
                            } catch (ParserException | LexerException | InterpreterException e){
                                System.out.println("Error while running snippet: " +e);
                            }
                        }
                    }
                }
                case "exit" -> throw new RuntimeDisruptedException("Debugger exited manually.");
                default -> System.out.println("Unknown command.");
            }
        }

        return eval;
    }


    private static void printWatchedVariables(Environment env) {
        if (watchlist.isEmpty()) return;
        System.out.println("[Watching variables]");
        for (String var : watchlist) {
            if (env.hasVariable(var)) {
                System.out.println("  " + var + " = " + env.lookupVariale(var));
            } else {
                System.out.println("  " + var + " (undefined)");
            }
        }
    }

    private static void setVariable(Environment env, String name, String valueStr) {
        try {
            Value newValue;
            if (valueStr.matches("-?\\d+")) { // integer
                newValue = new NumberValue(Integer.parseInt(valueStr));
            } else {
                newValue = new StringValue(valueStr);
            }

            if (env.hasVariable(name)) {
                env.assignVariable(name, newValue);
                System.out.println("Set " + name + " to " + newValue);
            } else {
                System.out.println("Variable '" + name + "' doesn't exist");
            }
        } catch (Exception e) {
            System.out.println("Failed to set variable: " + e.getMessage());
        }
    }
}
