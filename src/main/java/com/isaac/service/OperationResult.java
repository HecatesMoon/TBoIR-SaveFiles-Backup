package com.isaac.service;

import java.util.ArrayList;
import java.util.List;

public class OperationResult {
    private final boolean success;
    private final String Message;
    private final List<String> failedFiles;

    public OperationResult (boolean success, String Message){
        this.success = success;
        this.Message = Message;
        this.failedFiles = new ArrayList<>();
    }

    public OperationResult (boolean success, String Message, ArrayList<String> failedFiles){
        this.success = success;
        this.Message = Message;
        this.failedFiles = failedFiles;
    }

    public boolean getSuccess(){
        return this.success;
    }

    public String getMessage(){
        return this.Message;
    }

    public List<String> getFailedFiles(){
        return failedFiles;
    }
}
