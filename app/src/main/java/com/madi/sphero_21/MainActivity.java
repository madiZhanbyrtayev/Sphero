package com.madi.sphero_21;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter myadap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSecondActivity = new Intent(getApplicationContext(), SecondActivity.class);
                startActivity(toSecondActivity);
            }
        });
        fab.hide();
        final TextView mTxt = (TextView) findViewById(R.id.myTxt);
        checkCoarse();


        myadap = BluetoothAdapter.getDefaultAdapter();
        enableDevices();
        RobotControl mRobotControl = RobotControl.getInstance();
        mRobotControl.startDiscovery(getApplicationContext(), new Runnable() {
            @Override
            public void run() {
                mTxt.setText("Connected!!!");
                fab.show();
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkCoarse(){
        if(this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location permission.");
            builder.setMessage("Please allow access to the application for the Sphero detection!");
            builder.setPositiveButton("Yes", null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
            });
            builder.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(), "Thank you!", Toast.LENGTH_SHORT);
        }
    }

    public void enableDevices(){
        if(!myadap.isEnabled()){
            Intent turnOnBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnBluetooth, 0);
        }
    }
}
