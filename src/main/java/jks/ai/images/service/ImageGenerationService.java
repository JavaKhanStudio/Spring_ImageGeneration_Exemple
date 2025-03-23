package jks.ai.images.service;

import jks.ai.images.dto.GeneratedImageDTO;
import jks.ai.images.dto.UploadedImageDTO;
import jks.ai.images.entity.RecordedImage;
import jks.ai.images.repository.RecordedImagesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.image.Image;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ImageGenerationService {

    private final OpenAICallsService openAICallsService ;
    private final StabilityCallService stabilityCallService ;
    private final ImageUploadCloudinaryService imageUploadCloudinaryService;
    private final RecordedImagesRepository recordedImagesRepository ;

    private final MediaType imageType= MediaType.IMAGE_PNG ;

    public GeneratedImageDTO generateAndSaveImage(String prompt,
                                                  String provider) throws Exception {
        byte[] content = null;

        if ("dalle".equalsIgnoreCase(provider)) {
            content = getImageFromDalle(prompt);
        } else if ("stability".equalsIgnoreCase(provider)) {
            content = getImageFromStability(prompt);
        }

        RecordedImage recordedImage = saveImage(prompt,content) ;

        return GeneratedImageDTO
                .builder()
                .image(content)
                .internalID(recordedImage.getId())
                .externalID(recordedImage.getCloudID())
                .storageURL(recordedImage.getCloudURI())
                .build() ;
    }

    public byte[] getImageFromDalle(String prompt) throws Exception {
        String imageUrl = openAICallsService.generateWithDalle(prompt);

        if (imageUrl != null) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                URI uri = new URI(imageUrl); // preserve query params
                RequestEntity<Void> request = RequestEntity.get(uri).build();
                ResponseEntity<byte[]> imageResponse = restTemplate.exchange(request, byte[].class);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(imageType);
                return imageResponse.getBody() ;
            } catch (Exception e) {
                e.printStackTrace();
                throw e ;
            }
        }

        throw new Exception("No URL sent by OpenAI") ;
    }

    public byte[] getImageFromStability(String prompt) throws Exception {
        var result = stabilityCallService.getImage(prompt);

        if (result != null && result.getResult() != null && !result.getResults().isEmpty()) {
            Image image = result.getResult().getOutput();

            if (image.getB64Json() != null) {
                try {
                    return Base64.getDecoder().decode(image.getB64Json());
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    throw e ;
                }
            } else {
                throw new Exception("No B64 Json received") ;
            }
        }

        throw new Exception("No result Received from Stability") ;
    }

    public RecordedImage saveImage(String prompt, byte[] content) throws Exception {
        // Save on Cloud
        String fileName = generateFileNameFromPrompt(prompt, 100) ;
        UploadedImageDTO uploaderValue = imageUploadCloudinaryService.uploadImage(content) ;

        if(uploaderValue.URL() == null || uploaderValue.ID() == null) {
            throw new Exception("Error when uploading image to the cloud");
        }

        // Save on DB
        RecordedImage recordedImage = RecordedImage
                .builder()
                .imageName(fileName)
                .cloudURI(uploaderValue.URL())
                .cloudID(uploaderValue.ID())
                .prompt(prompt)
                .build();

        recordedImagesRepository.save(recordedImage) ;

        return recordedImage ;
    }

    public static String generateFileNameFromPrompt(String prompt, int maxLength) {
        if (prompt == null || prompt.isBlank()) return "file";

        StringBuilder fileName = new StringBuilder();
        String[] words = prompt.split("\\s+");

        for (String word : words) {
            // Remove non-alphanumeric characters
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "");
            if (cleanWord.isEmpty()) continue;

            // Check if adding this word would exceed the limit
            if (fileName.length() + cleanWord.length() + (fileName.length() > 0 ? 1 : 0) > maxLength) {
                break;
            }

            if (!fileName.isEmpty()) {
                fileName.append("_");
            }

            fileName.append(cleanWord);
        }

        return !fileName.isEmpty() ? fileName.toString().toLowerCase() + ".png" : "file";
    }

}
