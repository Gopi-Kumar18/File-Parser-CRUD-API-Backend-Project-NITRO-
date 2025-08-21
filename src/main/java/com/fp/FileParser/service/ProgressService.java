package com.fp.FileParser.service;

import com.fp.FileParser.dto.FileProgressDTO;
import com.fp.FileParser.entity.FileStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProgressService {

    private final Map<String, FileProgressDTO> progressMap = new ConcurrentHashMap<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void updateProgress(String fileId, FileStatus status, int progress) {
        FileProgressDTO progressDTO = new FileProgressDTO(fileId, status.name(), progress);
        progressMap.put(fileId, progressDTO);
        messagingTemplate.convertAndSend("/topic/progress/" + fileId, progressDTO);
    }

    public FileProgressDTO getProgress(String fileId) {
        return progressMap.getOrDefault(fileId, new FileProgressDTO(fileId, "NOT_FOUND", 0));
    }
}
