package com.epam.training.gen.ai.basics.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class ImageGeneratingService {
    private final RestTemplate restTemplate;
    private final String endpoint;
    private final String apiKey;
    private final String deploymentOrModelName;
    private static final String API_VERSION = "2024-02-01";

    @Autowired
    public ImageGeneratingService(
            @Value("${client-azureopenai-endpoint}") String endpoint,
            @Value("${client-azureopenai-key}") String apiKey,
            @Value("${client-azureopenai-deployment-name}") String deploymentOrModelName) {
        this.restTemplate = new RestTemplate();
        this.endpoint = endpoint;
        this.apiKey = apiKey;
        this.deploymentOrModelName = deploymentOrModelName;
    }

    public String generateImage(String prompt) {
        try {
            String url = String.format("%s/openai/deployments/%s/images/generations?api-version=%s",
                    endpoint, deploymentOrModelName, API_VERSION);

            log.info("Generating image with prompt: {}", prompt);

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // Prepare request body
            ImageGenerationRequest request = new ImageGenerationRequest();
            request.setModel("dall-e-3");
            request.setPrompt(prompt);
            request.setSize("1024x1024");
            request.setStyle("vivid");
            request.setQuality("standard");
            request.setN(1);

            HttpEntity<ImageGenerationRequest> requestEntity = new HttpEntity<>(request, headers);

            // Make the API call
            ResponseEntity<ImageGenerationResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ImageGenerationResponse.class
            );

            if (response.getBody() != null && 
                response.getBody().getData() != null && 
                !response.getBody().getData().isEmpty()) {
                String imageUrl = response.getBody().getData().get(0).getUrl();
                log.info("Image generated successfully. URL: {}", imageUrl);
                return imageUrl;
            } else {
                log.error("No image data in response");
                throw new RuntimeException("No image data in response");
            }

        } catch (Exception e) {
            log.error("Error generating image with prompt: {}", prompt, e);
            throw new RuntimeException("Failed to generate image", e);
        }
    }

    // Request DTO
    @Data
    private static class ImageGenerationRequest {
        private String model;
        private String prompt;
        private String size;
        private String style;
        private String quality;
        private Integer n;
    }

    // Response DTOs
    @Data
    private static class ImageGenerationResponse {
        private Long created;
        private List<ImageData> data;
    }

    @Data
    private static class ImageData {
        private String url;
        @JsonProperty("revised_prompt")
        private String revisedPrompt;
    }
}
