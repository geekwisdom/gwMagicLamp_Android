package org.geekwisdom.magiclamp;

import android.hardware.SensorListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class gwAbout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gwabout);
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log=new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("GWMainActivity:"))
                log.append(line);
            }
            TextView tv = (TextView)findViewById(R.id.logcat);
            tv.setMovementMethod(new ScrollingMovementMethod());
            tv.setText(log.toString());
        } catch (IOException e) {
            // Handle Exception
        }
    }

    public void closeScreen(View v) {
        this.finish();
    }
}
