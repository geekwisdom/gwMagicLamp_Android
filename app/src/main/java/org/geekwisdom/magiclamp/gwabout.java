/******************************************************************************
        File Name: gwAbout.java
@(#) Purpose: Implement an about page
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
