package com.isaac.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import com.isaac.Config;

public class RestoreManager {

    private Config config;

    public RestoreManager (Config config){
        this.config = config;
    }

    public boolean restore(GameVersion version){

        Path finalOriginPath = config.getOriginPath().resolve(config.ProtonUsageCheck(version));

        Path finalBackupPath = config.getBackupPath().resolve(version.getFolderName());

        //Validates if game folder inside the backup folder exists
        if (!Files.exists(finalBackupPath)){
            System.err.println("Backup folder not found: " + finalBackupPath);
            return false;
        }
        //Validates if origin folder exists
        if (!Files.exists(finalOriginPath)){
            System.out.println("Origin directory couldn't be found");
            System.out.println("Creating directory...");
            try {
                Files.createDirectories(finalOriginPath);
                System.out.println("Directory created: " + finalOriginPath);
            } catch (IOException e) {
                System.err.println("Failed creating needed directory: " + e.getMessage());
                return false;
            }
        }
        //Copies each file from backup folder to origin folder
        try (Stream<Path> saveFiles = Files.walk(finalBackupPath,2)) {
            List<Path> filesToCopy = saveFiles.filter(IOUtils::isTBoIFile).toList();
            System.out.println("Restoring from backup...");
            for (Path file : filesToCopy) {
                if (Files.isDirectory(file)){
                    Path folderInOrigin = finalOriginPath.resolve(file.getFileName());
                    try {
                        Files.createDirectories(folderInOrigin);
                    } catch (IOException e) {
                        System.err.println("Restoring couldn't end properly, try again: " + e.getMessage());
                        return false;
                    }
                } else if (Files.isRegularFile(file)){
                    IOUtils.copyFile(finalBackupPath, file, finalOriginPath);
                }
            }
                     
            return true;
        } catch (IOException e) {
            System.err.println("Failed trying to copy files from backup: " + e.getMessage());
            return false;
        }
    }

}
