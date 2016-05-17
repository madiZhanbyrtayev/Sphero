package com.madi.sphero_21;

import android.content.Context;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import ShortestPath.Graph;
import ShortestPath.JSONGraph;
import ShortestPath.Node;

public class SecondActivity extends AppCompatActivity implements SensorEventListener, View.OnTouchListener{
    private FloatingActionButton mButton;
    private SeekBar mSeekbar;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean started=false;
    private ImageMap myMap;
    private Graph mGraph=null;
    private List<Node> mNodes=null;
    private Spinner fromSpin;
    private Spinner toSpin;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        RobotControl.getInstance().turnOff();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        //Init
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mButton=(FloatingActionButton) findViewById(R.id.startFab);
        mSeekbar=(SeekBar) findViewById(R.id.mySeekBar);
        myMap = (ImageMap) findViewById(R.id.mMap);
        myMap.setOnTouchListener(this);
        fromSpin = (Spinner) findViewById(R.id.spinnerFrom);
        toSpin = (Spinner) findViewById(R.id.spinnerTo);
        //Step Sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);

        readGraph();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started) {
                    RobotControl.getInstance().setHeading();
                    mSeekbar.setEnabled(false);
                    /**
                     *     GRAPH CALCULATION
                     *     Begin                         **/
                    RobotControl rc = RobotControl.getInstance();

                    mGraph.setNewOrigin(rc.getX(), rc.getY());
                    final String from = fromSpin.getSelectedItem().toString();
                    final String to = toSpin.getSelectedItem().toString();
                    mNodes = mGraph.getShortestPath(from, to);
                    Log.d("SUPER LOG", mNodes.toString());
                    rc.driveAlong(mNodes, new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                            fromSpin.setPrompt(to);
                            mSeekbar.setEnabled(true);
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
                RobotControl.getInstance().rotateTo((float) progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void readGraph(){
        List<String> vals = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, vals);
        try {
            InputStream temp = getAssets().open("data.json");
            StringBuilder builder = new StringBuilder();

            byte[] buffer = new byte[1024];
            while (temp.read(buffer, 0, 1024) != -1) {
                builder.append(new String(buffer));
            }

            mGraph = JSONGraph.decodeGraph(new JSONObject(builder.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        for(Node n: mGraph.getNodeList()){
            vals.add(n.getLabel());
        }

        fromSpin.setAdapter(adapter);
        toSpin.setAdapter(adapter);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(started){
            RobotControl.getInstance().startMovement();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Path temp = new Path();
        float rx = event.getX();
        float ry = event.getY();
        mNodes = mGraph.getShortestPath(fromSpin.getSelectedItem().toString(), toSpin.getSelectedItem().toString());
        //temp.rMoveTo(rx, ry);

        float x,y;
        String res = " ";
        for(int i = 0 ; i<mNodes.size(); i++){
            x = (float) mNodes.get(i).getX();
            y = (float) mNodes.get(i).getY();
            res += mNodes.get(i).getLabel()+ " ";
            if(i!=0) {
                temp.lineTo(x, y);
            }

            temp.moveTo(x,y);
        }
        Log.d("Path", res);
        myMap.setMap(temp, 2, rx, ry);
        return true;
    }
}
