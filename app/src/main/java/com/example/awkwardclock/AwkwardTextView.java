package com.example.awkwardclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class AwkwardTextView extends AppCompatTextView {

    private boolean mRegistered;

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onTimeChanged();
        }
    };

    @SuppressWarnings("UnusedDeclaration")
    public AwkwardTextView(Context context) {
        super(context);
        init();
    }

    @SuppressWarnings("UnusedDeclaration")
    public AwkwardTextView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    private void init() {
        onTimeChanged();
    }

    @Override
    public boolean performClick(){
        onTimeChanged();
        return super.performClick();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mRegistered) {
            mRegistered = true;
            registerReceiver();
        }
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (isVisible) {
            onTimeChanged();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRegistered) {
            unregisterReceiver();
            mRegistered = false;
        }
    }

    private void registerReceiver() {
        final IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

        // OK, this is gross but needed. This class is supported by the
        // remote views mechanism and as a part of that the remote views
        // can be inflated by a context for another user without the app
        // having interact users permission - just for loading resources.
        // For example, when adding widgets from a managed profile to the
        // home screen. Therefore, we register the receiver as the user
        // the app is running as not the one the context is for.
        getContext().registerReceiver(mIntentReceiver, filter);
    }


    private void unregisterReceiver() {
        getContext().unregisterReceiver(mIntentReceiver);
    }


    /**
     * Update the displayed time
     */
    private void onTimeChanged() {
        setText(AwkwardClock.getTime(true));
    }

}
