package SalamiPackager.Packages;

import SalamiEvaluator.types.ast.ProgramNode;

import java.util.Map;

public class SalamiPackage {
    public String ID;
    public String version;
    public ProgramNode main;
    public Map<String, ProgramNode> contents;
    public Map<String, String> extrameta;
    public String location;

    public SalamiPackage(String ID, String version, ProgramNode main, Map<String, String> extrameta, String location) {
        this.ID = ID;
        this.version = version;
        this.main = main;
        this.extrameta = extrameta;
        this.location = location;
    }
}
