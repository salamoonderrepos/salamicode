package SalamiPackager.Packages;

import Helper.JsonReader;
import Helper.Logger.Timer;
import SalamiPreEvaluator.LexerException;
import SalamiPreEvaluator.Parser;
import SalamiPreEvaluator.ParserException;
import SalamiPreEvaluator.types.ast.ProgramNode;
import SalamiRuntime.InterpreterException;
import SalamiRuntime.RuntimeData.Environment;

import Helper.Logger.Logger;
import Structure.Process;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
//https://www.baeldung.com/java-compress-and-uncompress
public class Packager {
    public static final Logger logger = new Logger("Packager");
    public static HashSet<String> loadedPackageIDs = new HashSet<>();
    public static HashSet<SalamiPackage> e = new HashSet<>();
    public static HashSet<String> activleyLoadingPackages = new HashSet<>();
    public static final boolean harshDuplicateLoading = false;
    public static final boolean harshRecursivePorting = false;
    public static Environment loadFile(File file, Environment baseEnvironment) throws PackageException {
        Timer fileloadtimer = new Timer("FileLoadTimer");
        Environment newEnvironment;
        if (arePackageDuplicates(file.getName())){
            logger.whisper("Took "+fileloadtimer.time()+" milliseconds (FILELOAD) (DUPLICATE)");
            return baseEnvironment;

        }
        activleyLoadingPackages.add(file.getName());
        try {
            ProgramNode parsedFile = Parser.parseFile(file);
            Process tempProcess = new Process(
                    "PackagerFile/"+file.getName(),
                    parsedFile,
                    true,
                    baseEnvironment, logger.silent);
            //interpreter.evaluate(parsedFile, env, new ProgramCounter(0), parsedFile);
            tempProcess.begin();
            newEnvironment = tempProcess.getLocalEnvironment();
        } catch (ParserException | LexerException e) {
            throw new PackageException("Error parsing "+file.getName()+": "+e.getMessage(), e.getLocation());
        } catch (InterpreterException e ) {
            throw new PackageException("Exception occurred interpreting "+file.getName()+": "+ e.getMessage(), e.getLocation());
        } catch (IOException e ) {
            throw new PackageException("Could not locate file: " + file.getName(), null);
        }
        logger.whisper("Took "+fileloadtimer.time()+" milliseconds (FILELOAD)");
        activleyLoadingPackages.remove(file.getName());
        loadedPackageIDs.add(file.getName());
        return newEnvironment;
    }
    public static Environment loadFileFromPack(SalamiPackage pack, String file, Environment baseEnvironment) throws PackageException {
        Timer fileloadtimer = new Timer("FileLoadTimer");
        Environment newEnvironment;
        String fileID = pack.ID+"/"+file;
        if (arePackageDuplicates(fileID)){
            logger.whisper("Took "+fileloadtimer.time()+" milliseconds (LOCALFILELOAD) (DUPLICATE)");
            return baseEnvironment;

        }
        activleyLoadingPackages.add(fileID);
        try {
            ProgramNode libFile = pack.contents.get("lib/"+file);

            Process tempProcess = new Process(pack.ID+"/PackagerLibFile/"+file, libFile, true, baseEnvironment, logger.silent);
            tempProcess.begin();
            newEnvironment = tempProcess.getLocalEnvironment();
        } catch (InterpreterException e ) {
            throw new PackageException("Exception occurred interpreting "+fileID+": "+ e.getMessage(), e.getLocation());
        } catch (NullPointerException e ) {
            throw new PackageException("Could not locate file: " + fileID, null);
        }
        logger.whisper("Took "+fileloadtimer.time()+" milliseconds (FILELOAD)");
        activleyLoadingPackages.remove(fileID);
        loadedPackageIDs.add(fileID);
        return newEnvironment;
    }
    public static File findFileToLoad(String packid){
        // we need to check some directories for packages
        // first we should check the running directory
        // then we should check the specified location given by a process
        // then we should check some other user directories for installed packages
        // all should return a .scpkg file to be given to the package loader

        // for now we are just going to return packages found in the running directory
        // if you are on the testing branch then there should be a "packages" directory in the same level as `src`
        // if not then make that or just change the code below to look for packages in a specified directory


        // PACKAGES MUST END IN .scpkg


        String[] dirsToSearch = {
            System.getProperty("user.dir"),
            System.getProperty("user.dir")+File.separator+"packages",
            System.getProperty("user.dir")+File.separator+"lib"

        };
        for (String dir : dirsToSearch){
            File tempFile = new File(dir+File.separator+packid);
            if (tempFile.exists()){
                return tempFile;
            }
        }

        throw new PackageException("File OR Package: \""+packid+"\" could not be located.", null);
    }
    public static Environment loadPackage(SalamiPackage pack, Environment coolfreakingenvironment) throws PackageException {
        Timer packageloadtimer = new Timer("PackageLoadTimer");
        Environment newEnvironment;
        if (arePackageDuplicates(pack.ID)){
            logger.whisper("Took "+packageloadtimer.time()+" milliseconds (PACKLOAD) (DUPLICATE)");
            return coolfreakingenvironment;
        }
        activleyLoadingPackages.add(pack.ID);
        try {
            // we dont have to initialize the main.salami because it is automatically initialized in the evaluate function.
            Process tempProcess = new Process(pack.ID+"/PackagerProcess", pack.main,  true, coolfreakingenvironment, logger.silent);
            tempProcess.setSourcePackage(pack);
            tempProcess.begin();
            newEnvironment = tempProcess.getLocalEnvironment();

        } catch (InterpreterException e) {
            throw new PackageException("Exception occurred interpreting package: "+ pack.extrameta.get("name") + "\n" + e.getMessage(), null);
        }
        logger.whisper("Took "+packageloadtimer.time()+" milliseconds (PACKLOAD)");
        activleyLoadingPackages.remove(pack.ID);
        loadedPackageIDs.add(pack.ID);
        return newEnvironment;
    }
    public static boolean arePackageDuplicates(String packageID){
        if (loadedPackageIDs.contains(packageID)){
            if (harshDuplicateLoading){
                throw new PackageException("Tried to load package \""+packageID+"\" which has already been loaded into memory. (HARSH)", null);
            } else {
                return true;
            }
        }else if (activleyLoadingPackages.contains(packageID)){
            if (harshRecursivePorting){
                throw new PackageException("Tried to port file \""+packageID+"\" which is but its own parent porting process. (HARSH)", null);
            } else {
                return true;
            }
        }
        return false;
    }
    public static SalamiPackage zipPackage(String packageLoc) throws PackageException{
        return null;
    }
    public static SalamiPackage unzipPackage(File packagefile) throws PackageException {
        Timer packageunziptimer = new Timer("PackageZipTimer");
        try (ZipInputStream zipFileReaderTool = new ZipInputStream(new FileInputStream(packagefile))){
            ProgramNode main = null;
            Map<String, String> metadata = Map.of();
            Map<String, ProgramNode> libFilesInMemory = new HashMap<>();
            ZipEntry zipEntry;

            while ((zipEntry = zipFileReaderTool.getNextEntry()) != null) {
                String entryName = zipEntry.getName().replace("\\", "/");
                // replace the stupid backslashes with the cooler sibling: the forward slash
                if (entryName.contains("..") || entryName.startsWith("/")) {
                    throw new PackageException("Entry name attempts directory traversal: " + entryName, null);
                }

                byte[] data = readZipEntryData(zipFileReaderTool);
                ByteArrayInputStream datastream = new ByteArrayInputStream(data);
                // we can now loop over each entry in the scpkg file
                // we can assume its of standard contents, any part of the package thats broken will be caught and thrown
                if (entryName.equals("main.salami") || entryName.equals("main.sal")){
                    main = parseSalamiFile(datastream, entryName);
                } else if (entryName.equals("package.json")){
                    metadata = JsonReader.parseJsonStream(datastream);
                } else if (entryName.startsWith("lib/") && !zipEntry.isDirectory()) {
                    validateEntryDepth(entryName);
                    if (entryName.endsWith(".sal") || entryName.endsWith(".salami") || entryName.endsWith(".scpkg")) {
                        libFilesInMemory.put(entryName, parseSalamiFile(datastream, entryName));

                    } else {
                        throw new PackageException("Unsupported file type in lib: " + entryName, null);
                    }
                }
                //zipEntry = zipFileReaderTool.getNextEntry();
            }

            if (metadata.isEmpty()) throw new PackageException("Missing `package.json`", null);
            if (main == null) throw new PackageException("Missing `main.salami`", null);

            String id = metadata.get("id");
            String version = metadata.get("version");
            zipFileReaderTool.closeEntry();
            zipFileReaderTool.close();
            logger.whisper("Took "+packageunziptimer.time()+" milliseconds (UNZIP)");
            return new SalamiPackage(id ,version, main, metadata, libFilesInMemory, packagefile);



        } catch (IOException e) {
            throw new RuntimeException("IO ERROR OCCURRED WITHIN PACKAGE: " + e);
        }

    }

    private static void validateEntryDepth(String entryName) {
        int depth = entryName.split("[/\\\\]").length - 1; // handles / and \
        // wait why do we need to regex the slashes if its already switched to forward slash?
        if (depth > 5) {
            throw new PackageException("Exceeded max depth (5) in lib folder: " + entryName, null);
        }
    }

    public static SalamiPackage installPackage(String packageID) throws PackageException{
        return null;
    }
    public static ProgramNode parseSalamiFile(InputStream datacomingin, String nameoffile) throws FileNotFoundException, PackageException{
        try {
            return Parser.parseStream(datacomingin);
        } catch (ParserException | LexerException e) {
            throw new PackageException("Error parsing \""+nameoffile+"\": "+e.getMessage(), null);
        }
    }
    public static byte[] readZipEntryData(ZipInputStream zipFileInput) throws IOException{
        ByteArrayOutputStream tempdatabeingread = new ByteArrayOutputStream();
        // make a variable to store the data being read from the zip entry
        byte[] buffer = new byte[1024];
        // make a 1024 byte buffer for the data
        int lengthOfBytesReadIntoBuffer;
        // how many bytes were read into the buffer

        // actually get that length and store the data into the buffer from our zip file
        while ((lengthOfBytesReadIntoBuffer = zipFileInput.read(buffer)) > 0) {
            tempdatabeingread.write(buffer, 0, lengthOfBytesReadIntoBuffer);
            // while we still have data that we need to store in memory
            // write that data into our temp data variable from the buffer
            // still in 1024 chunks, but with a length addition basically saying
            // "hey we have like less than 1024 bytes here but still in 1024 chunks"
        }
        return tempdatabeingread.toByteArray();
    }
}
