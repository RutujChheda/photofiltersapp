package com.rutuj.photofiltersapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A class representing a Photo - contains dimensions, and a list of Pixels that make up the image.
 */
public final class Photo {
    private final List<Pixel> pixels;
    private final int height;
    private final int width;
    // used when saving to a buffered image
    private final int type;

    public Photo(List<Pixel> pixelList, int height, int width, int type) {
        if (pixelList.size() != (height * width)) {
            throw new IllegalArgumentException("Not enough pixels for the dimensions of the image.");
        }
        this.pixels = new ArrayList<>(pixelList);
        this.height = height;
        this.width = width;
        this.type = type;
    }

    public List<Pixel> getPixels() {
        return new ArrayList<>(pixels);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pixels, height, width, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Photo photo = (Photo) obj;

        return photo.height == this.height && photo.width == this.width &&
                photo.type == photo.type && Objects.equals(photo.pixels, this.pixels);
    }

}