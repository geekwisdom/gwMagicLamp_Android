package org.geekwisdom.magiclamp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.AndroidException;
import android.util.Log;
import android.widget.Toast;

public class gwAlarmReciever extends BroadcastReceiver {
    private static final String GW_ALARM_LOGGER  = GWMainActivity.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        // show toast
        Log.d(GW_ALARM_LOGGER,"Alarm Fired");
        GWQuotes newQuote = new GWQuotes(context);
        //Toast.makeText(context, newQuote.getQuote(), Toast.LENGTH_LONG).show();
        sendNotification(newQuote.getQuote(),context);
    }

    private void sendNotification(String the_message,Context mContext)
    {
        NotificationManager mNotificationManager;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext.getApplicationContext(), "notify_001");
        Intent ii = new Intent(mContext.getApplicationContext(), GWMainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(the_message);
        bigText.setBigContentTitle("Magic Lamp");
        NotificationCompat.BigTextStyle bigTextStyle = bigText.setSummaryText("Quote of the Day");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Magic Lamp");
        mBuilder.setContentText(the_message);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

//===removed some obsoletes
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            String channelId = "gw_Channel";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",  NotificationManager.IMPORTANCE_DEFAULT);

            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());

    }

}