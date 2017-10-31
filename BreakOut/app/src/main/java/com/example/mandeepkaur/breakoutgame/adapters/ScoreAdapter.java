package com.example.mandeepkaur.breakoutgame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mandeepkaur.breakoutgame.R;
import com.example.mandeepkaur.breakoutgame.models.ScoreDataModel;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ScoreAdapter extends ArrayAdapter {
    private final Context context;
    private final ArrayList<ScoreDataModel> scoreDataModelArrayList;
    TextView name, timeTaken, score;

    public ScoreAdapter(Context context, ArrayList<ScoreDataModel> scoreDataModelArrayList) {
        super(context, R.layout.hall_of_fame_elements, scoreDataModelArrayList);
        this.context = context;
        this.scoreDataModelArrayList = scoreDataModelArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.hall_of_fame_elements, parent, false);
        }

        name = (TextView)convertView.findViewById(R.id.name);
        name.setText(scoreDataModelArrayList.get(position).getName());

        timeTaken = (TextView)convertView.findViewById(R.id.timeTaken);
        long milliseconds = Long.valueOf(scoreDataModelArrayList.get(position).getTime());

        String time = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
        );
        timeTaken.setText(time);

        score = (TextView)convertView.findViewById(R.id.score);
        score.setText(scoreDataModelArrayList.get(position).getScore());
        return convertView;
    }
}
