package com.isaac;

import com.isaac.service.RestoreManager;
import com.isaac.service.SaveManager;
import com.isaac.ui.CLI;

public class App {
    public static void main(String[] args) {

        Config config = new Config();
        SaveManager saveManager = new SaveManager(config);
        RestoreManager restoreManager = new RestoreManager(config);
        CLI cli = new CLI(config, saveManager, restoreManager);
        cli.cliLoop();
    }
}
