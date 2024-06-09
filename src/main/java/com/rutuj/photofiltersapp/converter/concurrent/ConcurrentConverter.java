package com.rutuj.photofiltersapp.converter.concurrent;

import com.rutuj.photofiltersapp.converter.PhotoConverter;
import com.rutuj.photofiltersapp.model.Photo;

public class ConcurrentConverter implements Runnable {
    private final PhotoConverter converter;
    private final Photo inputImage;
    private final String fileName;
    private String convertedImageLocation;

    public ConcurrentConverter(final PhotoConverter converter, final Photo inputImage, final String fileName) {
        this.converter = converter;
        this.inputImage = inputImage;
        this.fileName = fileName;
    }

    public String getConvertedImageLocation() {
        if (convertedImageLocation == null) {
            throw new IllegalStateException("Conversion computation not complete yet, or did not succeed.");
        }
        return convertedImageLocation;
    }

    @Override
    public void run() {
        // Calls one of the strategies that implements PhotoConverter
        convertedImageLocation = converter.convert(inputImage, fileName);
    }
}
