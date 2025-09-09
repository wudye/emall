package com.mwu.productService.feignClient;


import com.mwu.productService.DTO.ImageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "image-service")
public interface ImageClient {

    @PostMapping(value = "/api/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ImageResponse> uploadImage(
            @RequestPart("image") MultipartFile image,
            @RequestParam("type") String type);

    @GetMapping(value = "/api/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<byte[]> getImage(@PathVariable String imageId);

    @DeleteMapping("/api/{imageId}")
    ResponseEntity<Void> deleteImage(@PathVariable String imageId);
}
