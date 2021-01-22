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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import inm7.Jutrack.jutrack_Social.BroadCastReciever.SensorBroadcastReceiver;


public class MainWorker extends Worker {
    static ActivityManager mmanager;

    Context mcontext;
    public MainWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mcontext = context;
    }

    public static void setManager(ActivityManager manager) {
        mmanager = manager;
    }



    private void study_duration_reached(){
        MyGlobalClass myGlobalClass = new MyGlobalClass(mcontext);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor sharedPrefDefaultedit  = sharedPref.edit();

        //check if study is over

            if (myGlobalClass.isNetworkAvailable()){

                Patinet patinet = new Patinet();

                patinet.setDeviceid(sharedPref.getString(Constants.PREF_UNIQUE_ID, null));
                patinet.setStatus(2);
                patinet.setStudyId(sharedPref.getString(Constants.studyId, null));
                patinet.setUsername(sharedPref.getString(Constants.UserName_text, null));
                patinet.setTime_left(System.currentTimeMillis());

                // sync before sync
                 myGlobalClass.manual_syc(); // can check the result if true or false,
                    // leave and stop
                myGlobalClass.leavePatient(patinet);




            }
            else { // network not available
                stopService(); // no internet leave


            }



    }




    public void stopService() {
        MyGlobalClass myGlobalClass = new MyGlobalClass(mcontext);

            myGlobalClass.startMainService("STOP_by_Leave_NoInternet");

    }


    @Override
    public void onStopped() {
        super.onStopped();
        Intent broadcastIntent = new Intent(getApplicationContext(), SensorBroadcastReceiver.class);
        broadcastIntent.setAction("START");

        getApplicationContext().sendBroadcast(broadcastIntent);
    }


    // This to check if app is already installed with older version
    // NO NEED FOR NEW STUDY OR EMA but better to keep it
    public void checkforPreviousInstall(){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor sharedPrefDefaultedit  = sharedPref.edit();

        if (sharedPref.getInt(Constants.isFirstLogin,1)==1){ // True its first logging

            if (sharedPref.getLong("patient_Time_Joined",0)!=0){
                sharedPrefDefaultedit.putInt(Constants.isFirstLogin, 0).apply(); // false
            }
        }
    }


    public void restartReminder(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        MyGlobalClass myGlobalClass = new MyGlobalClass(mcontext);


        Long sync_Time_diff_appUsage =  System.currentTimeMillis() -  sharedPref.getLong(Constants.appUsageStatLastSync, System.currentTimeMillis());
        Long sync_Time_diff_detectedActivity =  System.currentTimeMillis() -  sharedPref.getLong(Constants.detectedActivityLastSync, System.currentTimeMillis());
        Long sync_Time_diff_location =  System.currentTimeMillis() -  sharedPref.getLong(Constants.locationLastSync, System.currentTimeMillis());


        if ( (sync_Time_diff_appUsage>Constants.One_Day) | (sync_Time_diff_detectedActivity>Constants.One_Day) | (sync_Time_diff_location>Constants.One_Day) )
        {

            // check if manual active labeling is not active then start services.
            if (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("manual_ActiveLabeling_switch",false))
            {
                myGlobalClass.startMainService("START");
            }


            // KEEP THIS FOR FUTURE
            //This is to check if the last sync time is > 3 days, remind user to restart application for any possible problem
//            myGlobalClass.startMyOwnForeground("JuTrack neu starten","Bitte starten Sie JuTrack neu, damit es richtig funktioniert");

        }


    }

    @NonNull
    @Override
    public Result doWork() {

        MyGlobalClass myGlobalClass = new MyGlobalClass(mcontext);

        boolean duration_status = myGlobalClass.study_duration_reached();

         if (!duration_status ){

             checkforPreviousInstall();
             restartReminder();

        } else if(duration_status) {

            study_duration_reached();
        }


        return Result.success();
    }
}
