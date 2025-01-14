package com.zx.common;

import jakarta.validation.constraints.NotNull;

public class FileInfo {
    @NotNull(message = "File storage path cannot be null")
    private String directoryPath;//文件相对路径

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }
}
