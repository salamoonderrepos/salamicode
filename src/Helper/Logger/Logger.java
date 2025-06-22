package Helper.Logger;

enum logType {
    NORMAL,
    WHISPER,
    IMPORTANT,
    WHISPERIMPORTANT,
    DEBUG,
}

public class Logger {
    String location;
    boolean silent = false;
    boolean prettylint = false;
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
            String lintedtext = prettyize(obj);
            String coloredtext = colorize(PURPLE, '@' + logType.NORMAL.toString()) + " [" + location + "] " + colorize(YELLOW, lintedtext);
            System.out.println(coloredtext);
        }

    }
    public void logExtra(Object obj,String extra){
        if (!silent) {
            String lintedtext = prettyize(obj);
            String coloredtext = colorize(PURPLE, '@' + logType.NORMAL.toString())+"/"+extra + " [" + location + "] " + colorize(YELLOW, lintedtext);
            System.out.println(coloredtext);
        }

    }
    public void debuglog(Object obj){
        String coloredtext = colorize(PURPLE, "[" + location + "] " + colorize(GREEN, obj));
        System.out.println(coloredtext);

    }
    public void debuglog(Object obj, boolean pretty){
        if (pretty) {
            String lintedtext = prettyize(obj);
            String coloredtext = colorize(PURPLE, "[" + location + "] " + colorize(GREEN, lintedtext));
            System.out.println(coloredtext);
        } else {debuglog(obj);}

    }
    public void log(Object obj, String Color){
        if (!silent) {
            String lintedtext = prettyize(obj);
            String coloredtext = colorize(PURPLE, '@' + logType.NORMAL.toString()) + " [" + location + "] " + colorize(Color, lintedtext);

            System.out.println(coloredtext);
        }
    }

    public void whisper(Object obj){
        if (!silent) {
            String lintedtext = prettyize(obj);
            String coloredtext = colorize(GRAY, '@' + logType.WHISPER.toString() + " [" + location + "] " + lintedtext);

            System.out.println(coloredtext);
        }
    }

    public void whisperImportant(Object obj){
        String lintedtext = prettyize(obj);
        String coloredtext = colorize(GRAY, '@' + logType.WHISPERIMPORTANT.toString() + " [" + location + "] " + lintedtext);

        System.out.println(coloredtext);
    }
    public void yell(Object obj){
        String lintedtext = prettyize(obj);
        String coloredtext = colorize(CYAN, '@' + logType.IMPORTANT.toString()) + " [" + location + "] " + colorize(BLUE, lintedtext);
        System.out.println(coloredtext);
    }

    private String prettyize(Object text) {
        return prettylint ? SalamiPrettyLinter.format(text.toString()) : text.toString();
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
    public void prettify(){
        prettylint = true;
    }
    public void deprettify(){
        prettylint = false;
    }
}
