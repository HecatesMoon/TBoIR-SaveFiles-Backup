package com.isaac.service;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardCopyOption;

public class IOUtils {

    private final static FileSystem FS = FileSystems.getDefault();
    private final static PathMatcher SAVE_MATCHER = FS.getPathMatcher("glob:**persistentgamedata[1-3].dat");
    private final static PathMatcher GAMES_MATCHER = FS.getPathMatcher("glob:**gamestate[1-3].dat");

    public static void copyFile(Path sourceBase, Path sourceFile, Path destinationPath) throws IOException{
            Path filePath = destinationPath.resolve(sourceBase.relativize(sourceFile));
            Files.copy(sourceFile, filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void copyFile(Path sourceFile, Path destinationPath) throws IOException{
            Path filePath = destinationPath.resolve(sourceFile);
            Files.copy(sourceFile, filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    public static boolean isTBoIFile (Path file){
        if (Files.isDirectory(file) && file.getFileName().toString().equals("save_backups")){
            return true;
        }
        if (SAVE_MATCHER.matches(file) || GAMES_MATCHER.matches(file)){
            return true;
        }

        return false;
    }
}
