package com.discordvtt.Data;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Jared on 3/16/2018.
 */
public class FileIO {

     /*
      FileIO:

      A catch-all utility class for saving and loading GameMap's and GameInstance's
     */
     
     private final String GAMEMAP_FILE_EXT = "vttmap";
     private final String GAMEMAP_EXT_DESC = "DiscordVTT Game Map";
     

    /**
     * Gets the 'root file path' that points to the file folder where the SourceryText and LevelEditor .jar's are located.
     *
     * Useful for trying to reference files nearby the executable.
     *
     * Getting the root file path is handy because that means SourceryText requires no Installation Wizards to correctly configure.
     * This is also why you can't just run SourceryText out of the IDE, because the root path would be in src instead of CompiledGame
     *
     * You gotta configure for outputting a .jar and then run it from there.
     *
     * @return The root file path
     */
    public String getRootFilePath(){
        String path = decodeFilePath(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        //System.out.println("[FileIO.getRootFilePath] base path: " + path);
        String reducedPath = path.substring(0, path.lastIndexOf('/'));
        reducedPath += "/";
        //System.out.println("[FileIO.getRootFilePath] root path: " + reducedPath);
        return reducedPath;
    }

    /**
     * Takes a file path and 'cuts off' the portion that contains the root file path.
     *
     * If the file path doesn't contain the root path, it throws a hissy fit and returns "PATHS DO NOT MATCH"
     * So, that it might be easier to discover if the .jar is in the wrong spot.
     *
     * @param fullPath The 'full' file path
     * @return The file path relative to the root file path
     */
    public String getRelativeFilePath(String fullPath){
        String rootPath = getRootFilePath();
        if (fullPath.contains(rootPath)){
            return fullPath.substring(rootPath.length());
        }
        return "PATHS DO NOT MATCH";
    }

    /**
     * Cleans up file paths for easier interfacing with.
     *
     * 1) It parses the file path using URLDecoder
     * and on Windows:
     *   2) It replaces every '\' with a '/', which is less of a pain to work with.
     *   3) It removes the '/' at the start of the file path.
     *
     * @param rawPath The 'raw' file path that needs cleaning
     * @return A much cleaner file path
     */
    public String decodeFilePath(String rawPath){
        String path;
        path = URLDecoder.decode(rawPath, StandardCharsets.UTF_8);
        if (System.getProperty("os.name").startsWith("Windows")) {
            path = path.replace('\\', '/');
            if (path.startsWith("/")) path = path.substring(1);
        }
        return path;
    }

    /**
     * Gets the file extension of a file.
     * @param file The File you want to find the extension of
     * @return The file extension, including the '.'
     */
    public String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Runs the FileChooser to pick a .vttmap file.
     *
     * This method lazily starts the FileChooser beginning location at the root file path
     * @return The selected File
     */
    public File chooseGameMap(){
        String path = "";
        try {
            path = decodeFilePath(FileIO.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            path = path.substring(0, path.lastIndexOf('/'));
            path += "/GameMap";
            return chooseFile(path, GAMEMAP_FILE_EXT, GAMEMAP_EXT_DESC);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return chooseFile(path, GAMEMAP_FILE_EXT, GAMEMAP_EXT_DESC);
    }

    /**
     * Runs the FileChooser to pick a .vttmap file.
     *
     * @param path : The path you want to start with for the file chooser
     * @return The selected File
     */
    public File chooseGameMap(String path){
        return chooseFile(path, GAMEMAP_FILE_EXT, GAMEMAP_EXT_DESC);
    }

    /**
     * Runs the FileChooser to pick a file of an input file extension
     *
     * @param startingPath The beginning location of FileChooser
     * @return The selected File
     */
    private File chooseFile(String startingPath, String ext, String extDesc){
        System.out.printf("[FileIO.chooseGameMap] Starting path: %1$s\n", startingPath);
        JFileChooser chooser = new JFileChooser(startingPath);
        System.out.println(startingPath);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(extDesc, ext);
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(new Component() {
        });
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File chosenFile = chooser.getSelectedFile();
            System.out.println("[FileIO.chooseGameMap] Opening file: " +
                    chosenFile.getName() + " at " + chosenFile.getPath());
            return chosenFile;
        } else return null;
    }

    /**
     * De-serializes a .vttmap file that is compressed via GZIP.
     *
     * @param savedLevel The GameMap file being opened.
     * @return The now-usable GameMap.
     */
    public GameMap openLevel(File savedLevel){
        try {
            FileInputStream fileIn = new FileInputStream(savedLevel);
            GZIPInputStream gzipIn = new GZIPInputStream(fileIn);
            ObjectInputStream objIn = new ObjectInputStream(gzipIn);
            GameMap gameMap = (GameMap)objIn.readObject();
            objIn.close();
            gzipIn.close();
            fileIn.close();
            return gameMap;
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(new JFrame(), "ERROR: File being accessed is out of date / improper!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Serializes a GameMap (saving it) as a .vttmap file
     * @param gamemap GameMap being saved
     * @param startingPath file path to start prompt from
     * @return chosen file path
     */
    public String serializeGameMap(GameMap gamemap, String startingPath){
        String path;
        JFileChooser fileChooser = new JFileChooser(startingPath);
        int fileChooseOption = fileChooser.showSaveDialog(new Component(){});
        if (fileChooseOption == JFileChooser.APPROVE_OPTION){
            path = decodeFilePath(fileChooser.getSelectedFile().getPath());
            if (!path.endsWith(".vttmap")) { // Add '.vttmap' to file if user didn't.
                path += ".vttmap";
            }
            quickSerializeGameMap(gamemap, path);
            return path;
        }
        return "";
    }

    /**
     * Performs a serialization of a GameMap without opening a FileChooser.
     *
     * NOTE: the input file path must be the non-relative type.
     *
     * @param gamemap The GameMap being serialized
     * @param path The 'full' file path being saved to.
     */
    public void quickSerializeGameMap(GameMap gamemap, String path){
        try {
            FileOutputStream out = new FileOutputStream(path);
            GZIPOutputStream gzipOut = new GZIPOutputStream(out);
            ObjectOutputStream objOut = new ObjectOutputStream(gzipOut);
            objOut.writeObject(gamemap);
            objOut.flush();
            objOut.close();
            gzipOut.close();
            out.close();
            System.out.println("[FileIO.serializeGameMap] Saved level to: " + path);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private Object openSerializedFile(File gameFile){
        try {
            FileInputStream fileIn = new FileInputStream(gameFile);
            GZIPInputStream gzipIn = new GZIPInputStream(fileIn);
            ObjectInputStream objIn = new ObjectInputStream(gzipIn);
            Object obj = objIn.readObject();
            fileIn.close();
            gzipIn.close();
            objIn.close();
            System.out.println("[FileIO.openSerializedFile] path: " + gameFile.getPath());
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(new JFrame(), "ERROR: File being accessed is out of date / improper!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }

    private void serializeObject(Serializable obj, String path, String ext){
        if (!path.endsWith(ext)) { // Add '.sav' to file if user didn't.
            path += ext;
        }
        try {
            FileOutputStream out = new FileOutputStream(path);
            GZIPOutputStream gzipOut = new GZIPOutputStream(out);
            ObjectOutputStream objOut = new ObjectOutputStream(gzipOut);
            objOut.writeObject(obj);
            objOut.flush();
            objOut.close();
            gzipOut.close();
            out.close();
            System.out.println("[FileIO.serializeObject] Saved to: " + path);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
