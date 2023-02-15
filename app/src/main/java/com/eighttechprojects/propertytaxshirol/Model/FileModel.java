package com.eighttechprojects.propertytaxshirol.Model;

import java.util.ArrayList;

public class FileModel {

    private String path;

    private ArrayList<String> multiPaths;
    private boolean isLocal = false;

    public FileModel(String path, boolean isLocal) {
        this.path = path;
        this.isLocal = isLocal;
    }

    public FileModel(ArrayList<String> multiPaths, boolean isLocal) {
        this.multiPaths = multiPaths;
        this.isLocal = isLocal;
    }

    public ArrayList<String> getMultiPaths() {
        return multiPaths;
    }

    public void setMultiPaths(ArrayList<String> multiPaths) {
        this.multiPaths = multiPaths;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

}
