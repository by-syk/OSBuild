package com.by_syk.osbuild.util;

import java.text.DecimalFormat;

public class UnitUtil
{
    private static String UNKNOWN = "";
    /**
     * CPU频率
     * @param freq Hz
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
     * 文件大小
     * @param size B
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
     * 包装DP值
     */
    public static String convertDp(double size)
    {
        //四舍五入保留一位小数
        DecimalFormat decimalFormat = new DecimalFormat("#0.0dp");
        return decimalFormat.format(size);
    }
    /**
     * 包装英寸数
     */
    public static String convertInch(double size)
    {
        //四舍五入保留两位小数
        DecimalFormat decimalFormat = new DecimalFormat("#0.00\"");
        return decimalFormat.format(size);
    }
    /**
     * 化为百分数（精确到整数位）
     * @param usage 数组[总、可用]
     */
    public static String convertPercent(long[] usage)
    {
        return convertPercent(usage[0], usage[1]);
    }
    
    public static String convertPercent(long total, long avail)
    {
        if (total <= 0)
        {
            return UNKNOWN;
        }
        return String.format("%d%%", avail * 100 / total);
    }
    
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
     * 进制转换
     */
    public static String convertBits(int decimal, int radix)
    {
        String num = "";
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
