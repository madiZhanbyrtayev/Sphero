package com.madi.sphero_21;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.InputStreamReader;
import java.util.List;

import ShortestPath.Graph;
import ShortestPath.Node;

public class SecondActivity extends AppCompatActivity {
    private TextView mt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Graph graph = new Graph();
        try {
            graph.readFromFile(new InputStreamReader(getAssets().open("data.txt")));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        mt = (TextView) findViewById(R.id.myTxt);
        List<Node> myNodes = graph.getShortestPath("Madi", "Keks");
        Log.d("SUPER LOG", myNodes.toString());
        RobotControl rc = RobotControl.getInstance();
        rc.driveAlong(myNodes, new Runnable() {
            @Override
            public void run() {
                mt.setText("Finished!");
            }
        });

    }

}
