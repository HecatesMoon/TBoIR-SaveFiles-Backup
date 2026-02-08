package com.isaac.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.isaac.Config;

public class RestoreManager {

    private Config config;

    public RestoreManager (Config config){
        this.config = config;
    }

    public OperationResult restore(GameVersion version){

        Path finalOriginPath = config.getOriginPath().resolve(config.resolveProtonPath(version));

        Path finalBackupPath = config.getBackupPath().resolve(version.getFolderName());

        //Validates if game folder inside the backup folder exists
        if (!Files.exists(finalBackupPath)){
            return new OperationResult(false, "Backup folder not found: " + finalBackupPath.toString());
        }
        //Validates if origin folder exists
        if (!Files.exists(finalOriginPath)){
            System.out.println("Origin directory couldn't be found");
            System.out.println("Creating directory...");
            try {
                Files.createDirectories(finalOriginPath);
                System.out.println("Directory created: " + finalOriginPath);
            } catch (IOException e) {
                return new OperationResult(false, "Failed trying to create inexistent origin folder: " + e.getMessage());
            }
        }
        //Copies each file from backup folder to origin folder
        try (Stream<Path> saveFiles = Files.walk(finalBackupPath,2)) {
            List<Path> filesToCopy = saveFiles.filter(IOUtils::isTBoIFile).toList();
            System.out.println("Restoring from backup...");
            List<String> failedFiles = new ArrayList<>();
            for (Path file : filesToCopy) {
                try {
                    if (Files.isDirectory(file)){
                        Path folderInOrigin = finalOriginPath.resolve(file.getFileName());
                        Files.createDirectories(folderInOrigin); //todo: create folder method?
                    } else if (Files.isRegularFile(file)){
                        IOUtils.copyFile(finalBackupPath, file, finalOriginPath);
                    }
                } catch (IOException e) {
                    failedFiles.add(file.toString() + " : " + e.getMessage());
                }
            }
                     
            if (failedFiles.size() < 1){
                return new OperationResult(true, "Restore finished succesfully");
            } else {
                return new OperationResult(false, "Restore finished with some problems", failedFiles);
            }
        } catch (IOException e) {
            return new OperationResult(false, "Failed trying to copy files from backup: " + e.getMessage());
        }
    }

}
