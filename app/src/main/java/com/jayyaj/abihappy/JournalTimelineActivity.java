package com.jayyaj.abihappy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.jayyaj.abihappy.adapter.JournalRecyclerViewAdapter;
import com.jayyaj.abihappy.model.Journal;
import com.jayyaj.abihappy.util.JournalApi;

import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.List;

public class JournalTimelineActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private List<Journal> journalList;
    private JournalRecyclerViewAdapter journalRecyclerAdapter;
    private CollectionReference collectionReference = db.collection("Journal");

    private RecyclerView recyclerView;
    private TextView noJournal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_timeline);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        noJournal = findViewById(R.id.timelineNoThoughts);
        journalList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.actionAdd:
                if (user != null && firebaseAuth != null) {
                    startActivity(new Intent(JournalTimelineActivity.this, AddToJournalActivity.class));
                    //finish();
                }
                break;
            case R.id.actionSignOut:
                if (user != null && firebaseAuth != null) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(JournalTimelineActivity.this, MainActivity.class));
                    //finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.whereEqualTo("userId", JournalApi.getInstance()
                .getUserId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot journals : queryDocumentSnapshots) {
                            Journal journal = journals.toObject(Journal.class);
                            journalList.add(journal);
                        }

                        journalRecyclerAdapter = new JournalRecyclerViewAdapter(JournalTimelineActivity.this, journalList);
                        recyclerView.setAdapter(journalRecyclerAdapter);

                        recyclerView.getRecycledViewPool().clear();

                        //IMPORTANT for recycler view to know when to update itself when the journal data changes
                        journalRecyclerAdapter.notifyDataSetChanged();
                    } else {
                        noJournal.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(e -> {

                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        journalList.clear();
    }
}