/*
Huilai Liu 88432485
Zeshi Lyu 44019058
*/

package com.example.lab5_reddit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lab5_reddit.PostActivity;
import com.example.lab5_reddit.PostAdapter;
import com.example.lab5_reddit.PostMessage;
import com.example.lab5_reddit.R;
import com.example.lab5_reddit.ReplyActivity;
import com.example.lab5_reddit.ScoreCompare;
import com.example.lab5_reddit.SearchPage;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.io.Serializable;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase postsDb;
    private DatabaseReference postsRef;
    private ArrayList<PostMessage> postsList;
    private ChildEventListener postsChildListener;
    private PostAdapter listAdapter = null;
    private ListView postView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postsDb = FirebaseDatabase.getInstance();
        postsRef = postsDb.getReference("posts");

        /*
        postsList is a list of PostMessages. Used for constructor list adapter
        listAdapter a PostAdapter class object.
            1. Storing all posts loaded from database
            2. Construct a ListView displayed in main activity and containing all posts
         */
        postsList = new ArrayList<PostMessage>();
        listAdapter = new PostAdapter(this, postsList);

        /*
        postView is a ListView.
        All posts in database are
            1. loaded into listAdapter
            2. displayed on postView
         */
        postView = (ListView) findViewById(R.id.postView);
        postView.setAdapter(listAdapter);


        postsChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                /*
                Listener detects all children added into database
                It stores new posts into listAdapter (for tracking and displaying)
                Also sort posts according to their score in descending order.
                 */

                int score = 0;
                String pid;
                String text = "";
                ArrayList<String> replyList;

                /*
                Get data of the new added post
                pid is the key of post used as primary key to distinguish posts in DB
                score used for sorting posts (see PostMessage and ReplyActivity)
                text is the text of the post
                 */
                pid = dataSnapshot.getKey();
                if (dataSnapshot.hasChild("score"))
                    score = Integer.parseInt(dataSnapshot.child("score").getValue().toString());
                if (dataSnapshot.hasChild("text"))
                    text = dataSnapshot.child("text").getValue().toString();

                GenericTypeIndicator<ArrayList<String>> GTI = new GenericTypeIndicator<ArrayList<String>>() {
                };
                PostMessage temp_post = new PostMessage(pid,score,text);

                /*
                replyList temporarily stores all reply of the post in a ArrayList of String
                and these reply will be stored in PostMessage class objects
                 */
                replyList = null;
                if (dataSnapshot.hasChild("reply"))
                    replyList = (dataSnapshot.child("reply").getValue(GTI));

                if (replyList != null){
                    for (String reply: replyList)
                        temp_post.addReply(reply);
                }

                /*
                Insert new added post into listAdapter
                and sort the Adapter
                 */
                listAdapter.add(temp_post);
                ScoreCompare compare = new ScoreCompare();
                listAdapter.sort(compare);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                /*
                capture all changes in db
                and update listAdapter (contains data of all Postmessages)
                 */
                PostMessage temp_post;
                int score;
                String  key, text;

                /*
                using a ArrayList of String temporarily stores all replys
                related to the changed post

                will be used for updating replylist of the changed Postmessage object
                 */
                ArrayList<String> replyList;
                GenericTypeIndicator<ArrayList<String>> GTI = new GenericTypeIndicator<ArrayList<String>>() {
                };

                /*
                remember the key of the changed item
                for searching the target item in listAdapter
                 */
                key = dataSnapshot.getKey();

                if (dataSnapshot.hasChild("reply"))
                    replyList = dataSnapshot.child("reply").getValue(GTI);
                else
                    replyList = null;

                /*
                find the target item and
                update all possibly changed fields of the item
                (including score, replylist, text)
                 */
                for (int i = 0; i < postsList.size(); ++i){
                    temp_post = listAdapter.getItem(i);
                    if (temp_post.getPid() == key){
                        if (dataSnapshot.hasChild("score")) {
                            score = Integer.parseInt(dataSnapshot.child("score").getValue().toString());
                            temp_post.setScore(score);
                        }
                        if (dataSnapshot.hasChild("text")){
                            text = dataSnapshot.child("text").getValue().toString();
                            temp_post.setText(text);
                        }
                        if (temp_post.getReplys() != null )
                            temp_post.clearReply();
                        if (replyList != null)
                            for (String reply : replyList)
                                temp_post.addReply(reply);
                        break;
                    }

                }
                ScoreCompare compare = new ScoreCompare();
                listAdapter.sort(compare);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                for (int i = 0; i < postsList.size(); ++i){
                    if ( listAdapter.getItem(i).getPid() == key){
                        listAdapter.remove(listAdapter.getItem(i));
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        postsRef.addChildEventListener(postsChildListener);


        /*
        It enable the reply function by click on a post displayed in ListView
         */
        postView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PostMessage item = (PostMessage) parent.getItemAtPosition(position);
                Intent reply_intent = new Intent(MainActivity.this, ReplyActivity.class);
                reply_intent.putExtra("text", item.toString());
                reply_intent.putExtra("replys",item.getReplys());
                reply_intent.putExtra("key",item.getPid());
                reply_intent.putExtra("score",Integer.toString(item.getScore()));
                startActivity(reply_intent);
            }
        });

        /* create a post button which can post a new article or theme. */
        Button postBtn = (Button) findViewById(R.id.createPostBtn);

        postBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, PostActivity.class));
            }
        });


        /*
        delete the post by long clicking it. also a short message will show up.
         */
        postView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                postsDb = FirebaseDatabase.getInstance();
                postsRef = postsDb.getReference("posts");
                postsRef.child(listAdapter.getItem(position).getPid()).removeValue();
                Toast.makeText(getApplicationContext(), "Post is deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
        });




        /*
        store the text currently in searchText bar when the SearchBtn is clicked
        search posts using the stored text as key word
        and
        start the searchpage activity to dispaly all results contains the keyword
         */
        Button SearchBtn = (Button) findViewById(R.id.SearchBtn);
        SearchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText SearchText = (EditText) findViewById(R.id.SearchText);
                String keyword = SearchText.getText().toString();
                if (keyword.length() != 0) {
                    ArrayList<PostMessage> postlist = new ArrayList<PostMessage>();
                    for (PostMessage p : postsList) {
                        if (p.toString().indexOf(keyword) != -1) {
                            postlist.add(p);
                        }
                    }
                    Intent startIntent = new Intent(getApplicationContext(), SearchPage.class);
                    Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST",(Serializable)postlist);
                    startIntent.putExtra("BUNDLE",args);
                    startActivity(startIntent);
                }
                else{
                }
            }
        });



    }

}
