package com.zx.filetransferserver.web;

import com.zx.common.FileInfo;
import com.zx.common.TransferDirInfo;
import com.zx.filetransferserver.service.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/server")
public class ServerController {
    @Value("${file.upload.apiRandomStr}")
    private String apiRandomStr;
    private ServerService fileService;

    public ServerController(ServerService serverService) {
        this.fileService = serverService;
    }

    @PostMapping("/upload/{secretCode}")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("directoryPath") String directoryPath, @PathVariable String secretCode) {
        if (!secretCode.equals(apiRandomStr)) {
            return ResponseEntity.status(404).body("Not found");
        }
        log.info("Received file upload request: {}/{}", directoryPath, file.getOriginalFilename());
        try {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setDirectoryPath(directoryPath);
            String storedPath = fileService.saveFile(file, fileInfo);
            return ResponseEntity.ok("File uploaded successfully to: " + storedPath);
        } catch (Exception e) {
            log.error("File upload failed: {}", e.getMessage());
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    /**
     * 根据要同步的目录，查询目录下已同步的文件列表
     * @param transferDirInfo
     * @return
     */
    @PostMapping("/transferred/file/list")
    public ResponseEntity<List<String>> transferredFileList(@RequestBody TransferDirInfo transferDirInfo) {
        List<String> fileList = fileService.transferredFileList(transferDirInfo);
        return ResponseEntity.ok(fileList);
    }
}
