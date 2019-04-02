package com.example.lab5_reddit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.lab5_reddit.R.id.searchresult;

/*search page for the result*/
public class SearchPage extends AppCompatActivity {

    private FirebaseDatabase postDb;
    private DatabaseReference postRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        postDb = FirebaseDatabase.getInstance();
        postRef = postDb.getReference("posts");

        DatabaseReference tempRef;
        /*create a arraylist of postmessage to collect the result date.*/
        ArrayList<PostMessage> postList = new ArrayList<PostMessage>();
        PostAdapter listAdapter;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        postList = (ArrayList<PostMessage>) args.getSerializable("ARRAYLIST");
        listAdapter = new PostAdapter(this, postList);

        /* enhance the reply ability to the result in the listview.*/
        ListView postView = (ListView) findViewById(searchresult);
        postView.setAdapter(listAdapter);

        postView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PostMessage item = (PostMessage) parent.getItemAtPosition(position);
                Intent reply_intent = new Intent(SearchPage.this, ReplyActivity.class);
                reply_intent.putExtra("text", item.toString());
                reply_intent.putExtra("replys",item.getReplys());
                reply_intent.putExtra("key",item.getPid());
                reply_intent.putExtra("score",Integer.toString(item.getScore()));
                startActivity(reply_intent);
            }
        });




    }
}
