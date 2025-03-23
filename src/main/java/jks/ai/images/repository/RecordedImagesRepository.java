package jks.ai.images.repository;

import jks.ai.images.entity.RecordedImage;
import org.springframework.data.repository.CrudRepository;

public interface RecordedImagesRepository extends CrudRepository<RecordedImage, Integer> {
}
