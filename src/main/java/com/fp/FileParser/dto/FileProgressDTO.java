package com.fp.FileParser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileProgressDTO {
    private String fileId;
    private String status;
    private int progress;
}