package jks.ai.images.service;

import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.stabilityai.StabilityAiImageModel;
import org.springframework.ai.stabilityai.api.StabilityAiApi;
import org.springframework.ai.stabilityai.api.StabilityAiImageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StabilityCallService {

    private final StabilityAiImageModel stabilityAiImageModel ;

    public StabilityCallService(@Value("${spring.ai.stabilityai.api-key}")
                                String apiKey) {
        StabilityAiApi api = new StabilityAiApi(apiKey) ;
        StabilityAiImageOptions options = StabilityAiImageOptions.builder()
                .stylePreset("cinematic")
                .N(1)
                .height(1024)
                .width(1024).build() ;

        stabilityAiImageModel = new StabilityAiImageModel(api, options) ;
    }

    public ImageResponse getImage(String prompt) {
        return stabilityAiImageModel.call(
                new ImagePrompt(prompt)
        );
    }

    public ImageResponse getImage(String prompt, String stylePreset, int width, int height) {
        ImageResponse response = stabilityAiImageModel.call(
                new ImagePrompt(prompt,
                        StabilityAiImageOptions.builder()
                                .stylePreset(stylePreset)
                                .N(1)
                                .height(height)
                                .width(width).build())

        );

        return response;
    }

}