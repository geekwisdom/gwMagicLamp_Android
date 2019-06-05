/******************************************************************************
 File Name: gwAlarmReciever.java
 @(#) The alarm reciever is called by the AlarmManager to show a new notification
 @(#) message.
  **********************************************************************************
 Written By: Brad Detchevery
 Created: June 1, 2019
  ********************************************************************************
 MIT License [MODIFIED COPYRIGHT NOTICE]

 -- BEGIN COPYRIGHT NOTICE --
 Copyright (c) 2019 Brad Detchevery
 This product uses GeekWisdom.org Software, and has been provided FREE OF CHARGE.
 If you like it please consider becoming a Patron at https://patreon.com/GeekWisdom
 -- END COPYRIGHT NOTICE --

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice (text between the -- BEGIN COPYRIGHT NOTICE -- and -- END COPYRIGHT NOTICE --)
 and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.

 ********************************************************************************/

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
        Log.d(GW_ALARM_LOGGER, "Alarm Fired");
        GWQuotes newQuote = new GWQuotes(context);
        //Toast.makeText(context, newQuote.getQuote(), Toast.LENGTH_LONG).show();
        String quoteData = newQuote.getRndQuote();
        if (quoteData.contains("::")) {
            String[] output = quoteData.split("::");
            quoteData = output[0];
        }
        sendNotification(quoteData,context);

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