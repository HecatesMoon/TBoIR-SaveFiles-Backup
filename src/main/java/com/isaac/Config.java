package com.isaac;

import java.nio.file.Path;

public class Config {
    //todo: add os compatibilty
    private static final String USER_HOME = System.getProperty("user.home");
    private static final String BASE_DIR = System.getProperty("user.dir");

    public static final Path ORIGIN_PATH = Path.of(USER_HOME, "Documents", "My Games", "Binding of Isaac Repentance+");
    public static final Path BACKUP_PATH = Path.of(BASE_DIR, "Backups");
}
