package Structure;

public class BaseException extends RuntimeException {
    int[] lfc;
    public BaseException(String message, int[] location_from_file) {
        super(message);
        lfc = location_from_file;
    }
    public BaseException(String message) {
        super(message);
        lfc = null;
    }

    public int[] getLocation() {
        return lfc;
    }
    public int getLocationLineNumber() {
        return lfc[0];
    }
    public int getLocationColumnNumber() {
        return lfc[1];
    }
    @Override
    public String getMessage() {
        if(lfc==null){return super.getMessage();}
        return "[L. " + getLocationLineNumber() +", C. " +getLocationColumnNumber() +"] "+super.getMessage();
    }
}
