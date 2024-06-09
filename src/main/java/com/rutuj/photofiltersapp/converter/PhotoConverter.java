package com.rutuj.photofiltersapp.converter;


import com.rutuj.photofiltersapp.model.Photo;

/**
 * Strategy interface for converting images.
 */
public interface PhotoConverter {
    String convert(Photo image, String imageName);
}
