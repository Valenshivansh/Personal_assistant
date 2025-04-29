package com.example.speechtotext;

import java.util.Calendar;

public class Greet {
    static String wishMe() {
        String s = "";
        Calendar c = Calendar.getInstance();
        int time = c.get(Calendar.HOUR_OF_DAY);

        if (time >= 6 && time < 12) {
            s = "Good Morning Master";
        } else if (time >= 12 && time < 16) {
            s = "Good Afternoon Master";
        } else if (time >= 16 && time < 22) {
            s = "Good Evening Master";
        } else { // Covers 10 PM to 6 AM
            s = "Good Night Master";
        }

        return s;
    }
}