package SalamiRuntime.Manager;

import SalamiRuntime.Interpreter;
import SalamiRuntime.Runtime.Environment;

public class InterpreterFactory {

    public static Interpreter createMainInterpreter() {
        Environment global = EnvironmentFactory.createGlobalEnvironment();
        // Add core features
        return new Interpreter(global);
    }
    public static Interpreter createMainInterpreter(String location) {
        Environment global = EnvironmentFactory.createGlobalEnvironment();
        // Add core features
        return new Interpreter(location, global);
    }
    public static Interpreter createRootedInterpreter(Environment env) {
        // Add core features
        return new Interpreter(env);
    }

    public static Interpreter createBlankInterpreter() {
        Environment pluginEnv = new Environment();
        // Add plugin-specific or sandboxed rules
        return new Interpreter(pluginEnv);
    }
}
