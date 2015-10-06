package com.madi.sphero_21;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.orbotix.ConvenienceRobot;
import com.orbotix.async.DeviceSensorAsyncMessage;
import com.orbotix.common.ResponseListener;
import com.orbotix.common.Robot;
import com.orbotix.common.internal.AsyncMessage;
import com.orbotix.common.internal.DeviceResponse;

import java.io.BufferedReader;


public class ControlActivity extends Activity implements SensorEventListener{
    private SensorManager myManager;
    private Button red;
    private Button green;
    private  Button blue;
    private  Button SP;
    private Button stop;
    private Button up;
    private Button calibr;
    private TextView mtxtY;
    private TextView mtxtX;
    private TextView dTxtY;
    private TextView dTxtX;
    private float x, y, z;
    private static float velocity=0.5f;
    private float directionSphero=0.f;
    private  boolean onScreen=true;

    private float   mLimit = 10.00f;// 1.97  2.96  4.44  6.66  10.00  15.00  22.50  33.75  50.62
    private float   mLastValues[] = new float[3*2];
    private float   mScale[] = new float[2];
    private float   mYOffset;

    private float   mLastDirections[] = new float[3*2];
    private float   mLastExtremes[][] = { new float[3*2], new float[3*2] };
    private float   mLastDiff[] = new float[3*2];
    private int     mLastMatch = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        initViews();
        //
    }
    public void initViews(){

        myManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor myAccel=myManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(myAccel==null){
            System.out.println("No such sensor");
        } else {
            System.out.println(myAccel.getName());
        }
        myManager.registerListener(this, myAccel, SensorManager.SENSOR_DELAY_UI);
        red=(Button)findViewById(R.id.red);
        green=(Button)findViewById(R.id.green);
        blue=(Button)findViewById(R.id.blue);
        SP= (Button) findViewById(R.id.subButton);
        stop=(Button) findViewById(R.id.stopButton);
        calibr=(Button) findViewById(R.id.calibrate_sphero);
        mtxtY=(TextView)findViewById(R.id.resy);
        mtxtX=(TextView)findViewById(R.id.resx);
        dTxtY=(EditText)findViewById(R.id.desiredY);
        dTxtX=(EditText)findViewById(R.id.desiredX);
        up= (Button) findViewById(R.id.upButton);
        SP.setOnClickListener(mylistener);
        red.setOnClickListener(mylistener);
        green.setOnClickListener(mylistener);
        blue.setOnClickListener(mylistener);
        stop.setOnClickListener(mylistener);
        up.setOnClickListener(mylistener);
        calibr.setOnClickListener(mylistener);
        int h = 480; // TODO: remove this constant
        mYOffset = h * 0.5f;
        mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onScreen=false;
    }

    View.OnClickListener mylistener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.calibrate_sphero:
                    Intent mint=new Intent(getApplicationContext(), DirectionActivity.class);
                    startActivity(mint);
                    break;
                case R.id.red:
                //    MainActivity.mRobot.setLed(1.f, 0.f, 0.f);
                    break;
                case R.id.green:
                  //  MainActivity.mRobot.setLed(0.f, 1.f, 0.f);
                    break;
                case R.id.blue:
                   // MainActivity.mRobot.setLed(0.f, 0.f, 1.f);
                    break;
                case R.id.subButton:

                    calcAngle();
                    Log.d("Results", "Angle is " + Float.toString(directionSphero));
                    // MainActivity.mRobot.drive(direction, velocity);
                    break;
                case R.id.stopButton:
               //     MainActivity.mRobot.stop();
                    break;
                case R.id.upButton:
                 //   MainActivity.mRobot.drive(directionSphero, velocity);
                    break;
            }
        }
    };
    private float calcAngle(){
        try{

        }catch (NumberFormatException e){
            e.printStackTrace();
        }
    return 0.f;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        x=event.values[0];
        y=event.values[1];
        z=event.values[2];
        float g=(x*x+y*y+z*z)/(SensorManager.GRAVITY_EARTH*SensorManager.GRAVITY_EARTH);

        float vSum = 0;
        for (int i=0 ; i<3 ; i++) {
            final float v = mYOffset + event.values[i] * mScale[1];
            vSum += v;
        }
        int k = 0;
        float v = vSum / 3;

        float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
        if (direction == - mLastDirections[k]) {
            // Direction changed
            int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
            mLastExtremes[extType][k] = mLastValues[k];
            float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

            if (diff > mLimit) {

                boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k]*2/3);
                boolean isPreviousLargeEnough = mLastDiff[k] > (diff/3);
                boolean isNotContra = (mLastMatch != 1 - extType);

                if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                    Log.i("Abc", "step");
                    if(onScreen) {
                        this.onStepDetected();
                    }
                    mLastMatch = extType;
                }
                else {
                    mLastMatch = -1;
                }
            }
            mLastDiff[k] = diff;
        }
        mLastDirections[k] = direction;
        mLastValues[k] = v;

     /*   if(g>=2.f){
            if(prevG>=g) {
                this.onStepDetected();
                prevG=0;
            }
        }
        prevG=g;*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        onScreen=true;
    }

    private void onStepDetected(){
      //  MainActivity.mRobot.drive(calcAngle(), velocity);
        try{
            Thread.sleep(500);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
     //   MainActivity.mRobot.stop();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
