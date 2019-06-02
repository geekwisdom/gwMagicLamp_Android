package org.geekwisdom.magiclamp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.util.Log;
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
        quoteView =  findViewById(R.id.quoteView);
        quoteView.setText(the_quote.lastQuote());
        setShaker();

        View myView = findViewById(R.id.imageView);
        myView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return detectRub(v,event);

            }
        });
    }
    public void changeQuote(View v) {
        quoteView.setText(the_quote.getRndQuote());
    }
    public void shareQuote(View v) {
        String quoteText = quoteView.getText().toString();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = quoteText;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "GWQOTD");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody + " " + "http://geekwisdom.org/quotes");
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
private void doRub()
{
    Log.d(MAIN_ACTIVITY_LOGGER,"A RUB WAS DETECTED !!");
    Toast.makeText(getApplicationContext(), "Geek Wisdom Code is: ABCD", Toast.LENGTH_LONG).show();
}

private void doShake()
{
    //Toast.makeText(this, "Shaked !!!", Toast.LENGTH_SHORT).show();
    quoteView.setText(the_quote.getRndQuote());
}
}
