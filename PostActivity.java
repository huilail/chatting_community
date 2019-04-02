package com.example.lab5_reddit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class PostActivity extends AppCompatActivity {

    private DatabaseReference postRef;
    private FirebaseDatabase postDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);

        postDb = FirebaseDatabase.getInstance();
        postRef = postDb.getReference("posts");

        /*
        postBtn. When user clicks the button, it stores all text currently in editText
        and create a new item(post) in database

        set its text to be the stored text
        set its score to a random score in range of [0,100]
         */
        Button postBtn = (Button) findViewById(R.id.postBtn);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPost(v.getRootView());
            }
        });
    }

    public void createPost(View view){
        EditText postText = (EditText) findViewById(R.id.postText);
        String post_text = postText.getText().toString();

        Random rand = new Random();
        int score = rand.nextInt(100);

        String key;
        key = postRef.push().getKey();
        postRef.child(key).child("score").setValue(Integer.toString(score));
        postRef.child(key).child("text").setValue(post_text);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }
}
