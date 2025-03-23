package jks.ai.images.dto;

import lombok.Builder;

@Builder
public record GeneratedImageDTO(byte[] image, int internalID, String externalID, String storageURL) {
}
