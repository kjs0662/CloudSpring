package com.jinseonkim.photocloud.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinseonkim.photocloud.model.InfoModel;
import com.jinseonkim.photocloud.model.PhotoModel;
import com.jinseonkim.photocloud.model.PhotoRepository;
import com.jinseonkim.photocloud.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class CloudController {
    private final StorageService storageService;

    @Autowired
    private PhotoRepository repository;

    @Autowired
    public CloudController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public List<PhotoModel> listUploadedFiles() {
        List<PhotoModel> list = repository.findAll();
//        repository.deleteAll();
        return list;
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("files") MultipartFile[] files, @RequestParam("info") String info) {

        if (info.length() == 0) {
            return "No info";
        }

        List<InfoModel> list = new ArrayList<InfoModel>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String>[] map = mapper.readValue(info, Map[].class);
            for (Map<String, String> mappedInfo : map) {
                InfoModel infoModel = new InfoModel();
                infoModel.setIdentifier(mappedInfo.get("identifier"));
                infoModel.setCreatedDate(mappedInfo.get("createdDate"));
                list.add(infoModel);
            }
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }

        int index = 0;
        for (MultipartFile file : files) {
            storageService.store(file, list.get(index));
            index++;
        }
        return "success";
    }

    @DeleteMapping("/")
    public String handleDelete(@RequestBody List<InfoModel> infos) {
        if (infos == null) {
            return "Fail delete";
        }
        for (InfoModel info : infos) {
            PhotoModel photo = repository.findPhotoModelByIdentifier(info.getIdentifier());
            storageService.deletePhoto(photo);
        }
        return "Delete Success";
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleStorageFileNotFound(RuntimeException exc) {
        return ResponseEntity.notFound().build();
    }
}
