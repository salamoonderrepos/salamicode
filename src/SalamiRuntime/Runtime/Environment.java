package SalamiRuntime.Runtime;

import SalamiEvaluator.types.ast.ProgramNode;
import SalamiRuntime.Runtime.Method.Function;
import SalamiRuntime.Runtime.Method.MethodValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>An environment holds data about variables, labels, and subroutines. It essentially is the scope.
 * It also acts as a manager for declaring, forgetting, assigning, and searching for all variables.
 * </p>
 * @see SalamiRuntime.Interpreter
 * @see Value
 */
public class Environment {
    Map<String, Value> variables;
    ArrayList<String> finals;
    Environment parent;
    Map<String, Integer> labels;
    Map<String, SubroutineValue> subroutines;
    Map<String, MethodValue> methods;
    public Environment (){
        variables = new HashMap<>();
        finals = new ArrayList<String>();
        labels = new HashMap<>();
        subroutines = new HashMap<>();
        methods = new HashMap<>();
    }
    public Environment (Environment p){
        variables = new HashMap<>();
        finals = new ArrayList<String>();
        labels = new HashMap<>();
        subroutines = new HashMap<>();
        methods = new HashMap<>();
        parent = p;
    }

    // METHOD MANAGEMENT

    public MethodValue declareMethod(String identifier, List<Class<?>> params, Function code){
        if (hasMethod(identifier)){throw new ValueException("Method attempted to declare with the name of another method in use.");}
        MethodValue methodValue = new MethodValue(params, code);
        methods.put(identifier, methodValue);
        return methodValue;
    }
    public MethodValue lookupMethod(String identifier){
        Environment env = resolveMethod(identifier);
        return env.methods.get(identifier);
    }
    public boolean hasMethod(String identifier){
        return methods.containsKey(identifier);
    }

    // LABEL MANAGEMENT
    public Integer declareLabel(String identifier, Integer pc){
        if (hasLabel(identifier)){ return assignLabel(identifier, pc);}
        labels.put(identifier, pc);
        return pc;
    }
    public Integer lookupLabel(String identifier){
        final Environment environment = resolveLabel(identifier);
        return environment.labels.get(identifier);
    }
    public Integer assignLabel(String identifier, Integer pc){
        Environment environment = resolveLabel(identifier);
        environment.labels.put(identifier, pc);
        return pc;
    }
    public boolean hasLabel(String identifier){
        return labels.containsKey(identifier);
    }

    public void forgetLabel(String identifier) {labels.remove(identifier);}


    // SUBROUTINE MANAGEMENT

    public SubroutineValue declareSubroutine(String identifier, Integer pc, ArrayList<String> params, ProgramNode code){
        if (hasSubroutine(identifier)){throw new ValueException("Subroutine '"+identifier+"' already defined within scope.");}
        SubroutineValue subroutineValue = new SubroutineValue(this, params, pc, code);
        subroutines.put(identifier, subroutineValue);
        return subroutineValue;
    }
    public SubroutineValue lookupSubroutine(String identifier){
        Environment env = resolveSubroutine(identifier);
        return env.subroutines.get(identifier);
    }
    public boolean hasSubroutine(String identifier){
        return subroutines.containsKey(identifier);
    }
    public void forgetSubroutine(String identifier) {subroutines.remove(identifier);}

    // VARIABLE MANAGEMENT

    public Value lookupVariale(String identifier){
        final Environment environment = resolve(identifier);
        return environment.variables.get(identifier);
    }
    public Value declareVariable(String identifier, Value value, boolean isFinal) throws ValueException{
        //System.out.print(value);
        if (hasVariable(identifier)){ return assignVariable(identifier, value);}
        variables.put(identifier, value);
        if (isFinal) {
            finals.add(identifier);
        }
        return value;
    }
    public boolean isVariableFinal(String identifer){
        return finals.contains(identifer);
    }

    public void forgetVariable(String identifier) throws ValueException{
        variables.remove(identifier);
    }
    public Value assignVariable(String identifier, Value value) throws ValueException{
        Environment environment = resolve(identifier);
        if (environment.isVariableFinal(identifier)){
            throw new ValueException("Cannot write to a final variable.");
        }
        environment.variables.put(identifier, value);
        return value;

    }

    public Environment resolve(String identifier){
        if (hasVariable(identifier)){
            return this;
        }
        if (parent==null){
            throw new ValueException("Variable '"+identifier+"' does not exist in any scope.");
        }

        return parent.resolve(identifier);
    }

    public Environment resolveLabel(String identifier){
        if (hasLabel(identifier)){
            return this;
        }
        throw new ValueException("Label '"+identifier+"' does not exist in current scope.");
    }

    public boolean hasVariable(String identifier){
        return variables.containsKey(identifier);
    }
    public Environment resolveSubroutine(String identifier){
        if (hasSubroutine(identifier)){
            return this;
        }
        if (parent==null){
            throw new ValueException("Subroutine '"+identifier+"' does not exist in any scope.");
        }

        return parent.resolveSubroutine(identifier);
    }
    public Environment resolveMethod(String identifier){
        if (hasMethod(identifier)){
            return this;
        }
        if (parent==null){
            throw new ValueException("Method '"+identifier+"' does not exist. Maybe you misspelled a subroutine?");
        }

        return parent.resolveMethod(identifier);
    }
    public Boolean hasMethodRecursive(String identifier){
        if (hasMethod(identifier)){
            return true;
        }
        if (parent==null){
            return false;
        }

        return parent.hasMethodRecursive(identifier);
    }

    @Override
    public String toString() {
        return "Environment{" +
                "variables=" + variables +
                ", finals=" + finals +
                ", parent=" + parent +
                ", labels=" + labels +
                '}';
    }
}
