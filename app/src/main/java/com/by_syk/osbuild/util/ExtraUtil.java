package com.by_syk.osbuild.util;

import com.by_syk.osbuild.R;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.FileFilter;
import android.os.StatFs;
import android.os.Build;
import java.util.Locale;
import java.text.DecimalFormat;
import java.io.PrintWriter;
import android.telephony.TelephonyManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import android.text.TextUtils;

public class ExtraUtil
{
    //private static String L = "";
    private static String L0 = "\n";
    private static String L1 = "\n   ";
    private static String L2 = "\n      ";
    //private static String L3 = "\n         ";
    private static String SPACE = "  ";
    /**
     * @param format: y year
     *              M month in the year
     *              d day
     *              h hours(12)
     *              H hours(24)
     *              m minute
     *              s second
     *              S millisecond
     *              E weekday
     *              D days in the year
     */
    public static String convertMillisTime(long time_millis, String format)
    {
        if (time_millis <= 0)
        {
            return "";
        }
        String result = "";
        try
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
            result = dateFormat.format(new Date(time_millis));
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    
    public static String convertMillisTime(long time_millis)
    {
        return convertMillisTime(time_millis, "yyyy-MM-dd HH:mm:ss");
    }
    
    /*public static String getCurTime()
    {
        return convertMillisTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
    }*/
    /**
     * 获取状态栏高度（px）
     */
    /*public static int getStatusBarHeight(Context context)
    {
        Class<?> c;
        Object obj;
        Field field;
        int x;
        int result = -1;
        try
        {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            result = context.getResources().getDimensionPixelSize(x);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }*/
    /**
     * 获取ActionBar高度（px）
     */
    /*public static int getActionBarHeight(Context context)
    {
        TypedValue typedValue = new TypedValue();
        int result = -1;
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true))
        {
            result = TypedValue.complexToDimensionPixelSize(typedValue.data,
                context.getResources().getDisplayMetrics());
        }

        return result;
    }*/
    /**
     * 获取导航栏高度（px）
     */
    /*public static int getNavigationBarHeight(Context context)
    {
        int result = -1;
        try
        {
            Resources resources = context.getResources();
            int x = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            result = resources.getDimensionPixelSize(x);
        }
        catch (Resources.NotFoundException e)
        {
            e.printStackTrace();
        }

        return result;
    }*/
    /**
     * 获取本应用安装包的路径，/data/app目录下。
     */
    public static File pickUpMyPackage(Context context)
    {
        String apk_path = "";
        try
        {
            PackageInfo packageInfo = context.getPackageManager()
                .getPackageInfo(context.getPackageName(), 0);
            apk_path = packageInfo.applicationInfo.publicSourceDir;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        File file_s = new File(apk_path);
        File file_t = new File(context.getExternalCacheDir(),
            getVerInfo(context) + ".apk");
        //已提取则不提取
        if (file_s.exists() && file_t.exists() && file_s.length() == file_t.length())
        {
            return file_t;
        }
        fileChannelCopy(file_s, file_t);
        
        return file_t.exists() ? file_t : null;
    }
    /**
     * 高效文件复制方法（通过文件通道）
     */
    public static void fileChannelCopy(File source, File target)
    {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel fc_in = null;
        FileChannel fc_out = null;
        try
        {
            fis = new FileInputStream(source);
            fos = new FileOutputStream(target);
            fc_in = fis.getChannel();//得到对应的文件通道
            fc_out = fos.getChannel();//得到对应的文件通道
            fc_in.transferTo(0, fc_in.size(), fc_out);//连接两个通道，并且从in通道读取，然后写入out通道
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fc_out != null)
            {
                try
                {
                    fc_out.close();
                }
                catch (IOException e)
                {}
            }
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {}
            }
            if (fc_in != null)
            {
                try
                {
                    fc_in.close();
                }
                catch (IOException e)
                {}
            }
            if (fis != null)
            {
                try
                {
                    fis.close();
                }
                catch (IOException e)
                {}
            }
        }
    }
    
    public static String readFile(InputStream is)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        try
        {
            while((len = is.read(buf)) != -1)
            {
                baos.write(buf, 0, len);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return baos.toString();
    }

    public static String readFile(File file)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try
        {
            is = new FileInputStream(file);
            byte buf[] = new byte[1024];
            int len;
            while((len = is.read(buf)) != -1)
            {
                baos.write(buf, 0, len);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {}
            }
        }
        return baos.toString();
    }
    
    public static String readFile(String file_str)
    {
        return readFile(new File(file_str));
    }
    
    public static boolean saveFile(File path, String text)
    {
        boolean result = false;
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(path);
            fos.write(text.getBytes());
            result = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fos != null)
            {
                try
                {
                    fos.flush();
                    fos.close();
                }
                catch (IOException e)
                {}
            }
        }
        return result;
    }
    /**
     * @return 版本信息（如：OSBuild_v1.0(150309)）
     */
    public static String getVerInfo(Context context)
    {
        String app_name = context.getString(R.string.app_name);
        String ver_name = "";
        int ver_code = 0;
        try
        {
            PackageInfo packageInfo = context.getPackageManager()
                .getPackageInfo(context.getPackageName(), 0);
            ver_name = packageInfo.versionName;
            ver_code = packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        return String.format("%1$s_v%2$s(%3$d)", app_name, ver_name, ver_code);
    }
    /**
     * 获取CPU最大、最小频率
     */
    public static String getCPUFreq(boolean max)
    {
        final String[] ARGS_MAX = { "/system/bin/cat",
            "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
        final String[] ARGS_MIN = { "/system/bin/cat",
            "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq" };

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(max ? ARGS_MAX[1] : ARGS_MIN[1]);
        stringBuilder.append(L1).append(max ? "cpuinfo_max_freq: " : "cpuinfo_min_freq: ");

        InputStream inputStream = null;
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder(max ? ARGS_MAX : ARGS_MIN);
            Process process = processBuilder.start();
            inputStream = process.getInputStream();
            String temp = "";
            byte[] buffer = new byte[24];
            while (inputStream.read(buffer) != -1)
            {
                temp += (new String(buffer));
            }
            temp = temp.trim();
            if (!"".equals(temp))
            {
                int freq = Integer.parseInt(temp);
                stringBuilder.append(freq);
                stringBuilder.append(SPACE).append(UnitUtil.convertFreq(freq));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {}
            }
        }

        return stringBuilder.toString();
    }
    /**
     * 获取CPU信息（Processor、Hardware）
     */
    public static String getCPUName()
    {
        String text = readFile("/proc/cpuinfo");
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/proc/cpuinfo");
        //少数机型会使用“model name”而不是“Processir”，如魅蓝Note
        if (text.contains("model name"))
        {
            int index = text.indexOf("model name");
            stringBuilder.append(L1).append("model name: ");
            stringBuilder.append(text.substring(index + 13, text.indexOf("\n", index)));
        }
        else if (text.contains("Processor"))
        {
            int index = text.indexOf("Processor");
            stringBuilder.append(L1).append("Processor: ");
            stringBuilder.append(text.substring(index + 12, text.indexOf("\n", index)));
        }
        else
        {
            stringBuilder.append(L1).append("Processor: ");
        }
        
        if (text.contains("Hardware"))
        {
            int index = text.indexOf("Hardware");
            stringBuilder.append(L1).append("Hardware: ");
            stringBuilder.append(text.substring(index + 11, text.indexOf("\n", index)));
        }
        else
        {
            stringBuilder.append(L1).append("Hardware: ");
        }
        
        return stringBuilder.toString();
    }
    /**
     * 获取RAM大小
     */
    public static String getTotalRAM()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/proc/meminfo");
        stringBuilder.append(L1).append("MemTotal: ");

        String text = readFile("/proc/meminfo");
        if ("".equals(text))
        {
            return stringBuilder.toString();
        }
        Pattern pattern = Pattern.compile("[^0-9]");   
        Matcher matcher = pattern.matcher(text.substring(0, text.indexOf("\n")));   
        String temp = matcher.replaceAll("").trim();
        if (!"".equals(temp))
        {
            int size = Integer.parseInt(temp);//KB
            stringBuilder.append(size).append(SPACE)
                .append(UnitUtil.convertMemory(size * 1024));
        }
        
        return stringBuilder.toString();
    }
    /**
     * 化简宽高比（整数比）
     */
    public static String getWHRatioInt(int width, int height)
    {
        int p1 = width;
        int p2 = height;
        int temp;
        while (p2 != 0)
        {
            temp = p1 % p2;
            p1 = p2;
            p2 = temp;
        }
        return String.format("%1$d:%2$d", width / p1, height / p1);
    }
    /**
     * 化简宽高比（比值）
     */
    public static String getWHRatio(int width, int height)
    {
        //四舍五入保留三位小数
        DecimalFormat decimalFormat = new DecimalFormat("#0.000");
        return decimalFormat.format((double)width / height);
    }
    /**
     * 获取CPU核心数
     * 读取系统目录“/sys/devices/system/cpu/”下类似“/cpu0”文件夹的数量
     */
    public static String getCPUCores()
    {
        //获取CPU核心数 方法1
        int cores_runtime = Runtime.getRuntime().availableProcessors();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("java.lang.Runtime.");
        stringBuilder.append(L1).append("getRuntime().");
        stringBuilder.append(L2).append("availableProcessors(): ");
        stringBuilder.append(cores_runtime);
        //获取CPU核心数 方法2
        int cores = 1;
        class CpuFilter implements FileFilter
        {
            @Override
            public boolean accept(File pathname)
            {
                return Pattern.matches("cpu[0-9]", pathname.getName());
            }      
        }
        try
        {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());
            cores = files.length;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (cores != cores_runtime)
        {
            stringBuilder.append(L0).append("/sys/devices/system/cpu/");
            for (int i = 0; i < cores; ++ i)
            {
                stringBuilder.append(L1).append("cpu" + i + "/");
                stringBuilder.append(SPACE).append("true");
            }
        }
        
        return stringBuilder.toString();
    }
    /**
     * 获取根目录存储情况（RootDirectory，DataDirectory，ExternalStorageDirectory）
     * @return 数组[总大小、可用大小]，单位：Byte
     */
    public static long[] getStorageUsage(File storage)
    {
        long[] result = { -1, -1 };
        if (storage == null || !storage.exists())
        {
            return result;
        }
        StatFs statFs = new StatFs(storage.getPath());
        long block_size;//Byte
        long block_count;
        long avail_blocks;
        if (Build.VERSION.SDK_INT >= 18)
        {
            block_size = statFs.getBlockSizeLong();
            block_count = statFs.getBlockCountLong();
            avail_blocks = statFs.getAvailableBlocksLong();
        }
        else
        {
            block_size = statFs.getBlockSize();
            block_count = statFs.getBlockCount();
            avail_blocks = statFs.getAvailableBlocks();
        }
        result[0] = block_size * block_count;//Byte
        result[1] = block_size * avail_blocks;//Byte
        return result;
    }
    
    public static String readFileRoot(Context context, File source_file)
    {
        if (source_file == null)
        {
            return "";
        }
        File target_file = new File(context.getExternalCacheDir(),
            source_file.getName());
        String script = String.format("cp %1$s %2$s",
            source_file, target_file);
        
        Process process = null;
        PrintWriter printWriter = null;
        try
        {
            process = Runtime.getRuntime().exec("su");
            printWriter = new PrintWriter(process.getOutputStream());
            printWriter.println(script);
            printWriter.flush();
            printWriter.println("exit");
            printWriter.flush();
            //等待执行
            process.waitFor();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (process != null)
            {
                process.destroy();
            }
        }
        
        if (target_file.exists())
        {
            return readFile(target_file);
        }
        return "";
    }
    /**
     * 规范文件路径名（目录 - /x/x/，文件 - /x/x）
     */
    public static String getPathRuled(File file)
    {
        if (file == null)
        {
            return "";
        }
        if (file.isDirectory())
        {
            return file.getPath() + "/";
        }
        return file.getPath();
    }
    
    public static String getPathRuled(String file_str)
    {
        if (file_str == null)
        {
            return "";
        }
        return getPathRuled(new File(file_str));
    }
    
    public static void initMtkDoubleSim(Context context)
    {
        int simId_1;
        int simId_2;
        String imsi_1;
        String imsi_2;
        String imei_1;
        String imei_2;
        int phoneType_1;
        int phoneType_2;
        String defaultImsi;
        boolean isMtkDoubleSim;
        try
        {
            TelephonyManager tm = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> c = Class.forName("com.android.internal.telephony.Phone");
            Field fields1 = c.getField("GEMINI_SIM_1");
            fields1.setAccessible(true);
            simId_1 = (Integer) fields1.get(null);
            Field fields2 = c.getField("GEMINI_SIM_2");
            fields2.setAccessible(true);
            simId_2 = (Integer) fields2.get(null);
            Method m = TelephonyManager.class
                .getDeclaredMethod("getSubscriberIdGemini", int.class);
            imsi_1 = (String) m.invoke(tm, simId_1);
            imsi_2 = (String) m.invoke(tm, simId_2);
            Method m1 = TelephonyManager.class
                .getDeclaredMethod("getDeviceIdGemini", int.class);
            imei_1 = (String) m1.invoke(tm, simId_1);
            imei_2 = (String) m1.invoke(tm, simId_2);
            Method mx = TelephonyManager.class
                .getDeclaredMethod("getPhoneTypeGemini", int.class);
            phoneType_1 = (Integer) mx.invoke(tm, simId_1);
            phoneType_2 = (Integer) mx.invoke(tm, simId_2);
            if (TextUtils.isEmpty(imsi_1) && (!TextUtils.isEmpty(imsi_2)))
            {
                defaultImsi = imsi_2;
            }
            if (TextUtils.isEmpty(imsi_2) && (!TextUtils.isEmpty(imsi_1)))
            {
                defaultImsi = imsi_1;
            }
        }
        catch (Exception e)
        {
            isMtkDoubleSim = false;
            return;
        }
        isMtkDoubleSim = true;
    }
    
    public static void initMtkSecondDoubleSim(Context context)
    {
        int simId_1;
        int simId_2;
        String imsi_1;
        String imsi_2;
        String imei_1;
        String imei_2;
        int phoneType_1;
        int phoneType_2;
        String defaultImsi;
        boolean isMtkSecondDoubleSim;
        try
        {
            TelephonyManager tm = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> c = Class.forName("com.android.internal.telephony.Phone");
            Field fields1 = c.getField("GEMINI_SIM_1");
            fields1.setAccessible(true);
            simId_1 = (Integer) fields1.get(null);
            Field fields2 = c.getField("GEMINI_SIM_2");
            fields2.setAccessible(true);
            simId_2 = (Integer) fields2.get(null);
            Method mx = TelephonyManager.class.getMethod("getDefault",
                                                         int.class);
            TelephonyManager tm1 = (TelephonyManager) mx.invoke(tm, simId_1);
            TelephonyManager tm2 = (TelephonyManager) mx.invoke(tm, simId_2);
            imsi_1 = tm1.getSubscriberId();
            imsi_2 = tm2.getSubscriberId();
            imei_1 = tm1.getDeviceId();
            imei_2 = tm2.getDeviceId();
            phoneType_1 = tm1.getPhoneType();
            phoneType_2 = tm2.getPhoneType();
            if (TextUtils.isEmpty(imsi_1) && (!TextUtils.isEmpty(imsi_2)))
            {
                defaultImsi = imsi_2;
            }
            if (TextUtils.isEmpty(imsi_2) && (!TextUtils.isEmpty(imsi_1)))
            {
                defaultImsi = imsi_1;
            }
        }
        catch (Exception e)
        {
            isMtkSecondDoubleSim = false;
            return;
        }
        isMtkSecondDoubleSim = true;
    }
}
