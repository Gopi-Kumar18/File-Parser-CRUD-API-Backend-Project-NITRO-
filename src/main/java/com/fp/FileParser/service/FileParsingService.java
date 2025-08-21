package com.fp.FileParser.service;

import com.fp.FileParser.entity.FileEntity;
import com.fp.FileParser.entity.FileStatus;
import com.fp.FileParser.repository.FileRepository;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileParsingService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ProgressService progressService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Async("taskExecutor")
    public void parseFile(String fileId) {
        FileEntity fileEntity = fileRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));
        fileEntity.setStatus(FileStatus.PROCESSING);
        fileRepository.save(fileEntity);
        progressService.updateProgress(fileId, FileStatus.PROCESSING, 10);

        try {
            // Simulate parsing progress
            Thread.sleep(2000);
            progressService.updateProgress(fileId, FileStatus.PROCESSING, 50);

            String filePath = Paths.get(uploadDir, fileEntity.getFileName()).toString();
            // Assuming CSV for simplicity
            try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
                List<String[]> allRows = reader.readAll();
                String json = allRows.stream()
                        .map(row -> "{" + Arrays.stream(row).map(cell -> "\"" + cell + "\"").collect(Collectors.joining(",")) + "}")
                        .collect(Collectors.joining(",\n"));
                fileEntity.setParsedContent("[" + json + "]");
            }

            Thread.sleep(2000);
            fileEntity.setStatus(FileStatus.READY);
            fileRepository.save(fileEntity);
            progressService.updateProgress(fileId, FileStatus.READY, 100);

        } catch (Exception e) {
            fileEntity.setStatus(FileStatus.FAILED);
            fileRepository.save(fileEntity);
            progressService.updateProgress(fileId, FileStatus.FAILED, 0);
            e.printStackTrace();
        }
    }

    public Object getFileContent(String fileId) {
        FileEntity fileEntity = fileRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));
        if (fileEntity.getStatus() == FileStatus.READY) {
            return fileEntity.getParsedContent();
        }
        return "File upload or processing in progress. Please try again later.";
    }
}