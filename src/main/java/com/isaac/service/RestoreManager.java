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

    public boolean restore(){

        //Validates if backup folder exists
        if (!Files.exists(config.getBackupPath())){
            System.err.println("Backup folder not found: " + config.getBackupPath());
            return false;
        }
        //Validates if origin folder exists
        if (!Files.exists(config.getOriginPath())){
            System.err.println("Origin folder not found: " + config.getOriginPath());
            return false;
        }
        //Copies each file from backup folder to origin folder
        try (Stream<Path> saveFiles = Files.walk(config.getBackupPath(),1)) {
            List<Path> filesToCopy = saveFiles.filter(Files::isRegularFile).toList();
            System.out.println("Restoring from backup...");
            for (Path file : filesToCopy) {
                IOUtils.copyFile(file, config.getOriginPath());
            }
                     
            return true;
        } catch (IOException e) {
            System.err.println("Failed trying to copy files from backup: " + e.getMessage());
            return false;
        }
    }

}
