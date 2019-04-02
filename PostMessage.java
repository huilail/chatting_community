package com.example.lab5_reddit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class PostMessage implements Serializable {
    private String pid;
    private int score = 0;
    private String text = "";
    private ArrayList<String> replys = null;


    PostMessage(){
        text = "";
        pid = "";
        score = 0;
    }

    PostMessage(String t, int s) {
        text  = t;
        score = s;
    }

    PostMessage(String pid, int score, String text){
        this.pid = pid;
        this.score = score;
        this.text = text;
    }

    public String getPid() { return pid; }

    public int getScore() {
        return score;
    }

    public ArrayList<String> getReplys(){
        return replys;
    }

    public String toString(){
        return text;
    }

    public void addReply(String new_reply){
        if (replys == null)
            replys = new ArrayList<String>();
        replys.add(new_reply);
    }

    public void clearReply(){
        replys.clear();
    }

    protected void setScore(int new_score){
        score = new_score;
    }

    protected void setText(String new_text){
        text = new_text;
    }


}
