package com.madi.sphero_21;

import android.content.Context;
import android.util.Log;

import com.orbotix.ConvenienceRobot;
import com.orbotix.DualStackDiscoveryAgent;
import com.orbotix.async.DeviceSensorAsyncMessage;
import com.orbotix.command.ConfigureLocatorCommand;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.ResponseListener;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.common.internal.AsyncMessage;
import com.orbotix.common.internal.DeviceResponse;
import com.orbotix.common.sensor.SensorFlag;
import com.orbotix.subsystem.SensorControl;

import java.util.List;

import ShortestPath.Node;

/**
 * Created by User on 09.11.2015.
 */
public class RobotControl {
    private float x =0,y=0, prevX=0, prevY=0;
    private static final float EPS=60;
    private DualStackDiscoveryAgent myAgent;
    private ConvenienceRobot mRobot;
    private static RobotControl ourInstance = new RobotControl();
    private boolean robotFound=false;
    private float directionSphero=0;
    private int currPos=0;
    private List<Node> mPath;
    private Runnable onSuccess;
    private boolean running=false;

    public static RobotControl getInstance() {
        return ourInstance;
    }

    private RobotControl() {

    }
    public void driveAlong(List<Node> path, Runnable run){

        mPath=path;
        currPos=1;
        this.onSuccess=run;
        calcAngle((float) mPath.get(currPos).getX(), (float) mPath.get(currPos).getY());
        start();

    }
    public void rotateTO(float heading){
        mRobot.setBackLedBrightness(1.f);
        mRobot.drive(heading, 0.f);
    }
    public void setHeading(){
        mRobot.setZeroHeading();
        mRobot.setBackLedBrightness(0.f);
        mRobot.sendCommand(new ConfigureLocatorCommand(0, 0, 0, 0));
    }
    private void calcAngle(float nx, float ny){

        directionSphero = (float) Math.toDegrees(Math.atan2((double) (ny - y), (nx - x)));

        /*
        if(directionSphero<0.f){
            directionSphero+=360.f;
        }
        */

        directionSphero=90-directionSphero;
        if(directionSphero<0.f){
            directionSphero+=360.f;
        }
        /*
        if(x>=nx && ny>=y){
            directionSphero+=270.f;
        }else if(x<=nx && ny>=y){
            directionSphero=90.f-directionSphero;
        }else if(x>=nx && y>=ny){
            directionSphero=270.f-directionSphero;
        } else if(x<=nx && y>=ny){
            directionSphero+=90.f;
        }*/
        Log.d("Robot_Control", "New angle:" + directionSphero);
    }
    private void start(){
        mRobot.drive(directionSphero, 0.5f);
        running=true;
    }
    private void pause(){
        mRobot.stop();
        running=false;
    }
    private void initRobot(){
        mRobot.addResponseListener(new ResponseListener() {
            @Override
            public void handleResponse(DeviceResponse deviceResponse, Robot robot) {

            }

            @Override
            public void handleStringResponse(String s, Robot robot) {

            }

            @Override
            public void handleAsyncMessage(AsyncMessage asyncMessage, Robot robot) {
                if (asyncMessage instanceof DeviceSensorAsyncMessage && mPath != null && currPos < mPath.size()) {
                    float positionX = ((DeviceSensorAsyncMessage) asyncMessage).getAsyncData().get(0).getLocatorData().getPositionX();
                    float positionY = ((DeviceSensorAsyncMessage) asyncMessage).getAsyncData().get(0).getLocatorData().getPositionY();
                    x = positionX;//current positions
                    y = positionY;

                   // if(!added){
                     //   added=true;
                       // for(Node node: mPath){
                        //    node.setX(node.getX()+x);
                         //   node.setY(node.getY() + y);
                       // }
                  //  }
                    Log.d("Robot_Control", "x:" + x + " y:" + y);
                    if (Math.abs(mPath.get(currPos).getX() - x) < EPS && Math.abs(mPath.get(currPos).getY() - y) < EPS) {

                        if(running){
                            pause();
                        }
                        Log.d("Robot_Control", Math.abs(mPath.get(currPos).getX() - x) + " " + Math.abs(mPath.get(currPos).getY() - y));

                        if(Math.abs(prevX-x)<1 && Math.abs(prevY - y)<1) {
                            currPos++;

                            if (currPos < mPath.size()) {
                                Log.d("Robot_Control", "Current path node:" + mPath.get(currPos));
                                calcAngle((float) mPath.get(currPos).getX(), (float) mPath.get(currPos).getY());
                                start();
                            } else {
                                //Success
                                onSuccess.run();
                            }
                        }
                    }else{
                        start();
                    }

                }

                prevX=x;
                prevY=y;
                Log.d("Robot_Control", "PREV x,y = "+prevX + " "+prevY );
            }
        });
    }
    public void startDiscovery(Context mContext, final Runnable runnable){
        myAgent=DualStackDiscoveryAgent.getInstance();
        myAgent.addRobotStateListener(new RobotChangedStateListener() {
            @Override
            public void handleRobotChangedState(Robot robot, RobotChangedStateNotificationType robotChangedStateNotificationType) {
                switch (robotChangedStateNotificationType) {
                    case Online:
                        mRobot = new ConvenienceRobot(robot);
                        mRobot.setLed(255, 0, 255);
                        mRobot.setBackLedBrightness(1.f);
                        long sensorFlag = SensorFlag.VELOCITY.longValue() | SensorFlag.LOCATOR.longValue();
                        mRobot.enableSensors(sensorFlag, SensorControl.StreamingRate.STREAMING_RATE10);
                        mRobot.sendCommand(new ConfigureLocatorCommand(0, 0, 0, 0));

                        //Everything is ok
                        robotFound = true;
                        runnable.run();
                        initRobot();

                        if (myAgent.isDiscovering()) {
                            myAgent.stopDiscovery();
                        }
                        break;
                }
            }
        });
        try{
            if(myAgent.isDiscovering()){
                myAgent.stopDiscovery();
            }

            myAgent.startDiscovery(mContext);
        }catch (DiscoveryException e){
            Log.d("Exceptions", e.getMessage());
        }

    }
    public void turnOff(){
        mRobot.setBackLedBrightness(0.f);
        mRobot.disableSensors();
    }

}
