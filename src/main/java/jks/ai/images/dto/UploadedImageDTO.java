package jks.ai.images.dto;

import lombok.Builder;

@Builder
public record UploadedImageDTO(String URL, String ID) {
}
