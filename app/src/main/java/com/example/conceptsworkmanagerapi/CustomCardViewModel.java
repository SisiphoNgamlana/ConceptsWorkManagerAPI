package com.example.conceptsworkmanagerapi;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;

import com.example.conceptsworkmanagerapi.Workers.CardWorker;
import com.example.conceptsworkmanagerapi.Workers.CleanupWorker;
import com.example.conceptsworkmanagerapi.Workers.SaveCardToFileWorker;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import static com.example.conceptsworkmanagerapi.Constants.CUSTOM_QUOTE;
import static com.example.conceptsworkmanagerapi.Constants.IMAGE_PROCESSING_WORK_NAME;
import static com.example.conceptsworkmanagerapi.Constants.KEY_IMAGE_URI;
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

        WorkContinuation workContinuation = workManager
                .beginUniqueWork(IMAGE_PROCESSING_WORK_NAME, ExistingWorkPolicy.REPLACE,
                        OneTimeWorkRequest.from(CleanupWorker.class));

        OneTimeWorkRequest.Builder cardBuilder =
                new OneTimeWorkRequest.Builder(CardWorker.class);
        cardBuilder.setInputData(createInputDataForUri(quote));
        workContinuation = workContinuation.then(cardBuilder.build());

        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        OneTimeWorkRequest save = new OneTimeWorkRequest.Builder(SaveCardToFileWorker.class)
                .setConstraints(constraints)
                .addTag(TAG_OUTPUT)
                .build();

        workContinuation = workContinuation.then(save);

        workContinuation.enqueue();
    }

    private Data createInputDataForUri(String quote) {

        Data.Builder dataBuilder = new Data.Builder();
        if (!TextUtils.isEmpty(quote)) {
            dataBuilder.putString(KEY_IMAGE_URI, imageUri.toString());
            dataBuilder.putString(CUSTOM_QUOTE, quote);
            return dataBuilder.build();
        }
        return null;
    }

    private Uri uriOrNull(String uriString) {
        if (!TextUtils.isEmpty(uriString)) {
            return Uri.parse(uriString);
        }
        return null;
    }

    public Uri getOutUri() {
        return outputUri;
    }

    public void cancelWork() {

    }
}
