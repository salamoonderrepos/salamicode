package Logger;

enum logType {
    NORMAL,
    WHISPER,
    IMPORTANT,
    WHISPERIMPORTANT,
}

public class Logger {
    String location;
    boolean silent = false;
    public static boolean doColor = true;
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String GRAY = "\u001B[37m";
    public static final String CYAN = "\u001B[36m";
    public Logger (String loc){
        location = loc;
    }
    public Logger (String loc, boolean si){
        location = loc;
        silent = si;
    }
    public void log(Object obj){
        if (!silent) {
            System.out.println(colorize(PURPLE, '@' + logType.NORMAL.toString()) + " [" + location + "] " + colorize(YELLOW, obj));
        }

    }
    public void log(Object obj, String Color){
        if (!silent) {
            System.out.println(colorize(PURPLE, '@' + logType.NORMAL.toString()) + " [" + location + "] " + colorize(Color, obj));
        }
    }

    public void whisper(Object obj){
        if (!silent) {
            System.out.println(colorize(GRAY, '@' + logType.WHISPER.toString() + " [" + location + "] " + obj));
        }
    }

    public void whisperImportant(Object obj){
        System.out.println(colorize(GRAY, '@' + logType.WHISPERIMPORTANT.toString() + " [" + location + "] " + obj));
    }
    public void yell(Object obj){
        System.out.println(colorize(CYAN, '@' + logType.IMPORTANT.toString()) + " [" + location + "] " + colorize(BLUE, obj));
    }

    public static String colorize(String color, Object text){
        return doColor ? color + text + RESET : text.toString();
    }

    public void silence(){
        silent = true;
    }
    public void decolorize(){
        doColor = false;
    }
    public void amplify(){
        silent = false;
    }
    public void colorize(){
        doColor = true;
    }
}
