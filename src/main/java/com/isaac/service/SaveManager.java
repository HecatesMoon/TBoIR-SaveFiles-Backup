package com.isaac.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.isaac.Config;

public class SaveManager {

    private Config config;

    public SaveManager (Config config){
        this.config = config;
    }

    public OperationResult backup(GameVersion version){

        Path finalOriginPath = config.getOriginPath().resolve(config.resolveProtonPath(version));

        Path finalBackupPath = config.getBackupPath().resolve(version.getFolderName());

        //Validates if origin folder exists
        if (!Files.exists(finalOriginPath)){
            return new OperationResult(false, "Origin folder not found: " + finalOriginPath.toString());
        }

        //Validates if backup folder exists, if it doesn't, it creates it
        if (!Files.exists(finalBackupPath)){
            try {
                Files.createDirectories(finalBackupPath);
                System.out.println("Directory created: " + finalBackupPath);
            } catch (IOException e) {
                return new OperationResult(false, "Failed trying to create backup folder: " + e.getMessage());
            }
        }

        //Copies each file from origin folder to the backup folder, applying filters
        try (Stream<Path> saveFiles = Files.walk(finalOriginPath, 2)) {
            List<Path> filesToCopy = saveFiles.filter(IOUtils::isTBoIFile).toList();
            System.out.println("Making a backup...");
            List<String> failedFiles = new ArrayList<>();
            for(Path file:filesToCopy){
                try {
                    if (Files.isDirectory(file)){
                        Path folderInBackup = finalBackupPath.resolve(file.getFileName());
                        Files.createDirectories(folderInBackup);
                    } else if (Files.isRegularFile(file)){
                        IOUtils.copyFile(finalOriginPath, file, finalBackupPath);
                    }
                } catch (IOException e) {
                    failedFiles.add(file.toString() + " : " + e.getMessage());
                }
            }
            if (failedFiles.size() < 1){
                return new OperationResult(true, "Backup finished successfully");
            } else {
                return new OperationResult(false, "Backup finished with some problems", failedFiles);
            }
        } 
        catch (IOException e) {
            return new OperationResult(false, "Failed trying to copy fyles from origin: " + e.getMessage());
        }
    }
}
