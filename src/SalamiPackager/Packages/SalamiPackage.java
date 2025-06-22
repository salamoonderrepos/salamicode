package SalamiPackager.Packages;

import SalamiPreEvaluator.types.ast.ProgramNode;

import java.io.File;
import java.util.Map;

public class SalamiPackage {
    public String ID;
    public String version;
    public ProgramNode main;
    public Map<String, ProgramNode> contents;
    public Map<String, String> extrameta;
    public File sourceFile;

    public SalamiPackage(String ID, String version, ProgramNode main, Map<String, String> extrameta, Map<String, ProgramNode> libcontents, File _sourceFile) {
        this.ID = ID;
        this.version = version;
        this.main = main;
        this.extrameta = extrameta;
        this.sourceFile = _sourceFile;
        this.contents = libcontents;
    }
}
