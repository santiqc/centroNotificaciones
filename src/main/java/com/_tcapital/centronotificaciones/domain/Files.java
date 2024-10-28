package com._tcapital.centronotificaciones.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "files")
public class Files {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_file", nullable = false)
    private String nameFile;

    @Column(name = "tracking_id")
    private String trackingId;

    @Column(name = "content_type")
    private String contentType;

    @Lob
    @Column(name = "file_data")
    private byte[] fileData;

    @Column(name = "file_size")
    private Long fileSize;

    @CreationTimestamp
    @Column(name = "upload_date", nullable = false, updatable = false)
    private LocalDateTime uploadDate;

    @Column(name = "witness")
    private Boolean witness;
}
