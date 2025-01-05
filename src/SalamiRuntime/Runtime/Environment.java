package SalamiRuntime.Runtime;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    Map<String, Value> variables;
    Environment parent;
    public Environment (){
        variables = new HashMap<>();
    }
    public Environment (Environment p){
        variables = new HashMap<>();
        parent = p;
    }
    public Value lookupVariale(String identifier){
        final Environment environment = resolve(identifier);
        return environment.variables.get(identifier);
    }
    public Value declareVariable(String identifier, Value value) throws ValueException{
        if (hasVariable(identifier)){ throw new ValueException("Variable '" + identifier + "' is already declared within scope.");}
        variables.put(identifier, value);
        return value;
    }
    public Value forgetVariable(String identifier) throws ValueException{
        final Environment environment = resolve(identifier);
        Value v = environment.variables.get(identifier);
        environment.variables.remove(identifier);
        return v;
    }
    public Value assignVariable(String identifier, Value value) throws ValueException{
        final Environment environment = resolve(identifier);
        variables.put(identifier, value);
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
    public boolean hasVariable(String identifier){
        return variables.containsKey(identifier);
    }
}
