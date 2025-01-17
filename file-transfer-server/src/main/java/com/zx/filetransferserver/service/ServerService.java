package com.zx.filetransferserver.service;

import com.zx.common.FileInfo;
import com.zx.common.TransferDirInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    /*
        * 根据要同步的目录，查询目录下已同步的文件列表
     */
    public List<String> transferredFileList(TransferDirInfo transferDirInfo) {
        List<String> fileList = new ArrayList<>();
        File targetDirectory = new File(uploadDirectory, transferDirInfo.getTransferDir());
        //递归变量目录下的文件
        processDirectory(targetDirectory, fileList);
        return fileList;
    }

    private void processDirectory(File targetDirectory, List<String> fileList) {
        File[] files = targetDirectory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                fileList.add(file.getAbsolutePath().replace(uploadDirectory, "").replace(File.separator, "."));
            } else if (file.isDirectory()) {
                processDirectory(file, fileList);
            }
        }
    }
}
