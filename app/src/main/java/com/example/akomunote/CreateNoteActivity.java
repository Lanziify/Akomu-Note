package com.example.akomunote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

public class CreateNoteActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText noteTitle, noteDescription;
    ImageView getImage;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    AkomuNotes akomuNotes;
    Uri imageUri;
    TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        // initializing EditText
        noteTitle = findViewById(R.id.noteTitle);
        noteDescription = findViewById(R.id.noteDescription);

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Akomu Note").child(currentDate);

        toolbar = findViewById(R.id.toolbar_note);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_chevron_left_24);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getImage = findViewById(R.id.getImage);
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(CreateNoteActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                ocr();
            }
        } else {
            Toast.makeText(this, "image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        String title = noteTitle.getText().toString();
        String description = noteDescription.getText().toString();

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(description)) {
            finish();
        } else {
            saveData(title, description);
        }
        return true;
    }

    public void saveData(String title, String description) {
        akomuNotes = new AkomuNotes(title, description);
        databaseReference.setValue(akomuNotes);
        finish();
    }

    public void ocr() {
        if(imageUri!=null) {
            try {
                InputImage inputImage = InputImage.fromFilePath(CreateNoteActivity.this, imageUri);
                Task<Text> result = textRecognizer.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text text) {
                                String processedText = text.getText();
                                noteDescription.setText(processedText);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateNoteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}