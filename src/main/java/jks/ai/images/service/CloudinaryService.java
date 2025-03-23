package jks.ai.images.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import jks.ai.images.utils.CloudinaryEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static jks.ai.images.utils.CloudinaryEnum.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private final String folder = "AI_Image_Uploader";


    public Map<String, String> uploadImage(MultipartFile file) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "auto"
                    )
            );

            return Map.of(
                    SECURE_URL.value, uploadResult.get(SECURE_URL.value).toString(),
                    PUBLIC_ID.value, uploadResult.get(PUBLIC_ID.value).toString()
            );
        } catch (IOException e) {
            log.error("Error uploading image to Cloudinary", e);
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            log.error("Error deleting image from Cloudinary", e);
            throw new RuntimeException("Failed to delete image", e);
        }
    }

    public List<Map<String, String>> getAllCloudinaryImages() {
        try {
            ApiResponse response = cloudinary.api().resources(
                    ObjectUtils.asMap(
                            "type", "upload",
                            "prefix", "card-manager/",
                            "max_results", 500
                    )
            );

            List<Map<String, String>> images = new ArrayList<>();

            List<Map<String, Object>> resources = (List<Map<String, Object>>) response.get("resources");

            for (Map<String, Object> resource : resources) {
                String publicId = (String) resource.get("public_id");
                String url = (String) resource.get("secure_url");
                String format = (String) resource.get("format");
                String fileName = publicId.substring(publicId.lastIndexOf("/") + 1);

                images.add(Map.of(
                        "publicId", publicId,
                        "url", url,
                        "format", format,
                        "fileName", fileName
                ));
            }

            return images;
        } catch (Exception e) {
            log.error("Error fetching images from Cloudinary", e);
            throw new RuntimeException("Failed to fetch images from Cloudinary", e);
        }
    }
}