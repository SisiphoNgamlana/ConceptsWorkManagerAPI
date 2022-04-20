package com.example.conceptsworkmanagerapi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.List;

public class SelectImageActivity extends AppCompatActivity {

    private static final String KEY_PERMISSIONS_REQUEST_COUNT = "KEY_PERMISSIONS_REQUEST_COUNT";
    private static final int MAX_NUMBER_REQUEST_PERMISSIONS = 2;
    private static final int REQUEST_PERMISSIONS_CODE = 1010;
    private static final int REQUEST_IMAGE_CODE = 100;
    private static final String TAG = SelectImageActivity.class.getName();
    private int permissionsRequestCount;
    private Button buttonSelectImage;
    private static final List<String> permissions = Arrays.asList(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);
        buttonSelectImage = findViewById(R.id.select_image_button);

        if (savedInstanceState != null) {
            permissionsRequestCount = savedInstanceState.getInt(KEY_PERMISSIONS_REQUEST_COUNT, 0);
        }

        requestPermissionsIfNecessary();
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseImageIntent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(chooseImageIntent, REQUEST_IMAGE_CODE);
            }
        });
    }

    private void requestPermissionsIfNecessary() {
        if (!checkCallingPermission()) {
            if (permissionsRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                permissionsRequestCount += 1;
                ActivityCompat.requestPermissions(
                        this,
                        permissions.toArray(new String[0]),
                        REQUEST_PERMISSIONS_CODE
                );
            } else {
                Toast.makeText(this, R.string.go_set_permissions, Toast.LENGTH_SHORT).show();
                buttonSelectImage.setEnabled(false);
            }
        }
    }

    private boolean checkCallingPermission() {
        boolean hasPermission = true;
        for (String permission : permissions) {
            hasPermission &= ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED;
        }

        return hasPermission;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_IMAGE_CODE:
                    handleImageRequestResult(data);
                default:
                    Log.i(TAG, "Unknown request code");
            }
        } else {
            Log.i(TAG, "Unknown result code");
        }
    }

    private void handleImageRequestResult(Intent data) {
        Uri imageUri = null;
        if (data.getClipData() != null) {
            imageUri = data.getClipData().getItemAt(0).getUri();
        } else if (data.getData() != null) {
            imageUri = data.getData();
        }
        if (imageUri == null) {
            Log.i(TAG, "Invalid image input");
            return;
        }

        Intent filterIntent = new Intent(getApplicationContext(), CreateCardActivity.class);
        filterIntent.putExtra(Constants.KEY_IMAGE_URI, imageUri.toString());
        startActivity(filterIntent);
    }
}
