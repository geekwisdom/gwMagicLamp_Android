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
        if (LastQuote == "") LastQuote="Welcome to Magic Lamp!";
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
