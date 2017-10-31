package com.example.mandeepkaur.breakoutgame.models;

import java.util.Comparator;

public class ScoreDataModel implements Comparable {

    private String name;
    private String score;
    private String time;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getScore() {
        return score;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public static Comparator<ScoreDataModel> firstNameComparator = new Comparator<ScoreDataModel>() {

        public int compare(ScoreDataModel c1, ScoreDataModel c2) {

            int value1=c1.getScore().compareTo(c2.getScore());

            if(value1==0){
                return c1.getTime().compareTo(c2.getTime());
            }
            return value1;
        }};

    @Override
    public int compareTo(Object another) {
        return 0;
    }


}
