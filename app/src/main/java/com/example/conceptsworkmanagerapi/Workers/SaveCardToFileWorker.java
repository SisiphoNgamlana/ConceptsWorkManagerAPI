package com.example.conceptsworkmanagerapi.Workers;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.example.conceptsworkmanagerapi.Constants.KEY_IMAGE_URI;

public class SaveCardToFileWorker extends Worker {

    public static final String TAG = SaveCardToFileWorker.class.getName();
    public static final String TITLE = "Card Image";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT =
            new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault());


    public SaveCardToFileWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Context applicationContext = getApplicationContext();
        CardWorkerUtils.makeStatusNotification(applicationContext, "Saving image");
        CardWorkerUtils.sleep();

        ContentResolver contentResolver = applicationContext.getContentResolver();
        try {

            String resourceUri = getInputData().getString(KEY_IMAGE_URI);

            Bitmap bitmap = BitmapFactory.decodeStream(
                    contentResolver.openInputStream(Uri.parse(resourceUri)));

            String outputUri = MediaStore.Images.Media.insertImage(
                    contentResolver, bitmap, TITLE, SIMPLE_DATE_FORMAT.format(new Date()));

            if (TextUtils.isEmpty(outputUri)) {
                Log.d(TAG, "Writing to MediaStore failed");
                return Worker.Result.failure();
            }

            Data outputData = new Data.Builder()
                    .putString(KEY_IMAGE_URI, outputUri)
                    .build();

            return Worker.Result.success(outputData);

        } catch (Exception exception) {
            Log.e(TAG, "Unable to save image to Gallery", exception);
            return Worker.Result.failure();
        }
    }
}
