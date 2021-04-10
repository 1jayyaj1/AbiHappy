package com.jayyaj.abihappy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.jayyaj.abihappy.util.JournalApi;

public class AddToJournalActivity extends AppCompatActivity {
    private TextView test;
    private TextView test2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_journal);

        test = findViewById(R.id.test);
        test2 = findViewById(R.id.test2);

        JournalApi journalApi = JournalApi.getInstance();
        if (journalApi != null) {
            String username = journalApi.getUsername();
            String userId = journalApi.getUserId();
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(userId)) {
                test.setText(username);
                test2.setText(userId);
            }
        }
    }
}