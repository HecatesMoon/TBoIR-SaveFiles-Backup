package com.isaac;

import com.isaac.service.SaveManager;

public class App {
    public static void main(String[] args) {

        System.out.println("Ruta de origen: " + Config.ORIGIN_PATH);
        System.out.println("Ruta de origen: " + Config.BACKUP_PATH);
        SaveManager.backup();
    }
}
