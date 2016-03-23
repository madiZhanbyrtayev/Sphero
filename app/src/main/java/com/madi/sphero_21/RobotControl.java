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
import com.orbotix.common.sensor.DeviceSensorsData;
import com.orbotix.common.sensor.SensorFlag;
import com.orbotix.subsystem.SensorControl;

import java.util.List;

import ShortestPath.Node;

/**
 * Created by User on 09.11.2015.
 */
public class RobotControl {
    private static final String TAG = "RobotControl";
    private static final float STOPPING_DISTANCE = 60;
    private static final float STOPPING_VELOCITY = 1;

    private float x, y;
    private float goalX, goalY;
    private float robotDirection;
    private float robotSpeed;
    private boolean robotRunning;
    private boolean robotFound;

    private DualStackDiscoveryAgent myAgent;
    private ConvenienceRobot mRobot;
    private int goalPosition;
    private List<Node> mPath;
    private Runnable onSuccess;

    private static final RobotControl ourInstance = new RobotControl();

    public static RobotControl getInstance() {
        return ourInstance;
    }

    private RobotControl() {
        this.x = 0;
        this.y = 0;
        this.goalX = 0;
        this.goalY = 0;
        this.robotSpeed = 0.5f;
        this.robotFound = false;
        this.robotDirection = 0;
        this.goalPosition = 0;
        this.robotRunning = false;
    }

    private boolean setGoal(int newGoal) {
        this.goalPosition = newGoal;
        if (goalPosition < mPath.size()) {
            this.goalX = (float) mPath.get(goalPosition).getX();
            this.goalY = (float) mPath.get(goalPosition).getY();

            return true;
        } else {
            goalPosition = -1;

            return false;
        }
    }

    public void driveAlong(List<Node> path, Runnable run) {
        this.mPath = path;
        this.onSuccess = run;

        if (setGoal(1)) {
            calculateAngle(goalX, goalX);
            startMovement();
        }
    }

    public void rotateTo(float heading) {
        mRobot.setBackLedBrightness(1.f);
        mRobot.drive(heading, 0.f);
    }

    public void setHeading() {
        mRobot.setZeroHeading();
        mRobot.setBackLedBrightness(0.f);
        mRobot.sendCommand(new ConfigureLocatorCommand(0, 0, 0, 0));
    }

    private void calculateAngle(float nx, float ny) {
        robotDirection = (float) Math.toDegrees(Math.atan2((double) (ny - y), (nx - x)));

        robotDirection = 90.0f - robotDirection;

        if (robotDirection < 0.0f) {
            robotDirection += 360.f;
        }

        Log.d(TAG, "New angle:" + robotDirection);
    }

    private void startMovement() {
        mRobot.drive(robotDirection, robotSpeed);
        robotRunning = true;
    }

    private void pauseMovement() {
        mRobot.stop();
        robotRunning = false;
    }

    public void startDiscovery(Context mContext, final Runnable runnable) {
        myAgent = DualStackDiscoveryAgent.getInstance();
        myAgent.addRobotStateListener(new RobotChangedStateListener() {
            @Override
            public void handleRobotChangedState(Robot robot, RobotChangedStateNotificationType robotChangedStateNotificationType) {
                switch (robotChangedStateNotificationType) {
                    case Online:
                        mRobot = new ConvenienceRobot(robot);
                        robotFound = true;

                        initRobot();

                        runnable.run();

                        if (myAgent.isDiscovering()) {
                            myAgent.stopDiscovery();
                        }
                        break;
                }
            }
        });

        try {
            if (myAgent.isDiscovering()) {
                myAgent.stopDiscovery();
            }

            myAgent.startDiscovery(mContext);
        } catch (DiscoveryException e) {
            Log.d("Exceptions", e.getMessage());
        }
    }

    private void initRobot() {
        mRobot.setLed(255, 0, 255);
        mRobot.setBackLedBrightness(1.f);
        long sensorFlag = SensorFlag.VELOCITY.longValue() | SensorFlag.LOCATOR.longValue();
        mRobot.enableSensors(sensorFlag, SensorControl.StreamingRate.STREAMING_RATE10);
        mRobot.sendCommand(new ConfigureLocatorCommand(0, 0, 0, 0));

        mRobot.addResponseListener(new ResponseListener() {
            @Override
            public void handleResponse(DeviceResponse deviceResponse, Robot robot) {

            }

            @Override
            public void handleStringResponse(String s, Robot robot) {

            }

            @Override
            public void handleAsyncMessage(AsyncMessage asyncMessage, Robot robot) {
                if (asyncMessage instanceof DeviceSensorAsyncMessage && mPath != null && goalPosition < mPath.size()) {
                    DeviceSensorsData sensorsData = ((DeviceSensorAsyncMessage) asyncMessage).getAsyncData().get(0);

                    x = sensorsData.getLocatorData().getPositionX();
                    y = sensorsData.getLocatorData().getPositionY();

                    Log.d(TAG, "x:" + x + " y:" + y);

                    float deltaX = goalX - x;
                    float deltaY = goalY - y;

                    if (deltaX * deltaX + deltaY * deltaY <= STOPPING_DISTANCE * STOPPING_DISTANCE) {

                        if (robotRunning) {
                            pauseMovement();
                        }

                        Log.d(TAG, "Distance to the goal " + Math.sqrt(deltaX * deltaX + deltaY * deltaY));

                        float velocityX = sensorsData.getLocatorData().getVelocityX();
                        float velocityY = sensorsData.getLocatorData().getVelocityY();

                        if (velocityX * velocityX + velocityY * velocityY <= STOPPING_VELOCITY * STOPPING_VELOCITY) {
                            if (setGoal(goalPosition + 1)) {
                                Log.d(TAG, "Current path node:" + mPath.get(goalPosition));

                                calculateAngle(goalX, goalY);
                                startMovement();
                            } else {
                                //Success
                                onSuccess.run();
                            }
                        }
                    } else {
                        startMovement();
                    }
                }
            }
        });
    }

    public void turnOff() {
        mRobot.setBackLedBrightness(0.f);
        mRobot.disableSensors();
    }

}
