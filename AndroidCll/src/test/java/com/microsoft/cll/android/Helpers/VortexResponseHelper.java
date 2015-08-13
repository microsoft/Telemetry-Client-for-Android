package com.microsoft.cll.android.Helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VortexResponseHelper {
        public static int getNumberOfAcceptedEvents(String response) {
            String patternString = "\"acc\":(\\d*)";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matches = pattern.matcher(response);
            if(matches.find()) {
                return Integer.parseInt(matches.group(1));
            }

            return -1;
        }

        public static int getNumberOfRejectedEvents(String response) {
            String patternString = "\"rej\":(\\d*)";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matches = pattern.matcher(response);
            if(matches.find()) {
                return Integer.parseInt(matches.group(1));
            }

            return -1;
        }
}
