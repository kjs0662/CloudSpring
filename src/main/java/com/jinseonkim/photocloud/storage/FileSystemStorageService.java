package com.jinseonkim.photocloud.storage;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.jinseonkim.photocloud.awsService.AwsS3Service;
import com.jinseonkim.photocloud.model.InfoModel;
import com.jinseonkim.photocloud.model.PhotoModel;
import com.jinseonkim.photocloud.model.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private final AwsS3Service awsS3Service;

    @Autowired
    private PhotoRepository repository;

    @Autowired
    public FileSystemStorageService(StorageProperties properties, AwsS3Service awsService) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.awsS3Service = awsService;
    }

    @Override
    public void store(MultipartFile file, InfoModel info) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file " + filename);
        }
        if (filename.contains("..")) {
            // This is a security check
            throw new RuntimeException(
                    "Cannot store file with relative path outside current directory "
                            + filename);
        }

        PhotoModel photo = new PhotoModel();

        PhotoModel savedModel = repository.findPhotoModelByIdentifier(info.getIdentifier());
        if (savedModel == null) {
            String fileUrl = awsS3Service.uploadFile(file);
            String thumbUrl = awsS3Service.uploadThumbnail(file);
            photo.setIdentifier(info.getIdentifier());
            photo.setCreatedDate(info.getCreatedDate());
            photo.setImage(fileUrl);
            photo.setThumbnail(thumbUrl);
            photo.setName(filename);
            repository.save(photo);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new RuntimeException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
        repository.deleteAll();
    }

    @Override
    public void deletePhoto(PhotoModel photoModel) {
        repository.deletePhotoModelByIdentifier(photoModel.getIdentifier());
        awsS3Service.deleteFile(photoModel.getName());
        try {
            FileSystemUtils.deleteRecursively(rootLocation.resolve(photoModel.getName()));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: ", e);
        }

    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }
}
