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
import java.util.Random;

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

   public void setAlarm() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
       if (!prefs.getBoolean("alarmSet", false)) {
           Log.d(MAIN_ACTIVITY_LOGGER,"Setting Random Alarm");
           Random r=new Random();
           int randomNumber=r.nextInt(22) + 1;
           Context myContext = getApplicationContext();
           alarmMgr = (AlarmManager) myContext.getSystemService(myContext.ALARM_SERVICE);
           Intent intent = new Intent(myContext, gwAlarmReciever.class);
           alarmIntent = PendingIntent.getBroadcast(myContext, 0, intent, 0);

           alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                   SystemClock.elapsedRealtime() +
                           randomNumber * 60 * 1000, alarmIntent);

        //anoying 5 minute alarm test only !
        /*
           alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                   SystemClock.elapsedRealtime() +
                           5 * 1000, alarmIntent);

           SharedPreferences.Editor editor = prefs.edit();
           editor.putBoolean("alarmSet", true);
           editor.apply();
           */
       }

   }
}
