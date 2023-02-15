package com.eighttechprojects.propertytaxshirol.Model;

public class FileUploadViewModel {

    String name;
    String path;
    boolean isServerFile;

    public FileUploadViewModel(String name, String path, boolean isServerFile) {
        this.name = name;
        this.path = path;
        this.isServerFile = isServerFile;
    }

    public boolean isServerFile() {
        return isServerFile;
    }

    public void setServerFile(boolean serverFile) {
        isServerFile = serverFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
