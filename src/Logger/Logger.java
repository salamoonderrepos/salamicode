package Logger;

enum logType {
    NORMAL,
    WHISPER,
    IMPORTANT
}

public class Logger {
    String location;
    boolean silent = false;
    public Logger (String loc){
        location = loc;
    }
    public Logger (String loc, boolean si){
        location = loc;
        silent = si;
    }
    public void log(Object obj){
        if (!silent) {
            System.out.println('@' + logType.NORMAL.toString() + " [" + location + "] " + obj);
        }
    }
    public void whisper(Object obj){
        if (!silent) {
            System.out.println('@' + logType.WHISPER.toString() + " [" + location + "] " + obj);
        }
    }
    public void yell(Object obj){
        System.out.println('@' + logType.IMPORTANT.toString() + " [" + location + "] " + obj);
    }

    public void silence(){
        silent = true;
    }
    public void amplify(){
        silent = false;
    }
}
