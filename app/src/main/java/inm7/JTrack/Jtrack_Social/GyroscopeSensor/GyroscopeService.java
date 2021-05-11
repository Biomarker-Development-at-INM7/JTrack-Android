/**
 Copyright © 2020 JuTrack Mobile Framework, JuTrack Platform, JuTrack Social, JuTrack Move, JuTrack EMA

 Licensed under the Apache License, Version 2.0 (the “License”);
 you may not use this file except in compliance with the License.
 you may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an “AS IS” BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and limitations under the License.

 **/
// . for older devices without play sevice, not used in app.
package inm7.JTrack.Jtrack_Social.GyroscopeSensor;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import inm7.JTrack.Jtrack_Social.Constants;
import inm7.JTrack.Jtrack_Social.MainActivity;
import inm7.JTrack.Jtrack_Social.R;


public class GyroscopeService extends Service implements SensorEventListener {



    // Declaring a Location Manager
    private Notification mNotification;
    public SensorManager sensormanager = null;

    List<GyroscopeSensor> sensors = new ArrayList<>();


    public String username = " ";
    public String deviceid = " ";
    public String studyId = " ";
    public Integer freq;

// movement detection part
    float currentAcceleration = SensorManager.GRAVITY_EARTH;
    long lastMovementDetectedTime = System.currentTimeMillis();
    boolean isMoving = true;
    boolean isfromStilltoMove = false;
    long lastStartCheckedTime = System.currentTimeMillis();
    public boolean to_save=true;


    // restrat automatically.
    @Override
    public void onDestroy() {

        Toast.makeText(this,getString(R.string.Gyroscope_sensor_stopped) , Toast.LENGTH_SHORT).show();

        stopGyroscopeSensor();

        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(this,getString(R.string.Gyroscope_sensor_started) , Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {
            startMyOwnForegroundOld();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null && intent.getAction() != null) {

            if (intent.getAction().equals("STOP")) {
                //your end servce code
                stopForeground(true);
                stopSelf();
            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startMyOwnForeground();
                } else {
                    startMyOwnForegroundOld();
                }
            }

        }

        //**
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // get device id if null use username
        username = sharedPref.getString(Constants.UserName_text, " ");
        username = username.replaceAll("\\s+", "");

        deviceid = sharedPref.getString(Constants.PREF_UNIQUE_ID, username);
        studyId = sharedPref.getString(Constants.studyId, studyId);
        freq = Integer.parseInt(sharedPref.getString("example_list","10000"));

//


        registerSensors();

        return START_STICKY;

        //return super.onStartCommand(intent, flags, startId);


    }


    private void registerSensors() {
            sensormanager = (SensorManager) getSystemService(SENSOR_SERVICE);

            sensormanager.registerListener(GyroscopeService.this, sensormanager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), freq,1000000);


    }


    /**
     * notification functions
     */
    private void startMyOwnForegroundOld() {
        mNotification = new NotificationCompat.Builder(this)
                .setContentTitle(Constants.NOTIFICATION_Title)
                .setTicker(Constants.NOTIFICATION_Tiker)
                .setContentText(getText(R.string.mainServiceNotificationText))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .setGroup(Constants.NOTIFICATION_Title)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setOngoing(true)
                .setSortKey("1")
                .build();



        startForeground(Constants.mainNotificationId, mNotification);



    }

    private void startMyOwnForeground() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            NotificationChannel chan = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.channelName, NotificationManager.IMPORTANCE_HIGH);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    // this is vita to show notification
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(getText(R.string.mainServiceNotificationText))
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setCategory(Notification.CATEGORY_EVENT)
                    .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
                    .setOnlyAlertOnce(true)
                    .build();
            startForeground(Constants.mainNotificationId, notification);
        }
    }



    private void stopGyroscopeSensor() {
        if (sensormanager != null ){
            sensormanager.unregisterListener(this);

        }

    }





    private void savetodb(  List<GyroscopeSensor> sensors) {



        try {
            GetGyroscopeSensorsDataAsyc.populateSensors(GyroscopeSenorsAppDatabase.getAppDatabase(this), sensors);
        } catch (Exception e) {
            throw new NullPointerException();
        }


    }






    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_GYROSCOPE){


            GyroscopeSensor sensor = new GyroscopeSensor();
            sensor.setSensorname("gyroscope");
            sensor.setTimestamp(System.currentTimeMillis());
            sensor.setDeviceid(deviceid);
            sensor.setUsername(username);
            sensor.setStudyId(studyId);
            sensor.setX(BigDecimal.valueOf(event.values[0]).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue());
            sensor.setY(BigDecimal.valueOf(event.values[1]).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue());
            sensor.setZ(BigDecimal.valueOf(event.values[2]).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue());
            steady_check(sensor);
            sensors.add(sensor);
            if (sensors.size() > 20 - 1) {
                try {
                    if (to_save){
                        Log.d("SAVE",  Boolean.toString(to_save));

                        savetodb(sensors);

                    }

                } catch (Exception e) {
                    throw new NullPointerException();

                } finally {
                    sensors.clear();

                }
            }

            // GetAccelerationSensorsDataAsyc.populateSensors(AccelerationSenorsAppDatabase.getAppDatabase(this), sensor);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void steady_check(GyroscopeSensor sensor){


        double x = sensor.getX();
        double y = sensor.getY();
        double z = sensor.getZ();

    /* Logic:
      If there is no external force on the device, vector sum of accelerometer sensor values
      will be only gravity. If there is a change in vector sum of gravity, then there is a force.
      If this force is significant, you can assume device is moving.
      If vector sum is equal to gravity with +/- threshold its stable lying on table.
      credit to : https://github.com/bleidingGOE/ESA-climber-fall-detection
     */
        float lastAcceleration = currentAcceleration;
        currentAcceleration = (float) Math.sqrt(x * x + y * y + z * z);
        float delta = Math.abs(currentAcceleration - lastAcceleration);

        if (delta >= Constants.motionThreshold) {
            lastMovementDetectedTime = System.currentTimeMillis();


            //restart service coming from still mode to moving mode
            if (isMoving && isfromStilltoMove) {
                isfromStilltoMove = false;
                lastStartCheckedTime = System.currentTimeMillis();

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        to_save=true;
//                        Log.d("MOVE", Boolean.toString(to_save));
                    }
                });



            }

            // check periodically during movement for sudden or doze stopping since the checking is done in the
            //body there is no need to check here.
            if (isMoving && System.currentTimeMillis() - lastStartCheckedTime > Constants.One_Hours) {
                lastStartCheckedTime = System.currentTimeMillis();


            }
            isMoving = true;


            // checking if device is still for long time.
        } else {
            long timeDelta = (System.currentTimeMillis() - lastMovementDetectedTime);
            if (isMoving && timeDelta > Constants.THIRTY_MINUTES) {
                isMoving = false;
                isfromStilltoMove = true;

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        to_save=false;
//                        Log.d("MOVE",  Boolean.toString(to_save));

                    }
                });


            }
        }


    }


} // end of function