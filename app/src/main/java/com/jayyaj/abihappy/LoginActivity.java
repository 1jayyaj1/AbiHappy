package com.jayyaj.abihappy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jayyaj.abihappy.util.JournalApi;

public class LoginActivity extends AppCompatActivity {
    private Button signup;
    private Button signin;
    private AutoCompleteTextView email;
    private EditText password;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        signup = findViewById(R.id.createAccountSignInButton);
        signin = findViewById(R.id.signInButton);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        progressBar = findViewById(R.id.loginProgress);


        signin.setOnClickListener(v -> {
            signinEmailPasswordUser(email.getText().toString().trim(), password.getText().toString().trim());
        });

        signup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }

    private void signinEmailPasswordUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                        assert user != null;
                        String currentUserId = user.getUid();
                        collectionReference.whereEqualTo("userId", currentUserId)
                                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                            if (error != null) {}
                            assert queryDocumentSnapshots != null;
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    JournalApi journalApi = JournalApi.getInstance();
                                    journalApi.setUsername(snapshot.getString("username"));
                                    journalApi.setUserId(currentUserId);

                                    startActivity(new Intent(LoginActivity.this, JournalTimelineActivity.class));
                                }
                            }
                        });
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.INVISIBLE);
            });
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this, "Please enter an email and a password", Toast.LENGTH_SHORT).show();
        }
    }
}