package Helper.Parameters;

public class EnvironmentParameters {
    public boolean canPort = true;
    public boolean canReference = true;
    public boolean canSubroutine = true;
    public boolean canLabel = true;
    public boolean canMethod = true;
    public boolean canVariable = true;
    public boolean exitCapabilitiesQuietly = false; // Ignore capability exceptions or throw them

    public EnvironmentParameters(boolean _port,
                                 boolean _reference,
                                 boolean _subroutine,
                                 boolean _label,
                                 boolean _method,
                                 boolean _variable,
                                 boolean _quiet){
        boolean canPort = _port;
        boolean canReference = _reference;
        boolean canSubroutine = _subroutine;
        boolean canLabel = _label;
        boolean canMethod = _method;
        boolean canVariable = _variable;
        boolean exitCapabilitiesQuietly = _quiet; // Ignore capability exceptions or throw them
    }
}
