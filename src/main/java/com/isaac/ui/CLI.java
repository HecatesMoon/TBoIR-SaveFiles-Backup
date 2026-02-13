package com.isaac.ui;

import java.util.Scanner;
import java.util.function.Function;

import com.isaac.Config;
import com.isaac.service.Action;
import com.isaac.service.GameVersion;
import com.isaac.service.IOUtils;
import com.isaac.service.OperationResult;
import com.isaac.service.OptionsManager;
import com.isaac.service.RestoreManager;
import com.isaac.service.SaveManager;

public class CLI {

    private final Config config;
    private final SaveManager saveManager;
    private final RestoreManager restoreManager;
    private final OptionsManager optionsManager;

    private final Scanner scanner = new Scanner(System.in);

    private boolean cliLoopRuns = true;
    private boolean pathChangeRuns;
    private boolean pickGameVersionRuns;

    public CLI (Config config, SaveManager saveManager, RestoreManager restoreManager, OptionsManager optionsManager){
        this.config = config;
        this.saveManager = saveManager;
        this.restoreManager = restoreManager;
        this.optionsManager = optionsManager;
    }

    public void cliLoop(){
        
        clearConsole();
        
        while (this.cliLoopRuns) {
            System.out.println("Welcome to TBoIR Backup Maker");
            printMenu(Menu.MAIN); 

            System.out.print("> ");
            menuOption(this.scanner.nextLine().trim());
            
            if (cliLoopRuns){
                waitForEnter();
                clearConsole();
            }
        }
        scanner.close();
    };

    private void menuOption(String choosenOption){

        int choosenOptionInt;

        try {
            choosenOptionInt = IOUtils.checkNumberInput(choosenOption);
        } catch (NumberFormatException e) {
            System.err.println("Invalid input, number is out of limits: " + e.getMessage());
            choosenOptionInt = -1;
        }

        switch (choosenOptionInt) {
            case 1: //prints actual paths
                System.out.println("Source Path: " + config.getOriginPath().toString());
                System.out.println("Backup Path: " + config.getBackupPath().toString());
                break;
            case 2: // backups data
                pickGameVersionLoop(Action.BACKUP);
                break;
            case 3: // restores backup
                pickGameVersionLoop(Action.RESTORE);
                break;
            case 4: // opens path configuration
                pathChangeLoop();
                break;
            case 5: // toggles steam cloud
                pickGameVersionLoop(Action.TOGGLE_STEAMCLOUD);
            break;
            case 0: // exits program
                System.out.println("Closing program");
                cliLoopRuns = false;
                break;
            default:
                System.out.println("Write a valid number");
                break;
        }
    }

    private void pickGameVersionLoop (Action action){
        clearConsole();
        pickGameVersionRuns = true;

        while (pickGameVersionRuns) {

            System.out.println("-SELECT THE GAME VERSION-");
            System.out.println("Which game version do you want to use to " + action.getName() + "?");

            if (action == Action.TOGGLE_STEAMCLOUD){
                printMenu(Menu.STEAM_CLOUD_TOGGLE);
            } else {
                printMenu(Menu.GAME_VERSION);
            }

            System.out.print("> ");
            gameVersionOptions(scanner.nextLine().trim(), action);

            if (pickGameVersionRuns) {
                waitForEnter();
                clearConsole();
            }
        }

    }

    private void gameVersionOptions (String optionChoosen, Action action){
        
        int optionChoosenInt;

        try {
            optionChoosenInt = IOUtils.checkNumberInput(optionChoosen);
        } catch (NumberFormatException e) {
            System.err.println("Invalid input, number is out of limits: " + e.getMessage());
            optionChoosenInt = -1;
        }
        
        int maxOption = GameVersion.values().length + 1;

        if (action == Action.BACKUP || action == Action.RESTORE){
            maxOption++;
        }

        // checks if number is valid
        if (optionChoosenInt < 0 || optionChoosenInt > maxOption){
            System.out.println("Write a valid number");
            return;
        }

        GameVersion[] versions = GameVersion.values();
        Function<GameVersion, OperationResult> operation = v -> new OperationResult(true, "No action selected");
        
        switch (action) {
            case Action.BACKUP:
            operation = saveManager::backup;
                break;
            case Action.RESTORE:
            operation = restoreManager::restore;
                break;
            case Action.TOGGLE_STEAMCLOUD:
            operation = optionsManager::switchSteamCloud;
                break;
            default:
            System.out.println("Unknown action: " + action.name());
                break;
        }

        //exit menu
        if (optionChoosenInt == 0){
            pickGameVersionRuns = false;
            return;
        }
        //apply operation to all
        if (optionChoosenInt == 6 && action != Action.TOGGLE_STEAMCLOUD){
            executeOperationForAll(action, operation);
            pickGameVersionRuns = false;
            return;
        }
        //execute operation to selected game
        if ((optionChoosenInt >= 1) && (optionChoosenInt <= versions.length)){
            executeOperation(versions[optionChoosenInt-1], operation, action);
            pickGameVersionRuns = false;
            return;
        }
    }

    private void pathChangeLoop(){
        clearConsole();
        pathChangeRuns = true;

        while(pathChangeRuns){

            System.out.println("------CHANGE PATHS------");
            System.out.println("Origin Path:" + config.getOriginPath());
            System.out.println("Backup Path:" + config.getBackupPath());
            printMenu(Menu.PATH_CHANGE);

            System.out.print("> ");
            changePath(this.scanner.nextLine().trim());

            if (pathChangeRuns){
                waitForEnter();
                clearConsole();
            }
        }
    }

    private void changePath(String choosenOption){
            
        int choosenOptionInt;

        try {
            choosenOptionInt = IOUtils.checkNumberInput(choosenOption);
        } catch (NumberFormatException e) {
            System.err.println("Invalid input, number is out of limits: " + e.getMessage());
            choosenOptionInt = -1;
        }

        System.out.println("The folder you choose has to contain the savefile folder");
        System.out.println("eg: ../Documents/My Games/Binding of Isaac Rebirth/<your savefiles>");
        System.out.println("your path should be '../Documents/My games/'");
        
        System.out.println("Input the path you want to use for origin");

        //todo: consider being more specific in errors when changing paths
        switch (choosenOptionInt) {
            case 1:// changes origin path
                showChangePathResult(config.setOriginPath(this.scanner.nextLine().trim()));
                break;
            case 2:// changes backup path
                showChangePathResult(config.setBackupPath(this.scanner.nextLine().trim()));
                break;
            case 0://closes submenu
                System.out.println("Going back to main menu");
                pathChangeRuns = false;
                break;
            default:
                System.out.println("Write a valid number");
                break;
        }

    }

    private void printMenu(Menu menu){
        switch (menu) {
            case Menu.MAIN:
                System.out.println("----------MENU----------");
                System.out.println("1. Show current configured paths");
                System.out.println("2. Backup the savefiles (from source to backup folder)");
                System.out.println("3. Restore the savefiles (from backup to source folder)");
                System.out.println("4. Change Paths");
                System.out.println("5. toggle steamcloud (choose game)");
                System.out.println("0. Exit");
                System.out.println("What do you want to do? (press the number and then enter)");
                break;
            case Menu.PATH_CHANGE:
                System.out.println("----------MENU----------");
                System.out.println("1. Change Origin Path");
                System.out.println("2. Change Backup Path");
                System.out.println("0. Exit Path Configuration");
                System.out.println("What do you want to do? (press the number and then enter)");
                break;
            case Menu.GAME_VERSION:
                System.out.println("----------MENU----------"); //todo: check if folders exist in backup method
                System.out.println("1. The Binding of Isaac Rebirth");
                System.out.println("2. The Binding of Isaac Afterbirth");
                System.out.println("3. The Binding of Isaac Afterbirth+");
                if (Config.isLinux || Config.isWindows){
                System.out.println("4. The Binding of Isaac Repentance");
                System.out.println("5. The Binding of Isaac Repentance+");
                }
                System.out.println("6. All of them");
                System.out.println("0. Go back");
                System.out.println("What do you want to do? (press the number and then enter)");
                break;
            case Menu.STEAM_CLOUD_TOGGLE:
                System.out.println("----------MENU----------");
                System.out.println("1. The Binding of Isaac Rebirth | " + showSteamCloudStatus(GameVersion.REBIRTH));
                System.out.println("2. The Binding of Isaac Afterbirth| " + showSteamCloudStatus(GameVersion.AFTERBIRTH));
                System.out.println("3. The Binding of Isaac Afterbirth+ | " + showSteamCloudStatus(GameVersion.AFTERBIRTH_PLUS));
                if (Config.isLinux || Config.isWindows){
                System.out.println("4. The Binding of Isaac Repentance | " + showSteamCloudStatus(GameVersion.REPENTANCE));
                System.out.println("5. The Binding of Isaac Repentance+ | " + showSteamCloudStatus(GameVersion.REPENTANCE_PLUS));
                }
                System.out.println("0. Go back");
                System.out.println("What do you want to do? (press the number and then enter)");
                break;
            default:
                System.out.println("Unknown menu option: " + menu.name());
                break;
        }
    }

    private void executeOperation(GameVersion version, Function<GameVersion, OperationResult> task, Action action){
        OperationResult result = task.apply(version);
        if (result.getSuccess()){
            System.out.println("Success: " + result.getMessage());
        } else {
            System.out.println("There was an error while trying to " + action.getName() + " " + version.getName() + " savefiles");
            System.out.println(result.getMessage());
            if (result.getFailedFiles().size() > 0) {
                System.out.println("Files with problems:");
                for (String fileMessage : result.getFailedFiles()) {
                    System.out.println(fileMessage);
                }
            }
        }
    }

    private void executeOperationForAll(Action action, Function<GameVersion, OperationResult> operation){
        for (GameVersion v : GameVersion.values()){
                    executeOperation(v, operation, action);
                    System.out.println();
            }
    }

    private void showChangePathResult (OperationResult operation){
        if (operation.getSuccess()){
            System.out.println("Success: " + operation.getMessage());
        } else {
            System.out.println("The path couldn't be changed: " + operation.getMessage());
        }
    }

    private void clearConsole(){
        try{
            if (Config.isWindows){
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch(Exception e) {
            for (int i = 0; i < 50;i++) System.out.println();
        } 
    }

    private void waitForEnter(){ //todo: not all options need a wait for enter function
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Press enter to continue...");
        this.scanner.nextLine();
    }

    private String showSteamCloudStatus(GameVersion version){
        return optionsManager.getSteamCloudStatus(version).getMessage();
    }
}