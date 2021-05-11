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
package inm7.JTrack.Jtrack_Social.ActiveLabeling;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;


public class VoiceRecord {

    private MediaRecorder myAudioRecorder;
    private String outputFile;



    private Context mContext;

    public VoiceRecord(Context context) {
        mContext = context;

    }



    public void start_record (String recordName){

        ContextWrapper cw = new ContextWrapper(mContext);
        String fullPath =cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString();
        File directory = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
//        File directory = new File(fullPath, "/JTrack" + ".MP3");


//        String fullPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)  + "/JTrack";

//        File directory = new File(fullPath);
//        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "Inm7.JTrack");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        outputFile =fullPath+"/" + recordName+ ".3gp";
        Log.d("Voice", outputFile);

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);

        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IllegalStateException ise) {
            // make something ...
            Log.d("Voice", ise.toString());

        } catch (IOException ioe) {
            // make something
            Log.d("Voice", ioe.toString());

        }

    }

    public void stop_record (){

        try {
            myAudioRecorder.stop();
            myAudioRecorder.release();
            myAudioRecorder = null;

        }catch (Exception e){
            // make something
        }


    }



}
