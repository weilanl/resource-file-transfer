package com.zx.filetransferclient.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Repository
public class ClientService {
    @Value("${upload.server.url}")
    private String url;
    @Value("${upload.server.apiRandomStr}")
    private String apiRandomStr;
    private final ExecutorService executorService = Executors.newFixedThreadPool(8);

    public void uploadFilesFromFolder(String folderPath) {
        File folder = new File(folderPath);

        // 验证文件夹是否存在
        if (!folder.exists() || !folder.isDirectory()) {
            log.error("The provided path is not a valid folder: {}", folderPath);
            return;
        }

        // 开始递归上传
        processFolder(folder);

        // 关闭线程池（等待所有任务完成后关闭）
        executorService.shutdown();
    }

    private void processFolder(File folder) {
        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            log.info("No files found in the folder: {}", folder.getAbsolutePath());
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                // 将每个文件的上传任务提交到线程池
                executorService.submit(() -> uploadFile(file));
            } else if (file.isDirectory()) {
                // 递归处理子文件夹
                processFolder(file);
            }
        }
    }

    private void uploadFile(File file) {
        RestTemplate restTemplate = new RestTemplate();

        // 准备请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 准备文件和文件信息
        FileSystemResource fileResource = new FileSystemResource(file);

        // 构建多部分请求
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);
        body.add("directoryPath", file.getParent()); // 添加 FileInfo 信息

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url + apiRandomStr, requestEntity, String.class);
            log.info("Uploaded file: {} - Response: {}", file.getAbsolutePath(), response.getBody());
        } catch (Exception e) {
            log.error("Failed to upload file: {} - Error: {}", file.getAbsolutePath(), e.getMessage());
        }
    }
}
