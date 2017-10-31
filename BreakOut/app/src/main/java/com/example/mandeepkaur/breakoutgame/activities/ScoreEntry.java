package com.example.mandeepkaur.breakoutgame.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mandeepkaur.breakoutgame.models.ScoreDataModel;
import com.example.mandeepkaur.breakoutgame.utilities.FileHandler;
import com.example.mandeepkaur.breakoutgame.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class ScoreEntry extends AppCompatActivity {

    TextView entryTime, entryScore;
    EditText entryName;
    Button submitButton, cancelButton;
    ScoreDataModel receivedScoreDataModel;
    FileHandler fileHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_entry);

        entryName = (EditText)findViewById(R.id.entryName);
        entryScore = (TextView)findViewById(R.id.entryScore);
        entryTime = (TextView)findViewById(R.id.entryTime);

        submitButton = (Button)findViewById(R.id.buttonSubmit);
        cancelButton = (Button)findViewById(R.id.buttonCancel);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Score Entry");
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        toolbar.setTitleTextColor(Color.WHITE);


        receivedScoreDataModel = new ScoreDataModel();
        Bundle bundle = getIntent().getExtras();
        ArrayList<Integer> arrayList = bundle.getIntegerArrayList("caller");

        if(arrayList.size() != 0) {
            receivedScoreDataModel.setScore(arrayList.get(0).toString());
            receivedScoreDataModel.setTime(arrayList.get(1).toString());
            long milliseconds = Long.valueOf(receivedScoreDataModel.getTime());

            String time = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                    TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
            );
            entryTime.setText("Time : "+time);
            entryScore.setText("Score : "+receivedScoreDataModel.getScore().toString());
        }
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(entryName.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter your name.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                fileHandler = new FileHandler(getApplicationContext());

                ArrayList<ScoreDataModel> arrayList = new ArrayList<>();
                arrayList = fileHandler.getDataObject();

                receivedScoreDataModel.setId(fileHandler.getMaxId() + 1);
                receivedScoreDataModel.setName(entryName.getText().toString());

                arrayList.add(receivedScoreDataModel);

                Collections.sort(arrayList, new CustomComparator());

                if(arrayList.size() > 10){
                    for(int i=10;i<arrayList.size()-1;i++){
                        arrayList.remove(i);
                    }
                }

                fileHandler.setDataObject(arrayList);

                finish();
                startActivity(new Intent(getApplicationContext(), HallOfFame.class));
            }
        });


    }


    public class CustomComparator implements Comparator<ScoreDataModel> {

        @Override
        public int compare(ScoreDataModel lhs, ScoreDataModel rhs) {
            if (Integer.parseInt(lhs.getScore()) > Integer.parseInt(rhs.getScore())){
                return -1;
            }else if (Integer.parseInt(lhs.getScore()) < Integer.parseInt(rhs.getScore())){
                return 1;
            }else {
                return 0;
            }
        }
    }



}
