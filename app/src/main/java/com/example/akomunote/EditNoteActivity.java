package com.example.akomunote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class EditNoteActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText noteTitle, noteDescription;
    DatabaseReference databaseReference;
    AkomuNotes akomuNotes;
    String key = "";
    Boolean isEdited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        // initializing EditText
        noteTitle = findViewById(R.id.noteEditTitle);
        noteTitle.setText(getIntent().getExtras().getString("Title"));
        noteDescription = findViewById(R.id.noteEditDescription);
        noteDescription.setText(getIntent().getExtras().getString("Description"));
        key = getIntent().getExtras().getString("Key");

        databaseReference = FirebaseDatabase.getInstance().getReference("Akomu Note").child(key);

        toolbar = findViewById(R.id.toolbar_edit_note);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_chevron_left_24);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEdited = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        noteTitle.addTextChangedListener(textWatcher);
        noteDescription.addTextChangedListener(textWatcher);

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = noteTitle.getText().toString();
        String description = noteDescription.getText().toString();

        if (isEdited) {
            updateData(title, description);
        } else {
            finish();
        }
        return true;
    }

    public void updateData(String title, String description) {

        akomuNotes = new AkomuNotes(title, description);

        databaseReference.setValue(akomuNotes)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                    }
                });
    }
}