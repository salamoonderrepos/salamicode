import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class SalamiInterpreter {
    public static final String HOME = "";
    public static final String FILE_DIR = "assets\\source\\";
    public static List<Token<?>> stack = new ArrayList<>();
    public static void log(Object t){
        System.out.println("[LOG] "+t.toString());
    }
    public static void log(Object t, String ext){
        System.out.println("[LOG] "+"["+ext+"] "+t.toString());
    }
    // tokenize function
    public static List<Token<?>> tokenize(String foo){
        // split the string by whitespace
        String[] parts = foo.split("\\s+");


        // create an array for the tokens
        List<Token<?>> tokens = new ArrayList<>();

        // loop over each part of the split string
        // ex. 3, 5, +
        for (String part : parts) {
            // try to parse as an integer, otherwise treat as operator
            try {
                // parse integer
                int intValue = Integer.parseInt(part);
                // add it to the tokens array as an integer
                tokens.add(new Token<>(intValue)); // Token<Integer>
            } catch (NumberFormatException e) {
                // add it to the tokens array as a string
                // TODO have other type of "Operator" for better function control
                tokens.add(new Token<>(part)); // Token<String>
            }
        }
        return tokens;
    }
    public static Integer eval(String file_name){
        try {
            System.out.println();
            File file = new File(FILE_DIR + file_name);
            //System.out.println("Absolute path: " + file.getAbsolutePath());

            // create a new scanner
            Scanner scanner = new Scanner(file);

            // while the scanner can, well, scan, then tokenize each line and evaluate them
            while (scanner.hasNextLine()) {
                // clear the stack in case it had been used already
                stack.clear();

                // get the next line
                String line = scanner.nextLine();
                // empty lines are allowed
                if (line==""){
                    continue;
                }
                // debug print it
                System.out.println(line);

                // tokenize it
                List<Token<?>> tokens = tokenize(line);

                // loop over each token
                for (Token<?> t : tokens){
                    // if the token is an integer, then add it to the stack
                    if (t.isInt()){
                        stack.add(t);
                        //System.out.println(t.data);
                    } else if (t.dataEquals("+")){ // else if the token is an operator
                        // (specifically the +) then do what the operator does.
                        // again todo please add operator class so we can extend this

                        // declare rslt as 0
                        Integer rslt = 0;

                        // loop over all numbers in the stack (3 2 9 1 +, would be looping over 3, 2, 9, and 1)
                        for (int i=stack.size()-1; i>=0; i--){
                            //log(i,"i");
                            // get the token
                            Token<?> tokentoadd = stack.remove(i);
                            // add the tokens value to the result
                            rslt += (Integer) tokentoadd.getValue();
                        }
                        // create a new token for the result
                        Token<Integer> r = new Token<Integer>(rslt);
                        // add it to the stack
                        stack.add(r);
                    } else {
                        System.out.println("Error: Insufficient Stack Data");
                        return 0;
                    }
                }

                System.out.println(stack.get(0).getValue());

            }

            // stop the scanner cuz the while loop said its over
            scanner.close();

        } catch (FileNotFoundException e) { // file wasn't found :(
            // debug print error
            System.out.println("File not found: " + e.getMessage());
        }
        // return 3 because 3 is a funny number and i haven't done the eval calculations yet
        return 0;
    }
}
