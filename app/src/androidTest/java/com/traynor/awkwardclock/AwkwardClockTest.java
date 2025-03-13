package com.traynor.awkwardclock;

import android.util.SparseArray;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class AwkwardClockTest {

    @Test
    public void testRoundToFive() {
        for (int minute = 0 ; minute < 120; minute++){
            int rounded = AwkwardClock.roundToFive(minute);
            assertTrue("Result is evenly divided by 5",rounded % 5 == 0);
            if (minute % 5 == 0) {
                assertTrue("No need to round", minute == rounded);
            }
            else if (minute % 5 < 3) {
                assertTrue("Round down", minute > rounded);
            }
            else if (minute % 5 >= 3) {
                assertTrue("Round up", rounded > minute);
            }
        }
    }

    @Test
    public void testGetOffsetToNearestHour() {
        Calendar cal = Calendar.getInstance();
        SparseArray<String> offsetMap = AwkwardClock.getOffsetNumToString();
        for (int minute = 0; minute <= 60; minute++) {
            for (int offsetIdx = 0; offsetIdx < offsetMap.size(); offsetIdx++){
                int offset = offsetMap.keyAt(offsetIdx);
                cal.set(Calendar.MINUTE, minute);
                int offsetToNearestHour = AwkwardClock.getOffsetToNearestHour(cal, offset);
                assertNotNull("Returned value is in the map", offsetMap.get(offsetToNearestHour));
                int target = Math.abs(AwkwardClock.roundToFive(minute) + offset
                        + offsetToNearestHour);
                assertTrue("Adding offsets to current minute brings us to an hour",
                        target == 0 || target == 60);
            }
        }
    }

    @Test
    public  void testGetOffset() {
        for (int minute = 0; minute <= 60; minute++) {
            for (int i = 5; i < AwkwardClock.getOffsetNumToString().size(); i++) {
                int offset = AwkwardClock.getOffset(minute, i);
                assertTrue("Not 0", offset != 0);
                int minutePlusOffset = AwkwardClock.roundToFive(minute + offset) % 60;
                assertTrue("Does not add to next/prev hour with current minute",
                        minutePlusOffset != 0);
            }
        }
    }

    @Test
    public void testRoundToHour() {
        for (int minute = 0; minute <= 60; minute++) {
            for (int hour = 0; hour <= 12; hour++) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR, hour);
                calendar.set(Calendar.MINUTE, minute);
                AwkwardClock.roundToHour(calendar);
                assertEquals(0, calendar.get(Calendar.MINUTE));
                assertEquals(calendar.get(Calendar.HOUR), (minute > 30 ? hour + 1 : hour) % 12);
            }
        }
    }
}