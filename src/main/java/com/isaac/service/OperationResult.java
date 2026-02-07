package com.isaac.service;

import java.util.ArrayList;
import java.util.List;

public class OperationResult {
    private final boolean success;
    private final String errorMessage;
    private final List<String> failedFiles;

    public OperationResult (boolean success, String errorMessage){
        this.success = success;
        this.errorMessage = errorMessage;
        this.failedFiles = new ArrayList<>();
    }

    public OperationResult (boolean success, String errorMessage, ArrayList<String> failedFiles){
        this.success = success;
        this.errorMessage = errorMessage;
        this.failedFiles = failedFiles;
    }

    public boolean getSuccess(){
        return this.success;
    }

    public String getErrorMessage(){
        return this.errorMessage;
    }

    public List<String> getFailedFiles(){
        return failedFiles;
    }
}
