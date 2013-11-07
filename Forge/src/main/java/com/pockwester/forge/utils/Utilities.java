package com.pockwester.forge.utils;

/**
 * Created by AW on 10/2/13.
 * Utilities for application.
 */
public class Utilities {

    // Will return true if a string can be assumed to be an numeric value
    // http://stackoverflow.com/questions/1102891/how-to-check-a-string-is-a-numeric-type-in-java
    public static boolean IsNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }

        return true;
    }
}
