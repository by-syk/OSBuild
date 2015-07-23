/**
 * There are 11 modules:
 *     Primer Module
 *     Build Module
 *     Display Module
 *     Telephony Module
 *     CPU Module
 *     Memory Module
 *     Package Module
 *     Sensor Module
 *     Superuser Module
 *     Time Module
 *     About Module
 * @author By_syk
 */

package com.by_syk.osbuild;

import com.by_syk.osbuild.util.ExtraUtil;
import com.by_syk.osbuild.util.ConstUtil;
import com.by_syk.osbuild.util.UnitUtil;
import com.by_syk.osbuild.widget.MyTextView;
import com.by_syk.osbuild.kube.Kube;
import com.by_syk.osbuild.compass.Compass;

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
import java.util.Locale;
import android.content.pm.PackageManager;
import android.content.pm.FeatureInfo;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.os.storage.StorageManager;
import android.provider.Settings;
import java.util.Map;
import java.util.HashMap;
import android.hardware.Sensor;
import android.annotation.TargetApi;
import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;
import android.widget.RelativeLayout;
import android.widget.EditText;
import android.view.View.OnLongClickListener;
import android.view.View;
import android.widget.Toast;
import android.view.SubMenu;
import android.hardware.ConsumerIrManager;
import android.nfc.NfcManager;
import android.nfc.NfcAdapter;

public class MainActivity extends Activity
{
    SharedPreferences sharedPreferences;
    
    MyTextView tv_line_top;
    MyTextView tv_primer;
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
    
    //Map for Primer Module
    Map<String, String> map_primer = null;
    
    StringBuilder sb_line = null;
    StringBuilder sb_primer = null;
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
    
    //Android Version
    final int SDK = Build.VERSION.SDK_INT;
    
    final String L = "";
    final String L0 = "\n";
    final String L1 = "\n   ";
    final String L2 = "\n      ";
    //final String L3 = "\n         ";
    final String L_N = "\n";
    final String SPACE = "  ";
    
    final String TRUE = "TRUE";
    final String FALSE = "FALSE";
    final String ON = "ON";
    final String OFF = "OFF";
    final String YES = "Yes";//For Primer Module
    final String NO = "No";//For Primer Module
    final String UNKNOWN = "Unknown";//For Primer Module
    
    //Mark the status of current Activity, running or not.
    boolean isRunning = true;
    
    GLSurfaceView gLSurfaceView = null;
    GLSurfaceView.Renderer glsvRenderer = new GLSurfaceView.Renderer()
    {
        @Override
        public void onSurfaceCreated(GL10 p1, EGLConfig p2)
        {
            //Get GPU info and save them.
            sharedPreferences.edit()
                .putString("gl_renderer", p1.glGetString(GL10.GL_RENDERER))
                .putString("gl_vendor", p1.glGetString(GL10.GL_VENDOR))
                .putString("gl_version", p1.glGetString(GL10.GL_VERSION))
                .commit();
            
            //Remove useless view.
            runOnUiThread(new Runnable()
            {
                @TargetApi(11)
                @Override
                public void run()
                {
                    //((RelativeLayout)findViewById(R.id.rl_parent))
                    //    .removeView(gLSurfaceView);
                    if (SDK >= 11)
                    {
                        recreate();
                    }
                    else
                    {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public void onSurfaceChanged(GL10 p1, int p2, int p3)
        {}

        @Override
        public void onDrawFrame(GL10 p1)
        {}
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //Indeterminate progress can be showed on the ActionBar or TitleBar.
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        setContentView(R.layout.activity_main);
        
        //Count and log times of launching.
        stats();
        
        //Load data in another thread.
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
    
    /**
     * Count and log times of launching.
     */
    private void stats()
    {
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        
        //To show dialog with description of OSBuild after 2 seconds.
        if (!sharedPreferences.getBoolean("not_show_about", false))
        {
            (new Handler()).postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    //Check if the app is running to avoid crashing.
                    if (isRunning)
                    {
                        //Intent intent = new Intent(MainActivity.this, Kube.class);
                        //startActivity(intent);
                        appDescDialog();
                    }
                }
            }, 2000);
        }
        
        //Log how many times the app was launched.
        //Count from 0. Because launcing the app firstly will be counted twice.
        int launch_times = sharedPreferences.getInt("launch_times", -1);
        sharedPreferences.edit().putInt("launch_times", launch_times + 1).commit();
    }
    
    private class LoadDataTask extends AsyncTask<String, Integer, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            
            //Show round progress bar on the ActionBar.
            //Notice that, it doesn't work on Android 5.0 and above.
            //From the development team:
            //"...this is currently working as intended as the progress bar features are not supported
            //on Material action bars. This should throw an exception if you try to use them."
            setProgressBarIndeterminateVisibility(true);
            
            //Get and save GPU info.
            prepareGPUInfo();
            
            //Initialize text views.
            init();
        }
        
        @Override
        protected String doInBackground(String[] p1)
        {
            //Load data.
            loadData();
            
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            
            //Fill data to text views.
            fillData();
            
            //Show animation.
            ((LinearLayout)findViewById(R.id.ll_info)).setLayoutAnimation(AnimationUtils
                .loadLayoutAnimation(MainActivity.this, R.anim.layout_anim));
            
            //Hide round progress bar on the ActionBar.
            setProgressBarIndeterminateVisibility(false);
        }
    }

    /**
     * Get and save GPU info.
     */
    private void prepareGPUInfo()
    {
        if (!sharedPreferences.contains("gl_renderer"))
        {
            gLSurfaceView = new GLSurfaceView(this);
            gLSurfaceView.setRenderer(glsvRenderer);
            ((RelativeLayout)findViewById(R.id.rl_parent)).addView(gLSurfaceView);
        }
    }

    private void init()
    {
        tv_line_top = (MyTextView) findViewById(R.id.tv_line_top);
        tv_primer = (MyTextView) findViewById(R.id.tv_primer);
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
        //Init Map for Primer Module
        map_primer = new HashMap<>();
        
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
        
        //Load data for Primer Module at last, because it is from the others above.
        sb_primer = getPrimerInfo();
    }

    private void fillData()
    {
        tv_line_top.setText(sb_line);
        tv_primer.setText(sb_primer);
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
    
    /**
     * Build Module
     */
    @SuppressLint("NewApi")
    private StringBuilder getBuildInfo()
    {
        String unknown = Build.UNKNOWN;
        String model = Build.MODEL.equals(unknown) ? "" : Build.MODEL;
        String brand = Build.BRAND.equals(unknown) ? "" : Build.BRAND;
        String release = Build.VERSION.RELEASE;
        String manufacturer = Build.MANUFACTURER.equals(unknown) ? "" : Build.MANUFACTURER;
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("android.os.Build.");
        stringBuilder.append(L1).append("ID: ").append(Build.ID);
        stringBuilder.append(L1).append("DISPLAY: ").append(Build.DISPLAY);
        stringBuilder.append(L1).append("VERSION.");
        stringBuilder.append(L2).append("RELEASE: ").append(release);
        stringBuilder.append(L2).append("SDK_INT: ").append(SDK);
        stringBuilder.append(SPACE).append(ConstUtil.getSDKIntStr(Build.VERSION.SDK_INT));
        stringBuilder.append(SPACE).append(ConstUtil.getSDKIntTimeStr(Build.VERSION.SDK_INT));
        stringBuilder.append(L2).append("INCREMENTAL: ").append(Build.VERSION.INCREMENTAL);
        stringBuilder.append(L1).append("MODEL: ").append(model);
        stringBuilder.append(L1).append("BRAND: ").append(brand);
        stringBuilder.append(L1).append("MANUFACTURER: ").append(manufacturer);
        stringBuilder.append(L1).append("PRODUCT: ").append(Build.PRODUCT.equals(unknown)
            ? "" : Build.PRODUCT);
        stringBuilder.append(L1).append("DEVICE: ").append(Build.DEVICE.equals(unknown)
            ? "" : Build.DEVICE);
        stringBuilder.append(L1).append("BOARD: ").append(Build.BOARD.equals(unknown)
            ? "" : Build.BOARD);
        stringBuilder.append(L1).append("HARDWARE: ").append(Build.HARDWARE.equals(unknown)
            ? "" : Build.HARDWARE);
        if (SDK >= 21)
        {
            stringBuilder.append(L1).append("SUPPORTED_ABIS:");
            for (String supported_abi : Build.SUPPORTED_ABIS)
            {
                stringBuilder.append(" ").append(supported_abi);
            }
        }
        if (SDK >= 9)
        {
            stringBuilder.append(L1).append("SERIAL: ").append(Build.SERIAL.equals(unknown)
                ? "" : Build.SERIAL);
        }
        stringBuilder.append(L1).append("TIME: ").append(Build.TIME);
        stringBuilder.append(SPACE).append(ExtraUtil.convertMillisTime(Build.TIME, "yyyy-MM-dd"));
        
        stringBuilder.append(L0).append("android.provider.Settings.Secure.");
        stringBuilder.append(L1).append("get(ANDROID_ID): ").append(Settings.Secure
            .getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        
        //String os_name = System.getProperty("os.name");
        String os_version = System.getProperty("os.version");
        //String os_arch = System.getProperty("os.arch");
        String vm_version = System.getProperty("java.vm.version");
        
        stringBuilder.append(L0).append("java.lang.System.");
        /*stringBuilder.append(L1).append("getProperty(\"os.name\"): ");
        if (os_name != null)
        {
            stringBuilder.append(System.getProperty("os.name"));
        }*/
        stringBuilder.append(L1).append("getProperty(\"os.version\"): ");
        if (os_version != null)
        {
            stringBuilder.append(System.getProperty("os.version"));
        }
        /*stringBuilder.append(L1).append("getProperty(\"os.arch\"): ");
        if (os_arch != null)
        {
            stringBuilder.append(System.getProperty("os.arch"));
        }*/
        stringBuilder.append(L1).append("getProperty(\"java.vm.version\"): ");
        if (vm_version != null)
        {
            stringBuilder.append(System.getProperty("java.vm.version"));
        }
        
        //Get baseband.
        /*stringBuilder.append(L0).append("android.os.SystemProperties.");
        stringBuilder.append(L1).append("get(\"gsm.version.baseband\"): ");
        try
        {
            Class c = Class.forName("android.os.SystemProperties");
            Object invoker = c.newInstance();
            Method m = c.getMethod("get", new Class[] { String.class, String.class });
            Object result = m.invoke(invoker, new Object[] { "gsm.version.baseband",
                "no message" });
            stringBuilder.append(result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
        
        //Add data for Primer Module
        map_primer.put("model", model);
        if (brand.equals(manufacturer))
        {
            map_primer.put("brand", brand);
        }
        else
        {
            map_primer.put("brand", String.format("%1$s (%2$s)", brand,
                "".equals(manufacturer) ? UNKNOWN : manufacturer));
        }
        map_primer.put("version", "Android " + release);
        
        return stringBuilder;
    }
    
    /**
     * Display Module
     */
    @SuppressLint("NewApi")
    private StringBuilder getDisplayInfo()
    {
        Display display = getWindowManager().getDefaultDisplay();
        //Another way to get Display.
        /*WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();*/
        
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        //Another way to get DisplayMetrics.
        //DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        
        //The values of width and height will be exchanged when device is rotating.
        int width = -1;
        int height = -1;
        
        int rotation = display.getRotation();
        
        float density = displayMetrics.density;
        int density_dpi = displayMetrics.densityDpi;
        int width_pixels = displayMetrics.widthPixels;
        int height_pixels = displayMetrics.heightPixels;
        float xdpi = displayMetrics.xdpi;
        float ydpi = displayMetrics.ydpi;
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("android.view.Display.");
        //Get real physical resolution.
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
        
        Configuration configuration = getResources().getConfiguration();

        int orientation = configuration.orientation;
        int sl_size_mask = configuration.screenLayout
            & Configuration.SCREENLAYOUT_SIZE_MASK;
        Locale locale = configuration.locale;
        
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
        stringBuilder.append(L1).append("locale: ").append(locale);
        stringBuilder.append(SPACE).append(ConstUtil.getLocale(locale));
        
        stringBuilder.append(L0).append("Extra:");
        stringBuilder.append(L1).append("Width-height Ratio: ").append(ExtraUtil
            .getWHRatioInt(width, height));
        stringBuilder.append(SPACE).append(ConstUtil.getResolutionFormat(width, height));
        stringBuilder.append(L1).append("Diagonal Size: ").append(UnitUtil.convertInch(Math
            .sqrt(Math.pow(height / ydpi, 2) + Math.pow(width / xdpi, 2))));
        
        //Add data for Primer Module
        map_primer.put("resolution", width + "x" + height);
        
        return stringBuilder;
    }
    
    /**
     * Telephony Module
     */
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
            stringBuilder.append(SPACE).append(ConstUtil
                .getDeviceIdType(phone_type, device_id.length()));
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
        
        //Add data for Primer Module
        map_primer.put("imei", TextUtils.isEmpty(device_id) ? UNKNOWN : device_id);
        map_primer.put("imsi", TextUtils.isEmpty(subscriber_id) ? UNKNOWN : subscriber_id);
        
        return stringBuilder;
    }
    
    /**
     * CPU Module
     */
    private StringBuilder getCPUInfo()
    {
        String text = ExtraUtil.readFile("/proc/cpuinfo");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("/proc/cpuinfo");
        //There is not a string “Processor” in few devices.
        if (text.contains("model name"))
        {
            stringBuilder.append(L1).append("model name: ");
            int index = text.indexOf("model name");
            stringBuilder.append(text.substring(index + 13, text.indexOf("\n", index)));
        }
        else
        {
            stringBuilder.append(L1).append("Processor: ");
            if (text.contains("Processor"))
            {
                int index = text.indexOf("Processor");
                stringBuilder.append(text.substring(index + 12, text.indexOf("\n", index)));
            }
        }
        
        //There is not a string “Hardware” in few devices.
        if (text.contains("vendor_id"))
        {
            stringBuilder.append(L1).append("vendor_id: ");
            int index = text.indexOf("vendor_id");
            stringBuilder.append(text.substring(index + 12, text.indexOf("\n", index)));
        }
        else
        {
            stringBuilder.append(L1).append("Hardware: ");
            if (text.contains("Hardware"))
            {
                int index = text.indexOf("Hardware");
                stringBuilder.append(text.substring(index + 11, text.indexOf("\n", index)));
            }
        }
        
        final String CPU_PATH = "/sys/devices/system/cpu/";
        final String CPU0_PATH = CPU_PATH + "cpu0/cpufreq/";
        final String CPUINFO_MIN_FREQ = "cpuinfo_min_freq";
        final String CPUINFO_MAX_FREQ = "cpuinfo_max_freq";
        final String SCALING_GOVERNOR = "scaling_governor";
        
        int cores = ExtraUtil.getCPUCores();
        int min_freq = ExtraUtil.convertInt(ExtraUtil
            .readFile(CPU0_PATH + CPUINFO_MIN_FREQ));
        int max_freq = ExtraUtil.convertInt(ExtraUtil
            .readFile(CPU0_PATH + CPUINFO_MAX_FREQ));
        String governor = ExtraUtil.readFile(CPU0_PATH + SCALING_GOVERNOR).trim();
        
        stringBuilder.append(L0).append(CPU_PATH);
        stringBuilder.append(L1).append("cpu[0-9]/  ").append(cores);
        stringBuilder.append(L1).append("cpu0/cpufreq/");
        stringBuilder.append(L2).append(CPUINFO_MIN_FREQ).append(": ");
        if (min_freq != -1)
        {
            stringBuilder.append(min_freq);
            stringBuilder.append(SPACE).append(UnitUtil.convertFreq(min_freq));
        }
        stringBuilder.append(L2).append(CPUINFO_MAX_FREQ).append(": ");
        if (max_freq != -1)
        {
            stringBuilder.append(max_freq);
            stringBuilder.append(SPACE).append(UnitUtil.convertFreq(max_freq));
        }
        stringBuilder.append(L2).append(SCALING_GOVERNOR).append(": ");
        stringBuilder.append(governor);
        
        //GPU
        if (sharedPreferences.contains("gl_renderer"))
        {
            stringBuilder.append(L0).append("javax.microedition.khronos.opengles.GL10.");
            stringBuilder.append(L1).append("glGetString(GL_RENDERER): ")
                .append(sharedPreferences.getString("gl_renderer", ""));
            stringBuilder.append(L1).append("glGetString(GL_VENDOR): ")
                .append(sharedPreferences.getString("gl_vendor", ""));
            /*stringBuilder.append(L1).append("glGetString(GL_VERSION): ")
                .append(sharedPreferences.getString("gl_version", ""));*/
        }
        
        //Add data for Primer Module
        if (min_freq == -1 && max_freq == -1)
        {
            map_primer.put("cpu", String.format("%1$s (%2$dx)", UNKNOWN, cores));
        }
        else
        {
            map_primer.put("cpu", String.format("%1$s-%2$s (%3$dx)",
                (min_freq == -1 ? UNKNOWN : UnitUtil.convertFreq(min_freq)),
                (max_freq == -1 ? UNKNOWN : UnitUtil.convertFreq(max_freq)), cores));
        }
        
        return stringBuilder;
	}
    
    /**
     * Memory Module
     */
    @SuppressLint("NewApi")
    private StringBuilder getMemoryInfo()
    {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        
        int gles_version = activityManager.getDeviceConfigurationInfo().reqGlEsVersion;
        int mem_class = activityManager.getMemoryClass();//Unit: MB
        long threshold = memoryInfo.threshold;//Unit: byte
        long total_mem;
        long avail_mem = memoryInfo.availMem;//Unit: byte
        
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
            int large_mem_class = activityManager.getLargeMemoryClass();//Unit: MB
            stringBuilder.append(L1).append("getLargeMemoryClass(): ").append(large_mem_class);
            stringBuilder.append(SPACE).append(UnitUtil
                .convertMemory(large_mem_class * 1024 * 1024));
        }
        stringBuilder.append(L1).append("MemoryInfo.");
        stringBuilder.append(L2).append("threshold: ").append(threshold);
        stringBuilder.append(SPACE).append(UnitUtil.convertMemory(threshold));
        if (SDK >= 16)
        {
            total_mem = memoryInfo.totalMem;//Unit: byte
            stringBuilder.append(L2).append("totalMem: ").append(total_mem);
            stringBuilder.append(SPACE).append(UnitUtil.convertMemory(total_mem));
            stringBuilder.append(L2).append("availMem: ").append(avail_mem);
            stringBuilder.append(SPACE).append(UnitUtil.convertMemory(avail_mem));
            stringBuilder.append(SPACE).append(UnitUtil.convertPercent(total_mem, avail_mem));
        }
        else
        {
            total_mem = ExtraUtil.getTotalRAM() * 1024;//Unit: byte
            stringBuilder.append(L2).append("availMem: ").append(avail_mem);
            stringBuilder.append(SPACE).append(UnitUtil.convertMemory(avail_mem));
            stringBuilder.append(L0).append("/proc/meminfo");
            stringBuilder.append(L1).append("MemTotal: ");
            if (total_mem != -1)
            {
                stringBuilder.append(total_mem / 1024);
                stringBuilder.append(SPACE).append(UnitUtil.convertMemory(total_mem));
            }
        }
        
        String state = Environment.getExternalStorageState();
        File dir = Environment.getRootDirectory();
        long[] usage = ExtraUtil.getStorageUsage(dir);
        
        stringBuilder.append(L0).append("android.os.Environment.");
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
        stringBuilder.append(L1).append("getExternalStorageState(): ").append(state);
        stringBuilder.append(SPACE).append(ConstUtil.getExternalStorageState(state));
        if (SDK >= 9)
        {
            stringBuilder.append(L1).append("isExternalStorageRemovable(): ").append(Environment
                .isExternalStorageRemovable() ? TRUE : FALSE);
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
        
        //Get all available storage by reflecting.
        if (SDK >= 9)
        {
            stringBuilder.append(L0).append("android.os.storage.StorageManager.");
            stringBuilder.append(L1).append("getVolumePaths():");
            StorageManager storageManager = (StorageManager)
                getSystemService(STORAGE_SERVICE);
            try
            {
                Method method = storageManager.getClass().getDeclaredMethod("getVolumePaths");
                method.setAccessible(true);
                Object object = method.invoke(storageManager);
                if (object != null && object instanceof String[])
                {
                    String[] paths = (String[])object;
                    File temp_file;
                    for (String path : paths)
                    {
                        temp_file = new File(path);
                        stringBuilder.append(L2).append(ExtraUtil.getPathRuled(path));
                        stringBuilder.append(SPACE).append(temp_file.exists() && temp_file.canRead()
                            && temp_file.list().length > 0 ? TRUE : FALSE);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        //Add data for Primer Module
        map_primer.put("ram", total_mem == -1 ? UNKNOWN : UnitUtil.convertMemory(total_mem));
        
        return stringBuilder;
	}
    
    /**
     * Package Module
     */
    @TargetApi(19)
    private StringBuilder getPackageInfo()
    {
        PackageManager packageManager = getPackageManager();
        List<String> features = new ArrayList<>();
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("android.content.pm.PackageManager.");
        stringBuilder.append(L1).append("getSystemAvailableFeatures():");

        String temp_str;
        for (FeatureInfo featureInfo : packageManager.getSystemAvailableFeatures())
        {
            temp_str = featureInfo.name;
            if (temp_str != null)
            {
                features.add(temp_str);
            }
        }
        Collections.sort(features);
        for (String feature : features)
        {
            stringBuilder.append(L2).append(feature);
            stringBuilder.append(SPACE).append(ConstUtil.getFeature(feature));
        }
        
        /*String[] libraries = packageManager.getSystemSharedLibraryNames();
        Arrays.sort(libraries);
        stringBuilder.append(L1).append("getSystemSharedLibraryNames():");
        for (String library : libraries)
        {
            stringBuilder.append(L2).append(library);
        }*/
        
        //Add data for Primer Module
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_GSM))
        {
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA))
            {
                map_primer.put("phone_type", "GSM/CDMA");
            }
            else
            {
                map_primer.put("phone_type", "GSM");
            }
        }
        else if (packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA))
        {
            map_primer.put("phone_type", "CDMA");
        }
        else
        {
            //It's probably that the device doesn't has a telephony radio
            //with data communication support.
            map_primer.put("phone_type", UNKNOWN);
        }
        map_primer.put("gyro1", packageManager
            .hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE) ? YES : NO);//API 9
        
        map_primer.put("nfc1", packageManager
            .hasSystemFeature(PackageManager.FEATURE_NFC) ? YES : NO);//API 9
        if (SDK >= 10)
        {
            NfcManager nfcManager = (NfcManager) getSystemService(NFC_SERVICE);
            NfcAdapter nfcAdapter = nfcManager.getDefaultAdapter();
            map_primer.put("nfc2", nfcAdapter != null && nfcAdapter.isEnabled() ? YES : NO);
        }
        
        map_primer.put("otg", packageManager
            .hasSystemFeature(PackageManager.FEATURE_USB_HOST) ? YES : NO);//API 12
        
        map_primer.put("ir1", packageManager
            .hasSystemFeature(PackageManager.FEATURE_CONSUMER_IR) ? YES : NO);//API 19
        if (SDK >= 19)
        {
            ConsumerIrManager consumerIrManager = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);
            map_primer.put("ir2", consumerIrManager.hasIrEmitter() ? YES : NO);
        }
        
        map_primer.put("heart_rate1", packageManager
            .hasSystemFeature(PackageManager.FEATURE_SENSOR_HEART_RATE) ? YES : NO);//API 20
        
        return stringBuilder;
	}
    
    /**
     * Sensor Module
     */
    @TargetApi(21)
    private StringBuilder getSensorInfo()
    {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("android.hardware.SensorManager.");
        stringBuilder.append(L1).append("getSensorList(Sensor.TYPE_ALL):");
        
        Sensor temp_sensor;
        for (int i = 1; i <= 21; ++ i)
        {
            temp_sensor = sensorManager.getDefaultSensor(i);
            if (temp_sensor != null)
            {
                stringBuilder.append(L2).append(i);
                stringBuilder.append(SPACE).append(ConstUtil.getSensorTypeStr(i));
                if (SDK >= 21)
                {
                    stringBuilder.append(SPACE).append(temp_sensor
                        .isWakeUpSensor() ? ON : OFF);
                }
            }
        }
        
        //Add data for Primer Module
        map_primer.put("gyro2", sensorManager
            .getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null ? YES : NO);//API 3
        map_primer.put("heart_rate2", sensorManager
            .getDefaultSensor(Sensor.TYPE_HEART_RATE) != null ? YES : NO);//API 20
        
        return stringBuilder;
	}
    
    /**
     * Superuser Module
     */
    private StringBuilder getRootInfo()
    {
        final String[] SU_PATHS = { "/system/bin/su", "/system/xbin/su",
            "/system/sbin/su", "/sbin/su", "/vendor/bin/su" };
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("Superuser:");
        
        boolean rooted = false;
        for (String path : SU_PATHS)
        {
            if ((new File(path)).exists())
            {
                rooted = true;
                stringBuilder.append(L1).append(path);
                stringBuilder.append(SPACE).append(TRUE);
            }
        }
        
        //Add data for Primer Module
        map_primer.put("root", rooted ? YES : NO);

        return stringBuilder;
    }
    
    /**
     * Security Module
     */
    /*private StringBuilder getSecurityInfo()
    {
        Provider[] providers = Security.getProviders();
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("java.secirity.Security.");
        stringBuilder.append(L1).append("getProviders():");
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
    
    /**
     * Time Module
     */
    private StringBuilder getTimeInfo()
    {
        //The current time in milliseconds since January 1, 1970 00:00:00.0 UTC.
        long current_time = System.currentTimeMillis();//Unit: ms
        //Milliseconds since boot, including time spent in sleep.
        long elapsed_realtime = SystemClock.elapsedRealtime();//Unit: ms
        //Milliseconds since boot, not counting time spent in deep sleep.
        long uptime_millis = SystemClock.uptimeMillis();//Unit: ms

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
    
    /**
     * About Module
     */
    private StringBuilder getAboutInfo()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(L).append("About:");
        stringBuilder.append(L1).append(ExtraUtil.getVerInfo(this));
        stringBuilder.append(L1).append("Typeface: ").append("Monaco.ttf");
        stringBuilder.append(L1).append("Developer: ").append("By_syk");
        stringBuilder.append(L1).append(getString(R.string.copyright));

        return stringBuilder;
    }
    
    /**
     * Primer Module
     */
    private StringBuilder getPrimerInfo()
    {
        StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append(getString(R.string.module_primer));
        
        stringBuilder.append(L1).append(getString(R.string.primer_model));
        stringBuilder.append(map_primer.get("model"));
        
        stringBuilder.append(L1).append(getString(R.string.primer_brand));
        stringBuilder.append(map_primer.get("brand"));
        
        stringBuilder.append(L1).append(getString(R.string.primer_version));
        stringBuilder.append(map_primer.get("version"));
        
        stringBuilder.append(L1).append(getString(R.string.primer_resolution));
        stringBuilder.append(map_primer.get("resolution"));
        
        if (!map_primer.get("phone_type").equals(UNKNOWN))
        {
            stringBuilder.append(L1).append(getString(R.string.primer_phone_type));
            stringBuilder.append(map_primer.get("phone_type"));
            
            stringBuilder.append(L1).append(getString(R.string.primer_imei));
            stringBuilder.append(map_primer.get("imei"));
            
            stringBuilder.append(L1).append(getString(R.string.primer_imsi));
            stringBuilder.append(map_primer.get("imsi"));
        }
        
        stringBuilder.append(L1).append(getString(R.string.primer_cpu));
        stringBuilder.append(map_primer.get("cpu"));
        
        stringBuilder.append(L1).append(getString(R.string.primer_ram));
        stringBuilder.append(map_primer.get("ram"));
        
        if (SDK >= 9)
        {
            stringBuilder.append(L1).append(getString(R.string.primer_gyro));
            stringBuilder.append(map_primer.get("gyro1").equals(map_primer.get("gyro2"))
                ? map_primer.get("gyro1") : NO);
            
            stringBuilder.append(L1).append(getString(R.string.primer_nfc));
            if (SDK >= 10)
            {
                stringBuilder.append(map_primer.get("nfc1"));
            }
            else
            {
                stringBuilder.append(map_primer.get("nfc1").equals(map_primer.get("nfc2"))
                    ? map_primer.get("nfc1") : NO);
            }
        }
        
        if (SDK >= 12)
        {
            stringBuilder.append(L1).append(getString(R.string.primer_otg));
            stringBuilder.append(map_primer.get("otg"));
        }
        
        if (SDK >= 19)
        {
            stringBuilder.append(L1).append(getString(R.string.primer_ir));
            stringBuilder.append(map_primer.get("ir1").equals(map_primer.get("ir2"))
                ? map_primer.get("ir1") : NO);
        }
        
        if (SDK >= 20)
        {
            stringBuilder.append(L1).append(getString(R.string.primer_heart_rate));
            stringBuilder.append(map_primer.get("heart_rate1").equals(map_primer.get("heart_rate2"))
                ? map_primer.get("heart_rate1") : NO);
        }
        
        stringBuilder.append(L1).append(getString(R.string.primer_root));
        stringBuilder.append(map_primer.get("root"));
        
        return stringBuilder;
    }
    
    /**
     * Create Line on the top and the bottom.
     * 100 characters in total.
     */
    private StringBuilder getDotsLine()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 10; ++ i)
        {
            stringBuilder.append(i).append("+++++++++");
        }
        
        //Just have fun.
        String hex_launch_times = "H" + Integer.toHexString(sharedPreferences
            .getInt("launch_times", 1)).toUpperCase();
        stringBuilder.replace(stringBuilder.length() - hex_launch_times.length(),
            stringBuilder.length(), hex_launch_times);
        
        return stringBuilder;
    }
    
    /**
     * Dialog: Share the Page
     * Ask user whether to add “Telephony Module” to the sharing text file or not.
     * @param TO_DEVELOPER Whether or not to "Share the Page" to developer.
     */
    private void shareTextDialog(final boolean TO_DEVELOPER)
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
                    shareText(true, TO_DEVELOPER);
                }
            })
            .setNegativeButton(R.string.dia_neg_no, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    shareText(false, TO_DEVELOPER);
                }
            })
            .create();
        alertDialog.show();
    }
    
    /**
     * Create the sharing text file.
     * @param to_developer Whether or not to "Share the Page" to developer.
     */
    private void shareText(boolean with_telephony, boolean to_developer)
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
        
        //File name: Device Info__Extra Info__OSBuild Info.info.txt
        final String FILE_NAME = with_telephony ? "%1$s__wT__%2$s.info.txt" : "%1$s__%2$s.info.txt";
        String device = String.format("%1$s_%2$s_%3$s", Build.BRAND, Build.MODEL, SDK);
        String app = ExtraUtil.getVerInfo(this);
        String file_target_name = String.format(FILE_NAME, device, app);
        final File FILE_TARGET = new File(getExternalCacheDir(), file_target_name);

        if (ExtraUtil.saveFile(FILE_TARGET, stringBuilder.toString().trim()))
        {
            share(FILE_TARGET, to_developer);
        }
    }
    
    /**
     * Send the certain file via Intent.
     * @param to_developer Whether or not to "Share the Page" to developer.
     */
    private void share(File file, boolean to_developer)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        // If true, it's from Donate dialog and send to developer.
        if (to_developer)
        {
            intent.putExtra(Intent.EXTRA_EMAIL,
                new String[] { getString(R.string.my_email) });
        }
        //The "subject" is the name of the sending file.
        intent.putExtra(Intent.EXTRA_SUBJECT, file.getName());
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.setType(ConstUtil.getMIMEType(file.getName()));
        startActivity(Intent.createChooser(intent,
            getString(R.string.share_via)));
    }
    
    /**
     * Share the app, OSBuild.
     */
    private void shareThisApp()
    {
        File file = ExtraUtil.pickUpMyPackage(this);
        if (file != null)
        {
            share(file, false);
        }
    }
    
    /**
     * Dialog: Constant Reference
     * List modules to view its constant used in the page.
     */
    private void constDialog()
    {
        final String[] CONST_FILES = getResources().getStringArray(R.array.modules);
        final int[] CONST_FILES_ID = { R.raw.build, R.raw.display,
            R.raw.tel, R.raw.cpu, R.raw.mem, R.raw.pkg, R.raw.sensor };
        
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
     * Dialog
     * Show the content of certain text file.
     * @param file_id The id of target text file.
     * @param title The title of the dialog.
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
     * Dialog: View System Files
     * List some system text files to view its content.
     * Some may require root permission.
     */
    private void sysFilesDialog()
    {
        final ArrayList<String> LIST_TITLE = new ArrayList<>();
        final ArrayList<String> LIST_PATH = new ArrayList<>();
        //About WiFi passwords.
        LIST_TITLE.add("wpa_supplicant.conf");
        LIST_PATH.add("/data/misc/wifi/wpa_supplicant.conf");
        //About time of booting. (Only for MTK)
        String boot_proc = "/proc/bootprof";
        if ((new File(boot_proc).exists()))
        {
            LIST_TITLE.add("bootprof");
            LIST_PATH.add("/proc/bootprof");
        }
        //About CPU.
        LIST_TITLE.add("cpuinfo");
        LIST_PATH.add("/proc/cpuinfo");
        //About Memory.
        LIST_TITLE.add("meminfo");
        LIST_PATH.add("/proc/meminfo");
        //About build.
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
     * Dialog: Warning
     * Ask user whether to read text file which requires root permission or not.
     * If the file doesn's require root permission or user has hidden the dialog before,
     * skip over the dialog.
     */
    private void askSUDialog(final String FILE_STR)
    {
        File file = new File(FILE_STR);
        if (file.canRead())
        {
            //Does not require root permission.
            showTextFileDialog(FILE_STR, false);
            return;
        }
        else if (sharedPreferences.getBoolean("not_show_su", false))
        {
            //Requires require root permission.
            //User has hidden the asking dialog before, so just continue.
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
     * Dialog
     * Show the content of certain text file.
     * @param file_str The path of target text file.
     * @param need_su Requires root permission or not.
     */
    private void showTextFileDialog(String file_str, boolean need_su)
    {
        File file = new File(file_str);
        StringBuilder stringBuilder = new StringBuilder(file_str);
        stringBuilder.append("\n\n");
        if (need_su)
        {
            //Requires require root permission.
            stringBuilder.append(ExtraUtil.readFileRoot(file));
        }
        else
        {
            //Does not require require root permission.
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
     * Dialog: System Settings
     * List some system settings.
     */
    private void sysSettingsDialog()
    {
        final ArrayList<String> LIST_TITLE = new ArrayList<>();
        final ArrayList<String> LIST_PKG = new ArrayList<>();
        final ArrayList<String> LIST_CLASS = new ArrayList<>();
        //Developement Options
        LIST_TITLE.add(getString(R.string.sys_settings_development));
        LIST_PKG.add("com.android.settings");
        LIST_CLASS.add("com.android.settings.DevelopmentSettings");
        //Hardware Test (Only MIUI)
        if (Build.MANUFACTURER.equals("Xiaomi"))
        {
            LIST_TITLE.add(getString(R.string.sys_settings_miui_cit));
            LIST_PKG.add("com.miui.cit");
            LIST_CLASS.add("com.miui.cit.CitLauncherActivity");
        }
        //Test
        LIST_TITLE.add(getString(R.string.sys_settings_testing));
        LIST_PKG.add("com.android.settings");
        LIST_CLASS.add("com.android.settings.TestingSettings");
        
        //"com.android.settings.DeviceInfoSettings"
        
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
     * Launch certain Activity via Intent.
     * @param package_name Package name
     * @param class_name Class name
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
     * Dialog: Share Installed App
     * Share certain installed app.
     */
    private void shareAppDialog()
    {
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_share_app, null);
        final EditText ET_APP_NAME = (EditText) viewGroup.findViewById(R.id.et_app_name);
        ((TextView)viewGroup.findViewById(R.id.tv_share_app_steps)).setText(String
            .format(getString(R.string.share_app_steps), getExternalCacheDir() + "/"));
        
        AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setTitle(R.string.dia_title_share_app)
            .setView(viewGroup)
            .setPositiveButton(R.string.dia_pos_ok, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    String text = ET_APP_NAME.getText().toString();
                    File file = ExtraUtil.pickUpPackage(MainActivity.this, text);
                    if (file != null)
                    {
                        share(file, false);
                    }
                    else
                    {
                        shareAppDialog();
                    }
                }
            })
            .setNegativeButton(R.string.dia_neg_cancel, null)
            .create();
        alertDialog.show();
    }
    
    /**
     * Dialog: Donate
     * Request user to "Share the Page" to me by E-mail.
     * And the neutral button points to the url of this project on GitHub.
     */
    @TargetApi(11)
    private void donateDialog()
    {
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_text, null);
        ((TextView) viewGroup.findViewById(R.id.tv_text)).setText(R.string.donate_desc);
        
        final AlertDialog ALERTDIAOLG = new AlertDialog.Builder(this)
            .setTitle(R.string.dia_title_donate)
            .setView(viewGroup)
            .setPositiveButton(R.string.dia_pos_ok, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    //Strategy 1: Just copy developer's E-mail.
                    /*if (SDK >= 11)
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
                    Toast.makeText(MainActivity.this, R.string.copied, Toast.LENGTH_SHORT).show();*/
                    //Strategy 2: Link to "Share the Page" directly.
                    shareTextDialog(true);
                }
            })
            .setNeutralButton(R.string.dia_neu_github, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    try
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.program_github)));
                        startActivity(intent);
                    }
                    catch (ActivityNotFoundException e)
                    {
                        //There's no any browers.
                        e.printStackTrace();
                    }
                }
            })
            .setNegativeButton(R.string.dia_neg_cancel, null)
            .create();
        ALERTDIAOLG.show();
        
        if (sharedPreferences.getBoolean("more_menu", false))
        {
            return;
        }
        ALERTDIAOLG.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnLongClickListener(new OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View p1)
            {
                sharedPreferences.edit()
                    .putBoolean("more_menu", true).commit();
                Toast.makeText(MainActivity.this, R.string.show_more_menu,
                    Toast.LENGTH_LONG).show();
                ALERTDIAOLG.dismiss();
                return true;
            }
        });
    }
    
    /**
     * Dialog: What's OSBuild?
     * Show description about OSBuild.
     */
    private void appDescDialog()
    {
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_text_ask, null);
        ((MyTextView) viewGroup.findViewById(R.id.tv_desc)).setText(R.string.app_desc);
        
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
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }
    
    /**
     * Menu:
     *     Share
     *         Share the Page
     *         Share OSBuild
     *     Constant Reference
     *     View System Files
     *     System Settings
     *     Small Tools
     *         Share Installed App
     *         Rotation Vector
     *         Compass
     *         ASCII Chart
     *         White Paper
     *         Kube
     *     Donate
     */
    @TargetApi(9)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        
        if (sharedPreferences.getBoolean("more_menu", false))
        {
            menu.getItem(4).setVisible(true);
            
            SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            
            SubMenu subMenu = menu.getItem(4).getSubMenu();
            subMenu.getItem(1).setVisible(SDK >= 9
                && sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null);
            subMenu.getItem(2).setVisible(sensorManager
                .getDefaultSensor(Sensor.TYPE_ORIENTATION) != null);
        }
        
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            //Share
            case R.id.action_sub_share_text:
                shareTextDialog(false);
                return true;
            case R.id.action_sub_share_app:
                shareThisApp();
                return true;
            //Constant Reference
            case R.id.action_const:
                constDialog();
                return true;
            //View System Files
            case R.id.action_sys_files:
                sysFilesDialog();
                return true;
            //System Settings
            case R.id.action_sys_settings:
                sysSettingsDialog();
                return true;
            //Small Tools
            case R.id.action_sub_tools_app:
                shareAppDialog();
                return true;
            case R.id.action_sub_tools_rotation:
                item.setIntent(new Intent(this, RotationVector.class));
                return super.onOptionsItemSelected(item);
            case R.id.action_sub_tools_compass:
                item.setIntent(new Intent(this, Compass.class));
                return super.onOptionsItemSelected(item);
            case R.id.action_sub_tools_ascii:
                showTextFileDialog(R.raw.ascii, getString(R.string.dia_title_ascii));
                return true;
            case R.id.action_sub_tools_white:
                item.setIntent(new Intent(this, WhitePaper.class));
                return super.onOptionsItemSelected(item);
            case R.id.action_sub_tools_kube:
                item.setIntent(new Intent(this, Kube.class));
                return super.onOptionsItemSelected(item);
            //Donate
            case R.id.action_donate:
                donateDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
