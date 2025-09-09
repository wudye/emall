package com.mwu.image_service.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse {
    private String id;
    private String name;
    private String type;
    private long originalSize;
    private long compressedSize;
    private String url;
    private double compressionRatio;
    private Integer width;
    private Integer height;
}