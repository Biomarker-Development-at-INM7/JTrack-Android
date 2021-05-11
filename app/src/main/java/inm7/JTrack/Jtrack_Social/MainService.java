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
package inm7.JTrack.Jtrack_Social;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import inm7.JTrack.Jtrack_Social.BroadCastReciever.SensorBroadcastReceiver;



public class MainService extends Service  {

    public SensorManager sensormanager = null;
    boolean isSensorSelectedPref;
    public boolean isAutoRestartSelectedPref;
    public int isLeaveByUser=0;


    private Notification mNotification;

    public String username = " ";
    public String deviceid = " ";

    protected static final String TAG = MainService.class.getSimpleName();
    private float currentAcceleration = SensorManager.GRAVITY_EARTH;
    private long lastMovementDetectedTime = System.currentTimeMillis();
    private boolean isMoving = false;

    private long lastStartCheckedTime = System.currentTimeMillis();
    private boolean isfromStilltoMove = false;

    MyGlobalClass myGlobalClass = new MyGlobalClass(this);


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //called every time.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null && intent.getAction() != null)
        {

            if (intent.getAction().equals( "STOP_by_Leave"))
            {
                //if its by user request to leave :
                isLeaveByUser=2;
                stopForeground(true);
                stopSelf();
            }
            else if(intent.getAction().equals( "STOP_by_Leave_NoInternet"))
            {
            isLeaveByUser=1;
            stop_service();

            }
            // stop recording when app is in manual model for Active Labeling
            else if(intent.getAction().equals( "STOP_by_Active_Labeling"))
            {
                isLeaveByUser=0;

                stop_service();

            }
            else // start
                {
                Toast.makeText(this,getString(R.string.Main_rservice_started) , Toast.LENGTH_SHORT).show();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startMyOwnForeground();
                } else {
                    startMyOwnForegroundOld();
                }

                if (!myGlobalClass.study_duration_reached()){

                    start_service();
                    schedule_sync();
                }else
                {

                    schedule_sync();

                }



            }

        } // no intent value
        else {

            Toast.makeText(this,getString(R.string.Main_rservice_started) , Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startMyOwnForeground();
            } else {
                startMyOwnForegroundOld();
            }
            if ((PreferenceManager.getDefaultSharedPreferences(this).getInt("ActiveLabeling_switch",0))!=2)
            {
                start_service();

            }
            schedule_sync();
        }



        return START_STICKY;
        // return super.onStartCommand(intent, flags, startId);

    }





    //called only one time.

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {
            startMyOwnForegroundOld();
        }

    }


    @Override
    public void onDestroy() {

        super.onDestroy();

        Toast.makeText(this,getString(R.string.Main_rservice_stopped) , Toast.LENGTH_SHORT).show();

        // check if auto restart is selected
        isAutoRestartSelectedPref =  PreferenceManager.getDefaultSharedPreferences(this).getBoolean("restartSensorService_switch",false);


        if (isAutoRestartSelectedPref &&  PreferenceManager.getDefaultSharedPreferences(this).getInt(Constants.isFirstLogin, 1)==0 ) {
            Toast.makeText(MainService.this, getString(R.string.Main_rservice_ReStart), Toast.LENGTH_SHORT).show();


            // broadcast  for crash and restart
            Intent broadcastIntent = new Intent(this, SensorBroadcastReceiver.class);
            sendBroadcast(broadcastIntent);

        } else {



            stop_service();
            cancel_Sync();
        }


    }




//===============================================================================================SERVICES====================================================================================================================


    /**
     * startJobs
     */

    public void start_service(){


        myGlobalClass.startAccelerometerServices();
        myGlobalClass.startGyroscopeServices();
        myGlobalClass.scheduleAppUsageStatJob();
        myGlobalClass.startDetectedActivityServices();
        myGlobalClass.startLocationServices();

    }

    public void schedule_sync(){

        myGlobalClass.scheduleLocationSycJob();
        myGlobalClass.scheduleAppUsageStatSycJob();
        myGlobalClass.scheduleDetectedActivitySycJob();
        myGlobalClass.scheduleAccelerationSensorSycJob();
        myGlobalClass.scheduleGyroscopeSensorSycJob();

        myGlobalClass.scheduleActiveLabelingSensorSycJob();
    }



    /**
     * cancel Jobs
     */

    public void stop_service(){


        myGlobalClass.stopAccelerometerServices();
        myGlobalClass.stopGyrospoeServices();
        myGlobalClass.cancelAppUsageStatJob();
        myGlobalClass.stopDetectedActivityServices();
        myGlobalClass.stopLocationServices();
    }

    public void cancel_Sync(){
        myGlobalClass.cancelLocationSycJob();
        myGlobalClass.cancelAppUsageStatSycJob();
        myGlobalClass.cancelDetectedActivitySycJob();
        myGlobalClass.cancelAccelerationSensorSycJob();
        myGlobalClass.cancelGyroscopeSensorSycJob();

        myGlobalClass.cancelActiveLabelingSensorSycJob();

    }









    //==================================================================================================================================================================================================================


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
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_NO_CREATE))
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
                    .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_NO_CREATE))
                    .setOnlyAlertOnce(true)
                    .build();
            startForeground(Constants.mainNotificationId, notification);
        }
    }




    }

