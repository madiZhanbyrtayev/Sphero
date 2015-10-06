package com.madi.sphero_21;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.orbotix.ConvenienceRobot;
import com.orbotix.DualStackDiscoveryAgent;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.common.sensor.SensorFlag;
import com.orbotix.subsystem.SensorControl;

import org.w3c.dom.Text;


public class MainActivity extends Activity {
    BluetoothAdapter myadap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button controlButton=(Button) findViewById(R.id.gtc);
        controlButton.setVisibility(View.INVISIBLE);
        final TextView mytxt=(TextView) findViewById(R.id.txt);

        myadap=BluetoothAdapter.getDefaultAdapter();
        enableDevices();
        RobotControl mControl=RobotControl.getInstance();
        mControl.startDiscovery(getApplicationContext());
        controlButton.setVisibility(View.VISIBLE);
    }


    public void enableDevices(){
        if(myadap==null){
            // System.exit(-1);
        }
        if(!myadap.isEnabled()){
            Intent turnOnBluetooth=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnBluetooth, 0);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public void next(View view){
        Intent toControl=new Intent(getApplicationContext(), ControlActivity.class);
        startActivity(toControl);
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
}
