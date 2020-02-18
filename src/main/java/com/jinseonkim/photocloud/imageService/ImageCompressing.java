package com.jinseonkim.photocloud.imageService;

public class ImageCompressing {
    static {
        System.load("/Users/jinseonkim/go/src/libs/thumbnailer/libthumbnailer.dylib");
    }

    public native byte[] compressedImage(byte[] data, int width, int height);

    public ImageCompressing() {

    }
}
