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
package inm7.JTrack.Jtrack_Social.BroadCastReciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import inm7.JTrack.Jtrack_Social.Constants;
import inm7.JTrack.Jtrack_Social.MainService;

public class SensorBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


        Log.i(SensorBroadcastReceiver.class.getSimpleName(), "Sensor Service Stops!!!!!");

        if (PreferenceManager.getDefaultSharedPreferences(context).getInt(Constants.isFirstLogin, 1)==0 ){

            if ((PreferenceManager.getDefaultSharedPreferences(context).getInt("ActiveLabeling_switch",0))!=2)
            {

                Intent mainServiceIntent = new Intent(context, MainService.class);
                mainServiceIntent.setAction("START");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(mainServiceIntent);

                } else {
                    context.startService(mainServiceIntent);
                }

            }


        }


//        context.startService(new Intent(context, MainService.class));

       // if (intent.getAction().equals("done")){
        //    Log.i(SensorBroadcastReceiver.class.getSimpleName(), "done mehran");

        //}

    }
}
