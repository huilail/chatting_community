package com.example.lab5_reddit;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends ArrayAdapter<PostMessage> {
    private Context mContext;
    private List<PostMessage> postList;

    /*
    stores a postList (Lis of postmessage) for tracking and retrieving
    all postmessages in the db

    it updates when a child is changed, added or removed
    it defines the things displayed in the listview in MainActivity
     */
    public PostAdapter( Context context, ArrayList<PostMessage> list)
    {
        super( context, 0, list);
        mContext = context;
        postList = list;
    }

    /*
    return a listItem
    all listitems will be displayed in the listview
     */
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;


        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.post_view,parent,false);


        PostMessage currentPost = postList.get(position);

        TextView originalPost = (TextView) listItem.findViewById(R.id.originalPost);
        originalPost.setText(currentPost.toString());

        TextView scoreView = (TextView) listItem.findViewById(R.id.scoreView);
        scoreView.setText("Score: " + currentPost.getScore());

        return listItem;
    }
}
