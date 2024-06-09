package integration.helper;


import com.rutuj.photofiltersapp.activity.ConvertPhotoActivity;
import com.rutuj.photofiltersapp.dependency.DaggerServiceComponent;
import com.rutuj.photofiltersapp.dependency.ServiceComponent;

public final class ActivityProvider {
    private static final ServiceComponent DAGGER = DaggerServiceComponent.create();

    private ActivityProvider() {
    }

    public static ConvertPhotoActivity provideConvertPhotoActivity() {
        return DAGGER.provideConvertPhotoActivity();
    }
}

