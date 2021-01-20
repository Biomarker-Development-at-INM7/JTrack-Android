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
package inm7.Jutrack.jutrack_Social.AppUsageStatSensor;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import android.util.ArrayMap;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import inm7.Jutrack.jutrack_Social.BroadCastReciever.SensorBroadcastReceiver;
import inm7.Jutrack.jutrack_Social.Constants;
import inm7.Jutrack.jutrack_Social.MainService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Map;

public class AppUsageStatsJobService extends Worker {

    private static final String TAG = "AppUsageStatJobService";
    private boolean jobCancelled = false;


    private int patchsize = Constants.patchSize;

//    private UsageStatsManager mUsageStatsManager;
//    private PackageManager mPm;

    public String username = " ";
    public String deviceid = " ";
    public String studyId = " ";
    //**
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

    UsageStatsManager  mUsageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
    PackageManager mPm = getApplicationContext().getPackageManager();

    public AppUsageStatsJobService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        username = sharedPref.getString(Constants.UserName_text, " ");
        username = username.replaceAll("\\s+", "");

        deviceid = sharedPref.getString(Constants.PREF_UNIQUE_ID, username);
        studyId = sharedPref.getString(Constants.studyId, studyId);

    }




    private void syncOnBackGround() {

        // lastsyncvalue = getSharedPreferences(Constants.Barometerlastsync, Context.MODE_PRIVATE);
        // lastsyncvalueeditor = lastsyncvalue.edit();
        if (jobCancelled) {
            return;
        }
        //do it here------------------------
        new Thread(new Runnable() {
            @Override
            public void run() {


                UsageStatsAdapter();

                // when our job finished should send it, otherwise it will shows a notification on battery usage
                // true if need to re schedule


            }
        }).start();


    }



    public static class AppNameComparator implements Comparator<UsageStats> {
        private Map<String, String> mAppLabelList;

        AppNameComparator(Map<String, String> appList) {
            mAppLabelList = appList;
        }

        @Override
        public final int compare(UsageStats a, UsageStats b) {
            String alabel = mAppLabelList.get(a.getPackageName());
            String blabel = mAppLabelList.get(b.getPackageName());
            return alabel.compareTo(blabel);
        }
    }


    public static class LastTimeUsedComparator implements Comparator<UsageStats> {
        @Override
        public final int compare(UsageStats a, UsageStats b) {
            // return by descending order
            return (int) (b.getLastTimeUsed() - a.getLastTimeUsed());
        }
    }

    public static class UsageTimeComparator implements Comparator<UsageStats> {
        @Override
        public final int compare(UsageStats a, UsageStats b) {
            return (int) (b.getTotalTimeInForeground() - a.getTotalTimeInForeground());
        }
    }

    private void UsageStatsAdapter() {
        // Constants defining order for display order


        final ArrayMap<String, String> mAppLabelMap = new ArrayMap<>();
        final ArrayList<UsageStats> mPackageStats = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

      //  final List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, cal.getTimeInMillis(), System.currentTimeMillis());
        final Map<String, UsageStats> stats = mUsageStatsManager.queryAndAggregateUsageStats( cal.getTimeInMillis(), System.currentTimeMillis());
        if (stats == null) {
            return;
        }


        // for further usage only, now using only savetodb
        ArrayMap<String, UsageStats> map = new ArrayMap<>();
        final int statCount = stats.size();
        for (int i = 0; i < statCount; i++) {
            final android.app.usage.UsageStats pkgStats = (UsageStats) stats.values().toArray()[i];

            // load application labels for each application
            try {
                ApplicationInfo appInfo = mPm.getApplicationInfo(pkgStats.getPackageName(), 0);
                String label = appInfo.loadLabel(mPm).toString();
                mAppLabelMap.put(pkgStats.getPackageName(), label);

                UsageStats existingStats =
                        map.get(pkgStats.getPackageName());
                if (existingStats == null) {
                    map.put(pkgStats.getPackageName(), pkgStats);

                } else {

                    existingStats.add(pkgStats);


                }

            } catch (PackageManager.NameNotFoundException e) {

                Log.d(TAG, "UsageStatsAdapter: " + "This package may be gone.");
            }
        }


        mPackageStats.addAll(map.values());
        Log.d(TAG, "UsageStatsAdapter: " + mPackageStats);
        savetodb(mPackageStats);

        // Sort list
        //   mAppLabelComparator = new AppNameComparator(mAppLabelMap);
    }

    /**
     * save to database
     */

    public void savetodb(ArrayList<UsageStats> mPackageStats) {

        int mPackageStatsSize = mPackageStats.size();


        for (int i = 0; i < mPackageStatsSize; i++) {


            final android.app.usage.UsageStats pkgStats = mPackageStats.get(i);
            ApplicationInfo appInfo = null;
            try {
                appInfo = mPm.getApplicationInfo(mPackageStats.get(i).getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            String label = appInfo.loadLabel(mPm).toString();

            AppUsageStatSensor sensor = new AppUsageStatSensor();
            sensor.setSensorname("application_usage");
            sensor.setTimestamp(System.currentTimeMillis());
            sensor.setDeviceid(deviceid);
            sensor.setUsername(username);
            sensor.setStudyId(studyId);
            sensor.setAppName(label);
            sensor.setBeginTimeStamp(mPackageStats.get(i).getFirstTimeStamp());
            sensor.setEndTimeStamp(mPackageStats.get(i).getLastTimeStamp());
            sensor.setLastTimeUsed(mPackageStats.get(i).getLastTimeUsed());
            sensor.setTotalTimeinForeground(mPackageStats.get(i).getTotalTimeInForeground());

            //since time is in milisec its why i used 1000 so only more than 1 sec will be inserted to db
            if (mPackageStats.get(i).getTotalTimeInForeground() > 1000) {

                try {
                    GetAppUsageStatSensorsDataAsyc.populateSensors(AppUsageStatSenorsAppDatabase.getAppDatabase(getApplicationContext()), sensor);
                    jobCancelled=false;
                    //  Log.d(TAG, "UsageStatsAdapter:"+ sensor.getSensorname()+" "+sensor.getTotalTimeinForeground());
                } catch (Exception e) {
                    jobCancelled=true;
                    throw new NullPointerException();

                }


            }


        }

    }

    @Override
    public void onStopped() {
        super.onStopped();
        Intent broadcastIntent = new Intent(getApplicationContext(), SensorBroadcastReceiver.class);
       getApplicationContext().sendBroadcast(broadcastIntent);        
    }

    // functions related to job
    @NonNull
    @Override
    public Result doWork() {
        // get device id if null use username

        try {
            syncOnBackGround();

        } catch(Throwable e) {

            jobCancelled = false;
        }


        if(jobCancelled != false)
        {
            return Result.retry();

        } else {
            return Result.success();

        }

    }

}
