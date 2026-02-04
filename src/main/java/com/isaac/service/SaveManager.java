package com.isaac.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import com.isaac.Config;

public class SaveManager {

    private Config config;

    public SaveManager (Config config){
        this.config = config;
    }

    public boolean backup(GameVersion version){

        Path finalOriginPath;
        if ((version == GameVersion.REPENTANCE || version == GameVersion.REPENTANCE_PLUS) && Config.isLinux){
            finalOriginPath = config.getOriginPath().resolve(config.getProtonPath().resolve(version.getFolderName()));
        } else {
            finalOriginPath = config.getOriginPath().resolve(version.getFolderName());
        }

        Path finalBackupPath = config.getBackupPath().resolve(version.getFolderName());

        //Validates if origin folder exists
        if (!Files.exists(finalOriginPath)){
            System.err.println("Origin folder not found: " + finalOriginPath);
            return false;
        }

        //Validates if backup folder exists, if it doesn't, it creates it
        if (!Files.exists(finalBackupPath)){
            try {
                Files.createDirectories(finalBackupPath);
                System.out.println("Directory created: " + finalBackupPath);
            } catch (IOException e) {
                System.err.println("Failed creating backup directory: " + e.getMessage());
                return false;
            }
        }

        //Copies each file from origin folder to the backup folder, applying filters
        try (Stream<Path> saveFiles = Files.walk(finalOriginPath, 2)) {
            List<Path> filesToCopy = saveFiles.filter(IOUtils::isTBoIFile).toList();
            System.out.println("Making a backup...");
            //todo: add trycatch to create directory
            for(Path file:filesToCopy){
                if (Files.isDirectory(file)){
                    Path folderInBackup = finalBackupPath.resolve(file.getFileName());
                    Files.createDirectories(folderInBackup);
                } else if (Files.isRegularFile(file)){
                    IOUtils.copyFile( finalOriginPath, file, finalBackupPath);
                }
            }
            return true;
        } 
        catch (IOException e) {
            System.err.println("Failed trying to copy files from origin: " + e.getMessage());
            return false;
        }
    }
}
