package com.isaac;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.stream.Stream;

import com.isaac.service.GameVersion;
import com.isaac.service.OperationResult;

public class Config {

    private final Properties configs = new Properties();
    private final Path CONFIG_FILE;

    private static final String USER_HOME = System.getProperty("user.home");
    private static final String BASE_DIR = System.getProperty("user.dir");
    private static final String OS_NAME = System.getProperty("os.name");

    public static final boolean isWindows = OS_NAME.toLowerCase().contains("win");
    public static final boolean isLinux = OS_NAME.toLowerCase().contains("linux");
    public static final boolean isMac = OS_NAME.toLowerCase().contains("mac");

    private static final Path DEFAULT_ORIGIN_PATH = defaultOriginPath().toAbsolutePath().normalize();
    private static final Path DEFAULT_BACKUP_PATH = Path.of(BASE_DIR, "Isaac-Backups").toAbsolutePath().normalize();
    
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
            Path originPathCheck = Path.of(configs.getProperty("ORIGIN_PATH"));
            Path backupPathCheck = Path.of(configs.getProperty("BACKUP_PATH"));
            
            Path originResolved = resolveAbsoluteNormalizePath(originPathCheck);
            Path backupResolved = resolveAbsoluteNormalizePath(backupPathCheck);

            if (!originPathCheck.equals(originResolved) || !backupPathCheck.equals(backupResolved)){
                configs.setProperty("ORIGIN_PATH", originResolved.toString());
                configs.setProperty("BACKUP_PATH", backupResolved.toString());
                storeProperties();
            }
            
        } catch (IOException e){
            System.err.println("Failed trying to read config.properties: " + e.getMessage());
            System.err.println("Loading defaults");
            setDefaultsForProperties();
        }
    }
    //method that setups a fos and saves the properties in a config file
    private void storeProperties(){
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE.toFile())) {
            configs.store(fos, "Basic paths configs for Isaac Backup");
        } catch (IOException e) {
            System.err.println("Failed trying to store config.properties: " + e.getMessage());
            System.out.println("WARNING: the updated config file will not save when the program is closed");
        }
    }

    //Uses a init method to read the config file and have access to the setted paths, if there is no file, it uses default paths and creates a file
    private void init(){

        if (!Files.exists(CONFIG_FILE)){
            setDefaultsForProperties();
            storeProperties();
        } else {
            loadProperties();
        }

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

    private void setDefaultsForProperties(){
        configs.setProperty("ORIGIN_PATH", DEFAULT_ORIGIN_PATH.toString());
        configs.setProperty("BACKUP_PATH", DEFAULT_BACKUP_PATH.toString());
    }

    private Path resolveAbsoluteNormalizePath(Path path){
        Path pathCheck = path;
            if (!path.isAbsolute()){
                pathCheck = pathCheck.toAbsolutePath();
            }
            if (!pathCheck.equals(path.normalize())){
                pathCheck = path.normalize();
            }
        return pathCheck;
    }

    public OperationResult setOriginPath(String newOriginPath) {

        if (newOriginPath.isBlank()){
            return new OperationResult(false, "the path is blank");
        }

        Path newPath = Path.of(newOriginPath);
        newPath = resolveAbsoluteNormalizePath(newPath);

        if (newPath.equals(ORIGIN_PATH)){
            return new OperationResult(false, "you are already using this path");
        }

        if (!newPath.toFile().isDirectory()){
            return new OperationResult(false, "the path is not a directory");
        }
        if (!Files.isWritable(newPath)){
            return new OperationResult(false, "the path is not writable");
        }

        ORIGIN_PATH = newPath;
        configs.setProperty("ORIGIN_PATH", newPath.toString());
        storeProperties();
        return new OperationResult(true, "origin path changed successfully");
    }

    public OperationResult setBackupPath(String newBackupPath) {
        
        if (newBackupPath.isBlank()){
            return new OperationResult(false, "the path is blank");
        }
        
        Path newPath = Path.of(newBackupPath);
        newPath = resolveAbsoluteNormalizePath(newPath);

        if (newPath.equals(BACKUP_PATH)){
            return new OperationResult(false, "you are already using this path");
        }

        if (!newPath.toFile().isDirectory()){
            return new OperationResult(false, "the path is not a directory");
        } 
        if (!Files.isWritable(newPath)){
            return new OperationResult(false, "the path is not writable");
        }

        BACKUP_PATH = newPath;
        configs.setProperty("BACKUP_PATH", newPath.toString());
        storeProperties();
        return new OperationResult(true, "backup path changed successfully");
    }

    public Path getOriginPath() {
        return ORIGIN_PATH;
    }
    public Path getBackupPath() {
        return BACKUP_PATH;
    }
    //it checks if it needs to add to the path the proton prefix
    public Path resolveProtonPath(GameVersion version){
        Path protonPrefix = Path.of("Steam", "steamapps", "compatdata", "250900", "pfx", "drive_c", "users", "steamuser", "Documents", "My Games");
        Path gameFolder = version.getFolderName();
        if ((version == GameVersion.REPENTANCE || version == GameVersion.REPENTANCE_PLUS) && Config.isLinux){
            return protonPrefix.resolve(gameFolder);
        } else {
            return gameFolder;
        }
    }

    public boolean hasGameFolders(){

        boolean hasGameFolders;
        
        try (Stream<Path> files = Files.list(ORIGIN_PATH)) {
            
            hasGameFolders = files.anyMatch(this::matchAnyGameFolder);
            
            if (Config.isLinux && !hasGameFolders){
                try (Stream<Path> filesProton = Files.list(ORIGIN_PATH.resolve(Path.of("Steam", "steamapps", "compatdata", "250900", "pfx", "drive_c", "users", "steamuser", "Documents", "My Games")));){
                hasGameFolders = filesProton.anyMatch(this::matchAnyGameFolder);
                }
            }
            
        } catch (IOException e) {
            return false;
        }

        return hasGameFolders;
    }

    private boolean matchAnyGameFolder(Path folderPath){
        for(GameVersion name : GameVersion.values()){
            if (name.getFolderName().getFileName()
                                    .equals(folderPath.getFileName())){
                return true;
            }
        }   
        return false;
    }
}