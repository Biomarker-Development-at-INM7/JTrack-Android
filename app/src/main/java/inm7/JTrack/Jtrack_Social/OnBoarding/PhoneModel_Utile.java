package inm7.JTrack.Jtrack_Social.OnBoarding;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.CompoundButton;

import androidx.appcompat.widget.AppCompatCheckBox;

import java.util.List;

import inm7.JTrack.Jtrack_Social.R;

public class PhoneModel_Utile {


    public static void PhoneModel_Utile(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean skipMessage = sharedPref.getBoolean("switch_preference_skipProtectedAppCheck", false);

       // make it  !skipMessage if this done by selecting  from app
        if (!skipMessage) {
            final SharedPreferences.Editor editor = sharedPref.edit();
            boolean foundCorrectIntent = false;
            for (Intent intent : PhoneModel_Constants.POWERMANAGER_INTENTS) {
                if (isCallable(context, intent)) {
                    foundCorrectIntent = true;
                    final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(context);
                    dontShowAgain.setText("Do not show again");
                    dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            editor.putBoolean("switch_preference_skipProtectedAppCheck", isChecked);
                            editor.apply();
                        }
                    });





                    new AlertDialog.Builder(context)
                            .setTitle(android.os.Build.MANUFACTURER)
                            .setMessage((R.string.Protected_Apps_msg))
                            .setPositiveButton(R.string.Protected_Apps_btn, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    editor.putBoolean("switch_preference_skipProtectedAppCheck", true);
                                    editor.apply();
                                    context.startActivity(intent);

                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                    break;
                }
            }
            if (!foundCorrectIntent) {

                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",context.getPackageName(),null);
                i.setData(uri);


                new AlertDialog.Builder(context)
                        .setTitle(android.os.Build.MANUFACTURER)
                        .setMessage((R.string.Protected_Apps_msg))
                        .setPositiveButton(R.string.Protected_Apps_btn, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                editor.putBoolean("switch_preference_skipProtectedAppCheck", true);
                                editor.apply();
                                context.startActivity(i);

                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();


//                Intent intent = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
//
//                context.startActivity(intent);

            }
        }
    }

    private static boolean isCallable(Context context, Intent intent) {
        try {
            if (intent == null || context == null) {
                return false;
            } else {
                List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
                return list.size() > 0;
            }
        } catch (Exception ignored) {
            return false;
        }
    }
}
