package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotifyService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";

    public NotifyService(){
        super("Notify Service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("abc", "Timer has started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Service läuft weiter, auch wenn App geschlossen wird.
        return START_STICKY;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onHandleIntent(Intent intent) {
        while(intent == null){
           /** //Wenn App geschlossen, alle 5 sekunden eine Benachrichtigung. Nervt, deswegen auskommentiert...
             int time = 5;

            for(int i = 0; i< time; i++){
                Log.v("abc", "(intent null) i = "+i);
                try{
                    Thread.sleep(1000);
                }catch(Exception e){

                }
            }
            NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            Intent resultIntent = new Intent(getApplicationContext(), ReminderDetail.class);
            // TODO which Reminder Detail? putExtra()!
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel nChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

                //Configure
                nChannel.enableLights(true);
                nChannel.setLightColor(Color.CYAN);
                nm.createNotificationChannel(nChannel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.drawable.logo_meet_remind)
                    .setContentTitle("Titel der Erinnerung")
                    .setContentText("Beschreibung der Erinnerung")
                    .setContentIntent(resultPendingIntent);

            nm.notify(NOTIFICATION_ID, builder.build());
            **/
        }

        while(true) {
            ResultReceiver receiver = intent.getParcelableExtra("receiver");
            int time = intent.getIntExtra("time", 0);
            for (int i = 0; i < time; i++) {
                Log.v("abc", "(intent not null) i = " + i);

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Log.v("abc", "FEHLER");
                }
            }

            Bundle bundle = new Bundle();
            bundle.putString("message", "Counting done...");
            receiver.send(1234, bundle);
        }
    }
}
