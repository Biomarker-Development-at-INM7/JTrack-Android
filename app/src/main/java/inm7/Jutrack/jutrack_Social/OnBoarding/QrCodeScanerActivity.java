package inm7.Jutrack.jutrack_Social.OnBoarding;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import inm7.Jutrack.jutrack_Social.Constants;
import inm7.Jutrack.jutrack_Social.MainActivity;
import inm7.Jutrack.jutrack_Social.Patinet;
import inm7.Jutrack.jutrack_Social.Patinet;
import inm7.Jutrack.jutrack_Social.R;
import inm7.Jutrack.jutrack_Social.Serverinterface;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class QrCodeScanerActivity extends AppCompatActivity  {

    public int  status_code;
    private IntentIntegrator qrScan;

    private static final String TAG = "ZxingViewResult:";
    // added after room integration

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constants.serveraddress)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    Serverinterface serverinterface = retrofit.create(Serverinterface.class);


    SharedPreferences sharedPref = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        qrScan = new IntentIntegrator(this);
        qrScan.setTimeout(60000);
        qrScan.addExtra("PROMPT_MESSAGE", getString(R.string.QrcodeText));


        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_qr_code_scaner);


        if (!isNetworkAvailable()) {

            InternetDialog();
            return;
        } else {
            setQrScan();
        }

        // results from QR reader.


        findViewById(R.id.BTNRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setQrScan();
            }
        });
    }


    public void setQrScan() {

        qrScan.initiateScan();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {

            URL url = null;
            try {
                url = new URL(result.getContents());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            String protocol = url.getProtocol();
            String host = url.getHost();
            int port = url.getPort();
            String path = url.getPath();

            // check if its not our server dont accept it, also if TRUE reade user login info
            String our_server_address = protocol + "://" + host;
            if (our_server_address.equals(Constants.serveraddress)) {


                String[] params = url.getQuery().split("&");

                Patinet patinet = new Patinet();

                for (String param : params) {
                    String name = param.split("=")[0];
                    String value = param.split("=")[1];

                    if (name.equals("username")) {
                        patinet.setUsername(value);

                    }
                    if (name.equals("studyid")) {
                        patinet.setStudyId(value);

                    }

                }

                if (!patinet.getUsername().isEmpty() && !patinet.getStudyId().isEmpty()) {
                    patinet.setStatus(0);
                    patinet.setDeviceid(deviceid());
                    patinet.setPushNotification_token(pushNotification_token());
                    patinet.setTime_joined(System.currentTimeMillis());
//
//                    device and os information log
//                    patinet.setDeviceBrand(android.os.Build.MANUFACTURER);
//                    patinet.setDeviceModel(android.os.Build.MODEL);
//                    patinet.setOsVersion(android.os.Build.VERSION.RELEASE);

                    patinet.setDeviceBrand("no auth");
                    patinet.setDeviceModel("no auth");
                    patinet.setOsVersion("no auth");

                }


                //login and create
                if (isNetworkAvailable()) {

                    creatPatient(patinet);
                    //if (loginPatient(patinet)) {
                    //startActivity(new Intent(QrCodeScanerActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    //  Log.d(TAG, "onResult: " + "logged in");

                    //  }


                } else {
                    InternetDialog();
                }


            }
            // not our server
            else {
            }
            Log.i(TAG, "onResult:" + result);


        }
    }


    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {

        super.onPause();

    }




    /**
     * UNIQUES ID FOR DEVICE
     * this is also to check if its first time user is using app
     */

    public synchronized String deviceid() {
        String uniqueID = UUID.randomUUID().toString();
                // transformation value as  random.
        return uniqueID ;
    }
    /**
     * TOKEN  FOR PUSH NOTIFICATION
     * this is also to check if its first time user is using app
     */
    public synchronized String pushNotification_token() {

        return sharedPref.getString(Constants.pushNotification_token, null);
    }


    // create patient
    private void creatPatient(final Patinet patinet) {
        // Set up progress before call
        //TODO : needs check and test!!!
        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(this);
        progressDoalog.setMax(100);
        progressDoalog.setMessage(getText(R.string.UserCreateText));
        progressDoalog.setTitle(getText(R.string.UserCreateTitle));
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressDoalog.show();



        String md5_value = md5(patinet);


        final Call<Patinet> call = serverinterface.createPatient("UTF-8",Constants.add_user,md5_value,patinet);

        call.enqueue(new Callback<Patinet>() {

            @Override
            public void onResponse(Call<Patinet> call, Response<Patinet> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.code());

                    progressDoalog.dismiss();


                }

                if (response.code() == 200 ) {


                    Log.d(TAG, "onCreatePatient: " + response.raw().toString());
                    // username
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(Constants.UserName_text, patinet.getUsername()).apply();
                    editor.putString(Constants.studyId, patinet.getStudyId()).apply();
                    // save the assigned value of device_id
                    editor.putString(Constants.PREF_UNIQUE_ID, patinet.getDeviceid()).apply();

                    // check status as loggedin
                    editor.putInt(Constants.isFirstLogin, 0).apply();


                    //TODO
                    // will  be  fetched from  json
                    // this is default  value which should be used  in app setting  page if not set here, may face a problem.
                   if  (response.body().getSensors().contains("activity")){
                       editor.putBoolean("switch_preference_ActivityDetection",true).apply();
                   }

                    if  (response.body().getSensors().contains("application_usage")){
                        editor.putBoolean("switch_preference_AppUsage",true).apply();
                    }

                    if  (response.body().getSensors().contains("location")){
                        editor.putBoolean("switch_preference_Location",true).apply();
                    }

                    if  (response.body().getSensors().contains("accelerometer")){
                        editor.putBoolean("switch_preference_Acceleration",true).apply();

                    }
                    if  (response.body().getSensors().contains("gyroscope")){
                        editor.putBoolean("switch_preference_Gyroscope",true).apply();

                    }

                    editor.putBoolean("manual_ActiveLabeling_switch",true).apply();


                    editor.putBoolean("restartSensorService_switch",true).apply();
                    editor.putString("example_list",response.body().getFreq().concat("00")).apply();
                    editor.putInt("study_Duration_days",Integer.parseInt(  response.body().getStudy_duration() ) ).apply(); //by default its 30 days
                    editor.putLong("patient_Time_Joined",System.currentTimeMillis()).apply();




                    // transformation random value assignment
                    int transformationid = 360;

                    transformationid = new Random().nextInt((359 - 1) + 1) + 1;

                    editor.putInt(Constants.transformationid, transformationid).apply();

                    // stop progress bar
                    progressDoalog.dismiss();


                    startActivity(new Intent(QrCodeScanerActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();



                } else if (response.code() == 422) {

                    progressDoalog.dismiss();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            TextView TXT_Error = findViewById(R.id.TXT_Error);
                            TXT_Error.setText(R.string.User_Exist_Error);
                        }

                    });




                } else {
                    progressDoalog.dismiss();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            TextView TXT_Error = findViewById(R.id.TXT_Error);
                            TXT_Error.setText(R.string.User_Server_Error);
                        }

                    });

                }



            }

            @Override
            public void onFailure(Call<Patinet> call, Throwable t) {
                progressDoalog.dismiss();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        TextView TXT_Error = findViewById(R.id.TXT_Error);
                        TXT_Error.setText(R.string.User_Server_Error);
                    }

                });

            }

        });

    }







    private void InternetDialog() {
        final TextView view = new TextView(this);
        new AlertDialog.Builder(this)
                .setTitle(R.string.InternetPanelTitle)
                .setMessage(R.string.InternetPanelTextJoin)
                .setView(view)
                .setPositiveButton(R.string.InternetPanelOk, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(i);
                        finish();

                    }
                })
                .setNegativeButton(R.string.InternetPanelCancel, null)
                .show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }


    // MD5 function

    private static String md5(Patinet sensor) {

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

}
