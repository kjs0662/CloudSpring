package com.jinseonkim.photocloud.storage;

import com.jinseonkim.photocloud.model.InfoModel;
import com.jinseonkim.photocloud.model.PhotoModel;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(MultipartFile file, InfoModel info);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

    void deletePhoto(PhotoModel photoModel);

}
