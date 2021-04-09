package com.jayyaj.abihappy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

public class AddToJournalActivity extends AppCompatActivity {
    private TextView test;
    private TextView test2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_journal);

        test = findViewById(R.id.test);
        test2 = findViewById(R.id.test2);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String userId = intent.getStringExtra("userId");

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(userId)) {
            test.setText(username);
            test2.setText(userId);
        }
    }
}