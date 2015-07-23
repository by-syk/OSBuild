/**
 * @author By_syk
 */

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
import java.io.PrintWriter;
import java.util.List;

public class ExtraUtil
{
    /**
     * @param format:
     *     y year
     *     M month in the year
     *     d day
     *     h hours(12)
     *     H hours(24)
     *     m minute
     *     s second
     *     S millisecond
     *     E weekday
     *     D days in the year
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
    
    /*public static String convertMillisTime(String format)
    {
        return convertMillisTime(System.currentTimeMillis(), format);
    }*/
    
    /*public static String getCurTime()
    {
        return convertMillisTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
    }*/
    
    /**
     * Get the apk path of OSBuild, always located in /data/app/.
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
        
        if ("".equals(apk_path))
        {
            return null;
        }
        File file_s = new File(apk_path);
        File file_t = new File(context.getExternalCacheDir(),
            getVerInfo(context) + ".apk");
        //If the same file exists, just return it.
        if (file_t.exists() && file_s.length() == file_t.length())
        {
            return file_t;
        }
        fileChannelCopy(file_s, file_t);
        
        return file_t.exists() ? file_t : null;
    }
    
    /**
     * @param name App name or package name.
     */
    public static File pickUpPackage(Context context, String name)
    {
        if ("".equals(name))
        {
            return null;
        }
        
        String apk_path = "";
        String package_name = "";
        
        //Search by package name.
        if (name.contains("."))
        {
            try
            {
                PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(name, 0);
                apk_path = packageInfo.applicationInfo.publicSourceDir;
                package_name = name;
            }
            catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        //Search by app name.
        if ("".equals(apk_path))
        {
            List<PackageInfo> packages = context.getPackageManager()
                .getInstalledPackages(0);
            if (packages == null)
            {
                return null;
            }
            String temp_app_name;
            for (PackageInfo packageInfo : packages)
            {
                temp_app_name = packageInfo.applicationInfo
                    .loadLabel(context.getPackageManager()).toString();
                if (temp_app_name.equalsIgnoreCase(name))
                {
                    apk_path = packageInfo.applicationInfo.publicSourceDir;
                    package_name = packageInfo.packageName;
                    break;
                }
            }
        }
        
        if ("".equals(apk_path))
        {
            return null;
        }
        //Toast.makeText(context, apk_path, Toast.LENGTH_SHORT).show();
        File file_s = new File(apk_path);
        File file_t = new File(context.getExternalCacheDir(),
            package_name + ".apk");
        //If the same file exists, just return it.
        if (file_t.exists() && file_s.length() == file_t.length())
        {
            return file_t;
        }
        fileChannelCopy(file_s, file_t);

        return file_t.exists() ? file_t : null;
    }
    
    /**
     * Effective way to copy files by file channel.
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
            fc_in = fis.getChannel();
            fc_out = fos.getChannel();
            fc_in.transferTo(0, fc_in.size(), fc_out);
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
    
    /**
     * Read text file from its InputStream.
     */
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
    
    /**
     * Read certain text file.
     */
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
    
    /**
     * Read certain text file.
     * @param file_str The path of reading text file.
     */
    public static String readFile(String file_str)
    {
        return readFile(new File(file_str));
    }
    
    /**
     * Save text to certain file.
     * @param target_file The file to load the text.
     */
    public static boolean saveFile(File target_file, String text)
    {
        boolean result = false;
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(target_file);
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
     * @return Version info of this app, like OSBuild_v1.0(150309).
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
     * Get max, min CPU frequency.
     * Read "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq",
     * "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"
     */
    /*public static String getCPUFreq(boolean max)
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
    }*/
    
    /**
     * Get RAM size by this method if API level < 16.
     * Read "/proc/meminfo".
     */
    public static long getTotalRAM()
    {
        long result = -1;//Unit: byte
        
        String text = readFile("/proc/meminfo");
        if ("".equals(text))
        {
            return result;
        }
        
        Pattern pattern = Pattern.compile("[^0-9]");   
        Matcher matcher = pattern.matcher(text.substring(0, text.indexOf("\n")));   
        String temp = matcher.replaceAll("").trim();
        if (!"".equals(temp))
        {
            result = convertInt(temp);//Unit: KB
        }

        return result;
    }
    
    /**
     * Simplify the width-length ratio.
     * The value is integer to integer.
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
     * Simplify the width-length ratio.
     * The value is a double value.
     */
    /*public static String getWHRatio(int width, int height)
    {
        //Rounding-off method. Remain 3 decimal number.
        DecimalFormat decimalFormat = new DecimalFormat("#0.000");
        
        return decimalFormat.format((double)width / height);
    }*/
    
    /**
     * Get CPU cores.
     * Read “/sys/devices/system/cpu/cpu[0-9]"
     */
    public static int getCPUCores()
    {
        //Strategy 1.
        //int cores_runtime = Runtime.getRuntime().availableProcessors();
        
        //Strategy 2.
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

        return cores;
    }
    
    /**
     * Get storage info (RootDirectory, DataDirectory, ExternalStorageDirectory, etc.).
     * @return array like [total, available]. Unit: byte
     */
    public static long[] getStorageUsage(File storage)
    {
        long[] result = { -1, -1 };
        if (storage == null || !storage.exists())
        {
            return result;
        }
        StatFs statFs = new StatFs(storage.getPath());
        long block_size;//Unit: byte
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
        result[0] = block_size * block_count;//Unit: byte
        result[1] = block_size * avail_blocks;//Unit: byte
        return result;
    }
    
    /**
     * Read file which requires root permission.
     * Method 1: Copy it to external storage and then read it.
     */
    /*public static String readFileRoot(Context context, File source_file)
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
        PrintWriter printWriter;
        try
        {
            process = Runtime.getRuntime().exec("su");
            printWriter = new PrintWriter(process.getOutputStream());
            printWriter.println(script);
            printWriter.flush();
            printWriter.println("exit");
            printWriter.flush();
            
            //Causes the calling thread to wait for the native process
            //associated with this object to finish executing.
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
    }*/
    
    /**
     * Read file which requires root permission.
     * Method 2: Read it by command "cat" directly.
     */
    public static String readFileRoot(File source_file)
    {
        if (source_file == null)
        {
            return "";
        }
        String result = "";
        String script = "cat " + source_file;
        
        Process process = null;
        PrintWriter printWriter;
        try
        {
            //Execute command.
            process = Runtime.getRuntime().exec("su");
            printWriter = new PrintWriter(process.getOutputStream());
            printWriter.println(script);
            printWriter.flush();
            printWriter.println("exit");
            printWriter.flush();
            
            //Read file.
            result = readFile(process.getInputStream());
            
            //Causes the calling thread to wait for the native process
            //associated with this object to finish executing.
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
        
        return result;
    }
    
    /**
     * Format file name.
     * If it's a directory, modify it like "/x/x/";
     * else modify it like "/x/x".
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
    
    /**
     * Format file name.
     * If it's a directory, modify it like "/x/x/";
     * else modify it like "/x/x".
     * @param file_str The file path.
     */
    public static String getPathRuled(String file_str)
    {
        if (file_str == null)
        {
            return "";
        }
        return getPathRuled(new File(file_str));
    }
    
    /**
     * Convert integer wraped in string to int safely.
     */
    public static int convertInt(String text)
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
}
