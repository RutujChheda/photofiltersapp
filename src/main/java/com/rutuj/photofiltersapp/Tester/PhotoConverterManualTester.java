package com.rutuj.photofiltersapp.Tester;

import com.google.common.collect.ImmutableList;
import com.rutuj.photofiltersapp.activity.ConvertPhotoActivity;
import com.rutuj.photofiltersapp.dependency.DaggerServiceComponent;
import com.rutuj.photofiltersapp.dependency.ServiceComponent;
import com.rutuj.photofiltersapp.model.ConversionType;

/**
 * A class provided for interacting with the PhotoConverterService
 */
public class PhotoConverterManualTester {

    private static final ServiceComponent DAGGER = DaggerServiceComponent.create();

    /**
     * If you're having issues running the main method, check the "Before starting" steps in the README.
     */
    public static void main(String[] args) {
        runTest("src/main/resources/Photographing-NYC-skyline.jpg", ImmutableList.of(ConversionType.INVERSION,
                ConversionType.GREYSCALE, ConversionType.SEPIA));

        // Uncomment the below line to run a test that converts an image to a single filter type.
//         runTest("src/main/resources/Photographing-NYC-skyline.jpg", ImmutableList.of(ConversionType.SEPIA));
    }

    private static void runTest(String filePath, ImmutableList<ConversionType> conversions) {
        ConvertPhotoActivity activity = DAGGER.provideConvertPhotoActivity();
        activity.handleRequest(filePath, conversions);
    }
}