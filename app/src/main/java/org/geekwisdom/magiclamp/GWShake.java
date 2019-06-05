/********************************************************************************
 File Name: GWShake.java
 @(#) Purpose: Get a random 'funny' message when user Shakes the phone
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

public class GWShake {
    private android.content.res.AssetManager AssetManager;
    private Context Appcontext;
    ArrayList<String> lines = new ArrayList<String>();
    private static final String GW_QUOTES_ACTIVITY_LOGGER = GWMainActivity.class.getSimpleName();
    public GWShake(Context myContext)
    {
        Appcontext = myContext;
        AssetManager = myContext.getAssets();
    }



    public String getRndMsg()
    {
        return getRndQuote(0);
    }

        private boolean buildMsgs()
    {
    lines.add("EARTHQUAKE!!");
    lines.add("Hey I'm sleeping here");
    lines.add("OUCH - That hurt!");
    lines.add("Hey - I'm in here you know");
    lines.add("Please stop shaking my home around");
    return true;
    }
    public String getRndQuote(int seed) {

        buildMsgs();
        if (seed == 0) {
            Random r = new Random();
            int randomNumber = r.nextInt(lines.size());
            return getMessage(randomNumber);
        }
        return "Don’t Let Yesterday Take Up Too Much Of Today";
    }

    public String getMessage(int i) {


        String retval = "";
        //June 2, 2019 BDY - just read the file once per app startup
        buildMsgs();
        retval=lines.get(i);

        return retval;
    }

}
