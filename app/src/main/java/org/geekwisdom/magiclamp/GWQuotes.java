/******************************************************************************
 File Name: gwQuotes
 @(#) The class reads the 'inspiration.txt' flie and selects a random
 @(#) quote message and image (if applicable)
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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class GWQuotes {
    private android.content.res.AssetManager AssetManager;
    private Context Appcontext;
    ArrayList<String> lines = new ArrayList<String>();
    private static final String GW_QUOTES_ACTIVITY_LOGGER = GWMainActivity.class.getSimpleName();
    public GWQuotes(Context myContext)
    {
        Appcontext = myContext;
        AssetManager = myContext.getAssets();
    }

    public String lastQuote()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Appcontext);
        String LastQuote = prefs.getString("gwLastQuote","");
        if (LastQuote == "") LastQuote="Welcome to Magic Lamp!\n\nClick here to begin...";
        return LastQuote;
    }

    public String getRndQuote()
    {
    return getRndQuote(0);
    }

    public void AddQuote(String quote)
    {
        lines.add(quote);
    }
    private boolean buildQuotes()
    {
        BufferedReader reader;
        if (lines.size() <= 0)
        {
            try {


                final InputStream file = AssetManager.open("inspiration.txt");
                reader = new BufferedReader(new InputStreamReader(file));
                String line = reader.readLine();
                while (line != null) {
                    Log.d(GW_QUOTES_ACTIVITY_LOGGER, "Loaded Line" + line);
                    lines.add(line);
                    line = reader.readLine();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                lines.add("Don’t panic!");
                return false;
            }
        }
return true;
    }
    public String getRndQuote(int seed) {

        buildQuotes();
        if (seed == 0) {
            Random r = new Random();
            int randomNumber = r.nextInt(lines.size());
            return getQuote(randomNumber);
        }
        return "Don’t Let Yesterday Take Up Too Much Of Today";
    }

    public String getQuote(int i) {


        String retval = "";
        //June 2, 2019 BDY - just read the file once per app startup
        buildQuotes();

        retval=lines.get(i);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Appcontext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("gwLastQuote",retval);

        editor.apply();
        return retval;
    }

}
