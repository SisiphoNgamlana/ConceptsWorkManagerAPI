package com.example.conceptsworkmanagerapi.Workers;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.example.conceptsworkmanagerapi.Constants.CUSTOM_QUOTE;
import static com.example.conceptsworkmanagerapi.Constants.KEY_IMAGE_URI;

public class CardWorker extends Worker {

    public static final String TAG = CardWorker.class.getName();

    public CardWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Context applicationContext = getApplicationContext();
        CardWorkerUtils.makeStatusNotification(applicationContext, "Writing quote onto the Image");
        CardWorkerUtils.sleep();

        String imageResourceUri = getInputData().getString(KEY_IMAGE_URI);
        String quote = getInputData().getString(CUSTOM_QUOTE);

        ContentResolver contentResolver = applicationContext.getContentResolver();

        try {
            Bitmap photo = BitmapFactory.decodeStream(
                    contentResolver.openInputStream(Uri.parse(imageResourceUri)));

            Bitmap output = CardWorkerUtils.overLayTextOnBitmap(applicationContext, photo, quote);

            Uri outputUri = CardWorkerUtils.writeBitmapToFile(applicationContext, output);

            Data outputData = new Data.Builder()
                    .putString(KEY_IMAGE_URI, outputUri.toString())
                    .build();

            return Result.success(outputData);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "doWork: Error writing quote onto Image");
            return Result.failure();
        }
    }
}
