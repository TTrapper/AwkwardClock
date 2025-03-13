package com.example.awkwardclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

public class AwkwardAppWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "AwkwardAppWidget";
    private static final String ACTION_UPDATE_WIDGET = "com.example.awkwardclock.ACTION_UPDATE_WIDGET";

    // Helper method for debug logging; logs only in debug builds.
    private static void debugLog(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    static boolean getRoundingPref(Context context, int appWidgetID) {
        SharedPreferences prefs = context.getSharedPreferences(
                AwkwardAppWidgetConfigure.PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(AwkwardAppWidgetConfigure.PREF_ROUND_KEY + appWidgetID, true);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, boolean doRound) {
        String awkwardTime = AwkwardClock.getTime(doRound);
        debugLog("updateAppWidget: widget id " + appWidgetId + ", time: " + awkwardTime);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.awkward_appwidget);
        views.setTextViewText(R.id.appwidget_text, awkwardTime);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        debugLog("Widget Provider enabled - scheduling alarm");
        scheduleNextUpdate(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        debugLog("Widget Provider disabled - cancelling alarm");
        cancelAlarm(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        // Check if our custom action was received.
        if (ACTION_UPDATE_WIDGET.equals(intent.getAction())) {
            debugLog("onReceive for ACTION_UPDATE_WIDGET");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context, AwkwardAppWidgetProvider.class);
            int[] ids = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetID : ids) {
                boolean doRound = getRoundingPref(context, appWidgetID);
                updateAppWidget(context, appWidgetManager, appWidgetID, doRound);
            }
            // Schedule the next update.
            scheduleNextUpdate(context);
        }
    }

    /**
     * Schedules the next update aligned with the next minute boundary using AlarmManager.
     */
    private void scheduleNextUpdate(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AwkwardAppWidgetProvider.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        long now = System.currentTimeMillis();
        // Calculate delay until the next minute boundary.
        long delay = 60000 - (now % 60000);
        long nextUpdateTimeMillis = now + delay;

        // Use the appropriate alarm method depending on Android version.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, nextUpdateTimeMillis, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC, nextUpdateTimeMillis, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC, nextUpdateTimeMillis, pendingIntent);
        }
        debugLog("Scheduled next update at " + nextUpdateTimeMillis + " (in " + delay + "ms)");
    }

    /**
     * Cancels the scheduled alarm.
     */
    private void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AwkwardAppWidgetProvider.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
        debugLog("Cancelled alarm update");
    }
}
