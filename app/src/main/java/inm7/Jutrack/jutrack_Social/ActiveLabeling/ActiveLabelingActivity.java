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
package inm7.Jutrack.jutrack_Social.ActiveLabeling;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.os.CountDownTimer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import inm7.Jutrack.jutrack_Social.Constants;
import inm7.Jutrack.jutrack_Social.MyGlobalClass;
import inm7.Jutrack.jutrack_Social.R;

import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ActiveLabelingActivity extends AppCompatActivity implements View.OnClickListener {

    private long timeCountInMilliSeconds = 0;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    private ProgressBar progressBarCircle;

    private TextView textViewTime;
    private ImageView imageViewReset;
    private ImageView imageViewStartStop;
    private ImageView imageViewAdd;
    private CountDownTimer countDownTimer;
    private TextView taskTextView;


    public SharedPreferences activeMonitoringPreference;
    public SharedPreferences.Editor editor;

    public String username = " ";
    public String deviceid = " ";
    public String studyId = " ";
    public String voiceRecord_name="-1";


    long start_timestamp=0;
    long end_timestamp=0;
    String task_name_label="";
    int task_duration_label=0;
    Boolean record_voice_label= false;

    private MediaRecorder myAudioRecorder;
    private String outputFile;

    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
   VoiceRecord voiceRecord = new VoiceRecord();

    MyGlobalClass myGlobalClass = new MyGlobalClass(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_monitoring);

        activeMonitoringPreference = getSharedPreferences("ActiveMonitoring", Context.MODE_PRIVATE);
        editor = activeMonitoringPreference.edit();


        // method call to initialize the views
        initViews();
        // method call to initialize the listeners
        initListeners();

        //**
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // get device id if null use username
        username = sharedPref.getString(Constants.UserName_text, " ");
        username = username.replaceAll("\\s+", "");

        deviceid = sharedPref.getString(Constants.PREF_UNIQUE_ID, username);
        studyId = sharedPref.getString(Constants.studyId, studyId);





    }

    /**
     * method to initialize the views
     */
    public void initViews() {
        progressBarCircle = (ProgressBar) findViewById(R.id.progressBarCircle);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        imageViewReset = (ImageView) findViewById(R.id.imageViewReset);
        imageViewStartStop = (ImageView) findViewById(R.id.imageViewStartStop);
        imageViewAdd = (ImageButton) findViewById(R.id.imageButtonAdd);
        taskTextView = (TextView) findViewById(R.id.textViewTask);

    }

    /**
     * method to initialize the click listeners
     */
    private void initListeners() {
        imageViewReset.setOnClickListener(this);
        imageViewStartStop.setOnClickListener(this);
        imageViewAdd.setOnClickListener(this);
    }

    /**
     * implemented method to listen clicks
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewReset:
                reset();
                break;
            case R.id.imageViewStartStop:
                startStop();
                break;
            case R.id.imageButtonAdd:
             showAddItemDialog(this);
             break;
        }
    }

    /**
     * method to reset count down timer
     */
    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();


    }


    /**
     * method to start and stop count down timer
     */
    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED ) {
            if (timeCountInMilliSeconds!=0){

                // start services if manual mode is enabled for active labeling

                if (true==true)
                {
                    myGlobalClass.startMainService("START");
                }

                // voice
                if (record_voice_label){
                    if (CheckPermissions() ){
                        voiceRecord_name=start_timestamp+"_"+ deviceid+"_"+ username+"_"+studyId;
                        voiceRecord.start_record( voiceRecord_name);
                    }else {
                        RequestPermissions();
                    }
                }

                // call to initialize the progress bar values
                setProgressBarValues();
                // showing the reset icon
                imageViewReset.setVisibility(View.VISIBLE);
                // changing play icon to stop icon
                imageViewStartStop.setImageResource(R.drawable.icon_stop);
                // changing the timer status to started
                timerStatus = TimerStatus.STARTED;
                // call to start the count down timer
                startCountDownTimer();
                getTimeofTask(System.currentTimeMillis(),1);






            }
            else
            {
                Toast.makeText(this,"set value first",Toast.LENGTH_LONG).show();

            }


        }
        else {

            // hiding the reset icon
            imageViewReset.setVisibility(View.GONE);
            // changing stop icon to start icon
            imageViewStartStop.setImageResource(R.drawable.icon_start);
            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();
            getTimeofTask(System.currentTimeMillis(),0);
            //voice
            task_name_label="";
            // play bip
            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);

            if (record_voice_label){
                voiceRecord.stop_record();
            }

            if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("manual_ActiveLabeling_switch",false))
            {
                myGlobalClass.startMainService("STOP_by_Active_Labeling");

            }

        }

    }

    private void getTimeofTask(long task_time,int status) {

        // 1 start, 0 stop

        if( status==1) {
            start_timestamp=task_time;
        }
        else if (status==0){
            end_timestamp=task_time;
            // save to db
            ActiveLabelingSensor sensor = new ActiveLabelingSensor();

            sensor.setSensorname("Active_Labeling");
            sensor.setTimestamp(System.currentTimeMillis());
            sensor.setDeviceid(deviceid);
            sensor.setUsername(username);
            sensor.setStudyId(studyId);
            sensor.setDuration(task_duration_label);
            sensor.setStartTimestamp(start_timestamp);
            sensor.setEndTimestamp(end_timestamp);
            sensor.setTaskName(task_name_label);
            sensor.setVoiceId(voiceRecord_name);

            try {
                GetActiveLabelingSensorsDataAsyc.populateSensors(ActiveLabelingSenorsAppDatabase.getAppDatabase(this), sensor);
            } catch (Exception e) {
                throw new NullPointerException();

            }

        }

    }

    /**
     * method to initialize the values for count down timer
     */
    private void setTimerValues(String taskName, int taskDuration) {

        taskName=taskName.toLowerCase().trim().replace(" ","_");

        taskTextView.setText(taskName);

        textViewTime.setText(msTimeFormatter(taskDuration));
        // set timer global value
        timeCountInMilliSeconds=taskDuration* 1000;


    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {




        editor.putInt("ActivityId", 1);
        editor.apply();


        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {

                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                setProgressBarValues();
                // hiding the reset icon
                imageViewReset.setVisibility(View.GONE);
                // changing stop icon to start icon
                imageViewStartStop.setImageResource(R.drawable.icon_start);
                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED;

                // play bip
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);

                // to save time
                getTimeofTask(System.currentTimeMillis(),0);

                if (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("manual_ActiveLabeling_switch",false))
                {
                    myGlobalClass.startMainService("STOP_by_Active_Labeling");

                }

                task_name_label="";
                //voice
                voiceRecord.stop_record();



            }

        };

        countDownTimer.start();


    } //end of function

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        editor.putInt("ActivityId", 0);
        editor.apply();
        countDownTimer.cancel();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }


    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds))
        );

        return hms;


    }

    /**
     * method to convert seconds to time format
     *
     * @param seconds
     * @return HH:mm:ss time formatted string
     */
    private String msTimeFormatter(long seconds) {

        String ms = String.format("%02d:%02d:%02d",
                TimeUnit.SECONDS.toHours(seconds),
                TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(seconds)),
                TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds))
        );

        return ms;


    }



    private void showAddItemDialog(Context c) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.task_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText task_duration = (EditText) dialogView.findViewById(R.id.dialog_durationTxt);
        final EditText task_name = (EditText) dialogView.findViewById(R.id.dialog_task_autoTxt);
        final Switch record_voice = (Switch) dialogView.findViewById(R.id.Switch_voice);

        // autocomplete text view and list to adaptor
        final AutoCompleteTextView taskEditText = (AutoCompleteTextView) dialogView.findViewById(R.id.dialog_task_autoTxt);
        final String[] fruits = {"walking","running","balance","voice"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, fruits);
        taskEditText.setThreshold(1); //will start working from first character
        taskEditText.setAdapter(adapter);

        dialogBuilder.setTitle("Neue Aktivität");
        dialogBuilder.setMessage("Bitte geben Sie den Namen und die Dauer (falls notwendig) der Aktivität an");


        dialogBuilder.setPositiveButton("hinzufügen", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                task_name_label=task_name.getText().toString().trim();
                record_voice_label=record_voice.isChecked();

                if (task_duration.getText().toString().trim().isEmpty()){
                    task_duration_label=600; // default value TODO to constatnts

                }else
                {
                    task_duration_label=Integer.parseInt( task_duration.getText().toString() );

                }

                // set Task values
                if (task_name_label.isEmpty()  ){
                    setTimerValues("Error: task name can not be empty",0);

                }else if ( task_duration_label==600 ) {
                    setTimerValues(task_name.getText().toString(),task_duration_label);
                }
                else
                {

                    setTimerValues(task_name_label,task_duration_label);


                }

                if (record_voice.isChecked()){
                    if (CheckPermissions() ){

                    }else {
                        RequestPermissions();
                    }
                }



            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length> 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] ==  PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
    private void RequestPermissions() {
        ActivityCompat.requestPermissions(ActiveLabelingActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }


}
