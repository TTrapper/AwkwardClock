package com.example.awkwardclock;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import android.util.SparseArray;
import android.util.SparseIntArray;

class AwkwardClock {

    private static SparseArray<String> offsetNumToString = new SparseArray<>(12);
    static {
        offsetNumToString.append(-30, "half past");
        offsetNumToString.append(-25, "twenty five after");
        offsetNumToString.append(-20, "twenty after");
        offsetNumToString.append(-15, "a quarter after");
        offsetNumToString.append(-10, "ten after");
        offsetNumToString.append(-5, "five after");
        offsetNumToString.append(0, "pretty much");
        offsetNumToString.append(5, "five to");
        offsetNumToString.append(10, "ten to");
        offsetNumToString.append(15, "a quarter to");
        offsetNumToString.append(20, "twenty to");
        offsetNumToString.append(25, "twenty five to");
    }

    private static SparseIntArray minuteToOffset = new SparseIntArray(13);
    static {
        minuteToOffset.append(0, 0);
        minuteToOffset.append(5, -5);
        minuteToOffset.append(10, -10);
        minuteToOffset.append(15, -15);
        minuteToOffset.append(20, -20);
        minuteToOffset.append(25, -25);
        minuteToOffset.append(30, -30);
        minuteToOffset.append(35, 25);
        minuteToOffset.append(40, 20);
        minuteToOffset.append(45, 15);
        minuteToOffset.append(50, 10);
        minuteToOffset.append(55, 5);
        minuteToOffset.append(60, 0);
    }

    private static final String[] INT_TO_HOUR = {"twelve", "one", "two", "three", "four", "five",
            "six", "seven", "eight", "nine", "ten", "eleven", "twelve"};

    private static  final  String [] INT_TO_MINUTE = {
            "",
            " o'one",
            " o'two",
            " o'three",
            " o'four",
            " o'five",
            " o'six",
            " o'seven",
            " o'eight",
            " o'nine",
            " ten",
            " eleven",
            " twelve",
            " thirteen",
            " fourteen",
            " fifteen",
            " sixteen",
            " seventeen",
            " eighteen",
            " nineteen",
            " twenty",
            " twenty one",
            " twenty two",
            " twenty three",
            " twenty four",
            " twenty five",
            " twenty six",
            " twenty seven",
            " twenty eight",
            " twenty nine",
            " thirty",
            " thirty one",
            " thirty two",
            " thirty three",
            " thirty four",
            " thirty five",
            " thirty six",
            " thirty seven",
            " thirty eight",
            " thirty nine",
            " forty",
            " forty one",
            " forty two",
            " forty three",
            " forty four",
            " forty five",
            " forty six",
            " forty seven",
            " forty eight",
            " forty nine",
            " fifty",
            " fifty one",
            " fifty two",
            " fifty three",
            " fifty four",
            " fifty five",
            " fifty six",
            " fifty seven",
            " fifty eight",
            " fifty nine"};


    private static int getRandomOffset(int minute) {
        Random random = new Random();
        final int randomIndex = random.nextInt(offsetNumToString.size());
        return getOffset(minute, randomIndex);
    }

    /**
     * Get the offset at requested index iff:
     *      1) it's not an offset of 0.
     *      2) it doesn't add up to the nearest hour.
     *      3) it doesn't add to a minute whose offset to the nearest hour is equal to the original
     *         offset.
     * If it does, return an offset that doesn't.
     * @param minute the minute the offset is expected to be added to
     * @param offsetIndex index into the array of allowed offsets
     * @return
     */
    static int getOffset(int minute, int offsetIndex) {
        int offset = offsetNumToString.keyAt(offsetIndex);
        int newMinute = (roundToFive(minute) + offset) % 60;
        while (offset == 0 || newMinute == 0 || minuteToOffset.get(newMinute) == offset) {
            offsetIndex++;
            offset = offsetNumToString.keyAt(offsetIndex % offsetNumToString.size());
            newMinute = (roundToFive(minute) + offset) % 60;
        }
        return offset;

    }

    static int roundToFive(int minute) {
        final int FIVE = 5;
        return FIVE*(Math.round(((float) minute) / FIVE));
    }

    static int getOffsetToNearestHour(Calendar calendar, int offset) {
        calendar.add(Calendar.MINUTE, offset);
        int newMinute = calendar.get(Calendar.MINUTE);
        int newMinuteRounded = roundToFive(newMinute);
        return minuteToOffset.get(newMinuteRounded);
    }

    static void roundToHour(Calendar calendar) {
        if (calendar.get(Calendar.MINUTE) > 30) {
            calendar.add(Calendar.HOUR, 1);
        }
        calendar.set(Calendar.MINUTE, 0);
    }

    static String getTime(boolean roundToHour) {
        Calendar calendar = Calendar.getInstance();
        int minute = calendar.get(Calendar.MINUTE);
        int offset = getRandomOffset(minute);
        int secondOffset = getOffsetToNearestHour(calendar, offset);
        calendar.add(Calendar.MINUTE, secondOffset);
        if (roundToHour) {
            roundToHour(calendar);
        }
        return String.format("%s\n%s\n%s%s", offsetNumToString.get(offset),
                offsetNumToString.get(secondOffset), INT_TO_HOUR[calendar.get(Calendar.HOUR)],
                INT_TO_MINUTE[calendar.get(Calendar.MINUTE)]).toUpperCase();
    }

    static SparseArray<String> getOffsetNumToString() {
        return offsetNumToString;
    }
}
