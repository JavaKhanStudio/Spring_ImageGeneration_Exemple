package jks.ai.images.service;

import jks.ai.images.dto.UploadedImageDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {


    public UploadedImageDTO uploadImage(byte[] file) ;

}
