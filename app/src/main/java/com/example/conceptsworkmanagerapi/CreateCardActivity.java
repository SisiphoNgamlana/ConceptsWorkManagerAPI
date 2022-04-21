package com.example.conceptsworkmanagerapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.WorkInfo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.conceptsworkmanagerapi.Constants.KEY_IMAGE_URI;

public class CreateCardActivity extends AppCompatActivity {

    public static final String TAG = CreateCardActivity.class.getName();

    private CustomCardViewModel customCardViewModel;
    private Button processCard, cancelProcess, seeCardButton;
    private ImageView imageView;
    private EditText quoteEditText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);


        imageView = findViewById(R.id.image_view);
        quoteEditText = findViewById(R.id.custom_quote_edtx);
        seeCardButton = findViewById(R.id.see_card_button);
        cancelProcess = findViewById(R.id.cancel_button);
        processCard = findViewById(R.id.process_button);
        progressBar = findViewById(R.id.progress_bar);

        customCardViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(CustomCardViewModel.class);

        Intent intent = getIntent();
        String imageUriExtra = intent.getStringExtra(KEY_IMAGE_URI);
        customCardViewModel.setImageUri(imageUriExtra);

        if (customCardViewModel.getImageUri() != null) {
            Picasso.get()
                    .load(customCardViewModel.getImageUri())
                    .into(imageView);
        }

        customCardViewModel.getOutputWorkInfo().observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.isEmpty()) {
                    return;
                }
                WorkInfo workInfo = workInfos.get(0);
                boolean finished = workInfo.getState().isFinished();
                if (!finished) {
                    showWorkInProgress();

                } else {
                    showWorkFinished();
                    Data outputData = workInfo.getOutputData();
                    String outputImageUri = outputData.getString(KEY_IMAGE_URI);

                    if (!TextUtils.isEmpty(outputImageUri)) {
                        customCardViewModel.setOutputUri(outputImageUri);
                        seeCardButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        processCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(quoteEditText.getText().toString())) {
                    String quote = quoteEditText.getText().toString();
                    customCardViewModel.processImageToCard(quote);
                }
            }
        });

        seeCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "OnClick: See card");
                Uri currentUri = customCardViewModel.getOutUri();
                if (currentUri != null) {
                    Intent actionView = new Intent(Intent.ACTION_VIEW, currentUri);
                    if (actionView.resolveActivity(getPackageManager()) != null) {
                        startActivity(actionView);
                    }
                    processCard.setVisibility(View.VISIBLE);
                }
            }
        });

        cancelProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customCardViewModel.cancelWork();
            }
        });
    }


    private void showWorkFinished() {
        progressBar.setVisibility(View.GONE);
        cancelProcess.setVisibility(View.GONE);
        seeCardButton.setVisibility(View.VISIBLE);
    }

    private void showWorkInProgress() {
        progressBar.setVisibility(View.VISIBLE);
        cancelProcess.setVisibility(View.VISIBLE);
        seeCardButton.setVisibility(View.GONE);
    }

}
