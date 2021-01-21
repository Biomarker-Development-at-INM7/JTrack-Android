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
package inm7.Jutrack.jutrack_Social;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import inm7.Jutrack.jutrack_Social.AccelerationSensor.AccelerationService;
import inm7.Jutrack.jutrack_Social.AccelerationSensor.SyncAccelerationClass;
import inm7.Jutrack.jutrack_Social.AccelerationSensor.SyncAccelerationJobService;
import inm7.Jutrack.jutrack_Social.ActiveLabeling.SyncActiveMonitoringJobService;
import inm7.Jutrack.jutrack_Social.ActiveLabeling.SyncActiveMonitoringStatClass;
import inm7.Jutrack.jutrack_Social.AppUsageStatSensor.AppUsageStatsJobService;
import inm7.Jutrack.jutrack_Social.AppUsageStatSensor.SyncAppUsageStatClass;
import inm7.Jutrack.jutrack_Social.AppUsageStatSensor.SyncAppUsageStatJobService;
import inm7.Jutrack.jutrack_Social.DetectedActivitySensor.BackgroundDetectedActivitiesService;
import inm7.Jutrack.jutrack_Social.DetectedActivitySensor.SyncActivityClass;
import inm7.Jutrack.jutrack_Social.DetectedActivitySensor.SyncActivityJobService;
import inm7.Jutrack.jutrack_Social.GyroscopeSensor.GyroscopeService;
import inm7.Jutrack.jutrack_Social.GyroscopeSensor.SyncGyroscopeClass;
import inm7.Jutrack.jutrack_Social.GyroscopeSensor.SyncGyroscopeJobService;
import inm7.Jutrack.jutrack_Social.LocationSensor.LocationUpdatesService;
import inm7.Jutrack.jutrack_Social.LocationSensor.SyncLocationClass;
import inm7.Jutrack.jutrack_Social.LocationSensor.SyncLocationJobService;
import inm7.Jutrack.jutrack_Social.OnBoarding.OnBoardingActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyGlobalClass {
    Context mContext;
    // constructor
    public MyGlobalClass(Context context){
        this.mContext = context;
    }

    WorkManager AppUsageJobWorkManager = WorkManager.getInstance();
    WorkManager LocationSyncWorkManager = WorkManager.getInstance();
    WorkManager ActivitySyncWorkManager = WorkManager.getInstance();
    WorkManager AppUsageSyncWorkManager = WorkManager.getInstance();
    WorkManager GyroscopeSyncWorkManager = WorkManager.getInstance();
    WorkManager AccelerationSyncWorkManager = WorkManager.getInstance();
    WorkManager MainServiceManager = WorkManager.getInstance();
    WorkManager ActiveLabelingManager = WorkManager.getInstance();


    public void startMainService (String intent){

        Intent mainServiceIntent = new Intent(mContext, MainService.class);
        mainServiceIntent.setAction(intent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(mainServiceIntent);

        } else {
            mContext.startService(mainServiceIntent);
        }
    }

    public void startLocationServices() {

        if (!isMyServiceRunning(LocationUpdatesService.class)) {
            boolean isSensorSelectedPref = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("switch_preference_Location", false);
            if (isSensorSelectedPref != false) {
                Log.d("Main service", "startServices: " + "location service started");

                Intent locationServiceIntent = new Intent(mContext, LocationUpdatesService.class);
                locationServiceIntent.setAction("START");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    mContext.startForegroundService(locationServiceIntent);

                } else {
                    mContext.startService(locationServiceIntent);
                }
            }
        }

    }

    public void startDetectedActivityServices() {

        if (!isMyServiceRunning(BackgroundDetectedActivitiesService.class)) {

           boolean isSensorSelectedPref = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("switch_preference_ActivityDetection",false);
            if (isSensorSelectedPref != false) {
                Log.d("Main service", "startServices: " + "Detected activity service started");

                Intent detectedActivityIntent = new Intent(mContext, BackgroundDetectedActivitiesService.class);
                detectedActivityIntent.setAction("START");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mContext.startForegroundService(detectedActivityIntent);

                } else {
                    mContext.startService(detectedActivityIntent);
                }
            }
        }

    }

    public void startGyroscopeServices(){
        if (!isMyServiceRunning(GyroscopeService.class)) {

          boolean  isSensorSelectedPref = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("switch_preference_Gyroscope",false);

            if (isSensorSelectedPref != false) {
                Log.d("Main service", "startServices: " + "Gyroscope service started");

                Intent GyroscopeServiceIntent = new Intent(mContext, GyroscopeService.class);
                GyroscopeServiceIntent.setAction("START");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    mContext.startForegroundService(GyroscopeServiceIntent);

                } else {
                    mContext.startService(GyroscopeServiceIntent);
                }

            }

        }
    }


    public void startAccelerometerServices(){
        if (!isMyServiceRunning(AccelerationService.class)) {

            boolean isSensorSelectedPref = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("switch_preference_Acceleration",false);

            if (isSensorSelectedPref != false) {
                Log.d("Main service", "startServices: " + "Acceleration service started");

                Intent AccelerationServiceIntent = new Intent(mContext, AccelerationService.class);
                AccelerationServiceIntent.setAction("START");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    mContext.startForegroundService(AccelerationServiceIntent);

                } else {
                    mContext.startService(AccelerationServiceIntent);
                }

            }

        }
    }

    public void stopGyrospoeServices(){
        if (isMyServiceRunning(GyroscopeService.class)) {
            Log.d("main service", "stop Services: " + "Gyroscope service Stopped");

            Intent sensorsServiceIntent = new Intent(mContext, GyroscopeService.class);
            sensorsServiceIntent.setAction("STOP");
            mContext.startService(sensorsServiceIntent);
        }
    }

    public void stopAccelerometerServices(){
        Log.d("main service", "stop Services: " + "Acceleration service Stopped");

        Intent sensorsServiceIntent = new Intent(mContext, AccelerationService.class);
        sensorsServiceIntent.setAction("STOP");
        mContext.startService(sensorsServiceIntent);
    }

    public void stopLocationServices() {

        if (isMyServiceRunning(LocationUpdatesService.class)) {
            Log.d("main service", "stop Services: " + "Location service Stopped");

            Intent sensorsServiceIntent = new Intent(mContext, LocationUpdatesService.class);
            sensorsServiceIntent.setAction("STOP");
            mContext.startService(sensorsServiceIntent);
        }

    }
    public void stopDetectedActivityServices() {
        if (isMyServiceRunning(BackgroundDetectedActivitiesService.class)) {
            Log.d("main service", "stop Services: " + "Detected activity service Stopped");

            Intent sensorsServiceIntent = new Intent(mContext, BackgroundDetectedActivitiesService.class);
            sensorsServiceIntent.setAction("STOP");
            mContext.startService(sensorsServiceIntent);
        }

    }

    public void scheduleAppUsageStatJob() {

        boolean isSensorSelectedPref =  PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("switch_preference_AppUsage",false);
        if (isSensorSelectedPref != false) {
//            if (!isMyJobscheduled(Constants.AppUsageStatJobid)) {
//                ComponentName componentName = new ComponentName(this, AppUsageStatsJobService.class);
//
//                JobInfo jobInfo = new JobInfo.Builder(Constants.AppUsageStatJobid, componentName)
//                        .setPersisted(true)
//                        .setPeriodic(Constants.AppUsageStatjobPeriod)
//                        .setRequiresCharging(false)
//                        .build();
//                JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//                int resultcode = scheduler.schedule(jobInfo);
//
//                if (resultcode == JobScheduler.RESULT_SUCCESS) {
//                    Log.d(TAG, "scheduleAppUsageStatJob: " + "job scheduled");
//                } else {
//                    Log.d(TAG, "scheduleAppUsageStatJob: " + "job NOT scheduled");
//
//                }
//            }

            Constraints constraintsAppUsage = new Constraints.Builder()
                    .setRequiresCharging(false)
                    .build();


            PeriodicWorkRequest periodicAppUsageStatJobWork =
                    new PeriodicWorkRequest.Builder(AppUsageStatsJobService.class, Constants.AppUsageStatjobPeriod, TimeUnit.MILLISECONDS)
                            .addTag(Constants.AppUsageStatJobName)
                            // setting a backoff on case the work needs to retry
                            .setConstraints(constraintsAppUsage)
                            .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                            .build();

            AppUsageJobWorkManager.enqueueUniquePeriodicWork(
                    Constants.AppUsageStatJobName,
                    ExistingPeriodicWorkPolicy.KEEP, //Existing Periodic Work policy
                    periodicAppUsageStatJobWork //work request
            );

        }


    }

    public void cancelAppUsageStatJob() {
//        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        scheduler.cancel(Constants.AppUsageStatJobid);
        WorkManager.getInstance().cancelUniqueWork(Constants.AppUsageStatJobName);

        Log.d("TAG", "scheduleAppUsageStatJob: " + "job canceled!");
    }




    /**
     * schedule Location sync job
     */

    public void scheduleLocationSycJob() {

//        if (!isMyJobscheduled(Constants.LocationSyncJobid)) {
//
//            ComponentName componentName = new ComponentName(this, SyncLocationJobService.class);
//            JobInfo jobInfo = new JobInfo.Builder(Constants.LocationSyncJobid, componentName)
//                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//                    .setPersisted(true)
//                    .setPeriodic(Constants.jobPeriod)
//                    .build();
//            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//
//            int resultcode = scheduler.schedule(jobInfo);
//
//            if (resultcode == JobScheduler.RESULT_SUCCESS) {
//                Log.d(TAG, "scheduleLocationSycJob: " + "job scheduled");
//            } else {
//                Log.d(TAG, "scheduleLocationSycJob: " + "job NOT scheduled");
//
//            }
//
//        }


        boolean isSensorSelectedPref = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("switch_preference_Location", false);
        if (isSensorSelectedPref != false) {

            PeriodicWorkRequest periodicLocationSyncDataWork =
                    new PeriodicWorkRequest.Builder(SyncLocationJobService.class, Constants.jobPeriod, TimeUnit.MILLISECONDS)
                            .addTag(Constants.LocationSyncJobName)
                            .setConstraints(checkNetworkConstrain())
                            // setting a backoff on case the work needs to retry
                            .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                            .build();

            LocationSyncWorkManager.enqueueUniquePeriodicWork(
                    Constants.LocationSyncJobName,
                    ExistingPeriodicWorkPolicy.KEEP, //Existing Periodic Work policy
                    periodicLocationSyncDataWork //work request
            );


        }
    }

    public void cancelLocationSycJob() {
//        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        scheduler.cancel(Constants.LocationSyncJobid);

        WorkManager.getInstance().cancelUniqueWork(Constants.LocationSyncJobName);

        Log.d("TAG", "scheduleLocationSycJob: " + "job canceled!");
    }


    /**
     * schedule DetectedActivity  sync job
     */

    public void scheduleDetectedActivitySycJob() {

//        if (!isMyJobscheduled(Constants.DetectedActivitySyncJobid)) {
//
//            ComponentName componentName = new ComponentName(this, SyncActivityJobService.class);
//            JobInfo jobInfo = new JobInfo.Builder(Constants.DetectedActivitySyncJobid, componentName)
//                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//                    .setPersisted(true)
//                    .setPeriodic(Constants.jobPeriod)
//                    .build();
//            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//            int resultcode = scheduler.schedule(jobInfo);
//
//            if (resultcode == JobScheduler.RESULT_SUCCESS) {
//                Log.d(TAG, "scheduleDetectedActivitySycJob: " + "job scheduled");
//            } else {
//                Log.d(TAG, "scheduleDetectedActivitySycJob: " + "job NOT scheduled");
//
//            }
//
//        }

        boolean isSensorSelectedPref = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("switch_preference_ActivityDetection", false);
        if (isSensorSelectedPref != false) {
            PeriodicWorkRequest periodicActivitySyncDataWork =
                    new PeriodicWorkRequest.Builder(SyncActivityJobService.class, Constants.jobPeriod, TimeUnit.MILLISECONDS)
                            .addTag(Constants.DetectedActivitySyncJobName)
                            .setConstraints(checkNetworkConstrain())
                            // setting a backoff on case the work needs to retry
                            .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                            .build();

            ActivitySyncWorkManager.enqueueUniquePeriodicWork(
                    Constants.DetectedActivitySyncJobName,
                    ExistingPeriodicWorkPolicy.KEEP, //Existing Periodic Work policy
                    periodicActivitySyncDataWork //work request
            );

        }
    }

    public void cancelDetectedActivitySycJob() {
//        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        scheduler.cancel(Constants.DetectedActivitySyncJobid);
        WorkManager.getInstance().cancelUniqueWork(Constants.DetectedActivitySyncJobName);

        Log.d("TAG", "scheduleDetectedActivitySycJob: " + "job canceled!");
    }


    /**
     * schedule AppUsageStat  sync job
     */

    public void scheduleAppUsageStatSycJob() {
// using with  job
//        if (!isMyJobscheduled(Constants.AppUsageStatSyncJobid)) {
//            ComponentName componentName = new ComponentName(this, SyncAppUsageStatJobService.class);
//            JobInfo jobInfo = new JobInfo.Builder(Constants.AppUsageStatSyncJobid, componentName)
//                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//                    .setPersisted(true)
//                    .setPeriodic(Constants.jobPeriod)
//                    .build();
//            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//            int resultcode = scheduler.schedule(jobInfo);
//
//            if (resultcode == JobScheduler.RESULT_SUCCESS) {
//                Log.d(TAG, "scheduleAppUsageStatSycJob: " + "job scheduled");
//            } else {
//                Log.d(TAG, "scheduleAppUsageStatSycJob: " + "job NOT scheduled");
//
//            }  }


        boolean isSensorSelectedPref = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("switch_preference_AppUsage", false);
        if (isSensorSelectedPref != false) {
            PeriodicWorkRequest periodicAppUsageSyncDataWork =
                    new PeriodicWorkRequest.Builder(SyncAppUsageStatJobService.class, Constants.jobPeriod, TimeUnit.MILLISECONDS)
                            .addTag(Constants.AppUsageStatSyncJobName)
                            .setConstraints(checkNetworkConstrain())
                            // setting a backoff on case the work needs to retry
                            .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                            .build();

            AppUsageSyncWorkManager.enqueueUniquePeriodicWork(
                    Constants.AppUsageStatSyncJobName,
                    ExistingPeriodicWorkPolicy.KEEP, //Existing Periodic Work policy
                    periodicAppUsageSyncDataWork //work request
            );


        }
    }


    public void cancelAppUsageStatSycJob() {

        // using with  job
//        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        scheduler.cancel(Constants.AppUsageStatSyncJobid);
        WorkManager.getInstance().cancelUniqueWork(Constants.AppUsageStatSyncJobName);
        Log.d("TAG", "scheduleAppUsageStatSycJob: " + "job canceled!");
    }


    public void scheduleGyroscopeSensorSycJob() {
      boolean  isSensorSelectedPref =  PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("switch_preference_Gyroscope",false);
        if (isSensorSelectedPref != false) {
//            if (!isMyJobscheduled(Constants.AppUsageStatJobid)) {
//                ComponentName componentName = new ComponentName(this, AppUsageStatsJobService.class);
//
//                JobInfo jobInfo = new JobInfo.Builder(Constants.AppUsageStatJobid, componentName)
//                        .setPersisted(true)
//                        .setPeriodic(Constants.AppUsageStatjobPeriod)
//                        .setRequiresCharging(false)
//                        .build();
//                JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//                int resultcode = scheduler.schedule(jobInfo);
//
//                if (resultcode == JobScheduler.RESULT_SUCCESS) {
//                    Log.d(TAG, "scheduleAppUsageStatJob: " + "job scheduled");
//                } else {
//                    Log.d(TAG, "scheduleAppUsageStatJob: " + "job NOT scheduled");
//
//                }
//            }


            PeriodicWorkRequest periodicAccelerationJobWork =
                    new PeriodicWorkRequest.Builder(SyncGyroscopeJobService.class, Constants.GyroscopeJobPeriod, TimeUnit.MILLISECONDS)
                            .addTag(Constants.GyroscopeJobName)
                            // setting a backoff on case the work needs to retry
                            .setConstraints(checkNetworkConstrain())
                            .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                            .build();

            GyroscopeSyncWorkManager.enqueueUniquePeriodicWork(
                    Constants.GyroscopeJobName,
                    ExistingPeriodicWorkPolicy.KEEP, //Existing Periodic Work policy
                    periodicAccelerationJobWork //work request
            );

        }


    }

    public void cancelGyroscopeSensorSycJob() {
//        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        scheduler.cancel(Constants.AppUsageStatJobid);
        WorkManager.getInstance().cancelUniqueWork(Constants.GyroscopeJobName);

        Log.d("Main Service", "cancelGyroscopeSensorSycJob: " + "job canceled!");
    }



    public void scheduleAccelerationSensorSycJob() {
       boolean isSensorSelectedPref =  PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("switch_preference_Acceleration",false);
        if (isSensorSelectedPref != false) {
//            if (!isMyJobscheduled(Constants.AppUsageStatJobid)) {
//                ComponentName componentName = new ComponentName(this, AppUsageStatsJobService.class);
//
//                JobInfo jobInfo = new JobInfo.Builder(Constants.AppUsageStatJobid, componentName)
//                        .setPersisted(true)
//                        .setPeriodic(Constants.AppUsageStatjobPeriod)
//                        .setRequiresCharging(false)
//                        .build();
//                JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//                int resultcode = scheduler.schedule(jobInfo);
//
//                if (resultcode == JobScheduler.RESULT_SUCCESS) {
//                    Log.d(TAG, "scheduleAppUsageStatJob: " + "job scheduled");
//                } else {
//                    Log.d(TAG, "scheduleAppUsageStatJob: " + "job NOT scheduled");
//
//                }
//            }


            PeriodicWorkRequest periodicAccelerationJobWork =
                    new PeriodicWorkRequest.Builder(SyncAccelerationJobService.class, Constants.AccelerationJobPeriod, TimeUnit.MILLISECONDS)
                            .addTag(Constants.AccelerationJobName)
                            // setting a backoff on case the work needs to retry
                            .setConstraints(checkNetworkConstrain())
                            .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                            .build();

            AccelerationSyncWorkManager.enqueueUniquePeriodicWork(
                    Constants.AccelerationJobName,
                    ExistingPeriodicWorkPolicy.KEEP, //Existing Periodic Work policy
                    periodicAccelerationJobWork //work request
            );

        }


    }

    public void cancelAccelerationSensorSycJob() {
//        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        scheduler.cancel(Constants.AppUsageStatJobid);
        WorkManager.getInstance().cancelUniqueWork(Constants.AccelerationJobName);

        Log.d("Main Service", "cancelAccelerationSensorSycJob: " + "job canceled!");
    }





    public void scheduleActiveLabelingSensorSycJob() {
//        boolean isSensorSelectedPref =  PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("switch_preference_Acceleration",false);
        if (true) {
//            if (!isMyJobscheduled(Constants.AppUsageStatJobid)) {
//                ComponentName componentName = new ComponentName(this, AppUsageStatsJobService.class);
//
//                JobInfo jobInfo = new JobInfo.Builder(Constants.AppUsageStatJobid, componentName)
//                        .setPersisted(true)
//                        .setPeriodic(Constants.AppUsageStatjobPeriod)
//                        .setRequiresCharging(false)
//                        .build();
//                JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//                int resultcode = scheduler.schedule(jobInfo);
//
//                if (resultcode == JobScheduler.RESULT_SUCCESS) {
//                    Log.d(TAG, "scheduleAppUsageStatJob: " + "job scheduled");
//                } else {
//                    Log.d(TAG, "scheduleAppUsageStatJob: " + "job NOT scheduled");
//
//                }
//            }


            PeriodicWorkRequest periodicActiveLabelingJobWork =
                    new PeriodicWorkRequest.Builder(SyncActiveMonitoringJobService.class, Constants.ActiveLabelingJobPeriod, TimeUnit.MILLISECONDS)
                            .addTag(Constants.ActiveLabelingJobName)
                            // setting a backoff on case the work needs to retry
                            .setConstraints(checkNetworkConstrain())
                            .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                            .build();

            ActiveLabelingManager.enqueueUniquePeriodicWork(
                    Constants.ActiveLabelingJobName,
                    ExistingPeriodicWorkPolicy.KEEP, //Existing Periodic Work policy
                    periodicActiveLabelingJobWork //work request
            );

        }


    }

    public void cancelActiveLabelingSensorSycJob() {
//        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        scheduler.cancel(Constants.AppUsageStatJobid);
        WorkManager.getInstance().cancelUniqueWork(Constants.ActiveLabelingJobName);

        Log.d("Main Service", "cancelAccelerationSensorSycJob: " + "job canceled!");
    }



    //checks if data usage over carrier is allowed if not only sync on wifi

    public  Constraints checkNetworkConstrain(){
        boolean network_preference= PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("switch_preference_phoneData",false);

        // Create Network constraint
        if (network_preference==true) {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();
            return constraints;
        }
        else
        {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .build();
            return constraints;

        }
    }


    public void schedule_mainWorker(){
        // starter  a  worker  for periodic check of main service,

        ActivityManager manager = (ActivityManager) mContext.getSystemService(mContext.ACTIVITY_SERVICE);


        MainWorker.setManager(manager);
        PeriodicWorkRequest periodicAppUsageStatJobWork =
                new PeriodicWorkRequest.Builder(MainWorker.class, 60, TimeUnit.MINUTES)
                        .addTag(Constants.MainServiceJobName)
                        // setting a backoff on case the work needs to retry
                        .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                        .build();

        MainServiceManager.enqueueUniquePeriodicWork(
                Constants.MainServiceJobName,
                ExistingPeriodicWorkPolicy.KEEP, //Existing Periodic Work policy
                periodicAppUsageStatJobWork //work request



        );

    }

    //==================================================================================================================================================================================================================


    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager =(ActivityManager) mContext.getSystemService(mContext.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //not used since  moved to worker
    public boolean isMyJobscheduled(int jobId) {
        JobScheduler js = mContext.getSystemService(JobScheduler.class);
        List<JobInfo> jobs = js.getAllPendingJobs();
        if (jobs.size() == 0) {
            return false;
        }

        for (int i = 0; i < jobs.size(); i++) {
            if (jobs.get(i).getId() == jobId) {
                return true; //is not scheduled
            }

        }

        return false; // is not scheduled
    }


    //--------------------------------------


    public void startMyOwnForeground(String title, String body) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            NotificationChannel chan = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.channelName, NotificationManager.IMPORTANCE_HIGH);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext.getApplicationContext(), Constants.NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(false)
                    // this is vita to show notification
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getApplicationContext().getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setCategory(Notification.CATEGORY_EVENT)
                    .setContentIntent(PendingIntent.getActivity(mContext.getApplicationContext(), 1, new Intent(mContext.getApplicationContext(), MainActivity.class), PendingIntent.FLAG_NO_CREATE))
                    .setAutoCancel(true)
                    .build();
            manager.notify(Constants.pushNotificationId, notification);
        }
    }


    public static String md5(Patinet sensor) {

        // GsonBuilder gsonBuilder= new GsonBuilder();
        //  Gson gson = gsonBuilder.create();
        // String JsonObject=gson.toJson(sensor);

        Gson gson= new Gson();
        byte[] JsonObject = new byte[0];
        try {
            JsonObject = gson.toJson(sensor).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] thedigest = m.digest(JsonObject);
        String hash = String.format("%032x", new BigInteger(1, thedigest));
        return hash;


    }

    public boolean manual_syc(){

        SyncAppUsageStatClass syncAppUsageStatClass = new SyncAppUsageStatClass(mContext.getApplicationContext(), 0);
        syncAppUsageStatClass.syncOnBackGround();
        boolean AppUsageJobCanceled = syncAppUsageStatClass.jobCancelled;

        SyncActivityClass syncActivityClass = new SyncActivityClass(mContext.getApplicationContext(), 0);
        syncActivityClass.syncOnBackGround();
        boolean ActivityJobCanceled = syncActivityClass.jobCancelled;

        SyncLocationClass syncLocationClass = new SyncLocationClass(mContext.getApplicationContext(), 0);
        syncLocationClass.syncOnBackGround();
        boolean LocationJobCanceled = syncLocationClass.jobCancelled;

        SyncAccelerationClass syncAccelerationClass = new SyncAccelerationClass(mContext.getApplicationContext(),0);
        syncAccelerationClass.syncOnBackGround();
        boolean AccelerationJobCanceled = syncAccelerationClass.jobCancelled;

        SyncGyroscopeClass syncGyroscopeClass = new SyncGyroscopeClass(mContext.getApplicationContext(),0);
        syncGyroscopeClass.syncOnBackGround();
        boolean GyroscopeJobCanceled= syncGyroscopeClass.jobCancelled;

        SyncActiveMonitoringStatClass syncActiveMonitoringStatClass = new SyncActiveMonitoringStatClass(mContext.getApplicationContext(),0);
        syncActiveMonitoringStatClass.syncOnBackGround();
        boolean ActiveMonitoringJobCanceled = syncActiveMonitoringStatClass.jobCancelled;

        return LocationJobCanceled || ActivityJobCanceled || AppUsageJobCanceled || AccelerationJobCanceled || GyroscopeJobCanceled || AccelerationJobCanceled;

    }

    //  check if there is a Internet connection
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public boolean study_duration_reached(){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        SharedPreferences.Editor sharedPrefDefaultedit  = sharedPref.edit();

        //check if study is over
         return  System.currentTimeMillis() >= sharedPref.getLong("patient_Time_Joined",System.currentTimeMillis()) + //TimeUnit.MINUTES.toMillis(5) ;
               (  TimeUnit.DAYS.toMillis(sharedPref.getInt("study_Duration_days",30)) ) ;


    }


    public void leavePatient(final Patinet patinet) {
        MyGlobalClass myGlobalClass = new MyGlobalClass(mContext);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.serveraddress)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Serverinterface serverinterface = retrofit.create(Serverinterface.class);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        SharedPreferences.Editor sharedPrefDefaultedit  = sharedPref.edit();

        String md5_value = myGlobalClass.md5(patinet);



        final Call<Patinet> call = serverinterface.createPatient("UTF-8",Constants.update_user, md5_value, patinet);


        call.enqueue(new Callback<Patinet>() {

            @Override
            public void onResponse(Call<Patinet> call, Response<Patinet> response) {
                if (!response.isSuccessful()) {

                    return;
                }

                if (response.code() == 200) {


                   stop_normal();


                    startMyOwnForeground("Sie haben die Studie erfolgreich beendet","Sie können JuTrack social jetzt deinstallieren.");
                    // to get out of mainactivity

                    Intent i = new Intent(mContext.getApplicationContext(), OnBoardingActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);



                } else if (response.code() == 403) {

                    stop_normal();

                } else {


                }

            }

            @Override
            public void onFailure(Call<Patinet> call, Throwable t) {
                Log.d("error", "onCreatePatient: " + t.getMessage());


            }

        });

    }


    public void stop_normal(){


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        SharedPreferences.Editor sharedPrefDefaultedit  = sharedPref.edit();

        // set the stattus of first loggin to 1
        sharedPrefDefaultedit.putInt(Constants.isFirstLogin, 1).apply();
        sharedPrefDefaultedit.putLong("patient_Time_Joined",0).apply();


        // stop normal
        startMainService("STOP_by_Leave");
        WorkManager.getInstance().cancelUniqueWork(Constants.MainServiceJobName);

    }




}

