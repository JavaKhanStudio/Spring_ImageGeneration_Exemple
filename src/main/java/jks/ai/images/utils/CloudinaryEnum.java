package jks.ai.images.utils;

public enum CloudinaryEnum {

    URL("url"),
    PUBLIC_ID("public_id"),
    SECURE_URL("secure_url")
    ;

    public final String value;

    CloudinaryEnum(String value) {
        this.value = value;
    }
}
