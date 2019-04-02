package com.example.lab5_reddit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ReplyAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private ArrayList<String> replyList = new ArrayList<String>();

    public ReplyAdapter(Context context, ArrayList<String> list){
        super(context,0,list);
        mContext = context;
        replyList = list;
    }

    public View getView (int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View listItem = convertView;

        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.reply_view,parent,false);

        String currentString = replyList.get(position);

        TextView replyPost = (TextView) listItem.findViewById(R.id.replyPost);
        replyPost.setText(currentString);

        return listItem;
    }
}
