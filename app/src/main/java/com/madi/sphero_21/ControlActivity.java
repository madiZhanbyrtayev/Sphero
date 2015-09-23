package com.madi.sphero_21;

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


public class ControlActivity extends ActionBarActivity implements SensorEventListener{
    private SensorManager myManager;
   private Button red;
   private Button green;
   private  Button blue;
   private  Button SP;
    private Button stop;
    private Button up;
    private Button down;
   private TextView mtxtY;
   private TextView mtxtX;
    private TextView dTxtY;
    private TextView dTxtX;
    private float acc_x;
    private float acc_y;
    private float acc_z;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        initViews();

        MainActivity.mRobot.addResponseListener(new ResponseListener() {
            @Override
            public void handleResponse(DeviceResponse deviceResponse, Robot robot) {

            }

            @Override
            public void handleStringResponse(String s, Robot robot) {

            }

            @Override
            public void handleAsyncMessage(AsyncMessage asyncMessage, Robot robot) {
                if(asyncMessage instanceof DeviceSensorAsyncMessage){
                    float positionX = ( (DeviceSensorAsyncMessage) asyncMessage ).getAsyncData().get( 0 ).getLocatorData().getPositionX();
                    float positionY = ( (DeviceSensorAsyncMessage) asyncMessage ).getAsyncData().get( 0 ).getLocatorData().getPositionY();
                    mtxtX.setText(Float.toString(positionX));
                    mtxtY.setText(Float.toString(positionY));

                }else{
                    Log.e("tag", "not an instance");
                }
            }
        });
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
        mtxtY=(TextView)findViewById(R.id.resy);
        mtxtX=(TextView)findViewById(R.id.resx);
        dTxtY=(EditText)findViewById(R.id.desiredY);
        dTxtX=(EditText)findViewById(R.id.desiredX);
        up= (Button) findViewById(R.id.upButton);
        down=(Button) findViewById(R.id.downButton);
        SP.setOnClickListener(mylistener);
        red.setOnClickListener(mylistener);
        green.setOnClickListener(mylistener);
        blue.setOnClickListener(mylistener);
        stop.setOnClickListener(mylistener);
        up.setOnClickListener(mylistener);
        down.setOnClickListener(mylistener);

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
    View.OnClickListener mylistener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.red:
                    MainActivity.mRobot.setLed(1.f, 0.f, 0.f);
                    break;
                case R.id.green:
                    MainActivity.mRobot.setLed(0.f, 1.f, 0.f);
                    break;
                case R.id.blue:
                    MainActivity.mRobot.setLed(0.f, 0.f, 1.f);
                    break;
                case R.id.subButton:

                    float y=Float.parseFloat(dTxtY.getText().toString());
                    float x=Float.parseFloat(dTxtX.getText().toString());
                    float currY=Float.parseFloat(mtxtY.getText().toString());
                    float currX=Float.parseFloat(mtxtX.getText().toString());

                    float dist=(float)Math.sqrt(Math.pow((y-currY), 2.f)+Math.pow((x-currX), 2.f));
                    float angle=(float)Math.asin((y - currY) / dist);
                    Log.d("Results", "Angle is "+Float.toString(angle));
                    Log.d("Results", "Distance is " + Float.toString(dist));
                    MainActivity.mRobot.setZeroHeading();
                    MainActivity.mRobot.drive(angle, 0.5f);
                    break;
                case R.id.stopButton:
                    MainActivity.mRobot.stop();
                    break;
                case R.id.upButton:
                    MainActivity.mRobot.drive(0.f, 0.5f);
                    break;
                case R.id.downButton:
                    MainActivity.mRobot.drive(180.f, 0.5f);
                    break;
            }
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        acc_x=event.values[0];
        acc_y=event.values[1];
        acc_z=event.values[2];
        if(acc_y<=5f){
            MainActivity.mRobot.stop();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
