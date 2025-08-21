package com.fp.FileParser.controller;

import com.fp.FileParser.entity.FileEntity;
import com.fp.FileParser.service.FileParsingService;
import com.fp.FileParser.service.FileStorageService;
import com.fp.FileParser.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileParsingService fileParsingService;

    @Autowired
    private ProgressService progressService;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        FileEntity fileEntity = fileStorageService.storeFile(file);
        fileParsingService.parseFile(fileEntity.getId());
        return ResponseEntity.ok(fileEntity);
    }

    @GetMapping("/{fileId}/progress")
    public ResponseEntity<?> getFileProgress(@PathVariable String fileId) {
        return ResponseEntity.ok(progressService.getProgress(fileId));
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<?> getFile(@PathVariable String fileId) {
        return ResponseEntity.ok(fileParsingService.getFileContent(fileId));
    }

    @GetMapping
    public ResponseEntity<List<FileEntity>> listFiles() {
        return ResponseEntity.ok(fileStorageService.getAllFiles());
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileId) {
        fileStorageService.deleteFile(fileId);
        return ResponseEntity.ok("File deleted successfully");
    }
}