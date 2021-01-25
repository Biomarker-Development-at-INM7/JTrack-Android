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


import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import inm7.Jutrack.jutrack_Social.ActiveLabeling.ActiveLabelingActivity;
import inm7.Jutrack.jutrack_Social.AppSetting.SettingsActivity;
import inm7.Jutrack.jutrack_Social.OnBoarding.OnBoardingActivity;
import inm7.Jutrack.jutrack_Social.OnBoarding.PhoneModel_Utile;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.PACKAGE_USAGE_STATS;
import static android.Manifest.permission.ACTIVITY_RECOGNITION;



public class MainActivity extends AppCompatActivity implements YesOrNoAlertDialog.yesOrNoAlertDialogListener {


    private static final String TAG = "MainActivity";
    BroadcastReceiver broadcastReceiver;

    SharedPreferences sharedPrefDefault = null;
    SharedPreferences.Editor sharedPrefDefaultedit = null;

    // added after room integration
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constants.serveraddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    Serverinterface serverinterface = retrofit.create(Serverinterface.class);

    MyGlobalClass myGlobalClass = new MyGlobalClass(this);

    String versionName;
    public double versionValue;

    long firstInstallTime;
    long updateInstallTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //default preferences
        sharedPrefDefault = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefDefaultedit = sharedPrefDefault.edit();


        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));



        isFirstLogin();

        // if crashed before
        if (getIntent().getBooleanExtra("Crash Logger", false)) {
            Toast.makeText(this, getString(R.string.Crash_restart_msg), Toast.LENGTH_SHORT).show();


        }




        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);

                    Toast.makeText(getApplicationContext(),
                            getString(type),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };



    }


    @Override
    protected void onStart() {
        super.onStart();
        //check if it is the first time user here
        if (!isFirstLogin()){
            checkPermissions();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFirstLogin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            final EditText view = new EditText(this);
            view.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.adminPanelTitle)
                    .setMessage(R.string.adminPanelText)
                    .setView(view)
                    .setPositiveButton(R.string.adminPanelOk, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Integer.parseInt(view.getText().toString()) == Constants.adminPassword) {
                                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                            }

                        }
                    })
                    .setNegativeButton(R.string.adminPanelCancel, null)
                    .show();


            return true;
        }

        if (id == R.id.action_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));

        }

        if (id == R.id.active_monitoring) {
            startActivity(new Intent(MainActivity.this, ActiveLabelingActivity.class));

        }

        if (id == R.id.action_leave_study) {

            deleteDialog();
        }

        if (id == R.id.manual_sync) {
            if (!myGlobalClass.isNetworkAvailable()){

                InternetDialog();


            } else {

               if (!myGlobalClass.manual_syc()){
                   Toast.makeText(MainActivity.this, getString(R.string.Manual_Sync_Ok) , Toast.LENGTH_LONG).show();

                } else {
                   Toast.makeText(MainActivity.this, getString(R.string.Manual_Sync_Error), Toast.LENGTH_LONG).show();

               }



            }


        }

        return super.onOptionsItemSelected(item);
    }


    public boolean isFirstLogin(){

        // check if its first usage
        // if yes redirect it to registration page



//        try {
//            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
////             versionName=pInfo.versionName.toString();
////             versionValue=Double.parseDouble(versionName);
//
//              firstInstallTime= pInfo.firstInstallTime;
//              updateInstallTime= pInfo.lastUpdateTime;
//
//
//
//        }
//        catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

        // this to check if app is already installed with older version
        // NO NEED FOR NEW STUDY OR EMA better to keep it

        //---------

        if (sharedPrefDefault.getInt(Constants.isFirstLogin,1)==1){ // True its first logging

            if (sharedPrefDefault.getLong("patient_Time_Joined",0)!=0){
                sharedPrefDefaultedit.putInt(Constants.isFirstLogin, 0).apply(); // false
            }
        }
        if (sharedPrefDefault.getInt(Constants.isFirstLogin,1)==1) { // True its first logging

                Intent i = new Intent(this, OnBoardingActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                finish();
            return true;
        }else
        {
            return false;
        }

    }


    public void checkPermissions(){
        // check All Permissions
        if(checkPermissionAll()){
            // phone  model check!
            PhoneModel_Utile.PhoneModel_Utile(this);

            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                    !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                LocationDialog();

            }

            // if active labeling with manual mode is not selected then start the service every time main activity opened
            if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("manual_ActiveLabeling_switch",false))
            {
                startService();
            }

        }
        else {

            requestPermission();
        }
    }

    public static final int RequestPermissionCode = 1;

    public boolean checkPermissionAll() {

        int FifthPermissionResult=0;
        int ForthPermissionResult =0;
        int SecondPermissionResult=0;
        int ThirdPermissionResult=0;


        if ( PreferenceManager.getDefaultSharedPreferences(this).getBoolean("switch_preference_AppUsage",false))
        {
            AppOpsManager appOps = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
             ForthPermissionResult =appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), this.getPackageName());

        }else
        {
             ForthPermissionResult =0;
        }
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        if ( PreferenceManager.getDefaultSharedPreferences(this).getBoolean("switch_preference_Location",false)) {
             SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
             ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        }
        else
        {
        SecondPermissionResult=0;
        ThirdPermissionResult=0;
        }


        if ( PreferenceManager.getDefaultSharedPreferences(this).getBoolean("switch_preference_ActivityDetection",false)) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                FifthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACTIVITY_RECOGNITION);

            } else {
                FifthPermissionResult = 0;
            }

        }else
        {
            FifthPermissionResult = 0;
        }




        //  int ForthPermissionResult = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), this.getPackageName());

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ForthPermissionResult==AppOpsManager.MODE_ALLOWED &&
                FifthPermissionResult==PackageManager.PERMISSION_GRANTED;


    }

    private void requestPermission() {




        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {

                        CAMERA,
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION,
                        PACKAGE_USAGE_STATS,
                        ACTIVITY_RECOGNITION,
                }, RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean FineLocationPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean CoarselocationPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean PackagePermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean ActivityPermission = grantResults[4] == PackageManager.PERMISSION_GRANTED;

                    if (!PackagePermission) {

                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    if (CameraPermission && FineLocationPermission && CoarselocationPermission && PackagePermission && ActivityPermission) {
                        Toast.makeText(MainActivity.this,getString(R.string.Permissions_check_Ok) , Toast.LENGTH_LONG).show();
                    }

                    else {
                        Toast.makeText(MainActivity.this,getString(R.string.Permissions_check_Error),Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }




    /**
     * Start Main Service.
     */
    public void startService() {

            // starter  a  worker  for periodic check of main service,
            myGlobalClass.schedule_mainWorker();
            // start main service

            myGlobalClass.startMainService("START");

        }





    private void leavePatient(final Patinet patinet) {

        //  DO MANUAL SYNC BE FORE LEAVING TO PREVENT DATA LOSS
            myGlobalClass.manual_syc();

            Toast.makeText(MainActivity.this,getString(R.string.Manual_Sync_Error) , Toast.LENGTH_LONG).show();
            myGlobalClass.leavePatient(patinet);




    }




    //for delete alert dialog
    public void deleteDialog() {
        YesOrNoAlertDialog deleteDialog = new YesOrNoAlertDialog();
        deleteDialog.show(getSupportFragmentManager(), "example");
    }

    //for delete alert dialog
    @Override
    public void onYesClicked() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Patinet patinet = new Patinet();

        patinet.setDeviceid(sharedPref.getString(Constants.PREF_UNIQUE_ID, null));
        patinet.setStatus(1);
        patinet.setStudyId(sharedPref.getString(Constants.studyId, null));
        patinet.setUsername(sharedPref.getString(Constants.UserName_text, null));
        patinet.setTime_left(System.currentTimeMillis());
        if (!myGlobalClass.isNetworkAvailable()){

            InternetDialog();
            return;

        } else {

            leavePatient(patinet);


        }

    }


    /**
     * UNIQUES ID FOR DEVICE
     * this is also used to check if its first time user is using app
     */
    private static String uniqueID = null;

    public synchronized String hasUniqueID() {
        if (uniqueID == null) {

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            uniqueID = sharedPref.getString(Constants.PREF_UNIQUE_ID, null);

            return uniqueID;


        }
        return uniqueID;
    }






    private void InternetDialog() {
        final TextView view = new TextView(this);
        new AlertDialog.Builder(this)
                .setTitle(R.string.InternetPanelTitle)
                .setMessage(R.string.InternetPanelTextLeave)
                .setView(view)
                .setPositiveButton(R.string.InternetPanelOk, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(i);

                    }
                })
                .setNegativeButton(R.string.InternetPanelCancel, null)
                .show();
    }

    private void LocationDialog() {
        final TextView view = new TextView(this);
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.Location_request_head))
                .setMessage(getString (R.string.Location_request_body))
                .setView(view)
                .setPositiveButton(R.string.InternetPanelOk, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);

                    }
                })
                .setNegativeButton(R.string.InternetPanelCancel, null)
                .show();
    }






}
