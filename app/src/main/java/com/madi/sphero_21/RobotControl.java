package com.madi.sphero_21;

import android.content.Context;
import android.util.Log;

import com.orbotix.ConvenienceRobot;
import com.orbotix.DualStackDiscoveryAgent;
import com.orbotix.async.DeviceSensorAsyncMessage;
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
    private float x =0,y=0;
    private static final float EPS=50;
    private DualStackDiscoveryAgent myAgent;
    private ConvenienceRobot mRobot;
    private static RobotControl ourInstance = new RobotControl();
    private boolean robotFound=false;
    private float directionSphero=0;
    private int currPos=0;
    private List<Node> mPath;
    private Runnable onSuccess;
    public static RobotControl getInstance() {
        return ourInstance;
    }

    private RobotControl() {

    }
    public void driveAlong(List<Node> path, Runnable run){
        mPath=path;
        currPos=0;
        this.onSuccess=run;

        calcAngle((float)mPath.get(currPos).getX(),(float) mPath.get(currPos).getY());
        mRobot.drive(directionSphero, .5f);
    }

    private void calcAngle(float nx, float ny){

        directionSphero = (float) Math.toDegrees(Math.atan2((double) (ny - y), (nx - x)));
        if(x>=nx && ny>=y){
            directionSphero+=270.f;
        }else if(x<=nx && ny>=y){
            directionSphero=90.f-directionSphero;
        }else if(x>=nx && y>=ny){
            directionSphero=270.f-directionSphero;
        } else if(x<=nx && y>=ny){
            directionSphero+=90.f;
        }
    }
    private void start(){
        mRobot.drive(directionSphero, 0.5f);
    }
    private void pause(){
        mRobot.stop();
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
                if (asyncMessage instanceof DeviceSensorAsyncMessage && mPath!=null) {
                    float positionX = ((DeviceSensorAsyncMessage) asyncMessage).getAsyncData().get(0).getLocatorData().getPositionX();
                    float positionY = ((DeviceSensorAsyncMessage) asyncMessage).getAsyncData().get(0).getLocatorData().getPositionY();
                    x=positionX;//current positions
                    y=positionY;
                    if(Math.abs(mPath.get(currPos).getX()-x) < EPS && Math.abs(mPath.get(currPos).getY()-y)<EPS){
                        pause();

                        currPos++;
                        if(currPos<mPath.size()){
                            calcAngle((float)mPath.get(currPos).getX(),(float) mPath.get(currPos).getY());
                            start();
                        }else {
                            //Success
                            onSuccess.run();
                        }
                    }
                } else {
                    Log.e("tag", "not an instance");
                }
            }
        });
    }
    public void startDiscovery(Context mContext, final Runnable runnable){
        myAgent=DualStackDiscoveryAgent.getInstance();
        myAgent.addRobotStateListener(new RobotChangedStateListener() {
            @Override
            public void handleRobotChangedState(Robot robot, RobotChangedStateNotificationType robotChangedStateNotificationType) {
                switch (robotChangedStateNotificationType){
                    case Online:
                        mRobot=new ConvenienceRobot(robot);
                        mRobot.setLed(255, 0, 255);
                        long sensorFlag = SensorFlag.VELOCITY.longValue() | SensorFlag.LOCATOR.longValue();
                        mRobot.enableSensors(sensorFlag, SensorControl.StreamingRate.STREAMING_RATE10);
                        //Everything is ok
                        robotFound=true;
                        runnable.run();
                        initRobot();

                        if(myAgent.isDiscovering()) {
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

}
