package com.fp.FileParser.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Data
public class FileEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    private String fileName;
    private String fileType;
    private long size;

    @Enumerated(EnumType.STRING)
    private FileStatus status;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String parsedContent;

    private LocalDateTime createdAt;
}