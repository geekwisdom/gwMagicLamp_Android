package org.geekwisdom.magiclamp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class gwAlarmReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // show toast
        GWQuotes newQuote = new GWQuotes(context);
        Toast.makeText(context, newQuote.getQuote(), Toast.LENGTH_LONG).show();
    }
}