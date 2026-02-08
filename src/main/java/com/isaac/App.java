package com.isaac;

import com.isaac.service.OptionsManager;
import com.isaac.service.RestoreManager;
import com.isaac.service.SaveManager;
import com.isaac.ui.CLI;

public class App {
    public static void main(String[] args) {

        Config config = new Config();
        
        SaveManager saveManager = new SaveManager(config);
        RestoreManager restoreManager = new RestoreManager(config);
        OptionsManager optionsManager = new OptionsManager(config);

        CLI cli = new CLI(config, saveManager, restoreManager, optionsManager);
        cli.cliLoop();
    }
}
