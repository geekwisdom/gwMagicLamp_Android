package org.geekwisdom.magiclamp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class GWMainActivity extends AppCompatActivity {

    private TextView quoteView;
    private GWQuotes the_quote;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;


    private static final String MAIN_ACTIVITY_LOGGER = GWMainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gw_activity_main);
        setAlarm();
        Log.d(MAIN_ACTIVITY_LOGGER,"Inside On Create Function of Magic Lamp");
        the_quote = new GWQuotes(getApplicationContext());
        quoteView =  findViewById(R.id.quoteView);
        quoteView.setText(the_quote.getQuote());
    }
    public void changeQuote(View v) {
        quoteView.setText(the_quote.getQuote());
    }
    public void shareQuote(View v) {
        String quoteText = quoteView.getText().toString();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = quoteText;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "GWQOTD");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private String formatdt(String txtLastAlarm)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        long currentTimeStamp = Long.parseLong(txtLastAlarm);
        Date dateTime = new Date(currentTimeStamp);
        return dateFormat.format(dateTime);
    }
    public void setAlarm() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        long epoch=328738233;
        String LastAlarm = prefs.getString("gwLastAlarm",Long.toString(epoch));
        long currentTimeStamp = Long.parseLong(LastAlarm);
        long nextAlarmSet = currentTimeStamp + (60 * 60 * 1000 * 24 * 6 );
        Log.d(MAIN_ACTIVITY_LOGGER,"Last Alarms were set " + formatdt(LastAlarm));
        Log.d(MAIN_ACTIVITY_LOGGER,"Next Alarms will be set after  " + formatdt(Long.toString(nextAlarmSet)));
        if ( System.currentTimeMillis()> nextAlarmSet) {
        //if more then 6 days have elapsed since lst setting alarms. Set some new ones
           Log.d(MAIN_ACTIVITY_LOGGER,"Setting Random Alarm");
           Random r=new Random();
           int randomNumber=r.nextInt(22) + 1;
           Context myContext = getApplicationContext();
           alarmMgr = (AlarmManager) myContext.getSystemService(myContext.ALARM_SERVICE);
           Intent intent = new Intent(myContext, gwAlarmReciever.class);
           alarmIntent = PendingIntent.getBroadcast(myContext, 0, intent, 0);
        //set 7 random alarms
            for (int i=0;i<7;i++)
            {
                randomNumber=r.nextInt(22) + 1;
                Log.d(MAIN_ACTIVITY_LOGGER,"Alarm Random  " + i + " is " + randomNumber);
                //int alarmhours = ((i+1) * 24) * randomNumber * 60 * 60 * 1000;
                int alarmhours = ((i+1) * 24) * (60+randomNumber) * 60 * 1000;
                alarmMgr.set(AlarmManager.ELAPSED_REALTIME,
                        SystemClock.elapsedRealtime() + alarmhours  , alarmIntent);
                //randomNumber * 60 * 1000, alarmIntent);
                String nextAlarm = Long.toString(System.currentTimeMillis() + alarmhours);

                Log.d(MAIN_ACTIVITY_LOGGER,"Alarm " + i + " will fire at " + formatdt(nextAlarm));
            }
           /* alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                   SystemClock.elapsedRealtime() + randomNumber * 60 * 1000,
                   randomNumber * 60 * 1000, alarmIntent);
                           //randomNumber * 60 * 1000, alarmIntent);
           */



           SharedPreferences.Editor editor = prefs.edit();
           editor.putString("gwLastAlarm",Long.toString(System.currentTimeMillis()));

           editor.apply();

       }

   }
}
