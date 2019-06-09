/******************************************************************************
 File Name: gwMainActivity.java
 @(#) This is the MAIN activity, it displays rotating quotes, handles
 @(#) "rubbing" the lamp, and "shaking" the phone
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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class GWMainActivity extends AppCompatActivity {

    private TextView quoteView;
    private GWQuotes the_quote;
    private AlarmManager alarmMgr;
    private SensorManager sensorMgr;
    private Sensor mAccelerometer;
    private PendingIntent alarmIntent;
    private int whichX=1;
    private float x1,x2,x3;
    private int MAX_DISTANCE = 90;
    private int MIN_DISTANCE = 35;
    private int totalswipes=0;
    private ShakeDetector mShakeDetector;
    private String quoteURL = "http://geekwisdom.org/quotes";

    private static final String MAIN_ACTIVITY_LOGGER = GWMainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gw_activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.gw_toolbar);
        setSupportActionBar(myToolbar);

        setAlarm();
        Log.d(MAIN_ACTIVITY_LOGGER,"Inside On Create Function of Magic Lamp");
        the_quote = new GWQuotes(getApplicationContext());
        setQuote((the_quote.lastQuote()));

        setShaker();

        View myView = findViewById(R.id.imageView);
        myView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return detectRub(v,event);

            }
        });
    }
    private void setQuote(String quoteInfo)
    {
        //Log.d(MAIN_ACTIVITY_LOGGER,"TEST1: " + getApplicationContext().getResources().getResourceName(R.drawable.billgatesgeeks));
        quoteView =  findViewById(R.id.quoteView);
        if (quoteInfo.contains("::"))
        {
            Log.d(MAIN_ACTIVITY_LOGGER,"TEST1: 2 ");
            String[] output = quoteInfo.split("::");
            quoteView.setText(output[0]);
            quoteView.setVisibility(View.INVISIBLE);
            Log.d(MAIN_ACTIVITY_LOGGER,"TEST1: " + output[0]);
            Log.d(MAIN_ACTIVITY_LOGGER,"TEST1: " + output[1]);
            Context mycontext = getApplicationContext();
            Resources resources = mycontext.getResources();
            int resourceId = resources.getIdentifier(output[1], "drawable",
                    mycontext.getPackageName());
            Log.d(MAIN_ACTIVITY_LOGGER,"TEST1: " + resourceId);

            ImageView image = (ImageView) findViewById(R.id.quoteImage);
            image.setImageResource(resourceId);
            image.setVisibility(View.VISIBLE);
    if (output.length > 2) quoteURL=output[2];

        }
        else {
            quoteView.setVisibility(View.VISIBLE);
            quoteView.setText(quoteInfo);
            ImageView image = (ImageView) findViewById(R.id.quoteImage);
            image.setVisibility(View.INVISIBLE);
        }

    }
    public void changeQuote(View v) {

        String quoteInfo=the_quote.getRndQuote();
        setQuote(quoteInfo);
    }
    public void shareQuote(View v) {
        String quoteText = quoteView.getText().toString();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = quoteText;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "GWQOTD");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody + " " + quoteURL);
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

    public void setShaker()
    {
        Context myContext = getApplicationContext();
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                doShake();

            }
        });


        sensorMgr.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);

    }
    public void setAlarm() {


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        long epoch=328738233;
        String LastAlarm = prefs.getString("gwLastAlarm",Long.toString(epoch));
        long currentTimeStamp = Long.parseLong(LastAlarm);
        long nextAlarmSet = currentTimeStamp + (60 * 60 * 1000 * 24 * 6 );
        Log.d(MAIN_ACTIVITY_LOGGER,"Last Alarms were set " + formatdt(LastAlarm));
        Log.d(MAIN_ACTIVITY_LOGGER,"Next Alarms will be set after  " + formatdt(Long.toString(nextAlarmSet)));
        //See if alarm already set
        Context myContext = getApplicationContext();
        Intent intent = new Intent(myContext, gwAlarmReciever.class);
        alarmIntent = PendingIntent.getBroadcast(myContext, 0, intent, 0);
        boolean alarmUp = PendingIntent.getBroadcast(myContext, 0, intent, PendingIntent.FLAG_NO_CREATE) != null;
        if (!alarmUp) { Log.d(MAIN_ACTIVITY_LOGGER,"No Alarms found, re-setting!"); }
        if ( System.currentTimeMillis()> nextAlarmSet || !alarmUp) {
        //if more then 6 days have elapsed since lst setting alarms. Set some new ones
           Log.d(MAIN_ACTIVITY_LOGGER,"Setting Random Alarm");
           Random r=new Random();
           int randomNumber=r.nextInt(22) + 1;
           //Context myContext = getApplicationContext();
           alarmMgr = (AlarmManager) myContext.getSystemService(myContext.ALARM_SERVICE);
           //Intent intent = new Intent(myContext, gwAlarmReciever.class);

        //set 7 random alarms one-time alarms
            for (int i=0;i<7;i++)
            {

                randomNumber=r.nextInt(22) + 1;
                Log.d(MAIN_ACTIVITY_LOGGER,"Alarm Random  " + i + " is " + randomNumber);
                //int alarmhours = ((i+1) * 24) * randomNumber * 60 * 60 * 1000;
                int alarmhours = ((i+1) * 24) * (60+randomNumber) * 60 * 1000;

                if (i == 0 ) {
                    //intent = new Intent(myContext, gwAlarmReciever.class);
                    alarmhours = 1000 * 60 * 15; //10 minute test
                    alarmIntent = PendingIntent.getBroadcast(myContext, 0, intent, 0);
                    alarmMgr.set(AlarmManager.ELAPSED_REALTIME,
                            SystemClock.elapsedRealtime() + alarmhours  , alarmIntent);
                    //randomNumber * 60 * 1000, alarmIntent);
                }
                else {
                    Intent newIntent = new Intent(myContext, gwAlarmReciever.class);
                    PendingIntent nextAlarm = PendingIntent.getBroadcast(myContext, 0, newIntent, 0);
                    alarmMgr.set(AlarmManager.ELAPSED_REALTIME,
                            SystemClock.elapsedRealtime() + alarmhours  , nextAlarm);
                    //randomNumber * 60 * 1000, alarmIntent);
                }


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gwmenu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Log.d(MAIN_ACTIVITY_LOGGER,"About Clicked!");
            // User chose the "About" item, show the app settings UI...
                Intent intent = new Intent(this, gwAbout.class);
                startActivity(intent);
                return true;

            case R.id.quit:
                this.finish();
                return true;
                default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public boolean detectRub(View v, MotionEvent event)
    {
        int action = MotionEventCompat.getActionMasked(event);
        float deltaX;

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                x1 = event.getX();
                //Log.d(MAIN_ACTIVITY_LOGGER,"Action was DOWN, X1 is" + x1);
                return true;
            case (MotionEvent.ACTION_MOVE) :

                if (whichX == 1)
                {
                whichX=2;
                 x2 = event.getX();
                deltaX = x2 - x1;
                }
                else
                {
                    whichX=1;
                    x1 = event.getX();
                    deltaX = x2 - x1;
                }
                //Log.d(MAIN_ACTIVITY_LOGGER,"Action was MOVE: deltaX is " + deltaX);
                if (deltaX < MAX_DISTANCE && deltaX > MIN_DISTANCE && deltaX > 0)
                {
                    Log.d(MAIN_ACTIVITY_LOGGER,"A SWIPE WAS DETECTED " + deltaX);
                    totalswipes++;
                }


                return true;
            case (MotionEvent.ACTION_UP) :


                if (totalswipes > 4)
                {
                    totalswipes=0;
                    doRub();
                }
                else totalswipes=0;
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                Log.d(MAIN_ACTIVITY_LOGGER,"Action was CANCEL");
                totalswipes=0;
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                Log.d(MAIN_ACTIVITY_LOGGER,"Movement occurred outside bounds " +
                        "of current screen element");
                totalswipes=0;
                return true;
            default :
                totalswipes=0;
                return super.onTouchEvent(event);
        }
    }


    public void genieImage()
{
    ImageView image = (ImageView) findViewById(R.id.quoteImage);
    image.setImageResource(R.drawable.genie);


}
    private void doRub()
{
        Log.d(MAIN_ACTIVITY_LOGGER,"A RUB WAS DETECTED !!");
    ImageView image = (ImageView) findViewById(R.id.quoteImage);
    image.setImageResource(R.drawable.genie);
    image.setVisibility(quoteView.VISIBLE);
    quoteView =  findViewById(R.id.quoteView);
    quoteView.setVisibility(View.INVISIBLE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
       boolean showedcode = prefs.getBoolean("gwCodeGiven",false);
       if (!showedcode) {
           Toast.makeText(getApplicationContext(), "Geek Wisdom Code is: bPSO", Toast.LENGTH_LONG).show();
           SharedPreferences.Editor editor = prefs.edit();
           editor.putBoolean("gwCodeGiven",true);

           editor.apply();
       }
    else
       {
        //reminder: ONLY IF ON WIFI Connected!
           startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=MeabQjpkMFY&list=PLhxFBfGQPGOcEB60nUvcd3csMfIJItlDo")));
//           Log.i("Video", "Video Playing....");

       }
}

private void doShake()
{
    GWShake shakemessage = new GWShake(getApplicationContext());

    Toast.makeText(this, shakemessage.getRndMsg(), Toast.LENGTH_SHORT).show();

}
}
