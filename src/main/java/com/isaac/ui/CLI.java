package com.isaac.ui;

import java.util.Scanner;

import com.isaac.Config;
import com.isaac.service.RestoreManager;
import com.isaac.service.SaveManager;

public class CLI {

    private Config config;
    private SaveManager saveManager;
    private RestoreManager restoreManager;

    private Scanner scanner = new Scanner(System.in);
    private boolean cliLoopRuns = true;
    private boolean pathChangeLoop;

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
                if (saveManager.backup()){
                    System.out.println("Backup Done!");
                } else {
                    System.err.println("Backup failed!");
                }
                
                break;
            case "3": // restores backup
                if (restoreManager.restore()){
                    System.out.println("Savefiles restored from backup!");
                } else {
                    System.err.println("Restoring failed");
                }
                break;
            case "4": // opens path configuration
                changePathLoop();
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

    private void changePathLoop(){
        clearConsole();
        pathChangeLoop = true;

        while(pathChangeLoop){

            System.out.println("------CHANGE PATHS------");
            System.out.println("Origin Path:" + config.getOriginPath());
            System.out.println("Backup Path:" + config.getBackupPath());
            printMenu("paths");

            System.out.print("> ");
            changePath(this.scanner.nextLine().trim());

            if (pathChangeLoop){
                waitForEnter();
                clearConsole();
            }
        }
    }

    private void changePath(String choosenOption){
        
        switch (choosenOption) {
            case "1":// changes origin path
                System.out.println("Input the path you want to use for origin");
                System.out.print(">");
                if (config.setOriginPath(this.scanner.nextLine().trim())){
                    System.out.println("The origin path was changed succesfully");
                    System.out.println(config.getOriginPath());
                } else {
                    System.out.println("The path couldn't be changed, the path does not exist or is not writeable");
                }
                break;
            case "2":// changes backup path
                System.out.println("Input the path you want to use for backup");
                System.out.print(">");
                if (config.setBackupPath(this.scanner.nextLine().trim())){
                    System.out.println("The backup path was changed succesfully");
                    System.out.println(config.getBackupPath());
                } else {
                    System.out.println("The path couldn't be changed, the path does not exist or is not writeable");
                }
                break;
            case "3"://closes submenu
                System.out.println("Going back to main menu");
                pathChangeLoop = false;
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
            default:
                System.out.println("Write a valid number");
                break;
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

    private void waitForEnter(){
        System.out.println("Press enter to continue...");
        this.scanner.nextLine();
    }

}
