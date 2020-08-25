package com.example.awkwardclock;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class AwkwardAppWidgetConfigure extends Activity {

    static final String PREFS_NAME = "com.example.android.apis.appwidget.AwkwardAppWidgetProvider";
    static final String PREF_ROUND_KEY = "roundToHour_";
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private boolean doRound = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED); // Don't place widget if left unconfigured
        setContentView(R.layout.widget_config);
        setupSwitch();
        findViewById(R.id.configDone).setOnClickListener(mOnClickListener);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    private void setupSwitch() {
        Switch roundToHourSwitch = findViewById(R.id.roundToHourSwitch);
        roundToHourSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doRound = isChecked;
            }
        });
        // FIXME toggling twice to trigger onCheckedChangeListener
        roundToHourSwitch.toggle();
        roundToHourSwitch.toggle();
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = AwkwardAppWidgetConfigure.this;
            saveRoundingPref(context, mAppWidgetId, doRound);
            // Push widget update to surface with newly set prefix
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            AwkwardAppWidgetProvider.updateAppWidget(context, appWidgetManager,
                    mAppWidgetId, doRound);
            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    // Write the rounding pref to the SharedPreferences object for this widget
    static void saveRoundingPref(Context context, int appWidgetId, boolean doRound) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putBoolean(PREF_ROUND_KEY + appWidgetId, doRound);
        prefs.apply();
    }
}
