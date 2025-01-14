package com.zx.filetransferserver.service;

import com.zx.common.FileInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class ServerService {
    @Value("${file.upload.directory}")
    private String uploadDirectory;

    public String saveFile(MultipartFile file, FileInfo fileInfo) throws IOException {
        String targetDirectory = fileInfo.getDirectoryPath();
        File directory = new File(uploadDirectory, targetDirectory);
        if (!directory.exists()) {
            directory.mkdirs(); // Create directory if it does not exist
        }

        // Save the file to the specified directory
        File targetFile = new File(directory, file.getOriginalFilename());
        file.transferTo(targetFile);

        return targetFile.getAbsolutePath();
    }
}
