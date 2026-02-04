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

        Path finalOriginPath;

        if ((version == GameVersion.REPENTANCE || version == GameVersion.REPENTANCE_PLUS) && Config.isLinux){
            finalOriginPath = config.getOriginPath().resolve(config.getProtonPath().resolve(version.getFolderName()));
        } else {
            finalOriginPath = config.getOriginPath().resolve(version.getFolderName());
        }

        Path finalBackupPath = config.getBackupPath().resolve(version.getFolderName());

        //Validates if backup folder exists
        if (!Files.exists(finalBackupPath)){
            System.err.println("Backup folder not found: " + finalBackupPath);
            return false;
        }
        //Validates if origin folder exists
        if (!Files.exists(finalOriginPath)){
            System.err.println("Origin folder not found: " + finalOriginPath);
            return false;
        }
        //Copies each file from backup folder to origin folder
        try (Stream<Path> saveFiles = Files.walk(finalBackupPath,2)) {
            List<Path> filesToCopy = saveFiles.filter(IOUtils::isTBoIFile).toList();
            System.out.println("Restoring from backup...");
            //todo: add trycatch to create directory
            for (Path file : filesToCopy) {
                if (Files.isDirectory(file)){
                    Path folderInOrigin = finalOriginPath.resolve(file.getFileName());
                    Files.createDirectories(folderInOrigin);
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
