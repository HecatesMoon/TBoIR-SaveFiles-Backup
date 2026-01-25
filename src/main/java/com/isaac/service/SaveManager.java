package com.isaac.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.isaac.Config;

public class SaveManager {
    public static void backup(){
        try (Stream<Path> saveFiles = Files.walk(Config.ORIGIN_PATH)) {
            saveFiles.forEach(file -> System.err.println(file));
        } catch (IOException e) {
            System.out.println("Failed trying to access folder: " + e.getMessage());
        }
    }
}
