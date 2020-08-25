package com.example.awkwardclock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Listen for taps on the text view that displays time
        View textClockView = findViewById(R.id.textClock);
        textClockView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == (MotionEvent.ACTION_DOWN)) {
                    v.performClick();
                    return true;
                }
                return false;
            }
        });
    }



}

