package com.example.akomunote;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesHolder> {
    Context context;
    ArrayList<AkomuNotes> noteList;

    public NotesAdapter(Context context, ArrayList<AkomuNotes> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public NotesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new NotesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesHolder holder, int position) {
        AkomuNotes notes = noteList.get(position);
        holder.printNoteTitle.setText(notes.getNoteTitle());
        holder.printNoteDescription.setText(notes.getNoteDescription());

        holder.noteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditNoteActivity.class);
                intent.putExtra("Key", notes.getKey());
                intent.putExtra("Title", notes.getNoteTitle());
                intent.putExtra("Description", notes.getNoteDescription());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public static class NotesHolder extends RecyclerView.ViewHolder {

        TextView printNoteTitle, printNoteDescription;
        CardView noteCard;

        public NotesHolder(@NonNull View itemView) {
            super(itemView);

            printNoteTitle = itemView.findViewById(R.id.printNoteTitle);
            printNoteDescription = itemView.findViewById(R.id.printNoteDescription);
            noteCard = itemView.findViewById(R.id.noteCard);
        }
    }
}

