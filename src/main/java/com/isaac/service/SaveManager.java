package com.isaac.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

import com.isaac.Config;

public class SaveManager {
    public static boolean backup(){

        //Validates if origin folder exists
        if (!Files.exists(Config.getOriginPath())){
            System.err.println("Origin folder not found: " + Config.getOriginPath());
            return false;
        }

        //Validates if backup folder exists, if it doesn't, it creates it
        if (!Files.exists(Config.getBackupPath())){
            try {
                Files.createDirectories(Config.getBackupPath());
                System.out.println("Directory created: " + Config.getBackupPath());
            } catch (IOException e) {
                System.err.println("Failed creating backup directory: " + e.getMessage());
                return false;
            }
        }

        //Copies each file from origin folder to the backup folder, applying filters
        try (Stream<Path> saveFiles = Files.walk(Config.getOriginPath(), 1)) {
            List<Path> filesToCopy = saveFiles.filter(file -> file.getFileName()
                                                        .toString()
                                                        .contains("persistentgamedata")).toList();
            System.out.println("Making a backup...");
            for(Path file:filesToCopy){
                copyFile(file, Config.getBackupPath());
            }
            return true;
        } 
        catch (IOException e) {
            System.err.println("Failed trying to copy files from origin: " + e.getMessage());
            return false;
        }
        
    }

    private static void copyFile(Path sourceFile, Path destinationPath) throws IOException{
        
            Path filePath = destinationPath.resolve(sourceFile.getFileName());
            Files.copy(sourceFile, filePath, StandardCopyOption.REPLACE_EXISTING);
        
    }
}
