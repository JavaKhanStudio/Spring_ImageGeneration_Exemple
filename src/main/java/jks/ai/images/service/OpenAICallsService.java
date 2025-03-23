package jks.ai.images.service;

import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.image.ImageRequest;
import io.github.sashirestela.openai.domain.image.ImageResponseFormat;
import io.github.sashirestela.openai.domain.image.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenAICallsService {

    private final SimpleOpenAI openAI;

    public OpenAICallsService(@Value("${spring.ai.openai.api-key}")
                              String openAiApiKey) {
        openAI = SimpleOpenAI.builder()
                .apiKey(openAiApiKey)
                .build();
    }

     /*
    private String generateWithDalle(String prompt) {
        String apiUrl = "https://api.openai.com/v1/images/generations";
        String requestBody = "{" +
                "\"prompt\": \"" + prompt + "\"," +
                "\"size\": \"1024x1024\"}";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openAiApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestBody, String.class);
        return extractImageUrlFromResponse(response.getBody());
    }
    */

    public String generateWithDalle(String prompt){
        var imageRequest = ImageRequest.builder()
                .prompt(prompt)
                .n(1)
                .size(Size.X256)
                .responseFormat(ImageResponseFormat.URL)
                .model("dall-e-2")
                .build();
        var futureImage = openAI.images().create(imageRequest);
        var imageResponse = futureImage.join();
        imageResponse.stream().forEach(img -> System.out.println("\n" + img.getUrl()));

        return imageResponse.get(0).getUrl() ;
    }

}
