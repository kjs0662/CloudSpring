package com.jinseonkim.photocloud.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PhotoRepository extends MongoRepository<PhotoModel, String> {
    public PhotoModel findPhotoModelByIdentifier(String identifier);
    public void deletePhotoModelByIdentifier(String identifier);
}
