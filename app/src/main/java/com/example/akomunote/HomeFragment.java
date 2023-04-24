package com.example.akomunote;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    FloatingActionButton fab;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    ArrayList<AkomuNotes> notes;
    NotesAdapter notesAdapter;
    String key = "";
    ShimmerFrameLayout shimmerFrameLayout;
    SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        fab = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recyclerView);
        shimmerFrameLayout = view.findViewById(R.id.shimmer);
        refreshLayout = view.findViewById(R.id.swipeRefresh);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.orange_main), getResources().getColor(R.color.orange_pink));


        notes = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        notesAdapter = new NotesAdapter(getActivity(), notes);
        recyclerView.setAdapter(notesAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Akomu Note");
        shimmer();

        // Listen when there is changes with the data and fetch
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notes.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    AkomuNotes akomuNotes = itemSnapshot.getValue(AkomuNotes.class);
                    akomuNotes.setKey(itemSnapshot.getKey());
                    notes.add(akomuNotes);
                }
                notesAdapter.notifyDataSetChanged();
                closeShimmer();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // Open the activity which adds note
        fab.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CreateNoteActivity.class));
        });
        // Handles pull refresh
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shimmer();
                loadNotes();
                refreshLayout.setRefreshing(false);
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String noteKey = notes.get(position).getKey();
                databaseReference.child(noteKey).removeValue();
                notes.remove(position);
                notesAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.gray))
                        .addCornerRadius(0, 10)
                        .addActionIcon(R.drawable.ic_baseline_delete_24)
                        .setActionIconTint(ContextCompat.getColor(getActivity(), R.color.white))
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);

        return view;
    }
    // Fetch all notes on start
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadNotes();
    }
    public void shimmer(){
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        recyclerView.setVisibility(View.GONE);
    }
    public void closeShimmer() {
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
    public void loadNotes() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notes.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    AkomuNotes akomuNotes = itemSnapshot.getValue(AkomuNotes.class);
                    akomuNotes.setKey(itemSnapshot.getKey());
                    notes.add(akomuNotes);
                }
                notesAdapter.notifyDataSetChanged();
                closeShimmer();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}