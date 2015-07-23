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
    public static String convertFreq(int freq)
    {
        if (freq >= 1000 * 1000)
        {
            return String.format("%.2fGHz", (double)freq / (1000 * 1000));
        }
        else if (freq >= 1000)
        {
            return String.format("%.2fMHz", (double)freq / 1000);
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
    public static String convertMemory(long size)
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
    public static String convertDp(double size)
    {
        //Rounding-off method. Remain 1 decimal number.
        DecimalFormat decimalFormat = new DecimalFormat("#0.0dp");
        return decimalFormat.format(size);
    }
    
    /**
     * Process inch value.
     */
    public static String convertInch(double size)
    {
        //Rounding-off method. Remain 2 decimal number.
        DecimalFormat decimalFormat = new DecimalFormat("#0.00\"");
        return decimalFormat.format(size);
    }
    
    /**
     * Convert to percentage and drop out decimal number.
     * @param usage Array like [total, available]
     */
    public static String convertPercent(long[] usage)
    {
        return convertPercent(usage[0], usage[1]);
    }
    
    /**
     * Convert to percentage and drop out decimal number.
     */
    public static String convertPercent(long total, long avail)
    {
        return convertPercent(total, avail, 0);
    }
    
    /**
     * Convert to percentage.
     * @param precision How many decimal number to remain.
     */
    public static String convertPercent(long total, long avail, int precision)
    {
        if (total <= 0)
        {
            return UNKNOWN;
        }
        return String.format("%." + precision + "f%%", avail * 100.0 / total);
    }
    
    /**
     * Convert milliseconds to readable time.
     */
    public static String convertTime(long millis)
    {
        long day = millis / (24 * 60 * 60 * 1000);
        millis %= (24 * 60 * 60 * 1000);
        long hour = millis / (60 * 60 * 1000);
        millis %= (60 * 60 * 1000);
        long minute = millis / (60 * 1000);
        millis %= (60 * 1000);
        long second = millis / 1000;
        millis %= 1000;
        if (day > 0)
        {
            return String.format("%1$02d:%2$02d:%3$02d:%4$02d", day, hour, minute, second);
        }
        else if (hour > 0 || minute > 0)
        {
            return String.format("%1$02d:%2$02d:%3$02d", hour, minute, second);
        }
        return String.format("%1$02d:%2$03d", second, millis);
    }
    
    /**
     * Convert decimalism to other.
     * @param decimal Value in decimalism.
     * @param radix Which system to convert. (Only 2, 8, 16)
     */
    public static String convertBits(int decimal, int radix)
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
}
