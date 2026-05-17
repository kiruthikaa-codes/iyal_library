package com.example.library.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    
    private final Cloudinary cloudinary;
    
    public String uploadImage(MultipartFile file) throws IOException {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(
            file.getBytes(),
            ObjectUtils.asMap(
                "folder", "iyal/books",
                "resource_type", "image"
            )
        );
        return (String) uploadResult.get("secure_url");
    }
    
    public void deleteImage(String imageUrl) throws IOException {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String publicId = extractPublicId(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        }
    }
    
    private String extractPublicId(String imageUrl) {
        try {
            String[] parts = imageUrl.split("/");
            String fileName = parts[parts.length - 1];
            String folder = parts[parts.length - 2];
            String publicId = folder + "/" + fileName.split("\\.")[0];
            return publicId;
        } catch (Exception e) {
            return null;
        }
    }
}
