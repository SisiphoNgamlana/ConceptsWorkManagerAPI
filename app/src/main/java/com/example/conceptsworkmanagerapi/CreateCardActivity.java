package com.example.conceptsworkmanagerapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

public class CreateCardActivity extends AppCompatActivity {

    private CustomCardViewModel customCardViewModel;
    private Button processCard, cancelCard, seeCardButton;
    private ImageView imageView;
    private EditText quoteEditText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);



        imageView = findViewById(R.id.image_view);
        quoteEditText = findViewById(R.id.custom_quote_edtx);
        seeCardButton =  findViewById(R.id.see_card_button);
        cancelCard =  findViewById(R.id.cancel_button);
        processCard =  findViewById(R.id.process_button);
        progressBar =  findViewById(R.id.progress_bar);

        customCardViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(CustomCardViewModel.class);

        Intent intent = getIntent();
        String imageUriExtra = intent.getStringExtra(Constants.KEY_IMAGE_URI);
        customCardViewModel.setImageUri(imageUriExtra);

        if(customCardViewModel.getImageUri() != null){
            Picasso.get()
                    .load(customCardViewModel.getImageUri())
                    .into(imageView);
        }
    }
}
