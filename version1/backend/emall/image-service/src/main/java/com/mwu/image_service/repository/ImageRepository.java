package com.mwu.image_service.repository;

import com.mwu.image_service.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository  extends JpaRepository<ImageEntity, String> {
    List<ImageEntity> findByType(String type);

}
