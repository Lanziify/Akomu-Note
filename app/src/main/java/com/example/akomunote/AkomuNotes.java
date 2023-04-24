package com.example.akomunote;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class AkomuNotes {
    private String noteTitle;
    private String noteDescription;
    private String key;

    public AkomuNotes () {

    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public String getNoteDescription() {
        return noteDescription;
    }

    public AkomuNotes(String noteTitle, String noteDescription) {
        this.noteTitle = noteTitle;
        this.noteDescription = noteDescription;
    }
}
