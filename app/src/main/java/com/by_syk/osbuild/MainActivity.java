/**
 * There are 12 modules:
 *     (Primer Module)
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
 *     (App Module) (Not in the page)
 * @author By_syk
 */

package com.by_syk.osbuild;

import com.by_syk.osbuild.util.C;
import com.by_syk.osbuild.util.ExtraUtil;
import com.by_syk.osbuild.util.ConstUtil;
import com.by_syk.osbuild.util.UnitUtil;
import com.by_syk.osbuild.util.MinSDKVersionUtil;
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
import android.view.View;
import android.hardware.ConsumerIrManager;
import android.widget.ScrollView;
import android.widget.HorizontalScrollView;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionInfo;
import android.text.TextWatcher;
import android.text.Editable;
import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import android.content.pm.Signature;
import java.security.cert.CertificateException;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import android.util.Log;
import android.Manifest;
import android.widget.ImageButton;
import android.view.View.OnClickListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

public class MainActivity extends Activity
{
    SharedPreferences sharedPreferences;
    
    ScrollView sv_container;
    HorizontalScrollView hsv_container;
    
    MyTextView mtv_build;
    MyTextView mtv_display;
    MyTextView mtv_telephony;
    MyTextView mtv_cpu;
    MyTextView mtv_package;
    MyTextView mtv_memory;
    MyTextView mtv_sensor;
    MyTextView mtv_superuser;
    MyTextView mtv_time;
    MyTextView mtv_about;
    
    MyTextView mtv_line_top;
    MyTextView mtv_line_bottom;
    
    MyTextView mtv_primer;
    
    //Map for Primer Module
    Map<String, String> map_primer = null;
    
    //Build, Display, Telephony, CPU, Memory, Package, Sensor, Superuser, Time, About,
    //Line 1, Line 2, Primer.
    StringBuilder[] sb_modules = null;
    
    //Mark the status of current Activity, running or not.
    boolean is_running = true;
    
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
                //.putString("gl_version", p1.glGetString(GL10.GL_VERSION))
                .commit();
            
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    reboot();
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
    @TargetApi(23)
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //Indeterminate progress can be showed on the ActionBar or TitleBar.
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        setContentView(R.layout.activity_main);
        
        init();
        
        if (!sharedPreferences.contains("gl_renderer"))
        {
            //Just get and save GPU info when launching first time.
            //Then reboot OSBuild quickly.
            prepareGPUInfo();
            return;
        }
        
        //Ask for permission: READ_PHONE_STATE
        if (C.SDK >= 23 && checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED)
        {
            //Should we show an explanation?
            /*if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE))
            {}*/
            
            requestPermissions(new String[] { Manifest.permission.READ_PHONE_STATE }, 0);
        }
        else
        {
            //Load data in another thread.
            (new LoadDataTask()).execute();
        }
        
        //Ask for permission: WRITE_EXTERNAL_STORAGE
        if (C.SDK >= 23 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
        }
        
        //Count and log times of launching.
        stats();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        
        is_running = false;
    }
    
    private void init()
    {
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
    }
    
    /**
     * Get and save GPU info.
     */
    private void prepareGPUInfo()
    {
        gLSurfaceView = new GLSurfaceView(this);
        gLSurfaceView.setRenderer(glsvRenderer);
        ((RelativeLayout)findViewById(R.id.rl_parent)).addView(gLSurfaceView);
    }

    /**
     * API 23+
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case 0:
                //No matter what result is returned, continue to load data.
                //Load data in another thread.
                (new LoadDataTask()).execute();
        }
    }
    
    /**
     * Count and log times of launching.
     */
    private void stats()
    {
        //If "Don't show again." is not checked,
        //show dialog with description of OSBuild after 2 seconds.
        if (!sharedPreferences.getBoolean("not_show_about", false))
        {
            (new Handler()).postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    //Check if the app is running to avoid crashing.
                    if (!is_running)
                    {
                        return;
                    }
                    appDescDialog();
                }
            }, 2000);
        }
        
        //Log how many times the app was launched.
        final int LAUNCH_TIME = sharedPreferences.getInt("launch_times", 0) + 1;
        sharedPreferences.edit().putInt("launch_times", LAUNCH_TIME).commit();
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
            
            if (C.SDK >= 21)
            {
                showFAB();
            }
            
            //Initialize text views.
            initViews();
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
            LinearLayout ll_info = (LinearLayout) findViewById(R.id.ll_info);
            ll_info.setVisibility(View.VISIBLE);
            ll_info.setLayoutAnimation(AnimationUtils
                .loadLayoutAnimation(MainActivity.this, R.anim.layout_anim));
            
            skipPrimer(1200);
            
            //Hide round progress bar on the ActionBar.
            setProgressBarIndeterminateVisibility(false);
        }
    }

    private void initViews()
    {
        sv_container = (ScrollView) findViewById(R.id.sv_container);
        hsv_container = (HorizontalScrollView) findViewById(R.id.hsv_container);
        
        mtv_build = (MyTextView) findViewById(R.id.mtv_build);
        mtv_display = (MyTextView) findViewById(R.id.mtv_display);
        mtv_telephony = (MyTextView) findViewById(R.id.mtv_telephony);
        mtv_cpu = (MyTextView) findViewById(R.id.mtv_cpu);
        mtv_memory = (MyTextView) findViewById(R.id.mtv_memory);
        mtv_sensor = (MyTextView) findViewById(R.id.mtv_sensor);
        mtv_package = (MyTextView) findViewById(R.id.mtv_package);
        mtv_superuser = (MyTextView) findViewById(R.id.mtv_superuser);
        mtv_time = (MyTextView) findViewById(R.id.mtv_time);
        mtv_about = (MyTextView) findViewById(R.id.mtv_about);
        
        mtv_line_top = (MyTextView) findViewById(R.id.mtv_line_top);
        mtv_line_bottom = (MyTextView) findViewById(R.id.mtv_line_bottom);
        
        mtv_primer = (MyTextView) findViewById(R.id.mtv_primer);
        
        //Init Map for Primer Module
        map_primer = new HashMap<>();
        
        //Notice: All is null.
        sb_modules = new StringBuilder[13];
    }

    private void loadData()
    {
        prepareExtraInfo();
        
        sb_modules[0] = getBuildInfo();
        sb_modules[1] = getDisplayInfo();
        sb_modules[2] = getTelephonyInfo();
        sb_modules[3] = getCPUInfo();
        sb_modules[4] = getMemoryInfo();
        sb_modules[5] = getPackageInfo();
        sb_modules[6] = getSensorInfo();
        sb_modules[7] = getSuperuserInfo();
        sb_modules[8] = getTimeInfo();
        sb_modules[9] = getAboutInfo();
        
        //Get the max width and calculate the number of characters.
        //So load data at last.
        sb_modules[10] = getDotsLine();
        sb_modules[11] = new StringBuilder(sb_modules[10]);
        
        //Load data for Primer Module at last,
        //because it is from the others above.
        sb_modules[12] = getPrimerInfo();
    }

    /**
     * Load extra info (Screen Size, Width, Length, Thickness, Weight).
     */
    private void prepareExtraInfo()
    {
        if (!sharedPreferences.contains(getString(R.string.tag_cur_extra))
            || (sharedPreferences.getBoolean("recorded_extra", true)
            && !sharedPreferences.contains("extra_screen")))
        {
            Map<String, Float> extraInfoMap = ExtraUtil.getExtraInfo(this);
            if (extraInfoMap.size() > 0)
            {
                sharedPreferences.edit()
                    .putFloat("extra_screen", extraInfoMap.get("screen"))
                    .putFloat("extra_width", extraInfoMap.get("width"))
                    .putFloat("extra_length", extraInfoMap.get("length"))
                    .putFloat("extra_thickness", extraInfoMap.get("thickness"))
                    .putInt("extra_weight", extraInfoMap.get("weight").intValue())
                    .commit();
            }
            else
            {
                sharedPreferences.edit().putBoolean("recorded_extra", false).commit();
            }
            sharedPreferences.edit()
                .putBoolean(getString(R.string.tag_cur_extra), true).commit();
        }
    }

    private void fillData()
    {
        mtv_build.setText(sb_modules[0]);
        mtv_display.setText(sb_modules[1]);
        mtv_telephony.setText(sb_modules[2]);
        mtv_cpu.setText(sb_modules[3]);
        mtv_memory.setText(sb_modules[4]);
        mtv_package.setText(sb_modules[5]);
        mtv_sensor.setText(sb_modules[6]);
        mtv_superuser.setText(sb_modules[7]);
        mtv_time.setText(sb_modules[8]);
        mtv_about.setText(sb_modules[9]);
        
        mtv_line_top.setText(sb_modules[10]);
        mtv_line_bottom.setText(sb_modules[11]);
        
        mtv_primer.setText(sb_modules[12]);
        
        //For Testin
        //printAll();
    }
    
    /*private void printAll()
    {
        final String TAG = "===OSBuild===";
        
        //File name: Device Info__Extra Info__OSBuild Info.info.txt
        final String DEVICE = String.format("%1$s_%2$s_%3$s", Build.BRAND, Build.MODEL, C.SDK);
        final String APP = ExtraUtil.getVerInfo(this, false);
        final String FILE_TARGET_NAME = String.format("%1$s__wT__%2$s.info.txt", DEVICE, APP);
        
        final String[] MODULES = { FILE_TARGET_NAME, sb_modules[12].toString(),
            sb_modules[10].toString(), sb_modules[0].toString(), sb_modules[1].toString(),
            sb_modules[2].toString(), sb_modules[3].toString(), sb_modules[4].toString(),
            sb_modules[5].toString(), sb_modules[6].toString(), sb_modules[7].toString(),
            sb_modules[8].toString(), sb_modules[9].toString(),
            sb_modules[11].toString() };
        final boolean[] TWO_NL = { false, false,
            false, true, true,
            true, true, true,
            true, true, true,
            true, false,
            false };
            
        String temp_module;
        for (int i = 0, len = MODULES.length; i < len; ++ i)
        {
            temp_module = MODULES[i].replaceAll(" ", "~");
            for (String temp_line : temp_module.split("\n"))
            {
                Log.d(TAG, temp_line);
            }
            Log.d(TAG, "NL");
            if (TWO_NL[i])
            {
                Log.d(TAG, "NL");
            }
        }
    }*/
    
    /**
     * Build Module
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private StringBuilder getBuildInfo()
    {
        final String UNKNOWN = Build.UNKNOWN;
        final String MODEL = Build.MODEL.equals(UNKNOWN) ? "" : Build.MODEL;
        final String BRAND = Build.BRAND.equals(UNKNOWN) ? "" : Build.BRAND;
        final String RELEASE = Build.VERSION.RELEASE;
        final String MANUFACTURER = Build.MANUFACTURER.equals(UNKNOWN) ? "" : Build.MANUFACTURER;
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(C.L).append("android.os.Build.");
        stringBuilder.append(C.L1).append("ID: ").append(Build.ID);
        stringBuilder.append(C.L1).append("DISPLAY: ").append(Build.DISPLAY);
        stringBuilder.append(C.L1).append("VERSION.");
        stringBuilder.append(C.L2).append("RELEASE: ").append(Build.VERSION.RELEASE);
        stringBuilder.append(C.L2).append("SDK_INT: ").append(C.SDK);
        stringBuilder.append(C.SPACE).append(ConstUtil.getSDKIntStr(C.SDK));
        stringBuilder.append(C.SPACE).append(ConstUtil.getSDKIntDateStr(C.SDK));
        stringBuilder.append(C.L2).append("INCREMENTAL: ").append(Build.VERSION.INCREMENTAL);
        stringBuilder.append(C.L1).append("MODEL: ").append(MODEL);
        stringBuilder.append(C.L1).append("BRAND: ").append(BRAND);
        stringBuilder.append(C.L1).append("MANUFACTURER: ").append(MANUFACTURER);
        stringBuilder.append(C.L1).append("PRODUCT: ").append(Build.PRODUCT.equals(UNKNOWN)
            ? "" : Build.PRODUCT);
        stringBuilder.append(C.L1).append("DEVICE: ").append(Build.DEVICE.equals(UNKNOWN)
            ? "" : Build.DEVICE);
        stringBuilder.append(C.L1).append("BOARD: ").append(Build.BOARD.equals(UNKNOWN)
            ? "" : Build.BOARD);
        stringBuilder.append(C.L1).append("HARDWARE: ").append(Build.HARDWARE.equals(UNKNOWN)
            ? "" : Build.HARDWARE);
        if (C.SDK >= 21)
        {
            final String[] SUPPORTED_ABIS = Build.SUPPORTED_ABIS;
            
            stringBuilder.append(C.L1).append("SUPPORTED_ABIS: ").append(SUPPORTED_ABIS.length);
            for (String supported_abi : SUPPORTED_ABIS)
            {
                stringBuilder.append(C.L2).append(supported_abi);
            }
        }
        else
        {
            stringBuilder.append(C.L1).append("CPU_ABI: ").append(Build.CPU_ABI.equals(UNKNOWN)
                ? "" : Build.CPU_ABI);
            stringBuilder.append(C.L1).append("CPU_ABI2: ").append(Build.CPU_ABI2.equals(UNKNOWN)
                ? "" : Build.CPU_ABI2);
        }
        if (C.SDK >= 9)
        {
            stringBuilder.append(C.L1).append("SERIAL: ").append(Build.SERIAL.equals(UNKNOWN)
                ? "" : Build.SERIAL);
        }
        stringBuilder.append(C.L1).append("TIME: ").append(Build.TIME);
        stringBuilder.append(C.SPACE).append(ExtraUtil.convertMillisTime(Build.TIME, "yyyy-MM-dd"));
        
        //Get baseband.
        /*if (SDK >= 14)
        {
            //May return null (if, for instance, the radio is not currently on).
            stringBuilder.append(C.L1).append("getRadioVersion(): ").append(Build
                .getRadioVersion() == null ? "" : Build.getRadioVersion());
        }
        else //Another way to get baseband.
        {
            stringBuilder.append(C.L0).append("android.os.SystemProperties.");
            stringBuilder.append(C.L1).append("get(\"gsm.version.baseband\"): ");
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
            }
        }*/
        
        stringBuilder.append(C.L0).append("android.provider.Settings.Secure.");
        stringBuilder.append(C.L1).append("get(ANDROID_ID): ").append(Settings.Secure
            .getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        
        final String OS_ARCH = System.getProperty("os.arch", "");
        //final String OS_NAME = System.getProperty("os.name", "");
        final String OS_VERSION = System.getProperty("os.version", "");
        //final String VM_NAME = System.getProperty("java.vm.name", "");
        final String VM_VERSION = System.getProperty("java.vm.version", "");
        
        stringBuilder.append(C.L0).append("java.lang.System.");
        stringBuilder.append(C.L1).append("getProperty(\"os.arch\"): ");
        if (OS_ARCH != null)
        {
            stringBuilder.append(OS_ARCH);
        }
        /*stringBuilder.append(C.L1).append("getProperty(\"os.name\"): ");
        if (OS_NAME != null)
        {
            stringBuilder.append(OS_NAME);
        }*/
        stringBuilder.append(C.L1).append("getProperty(\"os.version\"): ");
        if (OS_VERSION != null)
        {
            stringBuilder.append(OS_VERSION);
        }
        /*stringBuilder.append(C.L1).append("getProperty(\"java.vm.name\"): ");
        if (VM_VERSION != null)
        {
            stringBuilder.append(VM_NAME);
        }*/
        stringBuilder.append(C.L1).append("getProperty(\"java.vm.version\"): ");
        if (VM_VERSION != null)
        {
            stringBuilder.append(VM_VERSION);
            stringBuilder.append(C.SPACE).append(ConstUtil.getVMType(VM_VERSION));
        }
        
        //Add data for Primer Module
        map_primer.put("model", MODEL);
        if (BRAND.equals(MANUFACTURER))
        {
            map_primer.put("brand", BRAND);
        }
        else
        {
            map_primer.put("brand", String.format("%1$s (%2$s)", BRAND,
                "".equals(MANUFACTURER) ? C.UNKNOWN : MANUFACTURER));
        }
        map_primer.put("version", "Android " + RELEASE);
        
        return stringBuilder;
    }
    
    /**
     * Display Module
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
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
        
        final int ROTATION = display.getRotation();
        
        final float DENSITY = displayMetrics.density;
        final int DENSITY_DPI = displayMetrics.densityDpi;
        final int WIDTH_PIXELS = displayMetrics.widthPixels;
        final int HEIGHT_PIXELS = displayMetrics.heightPixels;
        final float XDPI = displayMetrics.xdpi;
        final float YDPI = displayMetrics.ydpi;
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(C.L).append("android.view.Display.");
        //Get real physical resolution.
        if (C.SDK >= 17)
        {
            Point point = new Point();
            display.getRealSize(point);
            width = point.x;
            height = point.y;
            stringBuilder.append(C.L1).append("getRealSize().");
            stringBuilder.append(C.L2).append("x: ").append(width);
            stringBuilder.append(C.SPACE).append(UnitUtil.toDp(width / DENSITY));
            stringBuilder.append(C.L2).append("y: ").append(height);
            stringBuilder.append(C.SPACE).append(UnitUtil.toDp(height / DENSITY));
        }
        else if (C.SDK >= 13)
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
            stringBuilder.append(C.L1).append("getRawWidth(): ");
            if (width > 0)
            {
                stringBuilder.append(width);
                stringBuilder.append(C.SPACE).append(UnitUtil.toDp(width / DENSITY));
            }
            stringBuilder.append(C.L1).append("getRawHeight(): ");
            if (height > 0)
            {
                stringBuilder.append(height);
                stringBuilder.append(C.SPACE).append(UnitUtil.toDp(height / DENSITY));
            }
        }
        else
        {
            width = display.getWidth();
            height = display.getHeight();
            stringBuilder.append(C.L1).append("getWidth(): ").append(width);
            stringBuilder.append(C.SPACE).append(UnitUtil.toDp(width / DENSITY));
            stringBuilder.append(C.L1).append("getHeight(): ").append(height);
            stringBuilder.append(C.SPACE).append(UnitUtil.toDp(height / DENSITY));
        }
        stringBuilder.append(C.L1).append("getRefreshRate(): ").append(display.getRefreshRate());
        stringBuilder.append(C.L1).append("getRotation(): ").append(ROTATION);
        stringBuilder.append(C.SPACE).append(ConstUtil.getRotationStr(ROTATION));
        
        stringBuilder.append(C.L0).append("android.util.DisplayMetrics.");
        stringBuilder.append(C.L1).append("density: ").append(DENSITY);
        stringBuilder.append(C.L1).append("densityDpi: ").append(DENSITY_DPI);
        stringBuilder.append(C.SPACE).append(ConstUtil.getDensityDPIStr(DENSITY_DPI));
        stringBuilder.append(C.L1).append("widthPixels: ").append(WIDTH_PIXELS);
        stringBuilder.append(C.SPACE).append(UnitUtil.toDp(WIDTH_PIXELS / DENSITY));
        stringBuilder.append(C.L1).append("heightPixels: ").append(HEIGHT_PIXELS);
        stringBuilder.append(C.SPACE).append(UnitUtil.toDp(HEIGHT_PIXELS / DENSITY));
        stringBuilder.append(C.L1).append("xdpi: ").append(displayMetrics.xdpi);
        stringBuilder.append(C.SPACE).append(UnitUtil.toInch(width / XDPI));
        stringBuilder.append(C.L1).append("ydpi: ").append(displayMetrics.ydpi);
        stringBuilder.append(C.SPACE).append(UnitUtil.toInch(height / YDPI));
        
        Configuration configuration = getResources().getConfiguration();
        
        int screen_width_dp;
        int screen_height_dp = -1;
        final int ORIENTATION = configuration.orientation;
        final int SL_SIZE_MASK = configuration.screenLayout
            & Configuration.SCREENLAYOUT_SIZE_MASK;
        Locale locale = configuration.locale;
        
        stringBuilder.append(C.L0).append("android.content.res.Configuration.");
        if (C.SDK >= 13)
        {
            screen_width_dp = configuration.screenWidthDp;
            screen_height_dp = configuration.screenHeightDp;
            
            stringBuilder.append(C.L1).append("screenWidthDp: ").append(screen_width_dp);
            stringBuilder.append(C.SPACE).append(ConstUtil.getScreenWidthDpStr(screen_width_dp));
            stringBuilder.append(C.L1).append("screenHeightDp: ").append(screen_height_dp);
            stringBuilder.append(C.SPACE).append(ConstUtil.getScreenHeightDpStr(screen_height_dp));
        }
        stringBuilder.append(C.L1).append("orientation: ").append(ORIENTATION);
        stringBuilder.append(C.SPACE).append(ConstUtil.getOrientationStr(ORIENTATION));
        stringBuilder.append(C.L1).append("fontScale: ").append(configuration.fontScale);
        stringBuilder.append(C.L1).append("screenLayout & SCREENLAYOUT_SIZE_MASK: ").append(SL_SIZE_MASK);
        stringBuilder.append(C.SPACE).append(ConstUtil.getSLSizeMaskStr(SL_SIZE_MASK));
        stringBuilder.append(C.SPACE).append(ConstUtil.getDeviceTypeStr(SL_SIZE_MASK));
        stringBuilder.append(C.L1).append("locale: ").append(locale);
        stringBuilder.append(C.SPACE).append(ConstUtil.getLocale(locale));
        
        float extra_screen = sharedPreferences.getFloat("extra_screen", -1.0f);
        float[] extra_width_length = { -1, -1 };
        float extra_thickness = sharedPreferences.getFloat("extra_thickness", -1.0f);
        int extra_weight = sharedPreferences.getInt("extra_weight", -1);
        
        stringBuilder.append(C.L0).append("Extra:");
        stringBuilder.append(C.L1).append("Width-height Ratio: ").append(ExtraUtil
            .getWHRatioInt(width, height));
        stringBuilder.append(C.SPACE).append(ConstUtil.getResolutionFormat(width, height));
        stringBuilder.append(C.L1).append("Diagonal Size: ").append(UnitUtil.toInch(Math
            .sqrt(Math.pow(height / YDPI, 2) + Math.pow(width / XDPI, 2))));
        if (sharedPreferences.contains("extra_screen"))
        {
            stringBuilder.append(C.SPACE).append(UnitUtil.toInch(extra_screen));
        }
        //"height / DENSITY - screen_height_dp" should be the height of
        //the status bar and the navigation bar (if supported) in dp.
        //The standard height of the status bar is 25dp, and 48dp for the navigation bar.
        if (C.SDK >= 14)
        {
            stringBuilder.append(C.L1).append("Navigation Bar: ");
            if (height != -1 && screen_height_dp != -1)
            {
                stringBuilder.append(height / DENSITY
                    - screen_height_dp > 30 ? C.TRUE : C.FALSE);
            }
        }
        if (sharedPreferences.contains("extra_screen"))
        {
            extra_width_length = ExtraUtil.sort2Same(width, height,
                sharedPreferences.getFloat("extra_width", -1),
                sharedPreferences.getFloat("extra_length", -1));
            
            stringBuilder.append(C.L1).append("Dimensions: ").append(String.format("%1$.2f %2$.2f %3$.2f",
                extra_width_length[0], extra_width_length[1], extra_thickness));
            stringBuilder.append(C.L1).append("Weight: ").append(extra_weight);
        }
        
        //Add data for Primer Module
        String resolution = (width != -1 && height != -1)
            ? (String.format("%1$dx%2$d", width, height)) : C.UNKNOWN;
        if (sharedPreferences.contains("extra_screen"))
        {
            resolution = String.format("%1$s (%2$.2f\")", resolution, extra_screen);
            double ppi = ExtraUtil.getPPI(width, height, extra_screen);
            double screen_ratio = ExtraUtil.getScreenRatio(extra_width_length[0], extra_width_length[1],
                width, height, extra_screen);
            
            map_primer.put("ppi", String.format("%1$s (%2$s)",
                ppi > 0.0 ? UnitUtil.toPPI(ppi) : C.UNKNOWN,
                screen_ratio > 0.0 ? "~" + UnitUtil.toPercent(screen_ratio, 2) : C.UNKNOWN));
            map_primer.put("dimensions", String.format("%1$.2fx%2$.2fx%3$.2fmm (~%4$dg)",
                extra_width_length[0], extra_width_length[1], extra_thickness, extra_weight));
        }
        map_primer.put("resolution", resolution);
        
        return stringBuilder;
    }
    
    /**
     * Telephony Module
     */
    @TargetApi(23)
    private StringBuilder getTelephonyInfo()
    {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        
        int phone_count = 1;
        final int PHONE_TYPE = telephonyManager.getPhoneType();
        final int SIM_STATE = telephonyManager.getSimState();
        final String SIM_OPERATOR = telephonyManager.getSimOperator();
        String device_id = "";
        String subscriber_id = "";
        String sim_serial_number = "";
        try
        {
            device_id = telephonyManager.getDeviceId();
            subscriber_id = telephonyManager.getSubscriberId();
            sim_serial_number = telephonyManager.getSimSerialNumber();
        }
        catch (SecurityException e)
        {
            //Permission denied.
            e.printStackTrace();
        }
        final int NETWORK_TYPE = telephonyManager.getNetworkType();
        boolean is_world_phone = false;
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(C.L).append("android.telephony.TelephonyManager.");
        if (C.SDK >= 23)
        {
            phone_count = telephonyManager.getPhoneCount();
            stringBuilder.append(C.L1).append("getPhoneCount(): ").append(phone_count);
        }
        stringBuilder.append(C.L1).append("getPhoneType(): ").append(PHONE_TYPE);
        stringBuilder.append(C.SPACE).append(ConstUtil.getPhoneTypeStr(PHONE_TYPE));
        stringBuilder.append(C.L1).append("getDeviceId(): ");
        if (!TextUtils.isEmpty(device_id))
        {
            stringBuilder.append(device_id);
            stringBuilder.append(C.SPACE).append(ConstUtil
                .getDeviceIdType(PHONE_TYPE, device_id.length()));
        }
        if (C.SDK >= 23 && phone_count == 2)
        {
            String temp_device_id;
            for (int i = 0; i < 2; ++ i)
            {
                temp_device_id = telephonyManager.getDeviceId(i);
                stringBuilder.append(C.L1).append(String.format("getDeviceId(%d): ", i));
                if (TextUtils.isEmpty(temp_device_id))
                {
                    continue;
                }
                stringBuilder.append(device_id);
                stringBuilder.append(C.SPACE).append(ConstUtil
                    .getDeviceIdType(PHONE_TYPE, device_id.length()));
            }
        }
        stringBuilder.append(C.L1).append("getSimState(): ").append(SIM_STATE);
        stringBuilder.append(C.SPACE).append(ConstUtil.getSimStateStr(SIM_STATE));
        if (SIM_STATE == TelephonyManager.SIM_STATE_READY)
        {
            stringBuilder.append(C.L1).append("getSimOperator(): ").append(SIM_OPERATOR);
            stringBuilder.append(C.SPACE).append(ConstUtil.getSimOperator(SIM_OPERATOR));
            
            stringBuilder.append(C.L1).append("getSimOperatorName(): ").append(telephonyManager
                .getSimOperatorName());
        }
        stringBuilder.append(C.L1).append("getSubscriberId(): ");
        if (!TextUtils.isEmpty(subscriber_id))
        {
            stringBuilder.append(subscriber_id);
        }
        stringBuilder.append(C.L1).append("getSimSerialNumber(): ");
        if (!TextUtils.isEmpty(sim_serial_number))
        {
            stringBuilder.append(sim_serial_number);
        }
        
        stringBuilder.append(C.L1).append("getNetworkType(): ").append(NETWORK_TYPE);
        stringBuilder.append(C.SPACE).append(ConstUtil.getNetworkTypeStr(NETWORK_TYPE));
        if (C.SDK >= 23)
        {
            try
            {
                is_world_phone = telephonyManager.isWorldPhone();
                stringBuilder.append(C.L1).append("isWorldPhone(): ").append(is_world_phone);
            }
            catch (SecurityException e)
            {
                //Permission denied.
                e.printStackTrace();
            }
        }
        
/* Waiting for testing. */
        int active_subscription_info_count_max = 1;
        if (C.SDK >= 22)
        {
            SubscriptionManager subscriptionManager = SubscriptionManager.from(this);
            
            active_subscription_info_count_max = subscriptionManager
                .getActiveSubscriptionInfoCountMax();
            int active_subscription_info_count = -1;
            List<SubscriptionInfo> subscriptioninfos = null;
            try
            {
                active_subscription_info_count = subscriptionManager.getActiveSubscriptionInfoCount();
                subscriptioninfos = subscriptionManager.getActiveSubscriptionInfoList();
            }
            catch (SecurityException e)
            {
                //Permission denied.
                e.printStackTrace();
            }
            
            stringBuilder.append(C.L0).append("android.telephony.SubscriptionManager.");
            stringBuilder.append(C.L1).append("getActiveSubscriptionInfoCountMax(): ").append(active_subscription_info_count_max);
            stringBuilder.append(C.L1).append("getActiveSubscriptionInfoCount(): ");
            if (active_subscription_info_count != -1)
            {
                stringBuilder.append(active_subscription_info_count);
            }
            if (subscriptioninfos != null)
            {
                for (SubscriptionInfo subscriptionInfo : subscriptioninfos)
                {
                    stringBuilder.append(C.L1).append(String.format("getActiveSubscriptionInfoForSimSlotIndex(%d).",
                        subscriptionInfo.getSimSlotIndex()));
                    stringBuilder.append(C.L2).append("getDisplayName(): ").append(subscriptionInfo.getDisplayName());
                    stringBuilder.append(C.L2).append("getIccId(): ").append(subscriptionInfo.getIccId());
                }
            }
        }
        
        /*final String GSF_ID_KEY = ExtraUtil.getGSFIDKEY(this);
        if (!TextUtils.isEmpty(GSF_ID_KEY))
        {
            stringBuilder.append(C.L0).append("Extra:");
            stringBuilder.append(C.L1).append("GSF ID KEY: ").append(GSF_ID_KEY);
        }*/
        
        //Add data for Primer Module
        map_primer.put("imei", TextUtils.isEmpty(device_id) ? C.UNKNOWN : device_id);
        map_primer.put("imsi", TextUtils.isEmpty(subscriber_id) ? C.UNKNOWN : subscriber_id);
        map_primer.put("dual_sim", phone_count == 2 ? C.YES
            : (active_subscription_info_count_max == 2 ? C.YES : C.NO));//API 22, 23
        map_primer.put("world_phone", is_world_phone ? C.YES : C.NO);//API 23
        
        return stringBuilder;
    }
    
    /**
     * CPU Module
     */
    private StringBuilder getCPUInfo()
    {
        final String TEXT = ExtraUtil.readFile("/proc/cpuinfo");
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(C.L).append("/proc/cpuinfo");
        
        final String TAG_PROCESSOR_ARM = "Processor";
        final String TAG_HARDWARE_ARM = "Hardware";
        final String TAG_PROCESSOR_X86 = "model name";
        final String TAG_HARDWARE_X86 = "vendor_id";
        
        if (TEXT.contains(TAG_PROCESSOR_X86))
        {
            stringBuilder.append(C.L1).append("model name: ");
            final int INDEX = TEXT.indexOf(TAG_PROCESSOR_X86) + TAG_PROCESSOR_X86.length();
            final int INDEX_END = TEXT.indexOf("\n", INDEX);
            stringBuilder.append(TEXT.substring(INDEX + 3,
                INDEX_END > 0 ? INDEX_END : TEXT.length()));
        }
        else
        {
            //Like: "Processor	: ARMv7 Processor rev 1 (v7l)"
            stringBuilder.append(C.L1).append("Processor: ");
            if (TEXT.contains(TAG_PROCESSOR_ARM))
            {
                final int INDEX = TEXT.indexOf(TAG_PROCESSOR_ARM) + TAG_PROCESSOR_ARM.length();
                final int INDEX_END = TEXT.indexOf("\n", INDEX);
                stringBuilder.append(TEXT.substring(INDEX + 3,
                    INDEX_END > 0 ? INDEX_END : TEXT.length()));
            }
        }
        
        if (TEXT.contains(TAG_HARDWARE_X86))
        {
            stringBuilder.append(C.L1).append("vendor_id: ");
            final int INDEX = TEXT.indexOf(TAG_HARDWARE_X86) + TAG_HARDWARE_X86.length();
            final int INDEX_END = TEXT.indexOf("\n", INDEX);
            stringBuilder.append(TEXT.substring(INDEX + 3,
                INDEX_END > 0 ? INDEX_END : TEXT.length()));
        }
        else
        {
            //Like: "Hardware	: Qualcomm MSM8974PRO-AB"
            stringBuilder.append(C.L1).append("Hardware: ");
            if (TEXT.contains(TAG_HARDWARE_ARM))
            {
                final int INDEX = TEXT.indexOf(TAG_HARDWARE_ARM) + TAG_HARDWARE_ARM.length();
                final int INDEX_END = TEXT.indexOf("\n", INDEX);
                stringBuilder.append(TEXT.substring(INDEX + 3,
                    INDEX_END > 0 ? INDEX_END : TEXT.length()));
            }
        }
        
        final String ONLINE_INFO = ExtraUtil.readFile("/sys/devices/system/cpu/online").trim();
        final int ONLINE_CORES = ExtraUtil.getCPUOnlineCores(ONLINE_INFO);
        final int CORES = ExtraUtil.getCPUCores();
        final long MIN_FREQ = UnitUtil.toIntSafely(ExtraUtil
            .readFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"));//Unit: KHz
        final long MAX_FREQ = UnitUtil.toIntSafely(ExtraUtil
            .readFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"));//Unit: KHz
        final String CPU_GOVERNOR = ExtraUtil
            .readFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor").trim();
        
        stringBuilder.append(C.L0).append("/sys/devices/system/cpu/");
        stringBuilder.append(C.L1).append("online: ").append(ONLINE_INFO);
        if (ONLINE_CORES > 0)
        {
            stringBuilder.append(C.SPACE).append(ONLINE_CORES);
        }
        stringBuilder.append(C.L1).append("cpu[0-9]/  ").append(CORES);
        stringBuilder.append(C.L1).append("cpu0/cpufreq/");
        stringBuilder.append(C.L2).append("cpuinfo_min_freq: ");
        if (MIN_FREQ > 0)
        {
            stringBuilder.append(MIN_FREQ);
            stringBuilder.append(C.SPACE).append(UnitUtil.toFreq(MIN_FREQ * 1000));
        }
        stringBuilder.append(C.L2).append("cpuinfo_max_freq: ");
        if (MAX_FREQ > 0)
        {
            stringBuilder.append(MAX_FREQ);
            stringBuilder.append(C.SPACE).append(UnitUtil.toFreq(MAX_FREQ * 1000));
        }
        stringBuilder.append(C.L2).append("scaling_governor: ").append(CPU_GOVERNOR);
        
        //For Adreno GPUs.
        //("/sys/kernel/gpu_control/max_freq" for Defy 2.6?)
        //("/sys/devices/platform/omap/pvrsrvkm.0/sgx_fck_rate" for Defy 3.0?)
        final long MAX_GPU_CLK = UnitUtil.toIntSafely(ExtraUtil
            .readFile("/sys/class/kgsl/kgsl-3d0/max_gpuclk"));//Unit: Hz
        //final String GPU_GOVERNOR = ExtraUtil
        //    .readFile("/sys/devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/devfreq/governor");
        
        if (sharedPreferences.contains("gl_renderer"))
        {
            stringBuilder.append(C.L0).append("javax.microedition.khronos.opengles.GL10.");
            stringBuilder.append(C.L1).append("glGetString(GL_RENDERER): ")
                .append(sharedPreferences.getString("gl_renderer", ""));
            stringBuilder.append(C.L1).append("glGetString(GL_VENDOR): ")
                .append(sharedPreferences.getString("gl_vendor", ""));
            //stringBuilder.append(C.L1).append("glGetString(GL_VERSION): ")
            //    .append(sharedPreferences.getString("gl_version", ""));
        }
        
        if (MAX_GPU_CLK > 0)
        {
            stringBuilder.append(C.L0).append("/sys/class/kgsl/kgsl-3d0/");
            stringBuilder.append(C.L1).append("max_gpuclk: ").append(MAX_GPU_CLK);
            stringBuilder.append(C.SPACE).append(UnitUtil.toFreq(MAX_GPU_CLK));
        }
        /*if (!TextUtils.isEmpty(GPU_GOVERNOR))
        {
            stringBuilder.append(C.L1).append("devices/fdb00000.qcom,kgsl-3d0/kgsl/kgsl-3d0/devfreq/");
            stringBuilder.append(C.L2).append("governor: ").append(GPU_GOVERNOR);
        }*/
        
        //Add data for Primer Module
        if (MIN_FREQ > 0 && MAX_FREQ > 0)
        {
            map_primer.put("cpu", String.format("%1$s-%2$s (%3$dx)",
                UnitUtil.toFreq(MIN_FREQ), UnitUtil.toFreq(MAX_FREQ * 1000), CORES));
        }
        else
        {
            map_primer.put("cpu", String.format("%1$s (%2$dx)",
                (MAX_FREQ > 0 ? UnitUtil.toFreq(MAX_FREQ * 1000) : C.UNKNOWN), CORES));
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
        
        final int GLES_VERSION = activityManager.getDeviceConfigurationInfo().reqGlEsVersion;
        final int MEM_CLASS = activityManager.getMemoryClass();//Unit: MB
        final int large_mem_class;
        final long THRESHOLD = memoryInfo.threshold;//Unit: byte
        long total_mem;
        final long AVAIL_MEM = memoryInfo.availMem;//Unit: byte
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(C.L).append("android.app.ActivityManager.");
        stringBuilder.append(C.L1).append("getDeviceConfigurationInfo().");
        stringBuilder.append(C.L2).append("reqGlEsVersion: ").append(UnitUtil
            .toBits(GLES_VERSION, 16));
        stringBuilder.append(C.SPACE).append(ConstUtil.getGlEsVersion(GLES_VERSION));
        stringBuilder.append(C.L1).append("getMemoryClass(): ").append(MEM_CLASS);
        stringBuilder.append(C.SPACE).append(UnitUtil.toMemory(MEM_CLASS * 1024 * 1024));
        if (C.SDK >= 11)
        {
            large_mem_class = activityManager.getLargeMemoryClass();//Unit: MB
            stringBuilder.append(C.L1).append("getLargeMemoryClass(): ").append(large_mem_class);
            stringBuilder.append(C.SPACE).append(UnitUtil
                .toMemory(large_mem_class * 1024 * 1024));
        }
        if (C.SDK >= 19)
        {
            stringBuilder.append(C.L1).append("isLowRamDevice(): ").append(activityManager.isLowRamDevice());
        }
        stringBuilder.append(C.L1).append("MemoryInfo.");
        stringBuilder.append(C.L2).append("threshold: ").append(THRESHOLD);
        stringBuilder.append(C.SPACE).append(UnitUtil.toMemory(THRESHOLD));
        if (C.SDK >= 16)
        {
            total_mem = memoryInfo.totalMem;//Unit: byte
            stringBuilder.append(C.L2).append("totalMem: ").append(total_mem);
            stringBuilder.append(C.SPACE).append(UnitUtil.toMemory(total_mem));
            stringBuilder.append(C.L2).append("availMem: ").append(AVAIL_MEM);
            stringBuilder.append(C.SPACE).append(UnitUtil.toMemory(AVAIL_MEM));
            stringBuilder.append(C.SPACE).append(UnitUtil.toPercent(total_mem, AVAIL_MEM));
        }
        else
        {
            total_mem = ExtraUtil.getTotalRAM() * 1024;//Unit: byte
            stringBuilder.append(C.L2).append("availMem: ").append(AVAIL_MEM);
            stringBuilder.append(C.SPACE).append(UnitUtil.toMemory(AVAIL_MEM));
            stringBuilder.append(C.L0).append("/proc/meminfo");
            stringBuilder.append(C.L1).append("MemTotal: ");
            if (total_mem != -1)
            {
                stringBuilder.append(total_mem / 1024);
                stringBuilder.append(C.SPACE).append(UnitUtil.toMemory(total_mem));
            }
        }
        
        final String STATE = Environment.getExternalStorageState();
        File dir = Environment.getRootDirectory();
        long[] usage = ExtraUtil.getStorageUsage(dir);
        
        stringBuilder.append(C.L0).append("android.os.Environment.");
        stringBuilder.append(C.L1).append("getRootDirectory(): ").append(ExtraUtil.getPathRuled(dir));
        stringBuilder.append(C.L2).append("Total Size: ").append(usage[0]);
        stringBuilder.append(C.SPACE).append(UnitUtil.toMemory(usage[0]));
        stringBuilder.append(C.L2).append("Available Size: ").append(usage[1]);
        stringBuilder.append(C.SPACE).append(UnitUtil.toMemory(usage[1]));
        stringBuilder.append(C.SPACE).append(UnitUtil.toPercent(usage));
        dir = Environment.getDataDirectory();
        usage = ExtraUtil.getStorageUsage(dir);
        stringBuilder.append(C.L1).append("getDataDirectory(): ").append(ExtraUtil.getPathRuled(dir));
        stringBuilder.append(C.L2).append("Total Size: ").append(usage[0]);
        stringBuilder.append(C.SPACE).append(UnitUtil.toMemory(usage[0]));
        stringBuilder.append(C.L2).append("Available Size: ").append(usage[1]);
        stringBuilder.append(C.SPACE).append(UnitUtil.toMemory(usage[1]));
        stringBuilder.append(C.SPACE).append(UnitUtil.toPercent(usage));
        stringBuilder.append(C.L1).append("getExternalStorageState(): ").append(STATE);
        stringBuilder.append(C.SPACE).append(ConstUtil.getExternalStorageState(STATE));
        if (C.SDK >= 9)
        {
            stringBuilder.append(C.L1).append("isExternalStorageRemovable(): ").append(Environment
                .isExternalStorageRemovable());
        }
        stringBuilder.append(C.L1).append("getExternalStorageDirectory(): ");
        if (STATE.equals(Environment.MEDIA_MOUNTED))
        {
            dir = Environment.getExternalStorageDirectory();
            usage = ExtraUtil.getStorageUsage(Environment.getExternalStorageDirectory());
            stringBuilder.append(ExtraUtil.getPathRuled(dir));
            stringBuilder.append(C.L2).append("Total Size: ");
            stringBuilder.append(usage[0]);
            stringBuilder.append(C.SPACE).append(UnitUtil.toMemory(usage[0]));
            stringBuilder.append(C.L2).append("Available Size: ");
            stringBuilder.append(usage[1]);
            stringBuilder.append(C.SPACE).append(UnitUtil.toMemory(usage[1]));
            stringBuilder.append(C.SPACE).append(UnitUtil.toPercent(usage));
        }
        else
        {
            stringBuilder.append(C.L2).append("Total Size: ");
            stringBuilder.append(C.L2).append("Available Size: ");
        }
        
        //Get all available storage by reflecting.
        if (C.SDK >= 9)
        {
            StorageManager storageManager = (StorageManager)
                getSystemService(STORAGE_SERVICE);
            
            String[] paths = null;
            try
            {
                Method method = storageManager.getClass().getDeclaredMethod("getVolumePaths");
                method.setAccessible(true);
                Object object = method.invoke(storageManager);
                if (object != null && object instanceof String[])
                {
                    paths = (String[])object;
                }
            }
            catch (Exception e)
            {
                //ERROR 1: There's no such method "getVolumePaths" in some devices.
                e.printStackTrace();
            }
            
            stringBuilder.append(C.L0).append("android.os.storage.StorageManager.");
            stringBuilder.append(C.L1).append("getVolumePaths(): ");
            if (paths != null)
            {
                stringBuilder.append(paths.length);
                
                File temp_file;
                for (String path : paths)
                {
                    temp_file = new File(path);
                    stringBuilder.append(C.L2).append(ExtraUtil.getPathRuled(path));
                    stringBuilder.append(C.SPACE).append(temp_file.exists() && temp_file.canRead()
                        && temp_file.list().length > 0 ? C.TRUE : C.FALSE);
                }
            }
        }
        
        //Add data for Primer Module
        map_primer.put("ram", total_mem == -1 ? C.UNKNOWN : UnitUtil.toMemory(total_mem));
        
        return stringBuilder;
    }
    
    /**
     * Package Module
     */
    @TargetApi(19)
    private StringBuilder getPackageInfo()
    {
        PackageManager packageManager = getPackageManager();
        
        FeatureInfo[] featureInfos = packageManager.getSystemAvailableFeatures();
        
        List<String> features = new ArrayList<>();
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(C.L).append("android.content.pm.PackageManager.");
        stringBuilder.append(C.L1).append("getSystemAvailableFeatures(): ").append(featureInfos.length);

        String temp_str;
        for (FeatureInfo featureInfo : featureInfos)
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
            stringBuilder.append(C.L2).append(feature);
            stringBuilder.append(C.SPACE).append(ConstUtil.getFeature(feature));
        }
        
        /*String[] libraries = packageManager.getSystemSharedLibraryNames();
        Arrays.sort(libraries);
        stringBuilder.append(C.L1).append("getSystemSharedLibraryNames():");
        for (String library : libraries)
        {
            stringBuilder.append(C.L2).append(library);
        }*/
        
        /*if (SDK >= 10)
        {
            NfcAdapter nfcAdapter = null;
            try
            {
                NfcManager nfcManager = (NfcManager) getSystemService(NFC_SERVICE);
                nfcAdapter = nfcManager.getDefaultAdapter();
            }
            catch (SecurityException e)
            {
                //Permission "android.permission.NFC" denied.
                //It goes wrong only in some ROMs.
                e.printStackTrace();
            }
            
            stringBuilder.append(C.L0).append("android.nfc.NfcManager.");
            stringBuilder.append(C.L1).append("getDefaultAdapter().");
            stringBuilder.append(C.L2).append("isEnabled(): ");
            if (nfcAdapter != null)
            {
                //ON or OFF.
                stringBuilder.append(nfcAdapter.isEnabled());
            }
        }*/
        
//Waiting for testing.
        /*IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        List<IntentFilter> intentList = new ArrayList<>();
        intentList.add(intentFilter);
        
        List<ComponentName> componentList = new ArrayList<>();
        
        packageManager.getPreferredActivities(intentList, componentList, null);
        
        stringBuilder.append(C.L1).append("getPreferredActivities(): ").append(componentList.size());
        for (ComponentName componentName : componentList)
        {
            stringBuilder.append(C.L2).append(componentName.getPackageName());
        }*/
        
        boolean ir = false;
        if (C.SDK >= 19)
        {
            ConsumerIrManager consumerIrManager = (ConsumerIrManager)
                getSystemService(CONSUMER_IR_SERVICE);
            ir = consumerIrManager.hasIrEmitter();
            
            stringBuilder.append(C.L0).append("android.hardware.ConsumerIrManager.");
            stringBuilder.append(C.L1).append("hasIrEmitter(): ").append(ir);
        }
        
        stringBuilder.append(C.L0).append("Extra:");
        stringBuilder.append(C.L1).append("GSF: ").append(ExtraUtil.getPackageInfo(this,
            "com.google.android.gsf") != null ? C.TRUE : C.FALSE);
        
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
            map_primer.put("phone_type", C.UNKNOWN);
        }
        
        map_primer.put("nfc", packageManager
            .hasSystemFeature(PackageManager.FEATURE_NFC) ? C.YES : C.NO);//API 9
        
        map_primer.put("otg", packageManager
            .hasSystemFeature(PackageManager.FEATURE_USB_HOST) ? C.YES : C.NO);//API 12
        
        map_primer.put("ir", ir ? C.YES : C.NO);//API 19
        
        map_primer.put("hifi", packageManager
            .hasSystemFeature(PackageManager.FEATURE_HIFI_SENSORS) ? C.YES : C.NO);//API 23
        
        map_primer.put("fingerprint", packageManager
            .hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) ? C.YES : C.NO);//API 23
        
        map_primer.put("telephony", packageManager
            .hasSystemFeature(PackageManager.FEATURE_TELEPHONY) ? C.YES : C.NO);
        map_primer.put("camera", packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA) ? C.YES : C.NO);
        map_primer.put("flash_lamp", packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) ? C.YES : C.NO);
        map_primer.put("bluetooth", packageManager
            .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH) ? C.YES : C.NO);
        map_primer.put("wifi", packageManager
            .hasSystemFeature(PackageManager.FEATURE_WIFI) ? C.YES : C.NO);
        map_primer.put("gps", packageManager
            .hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS) ? C.YES : C.NO);
        map_primer.put("camera_front", packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ? C.YES : C.NO);//API 9
        map_primer.put("ble", packageManager
            .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) ? C.YES : C.NO);//API 18
        
        return stringBuilder;
	}
    
/* Waiting for Testing... */
    /**
     * Sensor Module
     */
    @TargetApi(21)
    private StringBuilder getSensorInfo()
    {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(C.L).append("android.hardware.SensorManager.");
        stringBuilder.append(C.L1).append("getSensorList(TYPE_ALL): ").append(sensors.size());

        if (sensors != null)
        {
            List<Integer> sensors_id = new ArrayList<>();
        
            for (Sensor sensor : sensors)
            {
                sensors_id.add(sensor.getType());
            }
            Collections.sort(sensors_id);
            Sensor tempSensor;
            for (int sensor_id : sensors_id)
            {
                tempSensor = sensorManager.getDefaultSensor(sensor_id);
/* Waiting for Testing... */
                if (tempSensor == null)
                {
                    continue;
                }
                
                stringBuilder.append(C.L2).append(sensor_id);
                stringBuilder.append(C.SPACE).append(ConstUtil.getSensorTypeStr(sensor_id));
                stringBuilder.append(C.SPACE).append(tempSensor.getVendor());
                if (C.SDK >= 21)
                {
                    stringBuilder.append(C.SPACE).append(tempSensor.isWakeUpSensor() ? C.ON : C.OFF);
                }
            }
        }
        
        //Add data for Primer Module
        map_primer.put("gyro", sensorManager
            .getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null ? C.YES : C.NO);
        map_primer.put("step", sensorManager
            .getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null ? C.YES : C.NO);//API 19
        map_primer.put("heart", sensorManager
            .getDefaultSensor(Sensor.TYPE_HEART_RATE) != null ? C.YES : C.NO);//API 20
        map_primer.put("light", sensorManager
            .getDefaultSensor(Sensor.TYPE_LIGHT) != null ? C.YES : C.NO);
        
        return stringBuilder;
	}
    
    /**
     * Superuser Module
     */
    private StringBuilder getSuperuserInfo()
    {
        final String[] SU_PATHS = { "/system/bin/su", "/system/xbin/su",
            "/system/sbin/su", "/sbin/su", "/vendor/bin/su" };
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(C.L).append("Superuser:");
        
        boolean rooted = false;
        for (String path : SU_PATHS)
        {
            if ((new File(path)).exists())
            {
                rooted = true;
                stringBuilder.append(C.L1).append(path);
                stringBuilder.append(C.SPACE).append(C.TRUE);
            }
        }
        
/* Waiting for Testing... */
        final String VER_BUSYBOX = ExtraUtil.getBusyBoxVer();
        
        stringBuilder.append(C.L0).append("BusyBox:");
        if (!TextUtils.isEmpty(VER_BUSYBOX))
        {
            stringBuilder.append(C.L1).append("Version: ").append(VER_BUSYBOX);
        }
        
        //Add data for Primer Module
        map_primer.put("root", rooted ? C.YES : C.NO);

        return stringBuilder;
    }
    
    /**
     * Security Module
     */
    /*private StringBuilder getSecurityInfo()
    {
        Provider[] providers = Security.getProviders();
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(C.L).append("java.secirity.Security.");
        stringBuilder.append(C.L1).append("getProviders():");
        for (Provider provider : providers)
        {
            stringBuilder.append(C.L2).append(provider.getName());
            stringBuilder.append(C.L3).append("Version: ").append(provider.getVersion());
            stringBuilder.append(C.L3).append("Info: ").append(provider.getInfo());
        }
        stringBuilder.append(C.L1).append("getAlgorithms(\"MessageDigest\"):");
        for (String algorithm : Security.getAlgorithms("MessageDigest"))
        {
            stringBuilder.append(C.L2).append(algorithm);
        }
        stringBuilder.append(C.L1).append("getAlgorithms(\"KeyPairGenerator\"):");
        for (String algorithm : Security.getAlgorithms("KeyPairGenerator"))
        {
            stringBuilder.append(C.L2).append(algorithm);
        }
        
        return stringBuilder;
    }*/
    
    /**
     * Time Module
     */
    private StringBuilder getTimeInfo()
    {
        //The current time in milliseconds since January 1, 1970 00:00:00.0 UTC.
        final long CURRENT_TIME = System.currentTimeMillis();//Unit: ms
        //Milliseconds since boot, including time spent in sleep.
        final long ELAPSED_REAL_TIME = SystemClock.elapsedRealtime();//Unit: ms
        //Milliseconds since boot, not counting time spent in deep sleep.
        final long UPTIME_MILLIS = SystemClock.uptimeMillis();//Unit: ms

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(C.L).append("java.lang.System.");
        stringBuilder.append(C.L1).append("currentTimeMillis(): ").append(CURRENT_TIME);
        stringBuilder.append(C.SPACE).append(ExtraUtil.convertMillisTime(CURRENT_TIME));
        stringBuilder.append(C.L0).append("android.os.SystemClock.");
        stringBuilder.append(C.L1).append("elapsedRealtime(): ").append(ELAPSED_REAL_TIME);
        stringBuilder.append(C.SPACE).append(UnitUtil.toTime(ELAPSED_REAL_TIME));
        stringBuilder.append(C.L1).append("uptimeMillis(): ").append(UPTIME_MILLIS);
        stringBuilder.append(C.SPACE).append(UnitUtil.toTime(UPTIME_MILLIS));
        stringBuilder.append(C.SPACE).append(UnitUtil.toPercent(ELAPSED_REAL_TIME, UPTIME_MILLIS));

        return stringBuilder;
    }
    
    /**
     * About Module
     */
    private StringBuilder getAboutInfo()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(C.L).append("About:");
        stringBuilder.append(C.L1).append(ExtraUtil.getVerInfo(this, false));
        stringBuilder.append(C.SPACE).append(getResources().getInteger(R.integer.cur_ver_order));
        stringBuilder.append(C.L1).append("Typeface: ").append("Monaco.ttf");
        stringBuilder.append(C.L1).append("Developer: ").append(getString(R.string.developer));
        stringBuilder.append(C.SPACE).append(getString(R.string.my_email));
        stringBuilder.append(C.L1).append(getString(R.string.copyright));

        return stringBuilder;
    }
    
    /**
     * Primer Module
     */
    private StringBuilder getPrimerInfo()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getString(R.string.module_primer));
        
        final String[] PRIMER_ALL = getResources().getStringArray(R.array.primer_all);
        
        //Line: Device Model
        stringBuilder.append(C.L1).append(PRIMER_ALL[0]);
        stringBuilder.append(map_primer.get("model"));
        
        //Line: Brand & Manufacturer
        stringBuilder.append(C.L1).append(PRIMER_ALL[1]);
        stringBuilder.append(map_primer.get("brand"));
        
        //Line: OS Version
        stringBuilder.append(C.L1).append(PRIMER_ALL[2]);
        stringBuilder.append(map_primer.get("version"));
        
        //Line: Screen Resolution
        stringBuilder.append(C.L1).append(PRIMER_ALL[3]);
        stringBuilder.append(map_primer.get("resolution"));
        
        if (sharedPreferences.contains("extra_screen"))
        {
            //Line: Pixel & Screen Area
            stringBuilder.append(C.L1).append(PRIMER_ALL[4]);
            stringBuilder.append(map_primer.get("ppi"));
            
            //Line: Dimensions
            stringBuilder.append(C.L1).append(PRIMER_ALL[5]);
            stringBuilder.append(map_primer.get("dimensions"));
        }
        
        if (!map_primer.get("phone_type").equals(C.UNKNOWN))
        {
            //Line: Phone Type
            stringBuilder.append(C.L1).append(PRIMER_ALL[6]);
            stringBuilder.append(map_primer.get("phone_type"));
            
            //Line: IMEI/MEID/ESN
            stringBuilder.append(C.L1).append(PRIMER_ALL[7]);
            stringBuilder.append(map_primer.get("imei"));
            
            //Line: IMSI
            stringBuilder.append(C.L1).append(PRIMER_ALL[8]);
            stringBuilder.append(map_primer.get("imsi"));
        }
        
        //Line: CPU Clock Speed
        stringBuilder.append(C.L1).append(PRIMER_ALL[9]);
        stringBuilder.append(map_primer.get("cpu"));
        
        //Line: Total RAM
        stringBuilder.append(C.L1).append(PRIMER_ALL[10]);
        stringBuilder.append(map_primer.get("ram"));
        
        if (C.SDK >= 9)
        {
            //Line: Gyroscope Sensor
            stringBuilder.append(C.L1).append(PRIMER_ALL[11]);
            stringBuilder.append(map_primer.get("gyro"));
            
            //Line: NFC
            stringBuilder.append(C.L1).append(PRIMER_ALL[12]);
            stringBuilder.append(map_primer.get("nfc"));
        }
        
        if (C.SDK >= 12)
        {
            //Line: OTG (USB Host)
            stringBuilder.append(C.L1).append(PRIMER_ALL[13]);
            stringBuilder.append(map_primer.get("otg"));
        }
        
        if (C.SDK >= 19)
        {
            //Line: IR Transmitter
            stringBuilder.append(C.L1).append(PRIMER_ALL[14]);
            stringBuilder.append(map_primer.get("ir"));
            
            //Line: Step Counter
            stringBuilder.append(C.L1).append(PRIMER_ALL[15]);
            stringBuilder.append(map_primer.get("step"));
        }
        
        if (C.SDK >= 20)
        {
            //Line: Heart Rate Monitor
            stringBuilder.append(C.L1).append(PRIMER_ALL[16]);
            stringBuilder.append(map_primer.get("heart"));
        }
        
        if (C.SDK >= 22)
        {
            //Line: Dual SIM
            stringBuilder.append(C.L1).append(PRIMER_ALL[17]);
            stringBuilder.append(map_primer.get("dual_sim"));
        }
        
        //Only for testing.
        if (C.SDK >= 23)
        {
            //Line: World Phone
            stringBuilder.append(C.L1).append(PRIMER_ALL[18]);
            stringBuilder.append(map_primer.get("world_phone"));
            
            //Line: Hi-Fi
            stringBuilder.append(C.L1).append(PRIMER_ALL[19]);
            stringBuilder.append(map_primer.get("hifi"));
            
            //Line: Fingerprint ID
            stringBuilder.append(C.L1).append(PRIMER_ALL[20]);
            stringBuilder.append(map_primer.get("fingerprint"));
        }
        
        //Line: Root Access
        stringBuilder.append(C.L1).append(PRIMER_ALL[21]);
        stringBuilder.append(map_primer.get("root"));
        
        final String[] TITLE_ABSENTEES = getResources().getStringArray(R.array.primer_absentees);
        final String[] TAG_ABSENTEES = { "light", "telephony", "camera", "flash_lamp",
            "bluetooth", "wifi", "gps", "camera_front", "ble" };
        final int[] NEED_SDKS = { 3, 7, 7, 7, 8, 8, 8, 9, 18 };
        final String STR_SPAN = ExtraUtil.makeStrs(' ', PRIMER_ALL[22].length());
        for (int i = 0, num_absentees = 0, len = TITLE_ABSENTEES.length; i < len; ++ i)
        {
            if (NEED_SDKS[i] <= C.SDK && map_primer.get(TAG_ABSENTEES[i]).equals(C.NO))
            {
                if (++ num_absentees == 1)
                {
                    stringBuilder.append(C.L1).append(PRIMER_ALL[22]);
                }
                else
                {
                    stringBuilder.append(C.L1).append(STR_SPAN);
                }
                stringBuilder.append(TITLE_ABSENTEES[i]);
            }
        }
        
        return stringBuilder;
    }
    
    /**
     * App Module
     * This module is not appeared in the main page.
     * @param packageInfo The tags contain GET_PERMISSIONS and GET_PERMISSIONS.
     * @param is_installed Indicate that whether the app is installed.
     */
    @TargetApi(9)
    private StringBuilder getAppInfo(PackageInfo packageInfo, boolean is_installed)
    {
        StringBuilder stringBuilder = new StringBuilder();
        
        if (packageInfo == null)
        {
            return stringBuilder;
        }
        
        PackageManager packageManager = getPackageManager();
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        
        final String PUBLIC_SOURCE_DIR = applicationInfo.publicSourceDir;
        final long SIZE_APK = (new File(PUBLIC_SOURCE_DIR)).length();
        final int MIN_SDK_VERSION = MinSDKVersionUtil.getMinSdkVersion(PUBLIC_SOURCE_DIR);
        final int TARGET_SDK_VERSION = applicationInfo.targetSdkVersion;
        int flag_system;
        final int FLAG_DEBUGGABLE = applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE;
        
        final String PACKAGE_NAME = packageInfo.packageName;
        final String VERSION_NAME = packageInfo.versionName;
        long first_install_time;
        long last_update_time;
        
        stringBuilder.append(C.L).append("android.content.pm.PackageInfo.");
        stringBuilder.append(C.L1).append("applicationInfo.");
        stringBuilder.append(C.L2).append("publicSourceDir: ").append(PUBLIC_SOURCE_DIR);
        stringBuilder.append(C.L3).append("Total Size: ").append(SIZE_APK);
        stringBuilder.append(C.SPACE).append(UnitUtil.toMemory(SIZE_APK));
        stringBuilder.append(C.L2).append("loadLabel(): ").append(applicationInfo
            .loadLabel(packageManager));
        if (MIN_SDK_VERSION > 0)
        {
            //Get minSdkVersion using undocumented API which is marked as
            //"not to be used by applications".
            stringBuilder.append(C.L2).append("minSdkVersion: ").append(MIN_SDK_VERSION);
            stringBuilder.append(C.SPACE).append(ConstUtil.getSDKIntStr(MIN_SDK_VERSION));
        }
        stringBuilder.append(C.L2).append("targetSdkVersion: ").append(TARGET_SDK_VERSION);
        stringBuilder.append(C.SPACE).append(ConstUtil.getSDKIntStr(TARGET_SDK_VERSION));
        if (is_installed)
        {
            flag_system = applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM;
            
            stringBuilder.append(C.L2).append("flags & FLAG_SYSTEM: ").append(flag_system);
            stringBuilder.append(C.SPACE).append(flag_system == ApplicationInfo.FLAG_SYSTEM ? C.TRUE : C.FALSE);
        }
        stringBuilder.append(C.L2).append("flags & FLAG_DEBUGGABLE: ").append(FLAG_DEBUGGABLE);
        stringBuilder.append(C.SPACE).append(FLAG_DEBUGGABLE == ApplicationInfo.FLAG_DEBUGGABLE ? C.TRUE : C.FALSE);
        
        stringBuilder.append(C.L1).append("packageName: ").append(PACKAGE_NAME);
        stringBuilder.append(C.L1).append("versionName: ").append(VERSION_NAME == null ? "" : VERSION_NAME);
        stringBuilder.append(C.L1).append("versionCode: ").append(packageInfo.versionCode);
        
        if (C.SDK >= 9 && is_installed)
        {
            first_install_time = packageInfo.firstInstallTime;
            last_update_time = packageInfo.lastUpdateTime;
            
            stringBuilder.append(C.L1).append("firstInstallTime: ").append(first_install_time);
            stringBuilder.append(C.SPACE).append(ExtraUtil.convertMillisTime(first_install_time));
            stringBuilder.append(C.L1).append("lastUpdateTime: ").append(last_update_time);
            stringBuilder.append(C.SPACE).append(ExtraUtil.convertMillisTime(last_update_time));
        }
        
        stringBuilder.append(C.L1).append("requestedPermissions: ");
        String[] requestedPermissions = packageInfo.requestedPermissions;
        //If the number of requested permissions is 0, it will be null.
        if (requestedPermissions != null)
        {
            stringBuilder.append(requestedPermissions.length);

            Arrays.sort(requestedPermissions);
            for (String requestedPermission : requestedPermissions)
            {
                stringBuilder.append(C.L2).append(requestedPermission);
            }
        }
        
        if (is_installed)
        {
            Intent launchIntent = packageManager.getLaunchIntentForPackage(PACKAGE_NAME);
            
            stringBuilder.append(C.L0).append("android.content.pm.PackageManager.");
            stringBuilder.append(C.L1).append("getLaunchIntentForPackage().");
            stringBuilder.append(C.L2).append("getComponent(): ");
            if (launchIntent != null)
            {
                stringBuilder.append(launchIntent.getComponent().getClassName());
            }
        }
        
        stringBuilder.append(C.L0).append("java.security.cert.X509Certificate.");
        InputStream inputStream = null;
        try
        {
            //"X509" or "X.509"?
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Signature[] signatures = packageInfo.signatures;
            if (signatures != null && signatures.length > 0)
            {
                inputStream = new ByteArrayInputStream(signatures[0].toByteArray());
                X509Certificate x509Certificate = (X509Certificate)
                    certificateFactory.generateCertificate(inputStream);

                long not_before = x509Certificate.getNotBefore().getTime();
                long not_after = x509Certificate.getNotAfter().getTime();
                String[] isser_DN = ExtraUtil.analyseSignature(x509Certificate.getIssuerDN(), "-_-").split("-_-");

                stringBuilder.append(C.L1).append("getSigAlgName(): ").append(x509Certificate.getSigAlgName());
                stringBuilder.append(C.L1).append("getNotBefore(): ").append(not_before);
                stringBuilder.append(C.SPACE).append(ExtraUtil.convertMillisTime(not_before, "yyyy-MM-dd"));
                stringBuilder.append(C.L1).append("getNotAfter(): ").append(not_after);
                stringBuilder.append(C.SPACE).append(ExtraUtil.convertMillisTime(not_after, "yyyy-MM-dd"));
                stringBuilder.append(C.L1).append("getIssuerDN(): ").append(isser_DN.length);
                for (String dn : isser_DN)
                {
                    stringBuilder.append(C.L2).append(dn);
                }
            }
        }
        catch (CertificateException e)
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
        
        return stringBuilder;
    }
    
    /**
     * Create Line on the top and the bottom.
     * 100 characters in total.
     */
    private StringBuilder getDotsLine()
    {
        StringBuilder stringBuilder = new StringBuilder();
        
        int chars_max_line = 0;
        int temp_num;
        for (int i = 0; i < 10; ++ i)
        {
            for (String str_line : sb_modules[i].toString().split("\n"))
            {
                temp_num = str_line.length();
                if (temp_num > chars_max_line)
                {
                    chars_max_line = temp_num;
                }
            }
        }

        stringBuilder.append(ExtraUtil.makeStrs('+', chars_max_line));
        final int NODES = chars_max_line / 10;
        for (int i = 0, len = (chars_max_line > 160 ? 16 : NODES); i < len; ++ i)
        {
            stringBuilder.replace(i * 10, i * 10 + 1, Integer.toHexString(i).toUpperCase());
        }
        if (chars_max_line % 10 > 0)
        {
            stringBuilder.replace(NODES * 10, NODES * 10 + 1, Integer.toHexString(NODES).toUpperCase());
        }
        
        //Log the number of launching.
        final String HEX_LAUNCH_TIMES = Integer.toHexString(sharedPreferences
            .getInt("launch_times", 1)).toUpperCase();
        stringBuilder.replace(stringBuilder.length() - HEX_LAUNCH_TIMES.length(),
            stringBuilder.length(), HEX_LAUNCH_TIMES);
        
        return stringBuilder;
    }
    
    @TargetApi(11)
    private void reboot()
    {
        if (C.SDK >= 11)
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
    
    private void skipPrimer(int duration)
    {
        (new Handler()).postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (sv_container != null && hsv_container != null)
                {
                    sv_container.smoothScrollTo(0, mtv_primer.getMeasuredHeight()
                        + (int)getResources().getDimension(R.dimen.space_block_half));
                    hsv_container.smoothScrollTo(0, 0);
                }
            }
        }, duration);
    }
    
    /**
     * Show the FAB and make it clickable.
     * API 21+
     */
    private void showFAB()
    {
        final ImageButton FAB_TOP = (ImageButton) findViewById(R.id.fab_top);
        
        FAB_TOP.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View p1)
            {
                skipPrimer(0);
            }
        });
       
        FAB_TOP.setVisibility(View.VISIBLE);
        FAB_TOP.setAnimation(AnimationUtils.loadAnimation(MainActivity.this,
            R.anim.anim_right_in));
    }
    
    /**
     * Dialog: Share the Page
     * Ask user whether to add “Telephony Module” to the sharing text file or not.
     * @param TO_DEVELOPER Whether or not to "Share the Page" to developer.
     */
    @TargetApi(11)
    private void shareTextDialog(final boolean TO_DEVELOPER)
    {
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_file, null);
        ((MyTextView)viewGroup.findViewById(R.id.mtv_file)).setText(sb_modules[2]);
        
        AlertDialog alertDialog = (C.SDK >= 21 ? (new AlertDialog.Builder(this,
            R.style.AlertDialogStyle)) : (new AlertDialog.Builder(this)))
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
            .setNeutralButton(R.string.dia_neu_cancel, null)
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
        
        stringBuilder.append(sb_modules[10]);
        
        stringBuilder.append(C.NL1).append(sb_modules[0]);
        stringBuilder.append(C.NL2).append(sb_modules[1]);
        if (with_telephony)
        {
            stringBuilder.append(C.NL2).append(sb_modules[2]);
        }
        stringBuilder.append(C.NL2).append(sb_modules[3]);
        stringBuilder.append(C.NL2).append(sb_modules[4]);
        stringBuilder.append(C.NL2).append(sb_modules[5]);
        stringBuilder.append(C.NL2).append(sb_modules[6]);
        stringBuilder.append(C.NL2).append(sb_modules[7]);
        stringBuilder.append(C.NL2).append(sb_modules[8]);
        stringBuilder.append(C.NL2).append(sb_modules[9]);
        
        stringBuilder.append(C.NL1).append(sb_modules[11]);
        
        //File name: Device Info__Extra Info__OSBuild Info.info.txt
        final String FILE_NAME = with_telephony ? "%1$s__wT__%2$s.info.txt" : "%1$s__%2$s.info.txt";
        String device = String.format("%1$s_%2$s_%3$s", Build.BRAND, Build.MODEL, C.SDK);
        String app = ExtraUtil.getVerInfo(this, false);
        String file_target_name = String.format(FILE_NAME, device, app);
        File file_target = new File(getExternalCacheDir(), file_target_name);

        ExtraUtil.saveFile(file_target, stringBuilder.toString().trim());
        if (!file_target.exists())
        {
            //There are no more space to copy file on external storage maybe.
            //So we have to try to copy it to internal storage.
            file_target = new File(getCacheDir(), file_target_name);
            ExtraUtil.saveFile(file_target, stringBuilder.toString().trim());
        }
        if (file_target.exists())
        {
            share(file_target, to_developer);
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
    @TargetApi(11)
    private void constDialog()
    {
        final String[] CONST_FILES = getResources().getStringArray(R.array.modules);
        final int[] CONST_FILES_ID = { R.raw.build_module, R.raw.display_module,
            R.raw.telephony_module, R.raw.cpu_module, R.raw.memory_module,
            R.raw.package_module, R.raw.sensor_module, R.raw.app_module };
        
        AlertDialog alertDialog = (C.SDK >= 21 ? (new AlertDialog.Builder(this,
            R.style.AlertDialogStyle)) : (new AlertDialog.Builder(this)))
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
    @TargetApi(11)
    private void showTextFileDialog(int file_id, String title)
    {
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_file, null);
        ((MyTextView)viewGroup.findViewById(R.id.mtv_file))
            .setText(ExtraUtil.readFile(getResources().openRawResource(file_id)));

        AlertDialog alertDialog = (C.SDK >= 21 ? (new AlertDialog.Builder(this,
            R.style.AlertDialogStyle)) : (new AlertDialog.Builder(this)))
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
    @TargetApi(11)
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
        
        AlertDialog alertDialog = (C.SDK >= 21 ? (new AlertDialog.Builder(this,
            R.style.AlertDialogStyle)) : (new AlertDialog.Builder(this)))
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
    @TargetApi(11)
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
        ((MyTextView)viewGroup.findViewById(R.id.mtv_desc)).setText(R.string.dia_su_desc);

        ((CheckBox)viewGroup.findViewById(R.id.cb_not_show))
            .setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton p1, boolean p2)
            {
                sharedPreferences.edit()
                    .putBoolean("not_show_su", p2).commit();
            }
        });

        AlertDialog alertDialog = (C.SDK >= 21 ? (new AlertDialog.Builder(this,
            R.style.AlertDialogStyle)) : (new AlertDialog.Builder(this)))
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
    @TargetApi(11)
    private void showTextFileDialog(String file_str, boolean need_su)
    {
        File file = new File(file_str);
        
        StringBuilder stringBuilder = new StringBuilder(file_str);
        stringBuilder.append("\n\n");
        if (need_su)
        {
            //Requires root permission.
            stringBuilder.append(ExtraUtil.readFileRoot(file));
        }
        else
        {
            //Does not require root permission.
            stringBuilder.append(ExtraUtil.readFile(file));
        }
        
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_file, null);
        ((MyTextView)viewGroup.findViewById(R.id.mtv_file)).setText(stringBuilder);
        
        AlertDialog alertDialog = (C.SDK >= 21 ? (new AlertDialog.Builder(this,
            R.style.AlertDialogStyle)) : (new AlertDialog.Builder(this)))
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
    @TargetApi(11)
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
        
        AlertDialog alertDialog = (C.SDK >= 21 ? (new AlertDialog.Builder(this,
            R.style.AlertDialogStyle)) : (new AlertDialog.Builder(this)))
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
        
        //For Android 4.3/4.4 to open the hidden "App Ops".
        //However, the method is useless after Android 4.4.2.
        /*Intent intent = new Intent();
        intent.setClassName("com.android.settings",
            "com.android.settings.Settings");
        intent.setAction(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
            "com.android.settings.applications.AppOpsSummary");
        startActivity(intent);*/
    }
    
    /**
     * Dialog: Check Installed App
     * What text is accepted?
     *     App name (or part) [Installed]
     *     Package name [Installed]
     *     APK file path [Uninstalled]
     */
    @TargetApi(11)
    private void checkAppDialog()
    {
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_input, null);
        final EditText ET_NAME = (EditText) viewGroup.findViewById(R.id.et_text);
        
        ET_NAME.setText(sharedPreferences.getString("app_searched", ""));
        
        AlertDialog alertDialog = (C.SDK >= 21 ? (new AlertDialog.Builder(this,
            R.style.AlertDialogStyle)) : (new AlertDialog.Builder(this)))
            .setTitle(R.string.dia_title_check_app)
            .setView(viewGroup)
            .setPositiveButton(R.string.dia_pos_ok, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    final String TEXT = ET_NAME.getText().toString().trim();
                    if ("".equals(TEXT))
                    {
                        checkAppDialog();
                        return;
                    }
                    
                    sharedPreferences.edit().putString("app_searched", TEXT).commit();
                    
                    //In order for system to hide soft keyboard more naturally and effectively.
                    (new Handler()).postDelayed(new Runnable()
                    {
                        public void run()
                        {
                            //Check if the app is running to avoid crashing.
                            if (!is_running)
                            {
                                return;
                            }
                            checkAppDialog(ExtraUtil.getPackageInfo(MainActivity.this, TEXT),
                                !(new File(TEXT)).isFile());
                        }
                    }, 600);
                }
            })
            .setNegativeButton(R.string.dia_neg_cancel, null)
            .setNeutralButton(R.string.dia_neu_installed, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    //In order for system to hide soft keyboard more naturally and effectively.
                    (new Handler()).postDelayed(new Runnable()
                    {
                        public void run()
                        {
                            //Check if the app is running to avoid crashing.
                            if (!is_running)
                            {
                                return;
                            }
                            //List all (including system apps).
                            checkAppDialog(ExtraUtil
                                .getAllInstalledPackageNames(MainActivity.this, true));
                        }
                    }, 600);
                }
            })
            .create();
        alertDialog.show();
        
        final Button BT_POS = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        ET_NAME.setOnEditorActionListener(new OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView p1, int p2, KeyEvent p3)
            {
                if (p2 == EditorInfo.IME_ACTION_DONE)
                {
                    BT_POS.performClick();
                    return true;
                }
                return false;
            }
        });
    }
    
    /**
     * Dialog: Check Installed App
     * App Module (About certail app)
     */
    @TargetApi(11)
    private void checkAppDialog(final PackageInfo PACKAGEINFO, boolean is_installed)
    {
        if (PACKAGEINFO == null)
        {
            checkAppDialog();
            return;
        }
        
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_file, null);
        ((MyTextView)viewGroup.findViewById(R.id.mtv_file))
            .setText(getAppInfo(PACKAGEINFO, is_installed));
        
        AlertDialog alertDialog = (C.SDK >= 21 ? (new AlertDialog.Builder(this,
            R.style.AlertDialogStyle)) : (new AlertDialog.Builder(this)))
            .setTitle(R.string.dia_title_check_app)
            .setView(viewGroup)
            .setPositiveButton(R.string.dia_pos_ok, null)
            .setNegativeButton(R.string.dia_neg_back, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    checkAppDialog();
                }
            })
            .setNeutralButton(R.string.dia_neu_share, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    File file_s = new File(PACKAGEINFO.applicationInfo.publicSourceDir);
                    File file_t = new File(getExternalCacheDir(),
                        ExtraUtil.getVerInfo(PACKAGEINFO));
                    //If the same file exists, just return it.
                    if (!file_t.exists() || file_s.length() != file_t.length())
                    {
                        ExtraUtil.fileChannelCopy(file_s, file_t);
                    }
                    if (file_t.exists())
                    {
                        share(file_t, false);
                    }
                }
            })
            .create();
        alertDialog.show();
    }
    
    /**
     * Dialog: Check Installed App
     * List package names of all installed apps.
     */
    @TargetApi(11)
    private void checkAppDialog(final String[] PACKAGE_NAMES)
    {
        if (PACKAGE_NAMES == null)
        {
            checkAppDialog();
            return;
        }
        
        AlertDialog alertDialog = (C.SDK >= 21 ? (new AlertDialog.Builder(this,
            R.style.AlertDialogStyle)) : (new AlertDialog.Builder(this)))
            .setTitle(R.string.dia_title_check_app)
            .setItems(PACKAGE_NAMES, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    sharedPreferences.edit().putString("app_searched", PACKAGE_NAMES[p2]).commit();
                    
                    checkAppDialog(ExtraUtil.getPackageInfo(MainActivity.this, PACKAGE_NAMES[p2]), true);
                }
            })
            .setPositiveButton(R.string.dia_pos_back, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    checkAppDialog();
                }
            })
            .setNegativeButton(R.string.dia_neg_cancel, null)
            .create();
        alertDialog.show();
    }
    
    /**
     * View certain url.
     */
    private void viewUrl(String url)
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            //There's no any browers or other apps to deal with it.
            e.printStackTrace();
        }
    }
    
    /**
     * Dialog: Unicode Viewer
     */
    @TargetApi(11)
    private void unicodeViewerDialog()
    {
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_unicode, null);
        final MyTextView MTV_UNICODE_RESULT = (MyTextView) viewGroup.findViewById(R.id.mtv_unicode_result);
        EditText et_chars = (EditText) viewGroup.findViewById(R.id.et_chars);
        
        et_chars.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
            {}

            @Override
            public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
            {}

            @Override
            public void afterTextChanged(Editable p1)
            {
                MTV_UNICODE_RESULT.setText(ExtraUtil.getUnicode(p1.toString(), 4));
            }
        });
        
        AlertDialog alertDialog = (C.SDK >= 21 ? (new AlertDialog.Builder(this,
            R.style.AlertDialogStyle)) : (new AlertDialog.Builder(this)))
            .setTitle(R.string.dia_title_unicode)
            .setView(viewGroup)
            .setNegativeButton(R.string.dia_neg_cancel, null)
            .create();
        alertDialog.show();
    }
    
    /**
     * Dialog: Raise Your Voice
     */
    @TargetApi(11)
    private void feedbackDialog()
    {
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_text, null);
        ((MyTextView)viewGroup.findViewById(R.id.mtv_text)).setText(R.string.dia_feedback_desc);

        final AlertDialog alertDialog = (C.SDK >= 21 ? (new AlertDialog.Builder(this,
            R.style.AlertDialogStyle)) : (new AlertDialog.Builder(this)))
            .setTitle(R.string.dia_title_feedback)
            .setView(viewGroup)
            .setPositiveButton(R.string.dia_pos_email, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    Intent intent = new Intent(Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto", getString(R.string.my_email), null));
                    intent.putExtra(Intent.EXTRA_SUBJECT, ExtraUtil.getVerInfo(MainActivity.this, false));

                    try
                    {
                        startActivity(intent);
                    }
                    catch (ActivityNotFoundException e)
                    {
                        //There's no any E-mail apps to deal with it.
                        e.printStackTrace();
                    }
                }
            })
            .setNegativeButton(R.string.dia_neg_market, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    viewUrl("market://details?id=" + getPackageName());
                }
            })
            .setNeutralButton(R.string.dia_neu_cancel, null)
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
        ((MyTextView)viewGroup.findViewById(R.id.mtv_text)).setText(R.string.dia_donate_desc);
        
        AlertDialog alertDialog = (C.SDK >= 21 ? (new AlertDialog.Builder(this,
            R.style.AlertDialogStyle)) : (new AlertDialog.Builder(this)))
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
            .setNegativeButton(R.string.dia_neg_cancel, null)
            .create();
        alertDialog.show();
    }
    
    /**
     * Dialog: What's OSBuild?
     * Show description about OSBuild.
     */
    @TargetApi(11)
    private void appDescDialog()
    {
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_text_ask, null);
        ((MyTextView)viewGroup.findViewById(R.id.mtv_desc)).setText(R.string.dia_app_desc);
        
        ((CheckBox)viewGroup.findViewById(R.id.cb_not_show))
            .setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton p1, boolean p2)
            {
                sharedPreferences.edit()
                    .putBoolean("not_show_about", p2).commit();
            }
        });
        
        AlertDialog alertDialog = (C.SDK >= 21 ? (new AlertDialog.Builder(this,
            R.style.AlertDialogStyle)) : (new AlertDialog.Builder(this)))
            .setTitle(R.string.dia_title_about)
            .setView(viewGroup)
            .setPositiveButton(R.string.dia_pos_ok, null)
            .create();
        alertDialog.show();
    }
    
    /**
     * For devices equiped with hard search button.
     */
    @Override
    public boolean onSearchRequested()
    {
        checkAppDialog();
        return true;
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        
/* Waiting for Testing... */
        skipPrimer(1000);
    }
    
    /**
     * Menu:
     *     Share
     *         Share the Page
     *         Share OSBuild
     *     Constant Reference
     *     View System Files
     *     System Settings
     *     Experimentally
     *         Check Installed App
     *         Unicode Viewer
     *         White Paper
     *         ASCII Chart
     *     More
     *         Sources on GitHub
     *         Raise Your Voice
     *         Donate
     */
    @TargetApi(9)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        
        if (C.SDK >= 21)
        {
            menu.getItem(0).setIcon(R.drawable.ic_action_share);
        }
        
        return true;
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
            //Experimentally
            case R.id.action_sub_ex_app:
                checkAppDialog();
                return true;
            case R.id.action_sub_ex_unicode:
                unicodeViewerDialog();
                return true;
            case R.id.action_sub_ex_white:
                item.setIntent(new Intent(this, WhitePaperActivity.class));
                return super.onOptionsItemSelected(item);
            case R.id.action_sub_ex_ascii:
                showTextFileDialog(R.raw.ascii, getString(R.string.dia_title_ascii));
                return true;
            //More
            case R.id.action_sub_more_github:
                viewUrl(getString(R.string.url_program));
                return true;
            case R.id.action_sub_more_feedback:
                feedbackDialog();
                return true;
            case R.id.action_sub_more_donate:
                donateDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
