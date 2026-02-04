package com.isaac;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Config {

    private final Properties configs = new Properties();
    private final Path CONFIG_FILE;

    private static final String USER_HOME = System.getProperty("user.home");
    private static final String BASE_DIR = System.getProperty("user.dir");
    private static final String OS_NAME = System.getProperty("os.name");

    public static final boolean isWindows = OS_NAME.toLowerCase().contains("win");
    public static final boolean isLinux = OS_NAME.toLowerCase().contains("linux");
    public static final boolean isMac = OS_NAME.toLowerCase().contains("mac");

    private static final Path DEFAULT_ORIGIN_PATH = defaultOriginPath();
    private static final Path DEFAULT_BACKUP_PATH = Path.of(BASE_DIR, "Isaac-Backups");
    
    private Path ORIGIN_PATH;
    private Path BACKUP_PATH;

    public Config (){
        this.CONFIG_FILE = Path.of("config.properties");
        init();
    }

    public Config (Path configFile){
        this.CONFIG_FILE = configFile;
        init();
    }

    //method that setups a fis and loads the properties from a config file
    private void loadProperties (){
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE.toFile())){
            configs.load(fis);
        } catch (IOException e){
            System.err.println("Failed trying to read config.properties: " + e.getMessage());
        }
    }
    //method that setups a fos and saves the properties in a config file
    private void storeProperties(){
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE.toFile())) {
            configs.store(fos, "Basic paths configs for Isaac Backup");
        } catch (IOException e) {
            System.err.println("Failed trying to store config.properties: " + e.getMessage());
        }
    }

    //Uses a init method to read the config file and have access to the setted paths, if there is no file, it uses default paths and creates a file
    private void init(){

        if (!Files.exists(CONFIG_FILE)){
            configs.setProperty("ORIGIN_PATH", DEFAULT_ORIGIN_PATH.toString());
            configs.setProperty("BACKUP_PATH", DEFAULT_BACKUP_PATH.toString());
            storeProperties();
        }
        
        //TODO: add defaults as exception is catched
        loadProperties();

        ORIGIN_PATH = Path.of(configs.getProperty("ORIGIN_PATH", DEFAULT_ORIGIN_PATH.toString()));
        BACKUP_PATH = Path.of(configs.getProperty("BACKUP_PATH", DEFAULT_BACKUP_PATH.toString()));
    
    }

    //Initialize default paths depending on the used OS
    private static Path defaultOriginPath(){

        if (isWindows){
            return Path.of(USER_HOME, "Documents", "My Games");
        } else if (isMac){
            return Path.of(USER_HOME, "Library", "Application Support");
        }else if (isLinux){
            String linuxHome = System.getenv("XDG_DATA_HOME");
            if (linuxHome != null){
                return Path.of(linuxHome);
            } else {
                return Path.of(USER_HOME, ".local","share");
            }
        } 

        throw new UnsupportedOperationException("Unsupported Operative System " + OS_NAME);
    }

    public boolean setOriginPath(String newOriginPath) {

        Path newPath = Path.of(newOriginPath);

        if (newPath.toFile().isDirectory() && Files.isWritable(newPath)){
            ORIGIN_PATH = newPath;
            configs.setProperty("ORIGIN_PATH", newOriginPath);
            storeProperties();
            return true;
        } else {
            return false;
        }
    }

    public boolean setBackupPath(String newBackupPath) {
        Path newPath = Path.of(newBackupPath);

        if (newPath.toFile().isDirectory() && Files.isWritable(newPath)){
            BACKUP_PATH = newPath;
            configs.setProperty("BACKUP_PATH", newBackupPath);
            storeProperties();
            return true;
        } else {
            return false;
        }
    }

    public Path getOriginPath() {
        return ORIGIN_PATH;
    }
    public Path getBackupPath() {
        return BACKUP_PATH;
    }

    public Path getProtonPath(){
        return Path.of("Steam", "steamapps", "compatdata", "250900", "pfx", "drive_c", "users", "steamuser", "Documents", "My Games");
    }

    
}
