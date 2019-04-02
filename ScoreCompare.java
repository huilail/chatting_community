package com.example.lab5_reddit;

import java.util.Comparator;

public class ScoreCompare implements Comparator<PostMessage> {

    public int compare(PostMessage p1, PostMessage p2){
        return p2.getScore()-p1.getScore();
    }
}
