package com.fp.FileParser.service;

import com.fp.FileParser.entity.FileEntity;
import com.fp.FileParser.entity.FileStatus;
import com.fp.FileParser.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public FileEntity storeFile(MultipartFile file) {
        String fileName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileName(fileName);
            fileEntity.setFileType(file.getContentType());
            fileEntity.setSize(file.getSize());
            fileEntity.setStatus(FileStatus.UPLOADING);
            fileEntity.setCreatedAt(LocalDateTime.now());
            return fileRepository.save(fileEntity);

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public List<FileEntity> getAllFiles() {
        return fileRepository.findAll();
    }

    public void deleteFile(String fileId) {
        FileEntity fileEntity = fileRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));
        try {
            Path filePath = this.fileStorageLocation.resolve(fileEntity.getFileName()).normalize();
            Files.deleteIfExists(filePath);
            fileRepository.delete(fileEntity);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file " + fileEntity.getFileName() + ". Please try again!", ex);
        }
    }
}
