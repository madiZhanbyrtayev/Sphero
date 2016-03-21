package com.madi.sphero_21;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import ShortestPath.Graph;
import ShortestPath.JSONGraph;
import ShortestPath.Node;

public class SecondActivity extends AppCompatActivity {
    private TextView mt;
    private Button mButton;
    private SeekBar mSeekbar;
    private boolean started=false;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        RobotControl.getInstance().turnOff();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mButton=(Button) findViewById(R.id.myB);
        mSeekbar=(SeekBar) findViewById(R.id.mySeekBar);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started) {
                    RobotControl.getInstance().setHeading();
                    mSeekbar.setEnabled(false);
                    /**
                     *     GRAPH CALCULATION
                     *     Begin                         **/
                    Graph graph = null;

                    try {
                        InputStream temp = getAssets().open("data.json");
                        StringBuilder builder = new StringBuilder();

                        byte[] buffer = new byte[1024];
                        while (temp.read(buffer) != -1) {
                            builder.append(buffer);
                        }

                        graph = JSONGraph.decodeGraph(new JSONObject(builder.toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                    mt = (TextView) findViewById(R.id.myTxt);
                    List<Node> myNodes = graph.getShortestPath("Madi", "Bekz");
                    Log.d("SUPER LOG", myNodes.toString());
                    RobotControl rc = RobotControl.getInstance();
                    rc.driveAlong(myNodes, new Runnable() {
                        @Override
                        public void run() {
                            mt.setText("Finished!");
                        }
                    });
                    // GRAPH PART END
                    started = true;
                }
            }
        });

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                RobotControl.getInstance().rotateTO((float) progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
