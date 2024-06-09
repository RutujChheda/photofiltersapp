package com.rutuj.photofiltersapp.converter;

import com.rutuj.photofiltersapp.model.ConversionType;
import com.rutuj.photofiltersapp.model.Photo;
import com.rutuj.photofiltersapp.model.Pixel;
import com.rutuj.photofiltersapp.model.RGB;
import com.rutuj.photofiltersapp.util.PhotoUtil;

import java.util.ArrayList;
import java.util.List;

public class SepiaConverter implements PhotoConverter {

    public String convert(final Photo image, final String imageName) {
        List<Pixel> pixels = new ArrayList<>();

        for (Pixel pixel : image.getPixels()) {
            RGB rgb = pixel.getRGB();
            rgb = rgb.toSepia();
            pixels.add(new Pixel(pixel.getX(), pixel.getY(), rgb));
        }

        Photo convertedImage = new Photo(pixels, image.getHeight(), image.getWidth(), image.getType());

        return PhotoUtil.savePhoto(convertedImage, imageName, ConversionType.SEPIA);
    }
}