package com.isaac.ui;

import java.util.Scanner;
import java.util.function.Predicate;

import com.isaac.Config;
import com.isaac.service.GameVersion;
import com.isaac.service.RestoreManager;
import com.isaac.service.SaveManager;

public class CLI {

    private Config config;
    private SaveManager saveManager;
    private RestoreManager restoreManager;

    private Scanner scanner = new Scanner(System.in);
    private boolean cliLoopRuns = true;
    private boolean pathChangeRuns;
    private boolean pickGameVersionRuns;

    public CLI (Config config, SaveManager saveManager, RestoreManager restoreManager){
        this.config = config;
        this.saveManager = saveManager;
        this.restoreManager = restoreManager;
    }

    public void cliLoop(){
        
        clearConsole();
        
        while (this.cliLoopRuns) {
            System.out.println("Welcome to TBoIR Backup Maker");
            printMenu("main"); 

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

        switch (choosenOption) {
            case "1": //prints actual paths
                System.out.println("Source Path: " + config.getOriginPath().toString());
                System.out.println("Backup Path: " + config.getBackupPath().toString());
                break;
            case "2": // backups data
                pickGameVersionLoop("backup");
                break;
            case "3": // restores backup
                pickGameVersionLoop("restore");
                break;
            case "4": // opens path configuration
                pathChangeLoop();
                break;
            case "5": // exits program
                System.out.println("Closing program");
                cliLoopRuns = false;
                break;
            default:
                System.out.println("Write a valid number");
                break;
        }
    }

    private void pickGameVersionLoop (String action){
        clearConsole();
        pickGameVersionRuns = true;

        while (pickGameVersionRuns) {
            System.out.println("-SELECT THE GAME VERSION-");
            System.out.println("Which game version do you want to use to" + action.toUpperCase() + "?");
            printMenu("gameversion");

            System.out.print(">");
            gameVersionOptions(scanner.nextLine().trim(), action);

            if (pickGameVersionRuns) {
                waitForEnter();
                clearConsole();
            }
        }

    }

    private void gameVersionOptions (String optionChoosen, String action){
        
        int optionChoosenInt = 0;
        GameVersion[] versions = GameVersion.values();
        Predicate<GameVersion> operation = (action.equals("backup")) ? saveManager::backup : restoreManager::restore;
        
        
        if (optionChoosen.matches("\\d+")) {
            optionChoosenInt = Integer.parseInt(optionChoosen);
        } 

        if (optionChoosenInt > 2 && optionChoosenInt <= versions.length+2){
                executeOperation(versions[optionChoosenInt-3], operation, action);
        }

        switch (optionChoosen) {
            case "1": //closes menu
                pickGameVersionRuns = false;
                break;
            case "2": //all tboi ver //todo: make a way to know which version are installed, like checking for folders on start
                for (GameVersion v : versions){
                    executeOperation(v, operation, action);
                }
                pickGameVersionRuns = false;
                break;
            default:
                System.out.println("Write a valid number");
                break;
        }

    }

    private void pathChangeLoop(){
        clearConsole();
        pathChangeRuns = true;

        while(pathChangeRuns){

            System.out.println("------CHANGE PATHS------");
            System.out.println("Origin Path:" + config.getOriginPath());
            System.out.println("Backup Path:" + config.getBackupPath());
            printMenu("paths");

            System.out.print("> ");
            changePath(this.scanner.nextLine().trim());

            if (pathChangeRuns){
                waitForEnter();
                clearConsole();
            }
        }
    }

    private void changePath(String choosenOption){
            
        System.out.println("The folder you choose has to contain the gamesave folder");
        System.out.println("eg: ../Documents/My Games/Binding of Isaac Rebirth/<your savefiles>");
        System.out.println("your path should be '../Documents/My games/'");
        
        System.out.println("Input the path you want to use for origin");
        System.out.print(">");

        switch (choosenOption) {
            case "1":// changes origin path
                if (config.setOriginPath(this.scanner.nextLine().trim())){
                    System.out.println("The origin path was changed succesfully");
                    System.out.println(config.getOriginPath());
                } else {
                    System.out.println("The path couldn't be changed, the path does not exist or is not writeable");
                }
                break;
            case "2":// changes backup path
                if (config.setBackupPath(this.scanner.nextLine().trim())){
                    System.out.println("The backup path was changed succesfully");
                    System.out.println(config.getBackupPath());
                } else {
                    System.out.println("The path couldn't be changed, the path does not exist or is not writeable");
                }
                break;
            case "3"://closes submenu
                System.out.println("Going back to main menu");
                pathChangeRuns = false;
                break;
            default:
                System.out.println("Write a valid number");
                break;
        }

    }

    private void printMenu(String whichMenu){
        switch (whichMenu) {
            case "main":
                System.out.println("----------MENU----------");
                System.out.println("1. Show current configured paths");
                System.out.println("2. Backup the savefiles (from source to backup folder)");
                System.out.println("3. Restore the savefiles (from backup to source folder)");
                System.out.println("4. Change Paths");
                System.out.println("5. Exit");
                System.out.println("What do you want to do? (press the number and then enter)");
                break;
            case "paths":
                System.out.println("----------MENU----------");
                System.out.println("1. Change Origin Path");
                System.out.println("2. Change Backup Path");
                System.out.println("3. Exit Path Configuration");
                System.out.println("What do you want to do? (press the number and then enter)");
                break;
            case "gameversion":
                System.out.println("----------MENU----------");
                System.out.println("1. Go back");
                System.out.println("2. All of them"); //todo: check if folders exist in backup method
                System.out.println("3. The Binding of Isaac Rebirth");
                System.out.println("4. The Binding of Isaac Afterbirth");
                System.out.println("5. The Binding of Isaac Afterbirth+");
                if (Config.isLinux || Config.isWindows){
                System.out.println("6. The Binding of Isaac Repentance");
                System.out.println("7. The Binding of Isaac Repentance+");
                }
                System.out.println("What do you want to do? (press the number and then enter)");
                break;
            default:
                System.out.println("The menu you called doesn't exist.");
                break;
        }
    }

    private void executeOperation(GameVersion version, Predicate<GameVersion> task, String taskName){
        if (task.test(version)){
            System.out.println(version.getName() + " "+ taskName + " was succesful");
        } else {
            System.out.println("There was an error while trying to " + taskName + " " + version.getName() + " savefiles");
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

}
