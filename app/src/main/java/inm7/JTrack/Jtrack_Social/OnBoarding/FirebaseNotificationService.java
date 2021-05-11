package inm7.JTrack.Jtrack_Social.OnBoarding;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import inm7.JTrack.Jtrack_Social.Constants;
import inm7.JTrack.Jtrack_Social.MainActivity;
import inm7.JTrack.Jtrack_Social.R;

public class FirebaseNotificationService extends FirebaseMessagingService {
    private Notification mNotification;


    public FirebaseNotificationService() {

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        String body = data.get("body");
        String title = data.get("title");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground(title,body);
        } else {
            startMyOwnForegroundOld(title,body);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w( "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                         String token =  task.getResult().getToken();
                        editor.putString(Constants.pushNotification_token,token).apply();
                        Log.e("My Token",task.getResult().getToken());
                    }
                });
    }



    /**
     * notification functions
     */
    private void startMyOwnForegroundOld(String title, String body) {
        mNotification = new NotificationCompat.Builder(this)
                .setContentTitle(Constants.NOTIFICATION_Title)
                .setTicker(Constants.NOTIFICATION_Tiker)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_NO_CREATE))
                .setGroup(title)
                .setContentText(body)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setOngoing(false)
                .setSortKey("1")
                .build();


        mNotification.notify();



    }

    private void startMyOwnForeground(String title, String body) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            NotificationChannel chan = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.channelName, NotificationManager.IMPORTANCE_HIGH);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(false)
                    // this is vita to show notification
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setCategory(Notification.CATEGORY_EVENT)
                    .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_NO_CREATE))
                    .setAutoCancel(true)
                    .build();
            manager.notify(Constants.pushNotificationId, notification);
        }
    }
}
