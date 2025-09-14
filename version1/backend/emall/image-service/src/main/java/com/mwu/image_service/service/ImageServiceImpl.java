package com.mwu.image_service.service;

import com.mwu.image_service.controller.ImageResponse;
import com.mwu.image_service.entity.ImageEntity;
import com.mwu.image_service.repository.ImageRepository;
import com.mwu.image_service.util.ImageCompressor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService{

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageCompressor imageCompressor;

    @Value("${server.port}")
    private String serverPort;

    @Value("${spring.application.name}")
    private String applicationName;
    @Override
    public ImageResponse storeImage(MultipartFile file, String type) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be null or empty");
        }

        String id = UUID.randomUUID().toString();

        byte[] originalDate = file.getBytes();
        byte[] compressedData = imageCompressor.compressImage(originalDate, type);

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setId(id);
        imageEntity.setName(file.getOriginalFilename());
        imageEntity.setType(type);
        imageEntity.setContentType(file.getContentType());
        imageEntity.setCompressedData(compressedData);
        imageEntity.setSize(originalDate.length);
        imageEntity.setCompressedSize(compressedData.length);
        imageEntity.setUploadedAt(LocalDateTime.now());

        imageRepository.save(imageEntity);
        String imageUrl = "/api/" + id;
        ImageResponse response = new ImageResponse();
        response.setId(id);
        response.setName(file.getOriginalFilename());
        response.setType(type);
        response.setOriginalSize(originalDate.length);
        response.setCompressedSize(compressedData.length);
        response.setUrl(imageUrl);

//        double compressionRatio = ((double)(originalDate.length - compressedData.length) / originalDate.length) * 100;
        double compressionRatio = 100.0 - ((double) compressedData.length / originalDate.length * 100.0);

        response.setCompressionRatio(Math.round(compressionRatio * 100.0) / 100.0 ); // rounding to 2 decimal places
        // Add dimensions to the response
        if ("product".equalsIgnoreCase(type)) {
            response.setWidth(600);
            response.setHeight(400);
        }
        return null;
    }

    @Override
    public byte[] retrieveImage(String id) throws IOException {
        ImageEntity imageEntity = imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", id));

        // Since we're using Thumbnailator, we don't need to decompress
        return imageEntity.getCompressedData();
    }

    @Override
    public void deleteImage(String id) {
        ImageEntity imageEntity = imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", id));

        imageRepository.delete(imageEntity);
    }
}
