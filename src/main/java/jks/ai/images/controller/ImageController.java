package jks.ai.images.controller;

import jks.ai.images.dto.GeneratedImageDTO;
import jks.ai.images.service.ImageGenerationService;
import jks.ai.images.utils.ImageProviderEnum;
import jks.ai.images.utils.QualityEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/generate-image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageGenerationService imageGenerationService ;

    @GetMapping(value = "/justImage", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateImageAndSend(
            @RequestParam String prompt,
            @RequestParam ImageProviderEnum provider,
            @RequestParam(defaultValue = "Low") QualityEnum quality) {

        GeneratedImageDTO dto ;

        try {
            dto = imageGenerationService.generateAndSaveImage(prompt, provider, quality) ;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return ResponseEntity.ok().headers(headers).body(dto.image());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }

    }

    @GetMapping(value = "/fullDTO", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GeneratedImageDTO> generateImage(
            @RequestParam String prompt,
            @RequestParam ImageProviderEnum provider,
            @RequestParam(defaultValue = "Low") QualityEnum quality) {

        GeneratedImageDTO dto ;

        try {
            dto = imageGenerationService.generateAndSaveImage(prompt, provider, quality) ;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity
                .ok(dto);
    }

}