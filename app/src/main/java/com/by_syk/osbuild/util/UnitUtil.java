/**
 * @author By_syk
 */

package com.by_syk.osbuild.util;

import java.text.DecimalFormat;

public class UnitUtil
{
    private static String UNKNOWN = "";
    
    /**
     * About CPU frequency.
     * @param freq Unit: Hz
     */
    public static String toFreq(long freq)
    {
        if (freq >= 1e9)
        {
            return String.format("%.2fGHz", (double)freq / (1e9));
        }
        else if (freq >= 1e6)
        {
            return String.format("%.2fMHz", (double)freq / 1e6);
        }
        else if (freq >= 1e3)
        {
            return String.format("%.2fKHz", (double)freq / 1e3);
        }
        else
        {
            return freq + "Hz";
        }
    }
    
    /**
     * About file size.
     * @param size Unit: byte
     */
    public static String toMemory(long size)
    {
        if (size >= 1024 * 1024 * 1024)
        {
            return String.format("%.2fGB", (double)size / (1024 * 1024 * 1024));
        }
        else if (size >= 1024 * 1024)
        {
            return String.format("%.2fMB", (double)size / (1024 * 1024));
        }
        else if (size >= 1024)
        {
            return String.format("%.2fKB", (double)size / 1024);
        }
        else
        {
            return size + "B";
        }
    }
    
    /**
     * Process dp value.
     */
    public static String toDp(double size)
    {
        //Rounding-off method. Remain 1 decimal number.
        DecimalFormat decimalFormat = new DecimalFormat("#0.0dp");
        
        return decimalFormat.format(size);
    }
    
    /**
     * Process inch value.
     */
    public static String toInch(double size)
    {
        //Rounding-off method. Remain 2 decimal number.
        DecimalFormat decimalFormat = new DecimalFormat("#0.00\"");
        
        return decimalFormat.format(size);
    }
    
    /**
     * Convert to percentage and drop out decimal number.
     * @param usage Array like [total, available]
     */
    public static String toPercent(long[] usage)
    {
        return toPercent(usage[0], usage[1]);
    }
    
    /**
     * Convert to percentage and drop out decimal number.
     */
    public static String toPercent(long total, long avail)
    {
        return toPercent(total, avail, 0);
    }
    
    /**
     * Convert to percentage.
     * @param precision How many decimal number to remain.
     */
    public static String toPercent(long total, long avail, int precision)
    {
        if (total <= 0)
        {
            return UNKNOWN;
        }
        return toPercent((double)avail / total, precision);
    }
    
    /**
     * @param precision How many decimal number to remain.
     */
    public static String toPercent(double value, int precision)
    {
        return String.format("%." + precision + "f%%", value * 100.0);
    }

    /**
     * Convert milliseconds to readable time.
     */
    public static String toTime(long millis)
    {
        long day = millis / (24 * 60 * 60 * 1000);
        millis %= (24 * 60 * 60 * 1000);
        long hour = millis / (60 * 60 * 1000);
        millis %= (60 * 60 * 1000);
        long minute = millis / (60 * 1000);
        millis %= (60 * 1000);
        long second = millis / 1000;
        
        if (day > 0)
        {
            return String.format("%1$03d:%2$02d:%3$02d:%4$02d", day, hour, minute, second);
        }
        return String.format("%1$02d:%2$02d:%3$02d", hour, minute, second);
    }
    
    /**
     * Convert decimalism to other.
     * @param decimal Value in decimalism.
     * @param radix Which system to convert. (Only 2, 8, 16)
     */
    public static String toBits(int decimal, int radix)
    {
        String num;
        switch (radix)
        {
            case 2:
                num = "00000000000000000000000000000000" + Integer.toBinaryString(decimal);
                return "0b" + num.substring(num.length() - 32);
            case 8:
                num = "00000000000" + Integer.toOctalString(decimal);
                return "0" + num.substring(num.length() - 11);
            case 16:
                num = "00000000" + Integer.toHexString(decimal);
                return "0x" + num.substring(num.length() - 8);
            default:
                return UNKNOWN;
        }
    }
    
    /**
     * Convert integer wraped in string to int safely.
     */
    public static int toIntSafely(String text)
    {
        int result = -1;

        text = text.trim();
        try
        {
            result = Integer.parseInt(text);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Convert float value wraped in string to float safely.
     */
    public static float toFloatSafely(String text)
    {
        float result = -1.0f;

        text = text.trim();
        try
        {
            result = Float.parseFloat(text);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        return result;
    }
    
    /**
     * Process ppi value.
     * @param ppi Unit: pixels
     */
    public static String toPPI(double ppi)
    {
        return String.format("%.0fppi", ppi);
    }
}
