package com.by_syk.osbuild;

import com.by_syk.osbuild.util.ExtraUtil;
import com.by_syk.osbuild.util.ConstUtil;
import com.by_syk.osbuild.util.UnitUtil;
import com.by_syk.osbuild.widget.MyTextView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import java.io.File;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.app.AlertDialog;
import android.view.ViewGroup;
import android.hardware.SensorManager;
import android.content.DialogInterface;
import android.app.ActivityManager;
import android.os.Environment;
import android.graphics.Point;
import android.content.ComponentName;
import android.content.ActivityNotFoundException;
import java.lang.reflect.Method;
import android.content.res.Configuration;
import android.os.SystemClock;
import android.view.Window;
import android.os.AsyncTask;
import android.widget.LinearLayout;
import android.view.animation.AnimationUtils;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.widget.Toast;
import java.util.Locale;
import android.content.pm.PackageManager;
import android.content.pm.FeatureInfo;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import android.content.SharedPreferences;
import java.io.IOException;
import android.os.Handler;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;import android.os.storage.StorageManager;
import java.lang.reflect.InvocationTargetException;
import android.provider.Settings;

public class MainActivity extends Activity
{
    SharedPreferences sharedPreferences;
    
    MyTextView tv_line_top;
    MyTextView tv_build;
    MyTextView tv_display;
    MyTextView tv_telephony;
    MyTextView tv_cpu;
    MyTextView tv_package;
    MyTextView tv_memory;
    MyTextView tv_sensor;
    MyTextView tv_root;
    MyTextView tv_time;
    MyTextView tv_about;
    MyTextView tv_line_bottom;
    
    StringBuilder sb_line = null;
    StringBuilder sb_build = null;
    StringBuilder sb_display = null;
    StringBuilder sb_telephony = null;
    StringBuilder sb_cpu = null;
    StringBuilder sb_memory = null;
    StringBuilder sb_package = null;
    StringBuilder sb_sensor = null;
    StringBuilder sb_root = null;
    StringBuilder sb_time = null;
    StringBuilder sb_about = null;
    
    final int SDK = Build.VERSION.SDK_INT;//系统版本
    
    final String L = "";
    final String L0 = "\n";
    final String L1 = "\n   ";
    final String L2 = "\n      ";
    //final String L3 = "\n         ";
    final String L_N = "\n";
    final String SPACE = "  ";
    
    boolean isRunning = true;//标识Activity是否活动
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //允许ActionBar或标题栏显示环形进度条
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        //统计启动次数
        stats();
        //加载数据
        (new LoadDataTask()).execute();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        isRunning = true;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        isRunning = false;
    }
    
    private void stats()
    {
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        //程序描述信息
        if (!sharedPreferences.getBoolean("not_show_about", false))
        {
            (new Handler()).postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    //避免退出程序后弹出对话框导致崩溃
                    if (isRunning)
                    {
                        aboutDialog();
                    }
                }
            }, 2000);
        }
        int launch_times = sharedPreferences.getInt("launch_times", 1);
        sharedPreferences.edit().putInt("launch_times", launch_times + 1).commit();
    }

    private class LoadDataTask extends AsyncTask<String, Integer, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            //环形进度条提示
            setProgressBarIndeterminateVisibility(true);
            //初始化组件
            init();
        }
        
        @Override
        protected String doInBackground(String[] p1)
        {
            //加载数据
            loadData();
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            //填充数据
            fillData();
            //动画
            ((LinearLayout)findViewById(R.id.ll_info)).setLayoutAnimation(AnimationUtils
                .loadLayoutAnimation(MainActivity.this, R.anim.layout_anim));
            //取消环形进度条提示
            setProgressBarIndeterminateVisibility(false);
        }
    }

    private void init()
    {
        tv_line_top = (MyTextView) findViewById(R.id.tv_line_top);
        tv_build = (MyTextView) findViewById(R.id.tv_build);
        tv_display = (MyTextView) findViewById(R.id.tv_display);
        tv_telephony = (MyTextView) findViewById(R.id.tv_telephony);
        tv_cpu = (MyTextView) findViewById(R.id.tv_cpu);
        tv_memory = (MyTextView) findViewById(R.id.tv_memory);
        tv_sensor = (MyTextView) findViewById(R.id.tv_sensor);
        tv_package = (MyTextView) findViewById(R.id.tv_package);
        tv_root = (MyTextView) findViewById(R.id.tv_root);
        tv_time = (MyTextView) findViewById(R.id.tv_time);
        tv_about = (MyTextView) findViewById(R.id.tv_about);
        tv_line_bottom = (MyTextView) findViewById(R.id.tv_line_bottom);
    }

    private void loadData()
    {
        sb_line = getDotsLine();
        sb_build = getBuildInfo();
        sb_display = getDisplayInfo();
        sb_telephony = getTelephonyInfo();
        sb_cpu = getCPUInfo();
        sb_memory = getMemoryInfo();
        sb_package = getPackageInfo();
        sb_sensor = getSensorInfo();
        sb_root = getRootInfo();
        sb_time = getTimeInfo();
        sb_about = getAboutInfo();
    }

    private void fillData()
    {
        tv_line_top.setText(sb_line);
        tv_build.setText(sb_build);
        tv_display.setText(sb_display);
        tv_telephony.setText(sb_telephony);
        tv_cpu.setText(sb_cpu);
        tv_memory.setText(sb_memory);
        tv_package.setText(sb_package);
        tv_sensor.setText(sb_sensor);
        tv_root.setText(sb_root);
        tv_time.setText(sb_time);
        tv_about.setText(sb_about);
        tv_line_bottom.setText(sb_line);
    }
    
    private StringBuilder getBuildInfo()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("android.os.Build.");
        stringBuilder.append(L1).append("ID: ").append(Build.ID);
        stringBuilder.append(L1).append("DISPLAY: ").append(Build.DISPLAY);
        stringBuilder.append(L1).append("VERSION.");
        stringBuilder.append(L2).append("RELEASE: ").append(Build.VERSION.RELEASE);
        stringBuilder.append(L2).append("SDK_INT: ").append(Build.VERSION.SDK_INT);
        stringBuilder.append(SPACE).append(ConstUtil.getSDKIntStr(Build.VERSION.SDK_INT));
        stringBuilder.append(SPACE).append(ConstUtil.getSDKIntTimeStr(Build.VERSION.SDK_INT));
        stringBuilder.append(L2).append("INCREMENTAL: ").append(Build.VERSION.INCREMENTAL);
        stringBuilder.append(L1).append("MODEL: ").append(Build.MODEL);
        stringBuilder.append(L1).append("BRAND: ").append(Build.BRAND);
        stringBuilder.append(L1).append("MANUFACTURER: ");
        if (!Build.MANUFACTURER.equals(Build.UNKNOWN))
        {
            stringBuilder.append(Build.MANUFACTURER);
        }
        stringBuilder.append(L1).append("PRODUCT: ").append(Build.PRODUCT);
        stringBuilder.append(L1).append("DEVICE: ").append(Build.DEVICE);
        stringBuilder.append(L1).append("BOARD: ");
        if (!Build.BOARD.equals(Build.UNKNOWN))
        {
            stringBuilder.append(Build.BOARD);
        }
        stringBuilder.append(L1).append("HARDWARE: ").append(Build.HARDWARE);
        if (SDK >= 9)
        {
            stringBuilder.append(L1).append("SERIAL: ");
            //获取序列号方法一
            if (!Build.SERIAL.equals(Build.UNKNOWN))
            {
                stringBuilder.append(Build.SERIAL);
            }
            //获取序列号方法二
            /*try
            {
                Class<?> c =Class.forName("android.os.SystemProperties");
                Method get =c.getMethod("get", String.class);
                stringBuilder.append((String)get.invoke(c, "ro.serialno"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }*/
        }
        stringBuilder.append(L1).append("TIME: ").append(Build.TIME);
        stringBuilder.append(SPACE).append(ExtraUtil.convertMillisTime(Build.TIME, "yyyy-MM-dd"));
        //获取内核版本
        /*String os_name = System.getProperty("os.name");
        String os_version = System.getProperty("os.version");
        String os_arch = System.getProperty("os.arch");
        stringBuilder.append(L0).append("java.lang.System.");
        stringBuilder.append(L1).append("getProperty(\"os.name\"): ");
        if (os_name != null)
        {
            stringBuilder.append(System.getProperty("os.name"));
        }
        stringBuilder.append(L1).append("getProperty(\"os.version\"): ");
        if (os_version != null)
        {
            stringBuilder.append(System.getProperty("os.version"));
        }
        stringBuilder.append(L1).append("getProperty(\"os.arch\"): ");
        if (os_arch != null)
        {
            stringBuilder.append(System.getProperty("os.arch"));
        }*/
        //获取基带版本
        /*String baseband = "";
        try
        {
            Class c = Class.forName("android.os.SystemProperties");
            Object invoker = c.newInstance();
            Method m = c.getMethod("get", new Class[] { String.class, String.class });
            Object result = m.invoke(invoker, new Object[] { "gsm.version.baseband",
                "no message" });
            baseband = (String)result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        stringBuilder.append(L0).append("android.os.SystemProperties.");
        stringBuilder.append(L1).append("get(\"gsm.version.baseband\"): ").append(baseband);*/
        
        return stringBuilder;
    }

    private StringBuilder getDisplayInfo()
    {
        //WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        //Display display = windowManager.getDefaultDisplay();
        Display display = getWindowManager().getDefaultDisplay();
        //DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        Configuration configuration = getResources().getConfiguration();
        
        int width = -1;//动态改变
        int height = -1;//动态改变
        int rotation = display.getRotation();
        float density = displayMetrics.density;
        int density_dpi = displayMetrics.densityDpi;
        int width_pixels = displayMetrics.widthPixels;
        int height_pixels = displayMetrics.heightPixels;
        float xdpi = displayMetrics.xdpi;
        float ydpi = displayMetrics.ydpi;
        int orientation = configuration.orientation;
        int sl_size_mask = configuration.screenLayout
            & Configuration.SCREENLAYOUT_SIZE_MASK;
        //int sl_long_mask = configuration.screenLayout
        //    & Configuration.SCREENLAYOUT_LONG_MASK;
        Locale locale = configuration.locale;
        /*int status_bar_height = ExtraUtil.getStatusBarHeight(this);
        int navigation_bar_height = ExtraUtil.getNavigationBarHeight(this);
        int action_bar_height = ExtraUtil.getActionBarHeight(this);*/
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("android.view.Display.");
        //真实分辨率
        if (SDK >= 17)
        {
            Point point = new Point();
            display.getRealSize(point);
            width = point.x;
            height = point.y;
            stringBuilder.append(L1).append("getRealSize().");
            stringBuilder.append(L2).append("x: ").append(width);
            stringBuilder.append(SPACE).append(UnitUtil.convertDp(width / density));
            stringBuilder.append(L2).append("y: ").append(height);
            stringBuilder.append(SPACE).append(UnitUtil.convertDp(height / density));
        }
        else if (SDK >= 13)
        {
            try
            {
                Method method_w = Display.class.getMethod("getRawWidth");
                Method method_h = Display.class.getMethod("getRawHeight");
                width = (Integer) method_w.invoke(display);
                height = (Integer) method_h.invoke(display);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            stringBuilder.append(L1).append("getRawWidth(): ");
            if (width > 0)
            {
                stringBuilder.append(width);
                stringBuilder.append(SPACE).append(UnitUtil.convertDp(width / density));
            }
            stringBuilder.append(L1).append("getRawHeight(): ");
            if (height > 0)
            {
                stringBuilder.append(height);
                stringBuilder.append(SPACE).append(UnitUtil.convertDp(height / density));
            }
        }
        else
        {
            width = display.getWidth();
            height = display.getHeight();
            stringBuilder.append(L1).append("getWidth(): ").append(width);
            stringBuilder.append(SPACE).append(UnitUtil.convertDp(width / density));
            stringBuilder.append(L1).append("getHeight(): ").append(height);
            stringBuilder.append(SPACE).append(UnitUtil.convertDp(height / density));
        }
        stringBuilder.append(L1).append("getRefreshRate(): ").append(display.getRefreshRate());
        stringBuilder.append(L1).append("getRotation(): ").append(rotation);
        stringBuilder.append(SPACE).append(ConstUtil.getRotationStr(rotation));
        
        stringBuilder.append(L0).append("android.util.DisplayMetrics.");
        stringBuilder.append(L1).append("density: ").append(density);
        stringBuilder.append(L1).append("densityDpi: ").append(density_dpi);
        stringBuilder.append(SPACE).append(ConstUtil.getDensityDPIStr(density_dpi));
        stringBuilder.append(L1).append("widthPixels: ").append(width_pixels);
        stringBuilder.append(SPACE).append(UnitUtil.convertDp(width_pixels / density));
        stringBuilder.append(L1).append("heightPixels: ").append(height_pixels);
        stringBuilder.append(SPACE).append(UnitUtil.convertDp(height_pixels / density));
        stringBuilder.append(L1).append("xdpi: ").append(displayMetrics.xdpi);
        stringBuilder.append(SPACE).append(UnitUtil.convertInch(width / xdpi));
        stringBuilder.append(L1).append("ydpi: ").append(displayMetrics.ydpi);
        stringBuilder.append(SPACE).append(UnitUtil.convertInch(height / ydpi));
        
        stringBuilder.append(L0).append("android.content.res.Configuration.");
        if (SDK >= 13)
        {
            stringBuilder.append(L1).append("screenWidthDp: ").append(configuration.screenWidthDp);
            stringBuilder.append(L1).append("screenHeightDp: ").append(configuration.screenHeightDp);
        }
        stringBuilder.append(L1).append("orientation: ").append(orientation);
        stringBuilder.append(SPACE).append(ConstUtil.getOrientationStr(orientation));
        stringBuilder.append(L1).append("fontScale: ").append(configuration.fontScale);
        stringBuilder.append(L1).append("screenLayout & 15: ").append(sl_size_mask);
        stringBuilder.append(SPACE).append(ConstUtil.getSLSizeMaskStr(sl_size_mask));
        stringBuilder.append(SPACE).append(ConstUtil.getDeviceTypeStr(sl_size_mask));
        //stringBuilder.append(L1).append("screenLayout & 48: ").append(sl_long_mask);
        //stringBuilder.append(SPACE).append(ConstUtil.getSLLongMaskStr(sl_long_mask));
        stringBuilder.append(L1).append("locale: ").append(locale);
        stringBuilder.append(SPACE).append(ConstUtil.getLocale(locale));
        //通过返回“Mobile”判断为手机，否则为平板
        /*stringBuilder.append(L0).append("WebView.");
        stringBuilder.append(L1).append(".getSettings().");
        stringBuilder.append(L2).append(".getUserAgentString(): ").append((new WebView(this))
            .getSettings().getUserAgentString());*/
        
        /*stringBuilder.append(L0).append("status_bar_height: ");
        if (status_bar_height > 0)
        {
            stringBuilder.append(status_bar_height);
            //标准：25dp
            stringBuilder.append(SPACE).append(UnitUtil.convertDp(status_bar_height / density));
        }
        if (SDK >= 11)
        {
            stringBuilder.append(L0).append("action_bar_height: ");
            if (action_bar_height > 0)
            {
                stringBuilder.append(action_bar_height);
                //标准：48dp
                stringBuilder.append(SPACE).append(UnitUtil.convertDp(action_bar_height / density));
            }
            stringBuilder.append(L0).append("navigation_bar_height: ");
            if (navigation_bar_height > 0)
            {
                stringBuilder.append(navigation_bar_height);
                //标准：48dp
                stringBuilder.append(SPACE).append(UnitUtil
                    .convertDp(navigation_bar_height / density));
            }
        }
        else
        {
            
        }*/
        
        stringBuilder.append(L0).append("Extra:");
        stringBuilder.append(L1).append("Width-height Ratio: ").append(ExtraUtil
            .getWHRatioInt(width, height));
        stringBuilder.append(SPACE).append(ConstUtil.getResolutionFormat(width, height));
        stringBuilder.append(L1).append("Diagonal Size: ").append(UnitUtil.convertInch(Math
            .sqrt(Math.pow(height / ydpi, 2) + Math.pow(width / xdpi, 2))));
        //stringBuilder.append(SPACE).append(UnitUtil.convertInch(Math.sqrt(Math
        //    .pow((double)height / density_dpi, 2) + Math.pow((double)width / density_dpi, 2))));
        //屏幕休眠时间
        /*try
        {
            float result = Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT);
            System.out.println(result);
        }
        catch (Settings.SettingNotFoundException e)
        {
            e.printStackTrace();
        }*/
        
        return stringBuilder;
    }
    
    private StringBuilder getTelephonyInfo()
    {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        int phone_type = telephonyManager.getPhoneType();
        int sim_state = telephonyManager.getSimState();
        String sim_operator = telephonyManager.getSimOperator();
        String device_id = telephonyManager.getDeviceId();
        String subscriber_id = telephonyManager.getSubscriberId();
        String sim_serial_number = telephonyManager.getSimSerialNumber();
        int network_type = telephonyManager.getNetworkType();
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("android.telephony.TelephonyManager.");
        stringBuilder.append(L1).append("getPhoneType(): ").append(phone_type);
        stringBuilder.append(SPACE).append(ConstUtil.getPhoneTypeStr(phone_type));
        stringBuilder.append(L1).append("getDeviceId(): ");
        if (!TextUtils.isEmpty(device_id))
        {
            stringBuilder.append(device_id);
            stringBuilder.append(SPACE).append(ConstUtil.getDeviceIdType(phone_type));
        }
        stringBuilder.append(L1).append("getSimState(): ").append(sim_state);
        stringBuilder.append(SPACE).append(ConstUtil.getSimStateStr(sim_state));
        if (sim_state == TelephonyManager.SIM_STATE_READY)
        {
            stringBuilder.append(L1).append("getSimOperator(): ").append(sim_operator);
            stringBuilder.append(SPACE).append(ConstUtil.getSimOperator(sim_operator));
        }
        stringBuilder.append(L1).append("getSubscriberId(): ");
        if (!TextUtils.isEmpty(subscriber_id))
        {
            stringBuilder.append(subscriber_id);
        }
        stringBuilder.append(L1).append("getSimSerialNumber(): ");
        if (!TextUtils.isEmpty(sim_serial_number))
        {
            stringBuilder.append(sim_serial_number);
        }
        stringBuilder.append(L1).append("getNetworkType(): ").append(network_type);
        stringBuilder.append(SPACE).append(ConstUtil.getNetworkTypeStr(network_type));
        
        String android_id = Settings.Secure.getString(getContentResolver(),
            Settings.Secure.ANDROID_ID);
        stringBuilder.append(L0).append("android.provider.Settings.Secure.");
        stringBuilder.append(L1).append("get(ANDROID_ID): ").append(android_id);
        
        return stringBuilder;
    }
    
    private StringBuilder getCPUInfo()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append(ExtraUtil.getCPUName());
        stringBuilder.append(L0).append(ExtraUtil.getCPUFreq(true));
        stringBuilder.append(L0).append(ExtraUtil.getCPUFreq(false));
        stringBuilder.append(L0).append(ExtraUtil.getCPUCores());
        
        return stringBuilder;
	}
    
    private StringBuilder getMemoryInfo()
    {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int gles_version = activityManager.getDeviceConfigurationInfo().reqGlEsVersion;
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        int mem_class = activityManager.getMemoryClass();//MB
        long threshold = memoryInfo.threshold;//Byte
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("android.app.ActivityManager.");
        stringBuilder.append(L1).append("getDeviceConfigurationInfo().");
        stringBuilder.append(L2).append("reqGlEsVersion: ").append(UnitUtil
            .convertBits(gles_version, 16));
        stringBuilder.append(SPACE).append(ConstUtil.getGlEsVersion(gles_version));
        stringBuilder.append(L1).append("getMemoryClass(): ").append(mem_class);
        stringBuilder.append(SPACE).append(UnitUtil.convertMemory(mem_class * 1024 * 1024));
        if (SDK >= 11)
        {
            int large_mem_class = activityManager.getLargeMemoryClass();//MB
            stringBuilder.append(L1).append("getLargeMemoryClass(): ").append(large_mem_class);
            stringBuilder.append(SPACE).append(UnitUtil
                .convertMemory(large_mem_class * 1024 * 1024));
        }
        stringBuilder.append(L1).append("MemoryInfo.");
        stringBuilder.append(L2).append("threshold: ").append(threshold);
        stringBuilder.append(SPACE).append(UnitUtil.convertMemory(threshold));
        if (SDK >= 16)
        {
            long total_mem = memoryInfo.totalMem;//Byte
            stringBuilder.append(L2).append("totalMem: ").append(total_mem);
            stringBuilder.append(SPACE).append(UnitUtil.convertMemory(total_mem));
        }
        else
        {
            stringBuilder.append(L0).append(ExtraUtil.getTotalRAM());
        }
        
        stringBuilder.append(L0).append("android.os.Environment.");
        File dir = Environment.getRootDirectory();
        long[] usage = ExtraUtil.getStorageUsage(dir);
        stringBuilder.append(L1).append("getRootDirectory(): ").append(ExtraUtil.getPathRuled(dir));
        stringBuilder.append(L2).append("Total Size: ").append(usage[0]);
        stringBuilder.append(SPACE).append(UnitUtil.convertMemory(usage[0]));
        stringBuilder.append(L2).append("Available Size: ").append(usage[1]);
        stringBuilder.append(SPACE).append(UnitUtil.convertMemory(usage[1]));
        stringBuilder.append(SPACE).append(UnitUtil.convertPercent(usage));
        dir = Environment.getDataDirectory();
        usage = ExtraUtil.getStorageUsage(dir);
        stringBuilder.append(L1).append("getDataDirectory(): ").append(ExtraUtil.getPathRuled(dir));
        stringBuilder.append(L2).append("Total Size: ").append(usage[0]);
        stringBuilder.append(SPACE).append(UnitUtil.convertMemory(usage[0]));
        stringBuilder.append(L2).append("Available Size: ").append(usage[1]);
        stringBuilder.append(SPACE).append(UnitUtil.convertMemory(usage[1]));
        stringBuilder.append(SPACE).append(UnitUtil.convertPercent(usage));
        String state = Environment.getExternalStorageState();
        stringBuilder.append(L1).append("getExternalStorageState(): ").append(state);
        stringBuilder.append(SPACE).append(ConstUtil.getExternalStorageState(state));
        if (SDK >= 9)
        {
            stringBuilder.append(L1).append("isExternalStorageRemovable(): ").append(Environment
                .isExternalStorageRemovable());
        }
        stringBuilder.append(L1).append("getExternalStorageDirectory(): ");
        if (state.equals(Environment.MEDIA_MOUNTED))
        {
            dir = Environment.getExternalStorageDirectory();
            usage = ExtraUtil.getStorageUsage(Environment.getExternalStorageDirectory());
            stringBuilder.append(ExtraUtil.getPathRuled(dir));
            stringBuilder.append(L2).append("Total Size: ");
            stringBuilder.append(usage[0]);
            stringBuilder.append(SPACE).append(UnitUtil.convertMemory(usage[0]));
            stringBuilder.append(L2).append("Available Size: ");
            stringBuilder.append(usage[1]);
            stringBuilder.append(SPACE).append(UnitUtil.convertMemory(usage[1]));
            stringBuilder.append(SPACE).append(UnitUtil.convertPercent(usage));
        }
        else
        {
            stringBuilder.append(L2).append("Total Size: ");
            stringBuilder.append(L2).append("Available Size: ");
        }
        //通过反射获取存储器列表
        if (SDK >= 9)
        {
            stringBuilder.append(L0).append("android.os.storage.StorageManager.");
            stringBuilder.append(L1).append("getVolumePaths().");
            StorageManager storageManager = (StorageManager)
                getSystemService(STORAGE_SERVICE);
            try
            {
                Method method = storageManager.getClass().getMethod("getVolumePaths");
                String[] paths = (String[]) method.invoke(storageManager);
                File temp_file;
                for (String path : paths)
                {
                    temp_file = new File(path);
                    stringBuilder.append(L2).append(ExtraUtil.getPathRuled(path));
                    stringBuilder.append(SPACE).append(temp_file.exists()
                                                       && temp_file.canRead());
                }
            }
            catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        
        return stringBuilder;
	}
    
    private StringBuilder getPackageInfo()
    {
        PackageManager packageManager = getPackageManager();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("android.content.pm.PackageManager.");
        stringBuilder.append(L1).append("getSystemAvailableFeatures():");

        List<String> features = new ArrayList<String>();
        String temp;
        for (FeatureInfo featureInfo : packageManager.getSystemAvailableFeatures())
        {
            temp = featureInfo.name;
            if (temp != null)
            {
                features.add(temp);
            }
        }
        Collections.sort(features);
        for (String feature : features)
        {
            stringBuilder.append(L2).append(feature);
            stringBuilder.append(SPACE).append(ConstUtil.getFeature(feature));
        }

        return stringBuilder;
	}

    private StringBuilder getSensorInfo()
    {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("android.hardware.SensorManager.");
        stringBuilder.append(L1).append("getSensorList(Sensor.TYPE_ALL):");

        for (int i = 1; i <= 21; ++ i)
        {
            if (sensorManager.getDefaultSensor(i) != null)
            {
                stringBuilder.append(L2).append(i);
                stringBuilder.append(SPACE).append(ConstUtil.getSensorTypeStr(i));
            }
        }

        return stringBuilder;
	}

    private StringBuilder getRootInfo()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("SuperSu:");
        
        final String[] SU_PATHS = { "/system/bin/su", "/system/xbin/su",
            "/system/sbin/su", "/sbin/su", "/vendor/bin/su" };
        for (String path : SU_PATHS)
        {
            if ((new File(path)).exists())
            {
                stringBuilder.append(L1).append(path);
                stringBuilder.append(SPACE).append("true");
            }
        }

        return stringBuilder;
    }
    
    /*private StringBuilder getSecurityInfo()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("java.secirity.Security.");
        stringBuilder.append(L1).append("getProviders():");
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers)
        {
            stringBuilder.append(L2).append(provider.getName());
            stringBuilder.append(L3).append("Version: ").append(provider.getVersion());
            stringBuilder.append(L3).append("Info: ").append(provider.getInfo());
        }
        stringBuilder.append(L1).append("getAlgorithms(\"MessageDigest\"):");
        for (String algorithm : Security.getAlgorithms("MessageDigest"))
        {
            stringBuilder.append(L2).append(algorithm);
        }
        stringBuilder.append(L1).append("getAlgorithms(\"KeyPairGenerator\"):");
        for (String algorithm : Security.getAlgorithms("KeyPairGenerator"))
        {
            stringBuilder.append(L2).append(algorithm);
        }
        
        return stringBuilder;
    }*/
    
    private StringBuilder getTimeInfo()
    {
        //当前时间
        long current_time = System.currentTimeMillis();
        //开机时间
        long elapsed_realtime = SystemClock.elapsedRealtime();
        //唤醒时间
        long uptime_millis = SystemClock.uptimeMillis();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("java.lang.System.");
        stringBuilder.append(L1).append("currentTimeMillis(): ").append(current_time);
        stringBuilder.append(SPACE).append(ExtraUtil.convertMillisTime(current_time));
        stringBuilder.append(L0).append("android.os.SystemClock.");
        stringBuilder.append(L1).append("elapsedRealtime(): ").append(elapsed_realtime);
        stringBuilder.append(SPACE).append(UnitUtil.convertTime(elapsed_realtime));
        stringBuilder.append(L1).append("uptimeMillis(): ").append(uptime_millis);
        stringBuilder.append(SPACE).append(UnitUtil.convertTime(uptime_millis));
        stringBuilder.append(SPACE).append(UnitUtil.convertPercent(elapsed_realtime, uptime_millis));

        return stringBuilder;
    }
    
    private StringBuilder getAboutInfo()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("About:");
        stringBuilder.append(L1).append(ExtraUtil.getVerInfo(this));
        stringBuilder.append(L1).append("Typeface: Monaco.ttf");
        stringBuilder.append(L1).append("Developer: By_syk");
        stringBuilder.append(L1).append(getString(R.string.copyright));

        return stringBuilder;
    }
    /**
     * 标尺线（宽度为100字符）
     */
    private StringBuilder getDotsLine()
    {
        //final String LAUNCH_TIMES = String.valueOf(sharedPreferences.getInt("launch_times", 1));

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 10; ++ i)
        {
            stringBuilder.append(i).append("+++++++++");
        }
        //stringBuilder.replace(stringBuilder.length() - LAUNCH_TIMES.length(),
        //    stringBuilder.length(), LAUNCH_TIMES);
        
        return stringBuilder;
    }
    /**
     * Share the Page
     * 弹出对话框询问是否将“Telephony”信息加入文本中
     */
    private void shareTextDialog()
    {
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_file, null);
        ((TextView) viewGroup.findViewById(R.id.tv_file)).setText(sb_telephony);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle(R.string.dia_title_with_tel)
            .setView(viewGroup)
            .setPositiveButton(R.string.dia_pos_yes, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    shareText(true);
                }
            })
            .setNegativeButton(R.string.dia_neg_no, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    shareText(false);
                }
            })
            .create();
        alertDialog.show();
    }
    /**
     * 分享文本
     */
    private void shareText(boolean with_telephony)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(sb_line);
        stringBuilder.append(L0).append(L_N).append(sb_build);
        stringBuilder.append(L0).append(L0).append(L_N).append(sb_display);
        if (with_telephony)
        {
            stringBuilder.append(L0).append(L0).append(L_N).append(sb_telephony);
        }
        stringBuilder.append(L0).append(L0).append(L_N).append(sb_cpu);
        stringBuilder.append(L0).append(L0).append(L_N).append(sb_memory);
        stringBuilder.append(L0).append(L0).append(L_N).append(sb_package);
        stringBuilder.append(L0).append(L0).append(L_N).append(sb_sensor);
        stringBuilder.append(L0).append(L0).append(L_N).append(sb_root);
        stringBuilder.append(L0).append(L0).append(L_N).append(sb_time);
        stringBuilder.append(L0).append(L0).append(L_N).append(sb_about);
        stringBuilder.append(L0).append(L_N).append(sb_line);
        //文件名：设备信息__附加信息__应用信息.info.txt
        final String FILE_NAME = with_telephony ? "%1$s__wT__%2$s.info.txt" : "%1$s__%2$s.info.txt";
        String device = String.format("%1$s_%2$s", Build.MODEL, SDK);
        String app = ExtraUtil.getVerInfo(this);
        String file_target_name = String.format(FILE_NAME, device, app);
        final File FILE_TARGET = new File(getExternalCacheDir(), file_target_name);

        if (ExtraUtil.saveFile(FILE_TARGET, stringBuilder.toString().trim()))
        {
            share(FILE_TARGET);
        }
    }
    /**
     * 发送指定文件
     */
    private void share(File file)
    {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra("subject", file.getName());
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("*/*");
        startActivity(Intent.createChooser(intent,
            getString(R.string.share_via)));
    }
    /**
     * Share OSBuild
     * 分享本程序
     */
    private void shareApp()
    {
        File file = ExtraUtil.pickUpMyPackage(this);
        if (file != null)
        {
            share(file);
        }
    }
    /**
     * Constant Reference
     * 相关常量列表对话框
     */
    private void constDialog()
    {
        final String[] CONST_FILES = { "Build Module", "Display Module",
            "Telephony Module", "Memory Module", "Package Module", "Sensor Module" };
        final int[] CONST_FILES_ID = { R.raw.build, R.raw.display,
            R.raw.tel, R.raw.mem, R.raw.pkg, R.raw.sensor };
        
        AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle(R.string.dia_title_const)
            .setItems(CONST_FILES, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    showTextFileDialog(CONST_FILES_ID[p2], CONST_FILES[p2]);
                }
            })
            .setNegativeButton(R.string.dia_neg_cancel, null)
            .create();
        alertDialog.show();
    }
    /**
     * 显示指定程序内置文件内容对话框
     * @param file_id 文本文件资源ID
     * @param title 对话框标题（文件名）
     */
    private void showTextFileDialog(int file_id, String title)
    {
        String text = ExtraUtil.readFile(getResources().openRawResource(file_id));

        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_file, null);
        ((MyTextView) viewGroup.findViewById(R.id.tv_file)).setText(text);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle(title)
            .setView(viewGroup)
            .setPositiveButton(R.string.dia_pos_ok, null)
            .create();
        alertDialog.show();
    }
    /**
     * View System Files
     * 显示支持读取的系统文件列表对话框
     * （包括一些需要ROOT权限的文件）
     */
    private void sysFilesDialog()
    {
        final ArrayList<String> LIST_TITLE = new ArrayList<>();
        final ArrayList<String> LIST_PATH = new ArrayList<>();
        //WiFi密码
        LIST_TITLE.add("wpa_supplicant.conf");
        LIST_PATH.add("/data/misc/wifi/wpa_supplicant.conf");
        //开机时间（仅MTK）
        String boot_proc = "/proc/bootprof";
        if ((new File(boot_proc).exists()))
        {
            LIST_TITLE.add("bootprof");
            LIST_PATH.add("/proc/bootprof");
        }
        //CPU
        LIST_TITLE.add("cpuinfo");
        LIST_PATH.add("/proc/cpuinfo");
        //Memory
        LIST_TITLE.add("meminfo");
        LIST_PATH.add("/proc/meminfo");
        //内核版本
        /*LIST_TITLE.add("version");
        LIST_PATH.add("/proc/version");*/
        //CPU可用频率
        /*LIST_TITLE.add("scaling_available_frequencies");
        LIST_PATH.add("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies");*/
        //系统信息
        LIST_TITLE.add("build.prop");
        LIST_PATH.add("/system/build.prop");
        
        AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle(R.string.dia_title_sys_files)
            .setItems(LIST_TITLE.toArray(new String[LIST_TITLE.size()]),
                new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    askSUDialog(LIST_PATH.get(p2));
                }
            })
            .setNegativeButton(R.string.dia_neg_cancel, null)
            .create();
        alertDialog.show();
    }
    /**
     * 弹出对话框询问是否继续读取需ROOT权限的文件
     * 不需要ROOT权限则跳过
     */
    private void askSUDialog(final String FILE_STR)
    {
        File file = new File(FILE_STR);
        if (file.canRead())//不需要ROOT权限
        {
            showTextFileDialog(FILE_STR, false);
            return;
        }
        else if (sharedPreferences.getBoolean("not_show_su", false))
        {
            showTextFileDialog(FILE_STR, true);
            return;
        }
        
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_text_ask, null);
        ((MyTextView) viewGroup.findViewById(R.id.tv_desc)).setText(R.string.dia_su_desc);

        ((CheckBox) viewGroup.findViewById(R.id.cb_not_show))
            .setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton p1, boolean p2)
            {
                sharedPreferences.edit()
                    .putBoolean("not_show_su", p2).commit();
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle(R.string.dia_title_warning)
            .setView(viewGroup)
            .setPositiveButton(R.string.dia_pos_continue,
            new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    showTextFileDialog(FILE_STR, true);
                }
            })
            .setNegativeButton(R.string.dia_neg_cancel, null)
            .create();
        alertDialog.show();
    }
    /**
     * 显示指定（系统）文件内容对话框
     * @param file_str 文件路径
     * @param need_su 是否需要ROOT权限
     */
    private void showTextFileDialog(String file_str, boolean need_su)
    {
        File file = new File(file_str);
        StringBuilder stringBuilder = new StringBuilder(file_str);
        //文件最后修改时间
        //stringBuilder.append(SPACE).append(ExtraUtil.convertMillisTime(file.lastModified()));
        stringBuilder.append("\n\n");
        if (need_su)//需要ROOT权限
        {
            stringBuilder.append(ExtraUtil.readFileRoot(this, file));
        }
        else//不需要ROOT权限
        {
            stringBuilder.append(ExtraUtil.readFile(file));
        }
        
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_file, null);
        ((MyTextView) viewGroup.findViewById(R.id.tv_file)).setText(stringBuilder);
        
        AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle(file.getName())
            .setView(viewGroup)
            .setPositiveButton(R.string.dia_pos_ok, null)
            .create();
        alertDialog.show();
    }
    /**
     * System Settings
     * 快捷系统设置列表对话框
     */
    private void sysSettingsDialog()
    {
        final ArrayList<String> LIST_TITLE = new ArrayList<>();
        final ArrayList<String> LIST_PKG = new ArrayList<>();
        final ArrayList<String> LIST_CLASS = new ArrayList<>();
        //开发者选项
        LIST_TITLE.add("Development");
        LIST_PKG.add("com.android.settings");
        LIST_CLASS.add("com.android.settings.DevelopmentSettings");
        //硬件检测（仅MIUI）
        if (Build.MANUFACTURER.equals("Xiaomi"))
        {
            LIST_TITLE.add("MIUI Cit");
            LIST_PKG.add("com.miui.cit");
            LIST_CLASS.add("com.miui.cit.CitLauncherActivity");
        }
        //测试
        LIST_TITLE.add("Testing");
        LIST_PKG.add("com.android.settings");
        LIST_CLASS.add("com.android.settings.TestingSettings");
        
        AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle(R.string.dia_title_sys_settings)
            .setItems(LIST_TITLE.toArray(new String[LIST_TITLE.size()]),
                new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    gotoSysSettings(LIST_PKG.get(p2), LIST_CLASS.get(p2));
                }
            })
            .setNegativeButton(R.string.dia_neg_cancel, null)
            .create();
        alertDialog.show();
    }
    /**
     * 通过Intent启动指定Activity
     * @param package_name 包名
     * @param class_name 类名
     */
    private void gotoSysSettings(String package_name, String class_name)
    {
        try
        {
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(package_name, class_name);
            intent.setComponent(componentName);
            intent.setAction(Intent.ACTION_VIEW);
            startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Donate
     * 捐赠对话框（询问是否反馈一份设备信息给开发者）
     * 项目开源链接置于此对话框
     */
    private void donateDialog()
    {
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_text, null);
        ((TextView) viewGroup.findViewById(R.id.tv_text)).setText(R.string.donate_desc);
        
        AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle(R.string.dia_title_donate)
            .setView(viewGroup)
            .setPositiveButton(R.string.dia_pos_ok, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    if (SDK >= 11)
                    {
                        ClipboardManager clipboardManager = (ClipboardManager)
                            getSystemService(CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("email", getString(R.string.my_email));
                        clipboardManager.setPrimaryClip(clipData);
                    }
                    else
                    {
                        android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager)
                            getSystemService(CLIPBOARD_SERVICE);
                        clipboardManager.setText(getString(R.string.my_email));
                    }
                    Toast.makeText(MainActivity.this, R.string.copied, Toast.LENGTH_SHORT).show();
                }
            })
            .setNeutralButton(R.string.dia_neu_github, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/by-syk/OSBuild"));
                    startActivity(intent);
                }
            })
            .setNegativeButton(R.string.dia_neg_cancel, null)
            .create();
        alertDialog.show();
    }
    /**
     * 启动程序时弹出对话框显示描述信息，帮助用户了解程序用途。
     */
    private void aboutDialog()
    {
        String text = "";
        try
        {
            text = ExtraUtil.readFile(getAssets().open("desc.txt"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_text_ask, null);
        ((MyTextView) viewGroup.findViewById(R.id.tv_desc)).setText(text);
        
        ((CheckBox) viewGroup.findViewById(R.id.cb_not_show))
            .setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton p1, boolean p2)
            {
                sharedPreferences.edit()
                    .putBoolean("not_show_about", p2).commit();
            }
        });
        
        AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle(R.string.dia_title_about)
            .setView(viewGroup)
            .setPositiveButton(R.string.dia_pos_ok, null)
            .create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_sub_share_text:
                shareTextDialog();
                return true;
            case R.id.action_sub_share_app:
                shareApp();
                return true;
            case R.id.action_const:
                constDialog();
                return true;
            case R.id.action_sys_files:
                sysFilesDialog();
                return true;
            case R.id.action_sys_settings:
                sysSettingsDialog();
                return true;
            case R.id.action_donate:
                donateDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
