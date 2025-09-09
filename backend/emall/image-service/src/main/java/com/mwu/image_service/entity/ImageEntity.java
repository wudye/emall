package com.mwu.image_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "images")
public class ImageEntity {

    @Id
    private String id;

    private String name;
    private String type; // product, profile, etc.
    private String contentType;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] compressedData;

    private long size; // original size in bytes
    private long compressedSize; // compressed size in bytes

    @CurrentTimestamp
    private LocalDateTime uploadedAt;
}
