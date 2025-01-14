package com.zx.filetransferclient;

import com.zx.filetransferclient.service.ClientService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {
    private ClientService fileUploadService;

    public Application(ClientService clientService) {
        this.fileUploadService = clientService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        if (args.length == 0) {
            System.err.println("Please provide the target folder path as a command-line argument.");
            return;
        }

        String folderPath = args[0];
        fileUploadService.uploadFilesFromFolder(folderPath);
    }
}
