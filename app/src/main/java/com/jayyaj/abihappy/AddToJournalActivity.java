package com.jayyaj.abihappy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jayyaj.abihappy.util.JournalApi;

import java.net.URI;

public class AddToJournalActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int GALLERY_REQUEST_CODE = 10;

    private Button saveButton;
    private ProgressBar progressBar;
    private ImageView addPhotobutton;
    private EditText titleEditText;
    private EditText thoughtsEditText;
    private TextView currentUserText;
    private TextView postDateText;
    private ImageView postImageView;
    private Uri imageUri;

    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Connection to cloud firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Journal");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_journal);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.postProgressbar);
        titleEditText = findViewById(R.id.postTitleEditText);
        thoughtsEditText = findViewById(R.id.postThoughtEditText);
        currentUserText = findViewById(R.id.postUserNameText);
        postDateText = findViewById(R.id.postDateText);
        postImageView = findViewById(R.id.postImageView);
        saveButton = findViewById(R.id.postSaveJournalButton);
        saveButton.setOnClickListener(this);

        addPhotobutton = findViewById(R.id.postCameraButton);
        addPhotobutton.setOnClickListener(this);

        if (JournalApi.getInstance() != null) {
            currentUserId = JournalApi.getInstance().getUserId();
            currentUserName = JournalApi.getInstance().getUsername();

            currentUserText.setText(currentUserName);
        }

        authStateListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            if (user != null) {

            } else {

            }
        };
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.postSaveJournalButton:
                //Save journal
                saveJournal();
                break;
            case R.id.postCameraButton:
                //Get image from camera roll
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                break;
        }
    }

    private void saveJournal() {
        progressBar.setVisibility(View.VISIBLE);
        String title = titleEditText.getText().toString().trim();
        String thoughts = thoughtsEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) && imageUri != null) {
            StorageReference filePath = storageReference.child("journal_images").child("img_" + Timestamp.now().getSeconds());
            filePath.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                progressBar.setVisibility(View.INVISIBLE);
                //TODO Create a journal object
                //TODO Invoke our collectionReference to create a journal collection
                //TODO Save a journal instance
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.INVISIBLE);
            });
        } else {
            Toast.makeText(AddToJournalActivity.this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                postImageView.setImageURI(imageUri);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}