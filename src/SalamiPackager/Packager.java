package SalamiPackager;

import Helper.Logger.Timer;
import SalamiEvaluator.LexerException;
import SalamiEvaluator.Parser;
import SalamiEvaluator.ParserException;
import SalamiEvaluator.types.ast.ProgramNode;
import SalamiPackager.Packages.SalamiPackage;
import SalamiRuntime.Interpreter;
import SalamiRuntime.InterpreterException;
import SalamiRuntime.Runtime.Environment;

import Helper.Logger.Logger;
import SalamiRuntime.Runtime.ProgramCounter;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Packager {
    public static final Logger logger = new Logger("Packager");
    public static HashSet<String> loadedPackages = new HashSet<>();
    public static HashSet<String> activleyLoadingPackages = new HashSet<>();
    public static final boolean harshDuplicateLoading = false;
    public static final boolean harshRecursivePorting = false;
    public static Environment loadFile(File file, Environment env) throws PackageException {
        Timer fileloadtimer = new Timer("FileLoadTimer");
        if (arePackageDuplicates(file.getName())){
            logger.whisper("Took "+fileloadtimer.time()+" milliseconds (FILELOAD) (DUPLICATE)");
            return env;

        }
        activleyLoadingPackages.add(file.getName());
        try {
            ProgramNode parsedFile = Parser.parseFile(file);
            Interpreter.evaluate(parsedFile, env, new ProgramCounter(0), parsedFile);
        } catch (ParserException | LexerException e) {
            throw new PackageException("Error parsing "+file.getName()+": "+e.getMessage());
        } catch (InterpreterException e ) {
            throw new PackageException("Exception occurred interpreting "+file.getName()+": "+ e.getMessage());
        } catch (IOException e ) {
            throw new PackageException("Could not locate file: " + file.getName());
        }
        logger.whisper("Took "+fileloadtimer.time()+" milliseconds (FILELOAD)");
        activleyLoadingPackages.remove(file.getName());
        loadedPackages.add(file.getName());
        return env;
    }
    public static Environment loadPackage(SalamiPackage pack, Environment env) throws PackageException {
        Timer packageloadtimer = new Timer("PackageLoadTimer");
        if (arePackageDuplicates(pack.ID)){
            logger.whisper("Took "+packageloadtimer.time()+" milliseconds (PACKLOAD) (DUPLICATE)");
            return env;
        }
        activleyLoadingPackages.add(pack.ID);
        try {
            // we dont have to initialize the main.salami because it is automatically initialized in the evaluate function.
            Interpreter.evaluate(pack.main, env, new ProgramCounter(0), pack.main);
            logger.whisper("Took "+packageloadtimer.time()+" milliseconds (PACKLOAD)");
            activleyLoadingPackages.remove(pack.ID);
            loadedPackages.add(pack.ID);
            return env;
        } catch (InterpreterException e) {
            throw new PackageException("Exception occurred interpreting package: "+ pack.extrameta.get("name") + "\n" + e.getMessage());
        }

    }
    public static boolean arePackageDuplicates(String packageID){
        if (loadedPackages.contains(packageID)){
            if (harshDuplicateLoading){
                throw new PackageException("Tried to load package \""+packageID+"\" which has already been loaded into memory. (HARSH)");
            } else {
                return true;
            }
        }else if (activleyLoadingPackages.contains(packageID)){
            if (harshRecursivePorting){
                throw new PackageException("Tried to port file \""+packageID+"\" which is but its own parent porting process. (HARSH)");
            } else {
                return true;
            }
        }
        return false;
    }
    public static SalamiPackage zipPackage(String packageLoc) throws PackageException{
        return null;
    }
    public static SalamiPackage unzipPackage(String packageLoc) throws PackageException {
        Timer packageunziptimer = new Timer("PackageZipTimer");
        try {
            ProgramNode main = null;
            Map<String, String> metadata = Map.of();
            ZipInputStream zipFileReaderTool = new ZipInputStream(new FileInputStream(packageLoc));
            ZipEntry zipEntry = zipFileReaderTool.getNextEntry();
            Map<String, ProgramNode> libFilesInMemory = new HashMap<>();
            while (zipEntry != null) {
                String entryName = zipEntry.getName().replace("\\", "/");
                // replace the stupid backslashes with the cooler sibling: the forward slash

                ByteArrayOutputStream tempdatabeingread = new ByteArrayOutputStream();
                // make a variable to store the data being read from the zip entry
                byte[] buffer = new byte[1024];
                // make a 1024 byte buffer for the data
                int lengthOfBytesReadIntoBuffer;
                // how many bytes were read into the buffer

                // actually get that length and store the data into the buffer from our zip file
                while ((lengthOfBytesReadIntoBuffer = zipFileReaderTool.read(buffer)) > 0) {
                    tempdatabeingread.write(buffer, 0, lengthOfBytesReadIntoBuffer);
                    // while we still have data that we need to store in memory
                    // write that data into our temp data variable from the buffer
                    // still in 1024 chunks, but with a length addition basically saying
                    // "hey we have like less than 1024 bytes here but still in 1024 chunks"
                }
                byte[] data = tempdatabeingread.toByteArray();
                // we can now loop over each entry in the scpkg file
                // we can assume its of standard contents, any part of the package thats broken will be caught and thrown
                File newFile = newFile(zipEntry);
                if (entryName.equals("main.salami")){
                    try (InputStream mainStream = new ByteArrayInputStream(data)) {
                        main = Parser.parseStream(mainStream);
                    } catch (ParserException | LexerException e) {
                        throw new PackageException("Error parsing main.salami: "+e.getMessage());
                    }
                } else if (entryName.equals("package.json")){
                    try (InputStream jsonStream = new ByteArrayInputStream(data)){
                        metadata = JsonReader.parseJsonStream(jsonStream);
                    }

                } else if (entryName.startsWith("lib/") && !zipEntry.isDirectory()) {
                    int depth = entryName.split("/").length - 1;
                    if (depth > 5) {
                        throw new PackageException("Exceeded max depth (5) in lib folder: " + entryName);
                    }
                    if (entryName.endsWith(".sal") || entryName.endsWith(".salami") || entryName.endsWith(".scpkg")) {
                        try (InputStream libfileStream = new ByteArrayInputStream(data)) {
                            libFilesInMemory.put(entryName, Parser.parseStream(libfileStream));
                        } catch (ParserException | LexerException e) {
                            throw new PackageException("Error parsing \""+entryName+"\": "+e.getMessage());
                        }

                    } else {
                        throw new PackageException("Unsupported file type in lib: " + entryName);
                    }
                }
                zipEntry = zipFileReaderTool.getNextEntry();
            }

            String id;
            String version;

            if (metadata.isEmpty()){
                throw new PackageException("Could not find `package.json`");
            } else {
                try {
                    id = metadata.get("id");
                    version = metadata.get("version");
                } catch (NullPointerException e){
                    throw new PackageException("Missing required components in `package.json`");
                }
            }
            if (main==null){
                throw new PackageException("Could not find `main.salami`");
            }

            zipFileReaderTool.closeEntry();
            zipFileReaderTool.close();
            logger.whisper("Took "+packageunziptimer.time()+" milliseconds (UNZIP)");
            return new SalamiPackage(id ,version, main, metadata, packageLoc);

        } catch (IOException e) {
            throw new RuntimeException("ERROR OCCURED WITHIN PACKAGE MODULE: " + e);
        }

    }
    public static SalamiPackage installPackage(String packageID) throws PackageException{
        return null;
    }
    //https://www.baeldung.com/java-compress-and-uncompress
    public static File newFile(ZipEntry zipEntry) throws IOException {
        // File destinationDir
        // new file method prevents zip slip
        File destFile = new File(zipEntry.getName());

//        String destDirPath = destinationDir.getCanonicalPath();
//        String destFilePath = destFile.getCanonicalPath();
//
//        if (!destFilePath.startsWith(destDirPath + File.separator)) {
//            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
//        }

        return destFile;
    }
}
