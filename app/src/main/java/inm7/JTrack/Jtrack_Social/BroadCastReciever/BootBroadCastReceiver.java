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

import inm7.JTrack.Jtrack_Social.Constants;
import inm7.JTrack.Jtrack_Social.MyGlobalClass;
import inm7.JTrack.Jtrack_Social.R;

public class BootBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

// TODO seems to have a problme to restart when target 30 sdk. commented code are due to new rules in android 12 not to start background app.
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            MyGlobalClass myGlobalClass = new MyGlobalClass(context);

            if (PreferenceManager.getDefaultSharedPreferences(context).getInt(Constants.isFirstLogin, 1)==1 ){


//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    MyGlobalClass myGlobalClass = new MyGlobalClass(context);
//                    myGlobalClass.startMyOwnForeground("Boot","please start application again");
//                }
//                else {
//
//                Intent i = new Intent(context, MainActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(i);
//                }
                // also checked in myglobal class/ main worker
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    myGlobalClass.startAppNotRunningNotification(context.getString(R.string.alert_restartApp_title),context.getString (R.string.alert_restartApp_body));
                } else {
                    myGlobalClass.startAppNotRunningNotification_old(context.getString(R.string.alert_restartApp_title),context.getString (R.string.alert_restartApp_body));
                }


            }




        }
    }


}
