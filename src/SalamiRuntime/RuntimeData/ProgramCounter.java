package SalamiRuntime.RuntimeData;

/**
 * A program counter manager. Instantiation creates a new counter which can be incremented, set, and get. Helps keep track of counters on a global scope.
 * @see SalamiRuntime.Interpreter#evaluate_program
 */
public class ProgramCounter {
    Integer internal;
    public ProgramCounter(Integer inter){
        internal = inter;
    }
    public Integer increment(){
        internal++;
        return internal;
    }
    public Integer set(Integer b){
        internal = b;
        return internal;
    }
    public Integer get(){
        return internal;
    }
}
