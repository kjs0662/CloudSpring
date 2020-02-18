package com.jinseonkim.photocloud.awsService;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.jinseonkim.photocloud.imageService.ImageCompressing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.*;

@Service
public class AwsS3Service {

    private AmazonS3 s3Client;

    @Value("jinseon-photo-bucket")
    private String imageBucketName;

    @Value("jinseon-thumbnail-bucket")
    private String thumbnailBucketName;

    @Value("https://s3.ap-northeast-1.amazonaws.com")
    private String endpoint;

    @Value("AKIARI7I35SVOYOCQOYH")
    private String accessKey;

    @Value("8R/rimRYf1IEhDHHxdupgVw4Q6D3QFmXBlMAc2vR")
    private String secretKey;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public String uploadThumbnail(MultipartFile multipartFile) {
        String fileUrl = "";
        File file = convertMultiPartToFile(multipartFile);
        String fileName = "thumb-" + multipartFile.getOriginalFilename();
        fileUrl = endpoint + "/" + thumbnailBucketName + "/" + fileName;

        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
            byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            byte[] imageData = new ImageCompressing().compressedImage(pixels, image.getWidth(), image.getHeight());

            BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(imageData, imageData.length), new Point()));
            ImageIO.write(img, "jpg", new File(fileName));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("", e);
        }

        File thumbFile = new File("thumb-" + multipartFile.getOriginalFilename());
        s3Client.putObject(new PutObjectRequest(thumbnailBucketName, fileName, thumbFile));
        thumbFile.delete();

        return fileUrl;
    }

    public String uploadFile(MultipartFile multipartFile) {

        String fileUrl = "";
        File file = convertMultiPartToFile(multipartFile);
        fileUrl = endpoint + "/" + imageBucketName + "/" + multipartFile.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(imageBucketName, multipartFile.getOriginalFilename(), file));

        file.delete();
        return fileUrl;
    }

    public void deleteFile(String name) {
        s3Client.deleteObject(imageBucketName, name);
        s3Client.deleteObject(thumbnailBucketName, "thumb-" + name);
    }

    private File convertMultiPartToFile(MultipartFile file) {
        try {
            File convFile = new File(file.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
            return convFile;
        } catch (IOException e) {
            throw  new RuntimeException("Could not convert MultipartFile to File", e);
        }
    }
}
