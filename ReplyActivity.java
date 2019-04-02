package com.example.lab5_reddit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ReplyActivity extends AppCompatActivity {
            /*
            store all data about the given post
            including key, score, text and replylist

            all of these attributes will be showed in reply activity

            user can click likeBtn/dislikeBtn to upvote or downvote a post
            the score will be in the range of [0,100]
            when the limit is reached, these buttons cannot make the score exceed the limit.

            user can reply a post by enter reply message and click replyBtn
            the new reply will be added to the end of reply list and be displyed in the reply list view
            in the reply activity interface
             */
    private FirebaseDatabase postsDb;
    private DatabaseReference postsRef;
    private ChildEventListener postsChildListener;
    private ArrayList<String> replyList;
    private String key;
    private int score = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postsDb = FirebaseDatabase.getInstance();
        postsRef = postsDb.getReference("posts");


        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        score = Integer.parseInt(intent.getStringExtra("score"));




        setContentView(R.layout.reply_activity);

        TextView originalText = (TextView) findViewById(R.id.originalText);
        originalText.setText(intent.getStringExtra("text"));


        ListView replyText = (ListView) findViewById(R.id.replyText);

        replyList = intent.getStringArrayListExtra("replys");
        if (replyList == null)
            replyList = new ArrayList<String>();

        final ReplyAdapter listAdapter = new ReplyAdapter(this, replyList);
        replyText.setAdapter(listAdapter);

        postsChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                GenericTypeIndicator<ArrayList<String>> GTI = new GenericTypeIndicator<ArrayList<String>>() {
                };
                System.out.println("*************************update " + key);
                System.out.println(dataSnapshot.toString());
                if (dataSnapshot.hasChild("reply")){
                    System.out.println(dataSnapshot.child("reply").getValue().toString());
                    ArrayList<String> tempList = (dataSnapshot.child("reply").getValue(GTI));
                    listAdapter.clear();
                    for (String reply : tempList)
                        listAdapter.add(reply);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        postsRef.addChildEventListener(postsChildListener);




        Button replyBtn = (Button) findViewById(R.id.replyBtn);
        replyBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                TextView editText = (TextView) findViewById(R.id.editText);
                String new_reply_text = editText.getText().toString();
                String new_reply_key = postsRef.child(key).child("reply").child(Integer.toString(replyList.size())).getKey();
                postsRef.child(key).child("reply").child(new_reply_key).setValue(new_reply_text);
                editText.setText("");
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });


        Button likeBtn = (Button) findViewById(R.id.likeBtn);
        Button dislikeBtn = (Button) findViewById(R.id.dislikeBtn);

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like();
            }
        });

        dislikeBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                dislike();
            }
        });

    }

    private void like(){
        System.out.println("old score = " + postsRef.child(key).child("score").toString() );
        if (score < 100){
            ++score;
            postsRef.child(key).child("score").setValue(Integer.toString(score));
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
    }

    private void dislike() {
        System.out.println("old score = " + postsRef.child(key).child("score").toString() );
        if (score > 0){
            --score;
            postsRef.child(key).child("score").setValue(Integer.toString(score));
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
    }

}
