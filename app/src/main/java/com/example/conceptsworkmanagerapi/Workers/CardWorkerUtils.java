package com.example.conceptsworkmanagerapi.Workers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.example.conceptsworkmanagerapi.Constants;
import com.example.conceptsworkmanagerapi.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.conceptsworkmanagerapi.Constants.CHANNEL_ID;
import static com.example.conceptsworkmanagerapi.Constants.DELAY_TIME_MILLIS;
import static com.example.conceptsworkmanagerapi.Constants.NOTIFICATION_ID;
import static com.example.conceptsworkmanagerapi.Constants.OUTPUT_PATH;

final public class CardWorkerUtils {

    static void makeStatusNotification(Context context, String message) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = Constants.VERBOSE_NOTIFICATION_CHANNEL_NAME;
            String description = Constants.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(channel);

        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[0]);

        NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_ID, notificationBuilder.build());

    }

    public static void sleep() {
        try {
            Thread.sleep(DELAY_TIME_MILLIS, 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @WorkerThread
    static Bitmap overLayTextOnBitmap(@NonNull Context applicationContext,
                                      @NonNull Bitmap bitmap,
                                      @NonNull String quote) {

        Bitmap bitmapOutput = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), bitmap.getConfig());

        Canvas canvas = new Canvas(bitmapOutput);

        float scale = applicationContext.getResources().getDisplayMetrics().density;

        TextPaint paint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);

        paint.setColor(Color.rgb(61, 61, 61));
        paint.setTextSize(28 * scale);

        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        int textWidth = (int) (canvas.getWidth() - (16*scale));

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        Point centerPointOfCanvas= new Point(canvasWidth / 2, canvasHeight / 2);

        int left = centerPointOfCanvas.x - bitmap.getWidth();
        int top = centerPointOfCanvas.y - bitmap.getHeight();
        int right = centerPointOfCanvas.x + bitmap.getWidth();
        int bottom = centerPointOfCanvas.y + bitmap.getHeight();

        RectF textBg = new RectF(left, top, right, bottom);
        Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setColor(Color.DKGRAY);
        rectPaint.setAlpha(255);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));

        StaticLayout staticLayout = StaticLayout.Builder.obtain(
                quote, 0, quote.length(), paint, textWidth)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setIncludePad(true)
                .setLineSpacing(1f, 1f)
//                .setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE)
                .setMaxLines(Integer.MAX_VALUE)
                .build();

        int textHeight = staticLayout.getHeight();

        float x = (bitmap.getWidth() - textWidth) >> 2;
        float y = (bitmap.getHeight() - textHeight) >> 2;
        canvas.save();

        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawRect(textBg, rectPaint);
        canvas.translate(x, y);

        staticLayout.draw(canvas);

        return bitmapOutput;
    }

    public CardWorkerUtils() {
    }

    public static Uri writeBitmapToFile(@NonNull Context applicationContext,
                                        @NonNull Bitmap bitmap) {
        String name = String.format("card-processed-output-%s.png", UUID.randomUUID().toString());

        File outputDirectory = new File(applicationContext.getFilesDir(), OUTPUT_PATH);
        if(!outputDirectory.exists()){
            outputDirectory.mkdirs();
        }
        File outputFile = new File(outputDirectory, name);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(outputFile);
    }
}
