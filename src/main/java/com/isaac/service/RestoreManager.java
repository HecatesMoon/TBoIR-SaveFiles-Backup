package com.isaac.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

import com.isaac.Config;

public class RestoreManager {
    public static boolean restore(){

        //Validates if backup folder exists
        if (!Files.exists(Config.getBackupPath())){
            System.err.println("Backup folder not found: " + Config.getBackupPath());
            return false;
        }
        //Validates if origin folder exists
        if (!Files.exists(Config.getOriginPath())){
            System.err.println("Origin folder not found: " + Config.getOriginPath());
            return false;
        }
        //Copies each file from backup folder to origin folder
        try (Stream<Path> saveFiles = Files.walk(Config.getBackupPath(),1)) {
            List<Path> filesToCopy = saveFiles.filter(Files::isRegularFile).toList();
            System.out.println("Restoring from backup...");
            for (Path file : filesToCopy) {
                copyFile(file, Config.getOriginPath());
            }
                     
            return true;
        } catch (IOException e) {
            System.err.println("Failed trying to copy files from backup: " + e.getMessage());
            return false;
        }
    }

    private static void copyFile(Path sourceFile, Path destinationPath) throws IOException{
            Path filePath = destinationPath.resolve(sourceFile.getFileName());
            Files.copy(sourceFile, filePath, StandardCopyOption.REPLACE_EXISTING);
    }
}
