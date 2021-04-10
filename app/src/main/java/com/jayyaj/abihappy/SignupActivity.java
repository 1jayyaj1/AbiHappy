package com.jayyaj.abihappy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jayyaj.abihappy.util.JournalApi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    private Button signup;
    private TextView username;
    private AutoCompleteTextView email;
    private TextView password;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.usernameSignup);
        email = findViewById(R.id.emailSignup);
        password = findViewById(R.id.passwordSignup);
        signup = findViewById(R.id.createAccountButton);
        progressBar = findViewById(R.id.signupProgress);

        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                //User is already logged in
            } else {
                //No users are logged in
            }
        };

        signup.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(email.getText().toString())
                    && !TextUtils.isEmpty(password.getText().toString())
                    && !TextUtils.isEmpty(username.getText().toString())) {

                String emailText = email.getText().toString().trim();
                String usernameText = username.getText().toString().trim();
                String passwordText = password.getText().toString().trim();

                createUserEmailAccount(emailText, passwordText, usernameText);
            }else {
                Toast.makeText(SignupActivity.this, "Fields can't be empty", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void createUserEmailAccount(String emailText, String passwordText, String usernameText) {
        if (!TextUtils.isEmpty(emailText) && !TextUtils.isEmpty(passwordText) && !TextUtils.isEmpty(usernameText)) {
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    //We take users to the AddJournalActivity
                    currentUser = firebaseAuth.getCurrentUser();
                    assert currentUser != null;
                    String currentUserId = currentUser.getUid();

                    //Creating user Map to be added to collection
                    Map<String, String> userObj = new HashMap<>();
                    userObj.put("userId", currentUserId);
                    userObj.put("username", usernameText);

                    //Save to firestore
                    collectionReference.add(userObj).addOnSuccessListener(documentReference -> {
                        documentReference.get().addOnCompleteListener(task1 -> {
                            if (Objects.requireNonNull(task1.getResult()).exists()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                String name = task1.getResult().getString("username");

                                JournalApi journalApi = JournalApi.getInstance();
                                journalApi.setUsername(name);
                                journalApi.setUserId(currentUserId);
                                Intent intent = new Intent(SignupActivity.this, AddToJournalActivity.class);
                                startActivity(intent);
                            } else {
                                Log.e(TAG, "Could not get result from document reference");
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }).addOnFailureListener(e -> {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.e(TAG, "Could not get the document reference");
                        });
                    }).addOnFailureListener(e -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.e(TAG, "Could not add user object to the collection reference");
                    });
                } else {
                    //Something went on
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.e(TAG, "Create user task was not successful");
                }
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.INVISIBLE);
                Log.e(TAG, "Could not create user with email and password");
            });
        } else {
            Toast.makeText(SignupActivity.this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}