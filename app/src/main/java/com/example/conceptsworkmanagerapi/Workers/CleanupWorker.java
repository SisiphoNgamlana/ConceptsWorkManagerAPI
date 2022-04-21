package com.example.conceptsworkmanagerapi.Workers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.example.conceptsworkmanagerapi.Constants;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class CleanupWorker extends Worker {

    public static final String TAG = CleanupWorker.class.getName();

    public CleanupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();
        CardWorkerUtils.makeStatusNotification(applicationContext, "Cleaning up old temporary files");
        CardWorkerUtils.sleep();

        try {
            File outputDirectory = new File(applicationContext.getFilesDir(), Constants.OUTPUT_PATH);
            if (outputDirectory.exists()) {
                File[] entries = outputDirectory.listFiles();
                if (entries != null && entries.length > 0) {
                    for (File entry : entries) {
                        String name = entry.getName();
                        if (!TextUtils.isEmpty(name) && name.endsWith("png")) {
                            boolean deleted = entry.delete();
                            Log.d(TAG, String.format("Deleted %s - %s", name, deleted));
                        }
                    }
                }

            }

            return Worker.Result.success();

        } catch (Exception exception) {
            Log.d(TAG, "Error Cleaning up", exception);
            return Worker.Result.failure();
        }
    }
}
