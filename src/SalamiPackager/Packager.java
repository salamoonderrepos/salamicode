package SalamiPackager;

import SalamiPackager.Packages.Package;
import SalamiRuntime.Runtime.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;

public class Packager {
    private final Environment env;
    private final Map<String, Package> packages = new HashMap<>();

    public Packager(Environment env) {
        this.env = env;
    }

    public void loadPackage(File packageDir) throws PackageException{
        File jsonFile = new File(packageDir, "package.json");
        if (!jsonFile.exists()) throw new PackageException("package.json not found");

    }
}
