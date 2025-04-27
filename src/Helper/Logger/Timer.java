package Helper.Logger;

public class Timer {
    long timestart;
    String descriptor;
    public Timer(String _descriptor){
        descriptor = _descriptor;
        startTime();
    }

    public double time(){
        return (System.nanoTime()-timestart)/1_000_000.0;
    }

    public void startTime(){
        timestart = System.nanoTime();
    }
}
