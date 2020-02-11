package com.jinseonkim.photocloud.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinseonkim.photocloud.model.InfoModel;
import com.jinseonkim.photocloud.storage.StorageService;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CloudController {
    private final StorageService storageService;

    @Autowired
    public CloudController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(CloudController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile[] files, @RequestParam("Info") String info) {

        if (info.length() == 0) {
            return "No info";
        }

        List<InfoModel> list = new ArrayList<InfoModel>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String>[] map = mapper.readValue(info, Map[].class);
            System.out.println(map);
            for (Map<String, String> mappedInfo : map) {
                InfoModel infoModel = new InfoModel(mappedInfo.get("identifier"), mappedInfo.get("createdDate"));
                list.add(infoModel);
            }
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }

        for (MultipartFile file : files) {
            storageService.store(file);
        }
        return "redirect:/";
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleStorageFileNotFound(RuntimeException exc) {
        return ResponseEntity.notFound().build();
    }
}
