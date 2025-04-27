package SalamiPackager;

import SalamiPackager.Packages.Package;
import SalamiRuntime.Runtime.Environment;

import Helper.Logger.Logger;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;

public class Packager {
    private static final Map<String, Package> packages = new HashMap<>();
    private static final Logger logger = new Logger("Packager");


    public static void loadPackage(String packageID, Environment env) throws PackageException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException {

        // loop over each file in our packages directory to see if they match
        packageID = "math";

        // main directory
        File directory = new File("src/SalamiPackager/Packages"); // Change to your directory path

        // check if the package directory exists in local packages
        boolean found = false;
        if (directory.exists() && directory.isDirectory()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.getName().equals(packageID)) {
                    found = true;
                }
            }
        } else {
            System.out.println("Directory not found!");
        }
        if (!found){throw new PackageException("Could not find "+packageID+" package.");}

        // directory for the package. if we got here we know for a fact it exists.
        File packagedirectory = new File(directory + "/" +packageID);


        // the json meta file
        File jsonFile = new File(packagedirectory, "package.json");
        if (!jsonFile.exists()) throw new PackageException("package.json not found in package: "+packageID);
        Map<String, String> data = JsonReader.parseJson(jsonFile);

        // the varibales from that meta file
        String mainclass = data.get("main");
        String id = data.get("id");
        String dependencies = data.get("dependencies");

        // check if the main class from the meta file exists
        File mainClassFile = new File(packagedirectory, mainclass + ".class");
        if (!mainClassFile.exists()) throw new PackageException(mainclass+".class was not found for package" + id);

        try {
            // Load class from directory
            URL classdir = packagedirectory.toURI().toURL(); // Point to directory
            URLClassLoader classLoader = new URLClassLoader(new URL[]{classdir});
            Class<?> loadedClass = Class.forName(mainclass, true, classLoader);

            Package aPackage = (Package) loadedClass.getDeclaredConstructor().newInstance();
            aPackage.initialize(env);

            packages.put(id, aPackage);
            System.out.println("Loaded package: " + id);
        } catch (ClassNotFoundException | NoSuchMethodException e){
            if (e.getClass()==NoSuchMethodException.class){
                throw new PackageException("Class "+mainclass+" does not have a defined constructor.");
            }
            throw new PackageException("Class "+mainclass+" was not found as stated from package.json in the "+packageID+" package.");
        }


        logger.yell(data);

    }
}
