package com.isaac.service;

import java.nio.file.Path;

import com.isaac.Config;

public enum GameVersion {
    REBIRTH("Binding of Isaac Rebirth", "binding of isaac rebirth", "Binding of Isaac Rebirth"),
    AFTERBIRTH("Binding of Isaac Afterbirth", "binding of isaac afterbirth", "Binding of Isaac Afterbirth"),
    AFTERBIRTH_PLUS("Binding of Isaac Afterbirth+", "binding of isaac afterbirth+", "Binding of Isaac Afterbirth+"),
    REPENTANCE("Binding of Isaac Repentance", "Binding of Isaac Repentance", "not available"),
    REPENTANCE_PLUS("Binding of Isaac Repentance+", "Binding of Isaac Repentance+", "not available");

    private final String winFolder;
    private final String linuxFolder;
    private final String macFolder;

    GameVersion (String winFolder, String linuxFolder, String macFolder){
        this.winFolder = winFolder;
        this.linuxFolder = linuxFolder;
        this.macFolder = macFolder;
    }

    public Path getFolderName (){
        if (Config.isWindows){
            return Path.of(winFolder);
        } else if (Config.isLinux) {
            return Path.of(linuxFolder);
        } else if (Config.isMac) {
            return Path.of(macFolder);
        }

        throw new UnsupportedOperationException("Operative System not detected, or not supported.");
    } 

    public String getName (){
        return winFolder;
    }
}
    
