package com.example.conceptsworkmanagerapi;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import static com.example.conceptsworkmanagerapi.Constants.IMAGE_PROCESSING_WORK_NAME;
import static com.example.conceptsworkmanagerapi.Constants.TAG_OUTPUT;

public class CustomCardViewModel extends AndroidViewModel {

    private Uri imageUri;
    private WorkManager workManager;
    private LiveData<List<WorkInfo>> savedWorkInfo;
    private Uri outputUri;

    public CustomCardViewModel(@NonNull Application application) {
        super(application);
        workManager = WorkManager.getInstance(application);

        savedWorkInfo = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT);
    }

    LiveData<List<WorkInfo>> getOutputWorkInfo() {
        return savedWorkInfo;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = uriOrNull(imageUri);
    }

    void setOutputUri(String outputImageUri) {
        outputUri = uriOrNull(outputImageUri);
    }

    public Uri getImageUri() {
        return imageUri;
    }

    void processImageToCard(String quote) {

        WorkContinuation workContinuation= workManager
                .beginUniqueWork(IMAGE_PROCESSING_WORK_NAME, ExistingWorkPolicy.REPLACE,
                        OneTimeWorkRequest.from(CleanupWorker.class));
    }

    private Uri uriOrNull(String uriString) {
        if (!TextUtils.isEmpty(uriString)) {
            return Uri.parse(uriString);
        }
        return null;
    }
}
