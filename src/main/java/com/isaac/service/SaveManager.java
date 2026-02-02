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

    public boolean backup(){

        //Validates if origin folder exists
        if (!Files.exists(config.getOriginPath())){
            System.err.println("Origin folder not found: " + config.getOriginPath());
            return false;
        }

        //Validates if backup folder exists, if it doesn't, it creates it
        if (!Files.exists(config.getBackupPath())){
            try {
                Files.createDirectories(config.getBackupPath());
                System.out.println("Directory created: " + config.getBackupPath());
            } catch (IOException e) {
                System.err.println("Failed creating backup directory: " + e.getMessage());
                return false;
            }
        }

        //Copies each file from origin folder to the backup folder, applying filters
        try (Stream<Path> saveFiles = Files.walk(config.getOriginPath(), 1)) {
            List<Path> filesToCopy = saveFiles.filter(file -> file.getFileName()
                                                        .toString()
                                                        .contains("persistentgamedata"))
                                                        .toList();
            System.out.println("Making a backup...");
            for(Path file:filesToCopy){
                IOUtils.copyFile(file, config.getBackupPath());
            }
            return true;
        } 
        catch (IOException e) {
            System.err.println("Failed trying to copy files from origin: " + e.getMessage());
            return false;
        }
        
    }
}
