package com.mwu.image_service.service;

import com.mwu.image_service.controller.ImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    ImageResponse storeImage(MultipartFile file, String type) throws IOException;
    byte[] retrieveImage(String id) throws IOException;
    void deleteImage(String id);
}
