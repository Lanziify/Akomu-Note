package com.example.akomunote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

public class CreateNoteActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText noteTitle, noteDescription;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    AkomuNotes akomuNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        // initializing EditText
        noteTitle = findViewById(R.id.noteTitle);
        noteDescription = findViewById(R.id.noteDescription);

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
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = noteTitle.getText().toString();
        String description = noteDescription.getText().toString();

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(description)) {
            finish();
        } else  {
            saveData(title, description);
        }
        return true;
    }
    public void saveData(String title, String description) {
        akomuNotes = new AkomuNotes(title, description);
        databaseReference.setValue(akomuNotes);
        finish();
    }
}