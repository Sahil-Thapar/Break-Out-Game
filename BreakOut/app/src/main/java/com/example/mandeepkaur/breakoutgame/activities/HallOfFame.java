package com.example.mandeepkaur.breakoutgame.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.example.mandeepkaur.breakoutgame.models.ScoreDataModel;
import com.example.mandeepkaur.breakoutgame.utilities.FileHandler;
import com.example.mandeepkaur.breakoutgame.R;
import com.example.mandeepkaur.breakoutgame.adapters.ScoreAdapter;

import java.util.ArrayList;

public class HallOfFame extends AppCompatActivity {

    FileHandler fileHandler = new FileHandler(this);
    ArrayList<ScoreDataModel> scoreDataModelArrayList = new ArrayList<>();
    ScoreAdapter scoreAdapter;
    ListView scoreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall_of_fame);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Hall of Fame");
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        toolbar.setTitleTextColor(Color.WHITE);


        scoreList = (ListView)findViewById(R.id.scoreList);

        scoreDataModelArrayList = fileHandler.getDataObject();

        scoreAdapter = new ScoreAdapter(getApplicationContext(), scoreDataModelArrayList);

        scoreList.setAdapter(scoreAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
