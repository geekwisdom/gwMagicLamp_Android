package org.geekwisdom.magiclamp;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class GWQuotes {
    private android.content.res.AssetManager AssetManager;
    private static final String GW_QUOTES_ACTIVITY_LOGGER = GWMainActivity.class.getSimpleName();
    public GWQuotes(Context myContext)
    {
        AssetManager = myContext.getAssets();
    }
    public String getQuote()
    {

        BufferedReader reader;
        String retval="";
        ArrayList<String> lines = new ArrayList<String>();
        try{


            final InputStream file = AssetManager.open("inspiration.txt");
            reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();
            while(line != null){
                Log.d(GW_QUOTES_ACTIVITY_LOGGER,"Loaded Line" + line);
                lines.add(line);
                line = reader.readLine();
            }
        } catch(IOException ioe){
            ioe.printStackTrace();
            return "The best revenge is massive success";


        }
        Random r=new Random();
        int randomNumber=r.nextInt(lines.size());
        retval=lines.get(randomNumber);
        return retval;
    }
}
