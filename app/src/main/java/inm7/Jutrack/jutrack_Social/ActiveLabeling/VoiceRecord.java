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

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;


public class VoiceRecord {

    private MediaRecorder myAudioRecorder;
    private String outputFile;







    public void start_record (String recordName){
        File directory = new File(Environment.getExternalStorageDirectory() + "/Jutrack");
        directory.mkdirs();
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Jutrack/" +recordName+ ".3gp";


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
        } catch (IOException ioe) {
            // make something
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
