package com.rutuj.photofiltersapp.dependency;

import com.rutuj.photofiltersapp.activity.ConvertPhotoActivity;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component()
public interface ServiceComponent {
    ConvertPhotoActivity provideConvertPhotoActivity();
}
