package com.isaac.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.isaac.Config;

public class OptionsManager {

    private final Path gamesFolder;

    public OptionsManager (Config config){
        this.gamesFolder = config.getOriginPath();
    }

    public OperationResult switchSteamCloud (GameVersion version){

        try{
            if (isSteamCloudOn(version)) {
                return changeOption(version, "SteamCloud", "0");
            } else {
                return changeOption(version, "SteamCloud", "1");
            }
        } catch(IOException e) {
            return new OperationResult(false, "Couldn't read options file: " + e.getMessage());
        }
    }

    private boolean isSteamCloudOn(GameVersion version) throws IOException{

        Path optionFile = getOptionsPath(version);

        List<String> allLines = Files.readAllLines(optionFile);
        return allLines.stream().anyMatch(line -> line.toLowerCase().replace(" ", "").startsWith("steamcloud=1"));
    }

    private OperationResult changeOption(GameVersion version, String key, String newValue){
        
        Path selectedGameFolder = gamesFolder.resolve(version.getFolderName());

        Path optionsFile = getOptionsPath(version);
        Path backupFile = selectedGameFolder.resolve("options.ini.bak");
        Path originalBackupFile = selectedGameFolder.resolve("options.ini.original");

        try{
            if (Files.exists(optionsFile) && !Files.exists(originalBackupFile)){
                    Files.copy(optionsFile, originalBackupFile);
            }
            if (Files.exists(optionsFile)){
                Files.copy(optionsFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e){
            return new OperationResult(false, "Couldn't create backups, will not change option lines: " + e.getMessage());
        }

        try{
            List<String> allLines = Files.readAllLines(optionsFile);
            List<String> updatedLines = allLines.stream().map(line -> line.toLowerCase().startsWith(key.toLowerCase()+"=") ? line.split("=")[0].trim()+"="+newValue : line).toList();
            Files.write(optionsFile, updatedLines);
            return new OperationResult(true, "Line changed succesfully, " + key + "=" + newValue);
        } catch (IOException e) {
            return new OperationResult(false, "Couldn't change line in option: " + e.getMessage());
        }
    }

    private Path getOptionsPath(GameVersion version){
        return gamesFolder.resolve(version.getFolderName()).resolve("options.ini");
    }

}
