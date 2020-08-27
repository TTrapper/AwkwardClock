package com.example.awkwardclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class AwkwardAppWidgetProvider extends AppWidgetProvider {

    private static BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d(getClass().getName(), String.format("Widget Provider received intent: %s",
                    intent));
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName thisAppWidget = new ComponentName(context,
                        AwkwardAppWidgetProvider.class);
                int [] ids = appWidgetManager.getAppWidgetIds(thisAppWidget);
                for (int appWidgetID: ids) {
                    boolean doRound = getRoundingPref(context, appWidgetID);
                    updateAppWidget(context, appWidgetManager, appWidgetID, doRound);
                }
            }
        }
    };

    static boolean getRoundingPref(Context context, int appWidgetID) {
        SharedPreferences prefs = context.getSharedPreferences(AwkwardAppWidgetConfigure.PREFS_NAME,
                0);
        return prefs.getBoolean(AwkwardAppWidgetConfigure.PREF_ROUND_KEY + appWidgetID,
                true);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, boolean doRound) {
        String awkwardTime = AwkwardClock.getTime(doRound);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.awkward_appwidget);
        views.setTextViewText(R.id.appwidget_text, awkwardTime);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            boolean doRound = getRoundingPref(context, appWidgetId);
            updateAppWidget(context, appWidgetManager, appWidgetId, doRound);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(getClass().getName(), "enabling provider");
        super.onEnabled(context);
        registerReceiver(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        // Enter relevant functionality for when the last widget is disabled
        unregisterReceiver(context);
    }

    private void registerReceiver(Context context) {
        Log.d(getClass().getName(), "registering receiver");
        context.getApplicationContext().registerReceiver(receiver,
                new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    private void unregisterReceiver(Context context) {
        Log.d(getClass().getName(), "unregistering receiver");
        try {
            context.getApplicationContext().unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            Log.e(getClass().getName(), "Unregistering receiver failed", e);
        }

    }

}
