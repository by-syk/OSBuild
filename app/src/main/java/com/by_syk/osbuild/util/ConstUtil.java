package com.by_syk.osbuild.util;

import android.os.Build;
import android.util.DisplayMetrics;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.view.Surface;
import android.telephony.TelephonyManager;
import java.util.Locale;
import android.content.pm.PackageManager;
import android.os.Environment;

public class ConstUtil
{
    private static int SDK = Build.VERSION.SDK_INT;//系统版本
    //private static String UNKNOWN = "UNKNOWN";
    private static String UNKNOWN = "";

    public static String getSDKIntStr(int sdk_int)
    {
        switch (sdk_int)
        {
            case Build.VERSION_CODES.BASE://1
                return "BASE";//Android 1.0
            case Build.VERSION_CODES.BASE_1_1://2
                return "BASE_1_1";//Android 1.1
            case Build.VERSION_CODES.CUPCAKE://3
                return "CUPCAKE";//Android 1.5
            case Build.VERSION_CODES.DONUT://4
                return "DONUT";//Android 1.6
            case Build.VERSION_CODES.ECLAIR://5
                return "ECLAIR";//Android 2.0
            case Build.VERSION_CODES.ECLAIR_0_1://6
                return "ECLAIR_0_1";//Android 2.0.1
            case Build.VERSION_CODES.ECLAIR_MR1://7
                return "ECLAIR_MR1";//Android 2.1.x
            case Build.VERSION_CODES.FROYO://8
                return "FROYO";//Android 2.2.x
            case Build.VERSION_CODES.GINGERBREAD://9
                return "GINGERBREAD";//Android 2.3 Android 2.3.1 Android 2.3.2
            case Build.VERSION_CODES.GINGERBREAD_MR1://10
                return "GINGERBREAD_MR1";//Android 2.3.3 Android 2.3.4
            case Build.VERSION_CODES.HONEYCOMB://11
                return "HONEYCOMB";//Android 3.0.x
            case Build.VERSION_CODES.HONEYCOMB_MR1://12
                return "HONEYCOMB_MR1";//Android 3.1.x
            case Build.VERSION_CODES.HONEYCOMB_MR2://13
                return "HONEYCOMB_MR2";//Android 3.2
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH://14
                return "ICE_CREAM_SANDWICH";//Android 4.0 Android 4.0.1 Android 4.0.2
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1://15
                return "ICE_CREAM_SANDWICH_MR1";//Android 4.0.3 Android 4.0.4
            case Build.VERSION_CODES.JELLY_BEAN://16
                return "JELLY_BEAN";//Android 4.1 Android 4.1.1
            case Build.VERSION_CODES.JELLY_BEAN_MR1://17
                return "JELLY_BEAN_MR1";//Android 4.2 Android 4.2.2
            case Build.VERSION_CODES.JELLY_BEAN_MR2://18
                return "JELLY_BEAN_MR2";//Android 4.3
            case Build.VERSION_CODES.KITKAT://19
                return "KITKAT";//Android 4.4
            case Build.VERSION_CODES.KITKAT_WATCH://20
                return "KITKAT_WATCH";//Android 4.4W
            case Build.VERSION_CODES.LOLLIPOP://21
                return "LOLLIPOP";//Android 5.0
            case Build.VERSION_CODES.LOLLIPOP_MR1://22
                return "LOLLIPOP_MR1";
            default:
                return UNKNOWN;
        }
    }
    
    public static String getSDKIntTimeStr(int sdk_int)
    {
        switch (sdk_int)
        {
            case Build.VERSION_CODES.BASE://0
                return "2008-10";//Android 1.0
            case Build.VERSION_CODES.BASE_1_1://1
                return "2009-02";//Android 1.1
            case Build.VERSION_CODES.CUPCAKE://3
                return "2009-05";//Android 1.5
            case Build.VERSION_CODES.DONUT://4
                return "2009-09";//Android 1.6
            case Build.VERSION_CODES.ECLAIR://5
                return "2009-11";//Android 2.0
            case Build.VERSION_CODES.ECLAIR_0_1://6
                return "2009-12";//Android 2.0.1
            case Build.VERSION_CODES.ECLAIR_MR1://7
                return "2010-01";//Android 2.1.x
            case Build.VERSION_CODES.FROYO://8
                return "2010-06";//Android 2.2.x
            case Build.VERSION_CODES.GINGERBREAD://9
                return "2010-11";//Android 2.3 Android 2.3.1 Android 2.3.2
            case Build.VERSION_CODES.GINGERBREAD_MR1://10
                return "2011-02";//Android 2.3.3 Android 2.3.4
            case Build.VERSION_CODES.HONEYCOMB://11
                return "2011-02";//Android 3.0.x
            case Build.VERSION_CODES.HONEYCOMB_MR1://12
                return "2011-05";//Android 3.1.x
            case Build.VERSION_CODES.HONEYCOMB_MR2://13
                return "2011-06";//Android 3.2
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH://14
                return "2011-10";//Android 4.0 Android 4.0.1 Android 4.0.2
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1://15
                return "2011-12";//Android 4.0.3 Android 4.0.4
            case Build.VERSION_CODES.JELLY_BEAN://16
                return "2012-06";//Android 4.1 Android 4.1.1
            case Build.VERSION_CODES.JELLY_BEAN_MR1://17
                return "2012-11";//Android 4.2 Android 4.2.2
            case Build.VERSION_CODES.JELLY_BEAN_MR2://18
                return "2013-07";//Android 4.3
            case Build.VERSION_CODES.KITKAT://19
                return "2013-10";//Android 4.4
            case Build.VERSION_CODES.KITKAT_WATCH://20
                return UNKNOWN;//Android 4.4W
            case Build.VERSION_CODES.LOLLIPOP:
                return UNKNOWN;//Android 5.0
            case Build.VERSION_CODES.LOLLIPOP_MR1:
                return UNKNOWN;
            default:
                return UNKNOWN;
        }
    }
    
    public static String getDensityDPIStr(int densityDpi)
    {
        switch (densityDpi)
        {
            case DisplayMetrics.DENSITY_LOW://120
                return "DENSITY_LOW";
            case DisplayMetrics.DENSITY_MEDIUM://160
                return "DENSITY_MEDIUM";//also "DENSITY_DEFAULT"
            case DisplayMetrics.DENSITY_TV://213
                return "DENSITY_TV";
            case DisplayMetrics.DENSITY_HIGH://240
                return "DENSITY_HIGH";
            case DisplayMetrics.DENSITY_280://280
                return "DENSITY_280";
            case DisplayMetrics.DENSITY_XHIGH://320
                return "DENSITY_XHIGH";
            case DisplayMetrics.DENSITY_400://400
                return "DENSITY_400";
            case DisplayMetrics.DENSITY_XXHIGH://480
                return "DENSITY_XXHDPI";
            case DisplayMetrics.DENSITY_560://560
                return "DENSITY_560";
            case DisplayMetrics.DENSITY_XXXHIGH://640
                return "DENSITY_XXXHDPI";
            default:
                return UNKNOWN;
        }
    }

    public static String getOrientationStr(int orientation)
    {
        switch (orientation)
        {
            case Configuration.ORIENTATION_UNDEFINED://0
                return "ORIENTATION_UNDEFINED";
            case Configuration.ORIENTATION_PORTRAIT://1
                return "ORIENTATION_PORTRAIT";
            case Configuration.ORIENTATION_LANDSCAPE://2
                return "ORIENTATION_LANDSCAPE";
            case Configuration.ORIENTATION_SQUARE://3
                return "ORIENTATION_SQUARE";
            default:
                return UNKNOWN;
        }
    }

    public static String getSensorTypeStr(int sensor_type)
    {
        switch (sensor_type)
        {
            case Sensor.TYPE_ALL://-1
                return "TYPE_ALL";
            case Sensor.TYPE_ACCELEROMETER://1 加速度传感器
                return "TYPE_ACCELEROMETER";
            case Sensor.TYPE_MAGNETIC_FIELD://2 磁场传感器
                return "TYPE_MAGNETIC_FIELD";
            case Sensor.TYPE_ORIENTATION://3 方向传感器
                return "TYPE_ORIENTATION";
            case Sensor.TYPE_GYROSCOPE://4 陀螺仪传感器
                return "TYPE_GYROSCOPE";
            case Sensor.TYPE_LIGHT://5 光线传感器
                return "TYPE_LIGHT";
            case Sensor.TYPE_PRESSURE://6 压力传感器
                return "TYPE_PRESSURE";
            case Sensor.TYPE_TEMPERATURE://7 温度传感器
                return "TYPE_TEMPERATURE";
            case Sensor.TYPE_PROXIMITY://8 距离传感器
                return "TYPE_PROXIMITY";
            case Sensor.TYPE_GRAVITY://9 重力传感器
                return "TYPE_GRAVITY";
            case Sensor.TYPE_LINEAR_ACCELERATION://10 线性加速度传感器
                return "TYPE_LINEAR_ACCELERATION";
            case Sensor.TYPE_ROTATION_VECTOR://11 旋转矢量传感器
                return "TYPE_ROTATION_VECTOR";
            case Sensor.TYPE_RELATIVE_HUMIDITY://12
                return "TYPE_RELATIVE_HUMIDITY";
            case Sensor.TYPE_AMBIENT_TEMPERATURE://13
                return "TYPE_AMBIENT_TEMPERATURE";
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED://14
                return "TYPE_MAGNETIC_FIELD_UNCALIBRATED";
            case Sensor.TYPE_GAME_ROTATION_VECTOR://15
                return "TYPE_GAME_ROTATION_VECTOR";
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED://16
                return "TYPE_GYROSCOPE_UNCALIBRATED";
            case Sensor.TYPE_SIGNIFICANT_MOTION://17
                return "TYPE_SIGNIFICANT_MOTION";
            case Sensor.TYPE_STEP_DETECTOR://18
                return "TYPE_STEP_DETECTOR";
            case Sensor.TYPE_STEP_COUNTER://19
                return "TYPE_STEP_COUNTER";
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR://20
                return "TYPE_GEOMAGNETIC_ROTATION_VECTOR";
            case Sensor.TYPE_HEART_RATE://21
                return "TYPE_HEART_RATE";
            default:
                return UNKNOWN;
        }
	}
    
    public static String getRotationStr(int rotation)
    {
        //设备逆时针旋转
        switch (rotation)
        {
            case Surface.ROTATION_0://0
                return "ROTATION_0";
            case Surface.ROTATION_90://1
                return "ROTATION_90";
            case Surface.ROTATION_180://2
                return "ROTATION_180";
            case Surface.ROTATION_270://3
                return "ROTATION_270";
            default:
                return UNKNOWN;
        }
    }
    
    public static String getSimOperator(String sim_operator)
    {
        if (sim_operator.equals("46000") || sim_operator.equals("46002") || sim_operator.equals("46007"))
        {
            return "China Mobile";//中国移动
        }
        else if (sim_operator.equals("46001") || sim_operator.equals("46006"))
        {
            return "China Unicom";//中国联通
        }
        else if (sim_operator.equals("46003") || sim_operator.equals("46005"))
        {
            return "China Telecom";//中国电信
        }
        return UNKNOWN;
	}
    
    public static String getExternalStorageState(String state)
    {
        if (state.equals(Environment.MEDIA_BAD_REMOVAL))//bad_removal
        {
            return "MEDIA_BAD_REMOVAL";
        }
        else if (state.equals(Environment.MEDIA_CHECKING))//checking
        {
            return "MEDIA_CHECKING";
        }
        else if (state.equals(Environment.MEDIA_MOUNTED))//mounted
        {
            return "MEDIA_MOUNTED";
        }
        else if (state.equals(Environment.MEDIA_MOUNTED_READ_ONLY))//mounted_read_only
        {
            return "MEDIA_MOUNTED_READ_ONLY";
        }
        else if (state.equals(Environment.MEDIA_NOFS))//nofs
        {
            return "MEDIA_NOFS";
        }
        else if (state.equals(Environment.MEDIA_REMOVED))//removed
        {
            return "MEDIA_REMOVED";
        }
        else if (state.equals(Environment.MEDIA_SHARED))//shared
        {
            return "MEDIA_SHARED";
        }
        else if (state.equals(Environment.MEDIA_UNKNOWN))//unknown
        {
            return "MEDIA_UNKNOWN";
        }
        else if (state.equals(Environment.MEDIA_UNMOUNTABLE))//unmountable
        {
            return "MEDIA_UNMOUNTABLE";
        }
        else if (state.equals(Environment.MEDIA_UNMOUNTED))//unmounted
        {
            return "MEDIA_UNMOUNTED";
        }
        return UNKNOWN;
    }
    
    public static String getPhoneTypeStr(int phone_type)
    {
        switch (phone_type)
        {
            case TelephonyManager.PHONE_TYPE_NONE://0
                return "PHONE_TYPE_NONE";
            case TelephonyManager.PHONE_TYPE_GSM://1
                return "PHONE_TYPE_GSM";
            case TelephonyManager.PHONE_TYPE_CDMA://2
                return "PHONE_TYPE_CDMA";
            case TelephonyManager.PHONE_TYPE_SIP://3
                return "PHONE_TYPE_SIP";//API 11
            default:
                return UNKNOWN;
        }
    }
    
    public static String getDeviceIdType(int phone_type)
    {
        switch (phone_type)
        {
            case TelephonyManager.PHONE_TYPE_GSM://1
                return "IMEI";
            case TelephonyManager.PHONE_TYPE_CDMA://2
                return "MEID";
            default:
                return UNKNOWN;
        }
    }
    
    public static String getSimStateStr(int sim_state)
    {
        switch (sim_state)
        {
            case TelephonyManager.SIM_STATE_UNKNOWN://0
                return "SIM_STATE_UNKNOWN";
            case TelephonyManager.SIM_STATE_ABSENT://1
                return "SIM_STATE_ABSENT";
            case TelephonyManager.SIM_STATE_PIN_REQUIRED://2
                return "SIM_STATE_PIN_REQUIRED";
            case TelephonyManager.SIM_STATE_PUK_REQUIRED://3
                return "SIM_STATE_PUK_REQUIRED";
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED://4
                return "SIM_STATE_NETWORK_LOCKED";
            case TelephonyManager.SIM_STATE_READY://5
                return "SIM_STATE_READY";
            default:
                return UNKNOWN;
        }
    }
    
    public static String getLocale(Locale locale)
    {
        if (SDK >= 9 && locale.equals(Locale.ROOT))//null
        {
            return "ROOT";
        }
        else if (locale.equals(Locale.GERMAN))//de
        {
            return "GERMAN";
        }
        else if (locale.equals(Locale.GERMANY))//de_DE
        {
            return "GERMANY";
        }
        else if (locale.equals(Locale.ENGLISH))//en
        {
            return "ENGLISH";
        }
        else if (locale.equals(Locale.CANADA))//en_CA
        {
            return "CANADA";
        }
        else if (locale.equals(Locale.UK))//en_GB
        {
            return "UK";
        }
        else if (locale.equals(Locale.US))//en_US
        {
            return "US";
        }
        else if (locale.equals(Locale.FRENCH))//fr
        {
            return "FRENCH";
        }
        else if (locale.equals(Locale.CANADA_FRENCH))//fr_CA
        {
            return "CANADA_FRENCH";
        }
        else if (locale.equals(Locale.FRANCE))//fr_FR
        {
            return "FRANCE";
        }
        else if (locale.equals(Locale.ITALIAN))//it
        {
            return "ITALIAN";
        }
        else if (locale.equals(Locale.ITALY))//it_IT
        {
            return "ITALY";
        }
        else if (locale.equals(Locale.JAPANESE))//ja
        {
            return "JAPANESE";
        }
        else if (locale.equals(Locale.JAPAN))//ja_JP
        {
            return "JAPAN";
        }
        else if (locale.equals(Locale.KOREAN))//ko
        {
            return "KOREAN";
        }
        else if (locale.equals(Locale.KOREA))//ko_KR
        {
            return "KOREA";
        }
        else if (locale.equals(Locale.CHINESE))//zh
        {
            return "CHINESE";
        }
        else if (locale.equals(Locale.SIMPLIFIED_CHINESE))//zh_CN
        {
            return "SIMPLIFIED_CHINESE CHINA PRC";
        }
        /*else if (locale.equals(Locale.CHINA))//zh_CN
        {
            return "CHINA";
        }*/
        /*else if (locale.equals(Locale.PRC))//zh_CN
        {
            return "PRC";
        }*/
        else if (locale.equals(Locale.TAIWAN))//zh_TW
        {
            return "TAIWAN TRADITIONAL_CHINESE";
        }
        /*else if (locale.equals(Locale.TRADITIONAL_CHINESE))//zh_TW
        {
            return "TRADITIONAL_CHINESE";
        }*/
        return UNKNOWN;
    }
    
    public static String getFeature(String feature)
    {
        if (feature.equals(PackageManager.FEATURE_APP_WIDGETS))//android.software.app_widgets
        {
            return "FEATURE_APP_WIDGETS";
        }
        else if (feature.equals(PackageManager.FEATURE_AUDIO_LOW_LATENCY))//android.hardware.audio.low_latency
        {
            return "FEATURE_AUDIO_LOW_LATENCY";
        }
        else if (feature.equals(PackageManager.FEATURE_AUDIO_OUTPUT))//android.hardware.audio.output
        {
            return "FEATURE_AUDIO_OUTPUT";
        }
        else if (feature.equals(PackageManager.FEATURE_BACKUP))//android.software.backup
        {
            return "FEATURE_BACKUP";
        }
        else if (feature.equals(PackageManager.FEATURE_BLUETOOTH))//android.hardware.bluetooth
        {
            return "FEATURE_BLUETOOTH";
        }
        else if (feature.equals(PackageManager.FEATURE_BLUETOOTH_LE))//android.hardware.bluetooth_le
        {
            return "FEATURE_BLUETOOTH_LE";
        }
        else if (feature.equals(PackageManager.FEATURE_CAMERA))//android.hardware.camera
        {
            return "FEATURE_CAMERA";
        }
        else if (feature.equals(PackageManager.FEATURE_CAMERA_ANY))//android.hardware.camera.any
        {
            return "FEATURE_CAMERA_ANY";
        }
        else if (feature.equals(PackageManager.FEATURE_CAMERA_AUTOFOCUS))//android.hardware.camera.autofocus
        {
            return "FEATURE_CAMERA_AUTOFOCUS";
        }
        else if (feature.equals(PackageManager.FEATURE_CAMERA_CAPABILITY_MANUAL_POST_PROCESSING))
        {//android.hardware.camera.capability.manual_post_processing
            return "FEATURE_CAMERA_CAPABILITY_MANUAL_POST_PROCESSING";
        }
        else if (feature.equals(PackageManager.FEATURE_CAMERA_CAPABILITY_MANUAL_SENSOR))
        {//android.hardware.camera.capability.manual_sensor
            return "FEATURE_CAMERA_CAPABILITY_MANUAL_SENSOR";
        }
        else if (feature.equals(PackageManager.FEATURE_CAMERA_CAPABILITY_RAW))
        {//android.hardware.camera.capability.raw
            return "FEATURE_CAMERA_CAPABILITY_RAW";
        }
        else if (feature.equals(PackageManager.FEATURE_CAMERA_EXTERNAL))//android.hardware.camera.external
        {
            return "FEATURE_CAMERA_EXTERNAL";
        }
        else if (feature.equals(PackageManager.FEATURE_CAMERA_FLASH))//android.hardware.camera.flash
        {
            return "FEATURE_CAMERA_FLASH";
        }
        else if (feature.equals(PackageManager.FEATURE_CAMERA_FRONT))//android.hardware.camera.front
        {
            return "FEATURE_CAMERA_FRONT";
        }
        else if (feature.equals(PackageManager.FEATURE_CAMERA_LEVEL_FULL))//android.hardware.camera.level.full
        {
            return "FEATURE_CAMERA_LEVEL_FULL";
        }
        else if (feature.equals(PackageManager.FEATURE_CONNECTION_SERVICE))//android.software.connectionservice
        {
            return "FEATURE_CONNECTION_SERVICE";
        }
        else if (feature.equals(PackageManager.FEATURE_CONSUMER_IR))//android.hardware.consumerir
        {
            return "FEATURE_CONSUMER_IR";
        }
        else if (feature.equals(PackageManager.FEATURE_DEVICE_ADMIN))//android.software.device_admin
        {
            return "FEATURE_DEVICE_ADMIN";
        }
        else if (feature.equals(PackageManager.FEATURE_FAKETOUCH))//android.hardware.faketouch
        {
            return "FEATURE_FAKETOUCH";
        }
        else if (feature.equals(PackageManager.FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT))
        {//android.hardware.faketouch.multitouch.distinct
            return "FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT";
        }
        else if (feature.equals(PackageManager.FEATURE_FAKETOUCH_MULTITOUCH_JAZZHAND))
        {//android.hardware.faketouch.multitouch.jazzhand
            return "FEATURE_FAKETOUCH_MULTITOUCH_JAZZHAND";
        }
        else if (feature.equals(PackageManager.FEATURE_GAMEPAD))//android.hardware.gamepad
        {
            return "FEATURE_GAMEPAD";
        }
        else if (feature.equals(PackageManager.FEATURE_HOME_SCREEN))//android.software.home_screen
        {
            return "FEATURE_HOME_SCREEN";
        }
        else if (feature.equals(PackageManager.FEATURE_INPUT_METHODS))//android.software.input_methods
        {
            return "FEATURE_INPUT_METHODS";
        }
        else if (feature.equals(PackageManager.FEATURE_LEANBACK))//android.software.leanback
        {
            return "FEATURE_LEANBACK";
        }
        else if (feature.equals(PackageManager.FEATURE_LIVE_TV))//android.software.live_tv
        {
            return "FEATURE_LIVE_TV";
        }
        else if (feature.equals(PackageManager.FEATURE_LIVE_WALLPAPER))//android.software.live_wallpaper
        {
            return "FEATURE_LIVE_WALLPAPER";
        }
        else if (feature.equals(PackageManager.FEATURE_LOCATION))//android.hardware.location
        {
            return "FEATURE_LOCATION";
        }
        else if (feature.equals(PackageManager.FEATURE_LOCATION_GPS))//android.hardware.location.gps
        {
            return "FEATURE_LOCATION_GPS";
        }
        else if (feature.equals(PackageManager.FEATURE_LOCATION_NETWORK))//android.hardware.location.network
        {
            return "FEATURE_LOCATION_NETWORK";
        }
        else if (feature.equals(PackageManager.FEATURE_MANAGED_USERS))//android.software.managed_users
        {
            return "FEATURE_MANAGED_USERS";
        }
        else if (feature.equals(PackageManager.FEATURE_MICROPHONE))//android.hardware.microphone
        {
            return "FEATURE_MICROPHONE";
        }
        else if (feature.equals(PackageManager.FEATURE_NFC))//android.hardware.nfc
        {
            return "FEATURE_NFC";
        }
        else if (feature.equals(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION))//android.hardware.nfc.hce
        {
            return "FEATURE_NFC_HOST_CARD_EMULATION";
        }
        else if (feature.equals(PackageManager.FEATURE_OPENGLES_EXTENSION_PACK))//android.hardware.opengles.aep
        {
            return "FEATURE_OPENGLES_EXTENSION_PACK";
        }
        else if (feature.equals(PackageManager.FEATURE_PRINTING))//android.software.print
        {
            return "FEATURE_PRINTING";
        }
        else if (feature.equals(PackageManager.FEATURE_SCREEN_LANDSCAPE))//android.hardware.screen.landscape
        {
            return "FEATURE_SCREEN_LANDSCAPE";
        }
        else if (feature.equals(PackageManager.FEATURE_SCREEN_PORTRAIT))//android.hardware.screen.portrait
        {
            return "FEATURE_SCREEN_PORTRAIT";
        }
        else if (feature.equals(PackageManager.FEATURE_SECURELY_REMOVES_USERS))
        {//android.software.securely_removes_users
            return "FEATURE_SECURELY_REMOVES_USERS";
        }
        else if (feature.equals(PackageManager.FEATURE_SENSOR_ACCELEROMETER))
        {//android.hardware.sensor.accelerometer
            return "FEATURE_SENSOR_ACCELEROMETER";
        }
        else if (feature.equals(PackageManager.FEATURE_SENSOR_AMBIENT_TEMPERATURE))
        {//android.hardware.sensor.ambient_temperature
            return "FEATURE_SENSOR_AMBIENT_TEMPERATURE";
        }
        else if (feature.equals(PackageManager.FEATURE_SENSOR_BAROMETER))//android.hardware.sensor.barometer
        {
            return "FEATURE_SENSOR_BAROMETER";
        }
        else if (feature.equals(PackageManager.FEATURE_SENSOR_COMPASS))//android.hardware.sensor.compass
        {
            return "FEATURE_SENSOR_COMPASS";
        }
        else if (feature.equals(PackageManager.FEATURE_SENSOR_GYROSCOPE))//android.hardware.sensor.gyroscope
        {
            return "FEATURE_SENSOR_GYROSCOPE";
        }
        else if (feature.equals(PackageManager.FEATURE_SENSOR_HEART_RATE))//android.hardware.sensor.heartrate
        {
            return "FEATURE_SENSOR_HEART_RATE";
        }
        else if (feature.equals(PackageManager.FEATURE_SENSOR_HEART_RATE_ECG))
        {//android.hardware.sensor.heartrate.ecg
            return "FEATURE_SENSOR_HEART_RATE_ECG";
        }
        else if (feature.equals(PackageManager.FEATURE_SENSOR_LIGHT))//android.hardware.sensor.light
        {
            return "FEATURE_SENSOR_LIGHT";
        }
        else if (feature.equals(PackageManager.FEATURE_SENSOR_PROXIMITY))//android.hardware.sensor.proximity
        {
            return "FEATURE_SENSOR_PROXIMITY";
        }
        else if (feature.equals(PackageManager.FEATURE_SENSOR_RELATIVE_HUMIDITY))
        {//android.hardware.sensor.relative_humidity
            return "FEATURE_SENSOR_RELATIVE_HUMIDITY";
        }
        else if (feature.equals(PackageManager.FEATURE_SENSOR_STEP_COUNTER))
        {//android.hardware.sensor.stepcounter
            return "FEATURE_SENSOR_STEP_COUNTER";
        }
        else if (feature.equals(PackageManager.FEATURE_SENSOR_STEP_DETECTOR))
        {//android.hardware.sensor.stepdetector
            return "FEATURE_SENSOR_STEP_DETECTOR";
        }
        else if (feature.equals(PackageManager.FEATURE_SIP))//android.software.sip
        {
            return "FEATURE_SIP";
        }
        else if (feature.equals(PackageManager.FEATURE_SIP_VOIP))//android.software.sip.voip
        {
            return "FEATURE_SIP_VOIP";
        }
        else if (feature.equals(PackageManager.FEATURE_TELEPHONY))//android.hardware.telephony
        {
            return "FEATURE_TELEPHONY";
        }
        else if (feature.equals(PackageManager.FEATURE_TELEPHONY_CDMA))//android.hardware.telephony.cdma
        {
            return "FEATURE_TELEPHONY_CDMA";
        }
        else if (feature.equals(PackageManager.FEATURE_TELEPHONY_GSM))//android.hardware.telephony.gsm
        {
            return "FEATURE_TELEPHONY_GSM";
        }
        else if (feature.equals(PackageManager.FEATURE_TELEVISION))//android.hardware.type.television
        {
            return "FEATURE_TELEVISION";
        }
        else if (feature.equals(PackageManager.FEATURE_TOUCHSCREEN))//android.hardware.touchscreen
        {
            return "FEATURE_TOUCHSCREEN";
        }
        else if (feature.equals(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH))
        {//android.hardware.touchscreen.multitouch
            return "FEATURE_TOUCHSCREEN_MULTITOUCH";
        }
        else if (feature.equals(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT))
        {//android.hardware.touchscreen.multitouch.distinct
            return "FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT";
        }
        else if (feature.equals(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_JAZZHAND))
        {//android.hardware.touchscreen.multitouch.jazzhand
            return "FEATURE_TOUCHSCREEN_MULTITOUCH_JAZZHAND";//API 19
        }
        else if (feature.equals(PackageManager.FEATURE_USB_ACCESSORY))//android.hardware.usb.accessory
        {
            return "FEATURE_USB_ACCESSORY";
        }
        else if (feature.equals(PackageManager.FEATURE_USB_HOST))//android.hardware.usb.host
        {
            return "FEATURE_USB_HOST";
        }
        else if (feature.equals(PackageManager.FEATURE_VERIFIED_BOOT))//android.software.verified_boot
        {
            return "FEATURE_VERIFIED_BOOT";
        }
        else if (feature.equals(PackageManager.FEATURE_WATCH))//android.hardware.type.watch
        {
            return "FEATURE_WATCH";
        }
        else if (feature.equals(PackageManager.FEATURE_WEBVIEW))//android.software.webview
        {
            return "FEATURE_WEBVIEW";
        }
        else if (feature.equals(PackageManager.FEATURE_WIFI))//android.hardware.wifi
        {
            return "FEATURE_WIFI";
        }
        else if (feature.equals(PackageManager.FEATURE_WIFI_DIRECT))//android.hardware.wifi.direct
        {
            return "FEATURE_WIFI_DIRECT";
        }
        return UNKNOWN;
    }
    /**
     * 获取分辨率格式
     */
    public static String getResolutionFormat(int width, int height)
    {
        int pixels = width * height;//总像素数
        int min = width <= height ? width : height;//短边像素数
        switch (pixels)
        {
            /*
             * Video Graphics Array
             */
            case 240 * 320://76800
                return "QVGA";//Quarter VGA
            case 240 * 400://96000
                return "WQVGA";//Wide QVGA, like Samsung I5800
            case 320 * 480://153600
                return "HVGA";//Half-size VGA
            case 480 * 640://307200
                return "VGA";//Video Graphics Array, like Motorola ME632
            case 640 * 960://614400
                if (min == 600)//case 600 * 1024://614400
                {
                    return "WSVGA 2";
                }
                return "DVGA";//Double-size VGA, like Meizu MX
            case 480 * 800://384000
                return "WVGA";//Wide VGA
            case 480 * 854://409920
                return "FWVGA";//Full WVGA
            case 600 * 800://480000
                return "SVGA";//Super VGA, like ONDA Vi30W
            //case 576 * 1024://589824
            //    return "WSVGA 1";//Wide SVGA
            //case 600 * 1024://614400
            //    return "WSVGA 2";//Like CUBE U25GT
            /*
             * Extended Graphics Array
             */
            case 768 * 1024://786432
                return "XGA";//Extended Graphics Array
            case 768 * 1280://983040
                return "WXGA 1";//Wide XGA, like Google Nexus 4
            case 800 * 1280://1024000
                return "WXGA 2";//Like Google Nexus 7
            //case 1200 * 1600://1920000
            //    return "UXGA";//Ultra XGA
            case 1200 * 1920://2304000
                return "WUXGA";//Widescreen UXGA, like Google Nexus 7 Ⅱ
            case 1536 * 2048://3145728
                return "QXGA";//Quad XGA, like Google Nexus 9
            case 1600 * 2560://4096000
                return "WQXGA";//Wide QXGA, like Google Nexus 10
            /*
             * High-Definition
             */
            case 540 * 960://518400
                return "QHD";//Quarter HD
            case 720 * 1280://921600
                return "HD";//High-Definition
            //case 900 * 1600://1440000
            //    return "HD+";//HD Plus
            case 1080 * 1920://2073600
                return "FHD";//Full HD
            //case 1080 * 2048://2211840
            //    return "2K";
            case 1440 * 2560://3686400
                return "QHD";//Quad HD, also WQHD, 2K, like Google Nexus 6
            case 2160 * 3840://8294400
                return "UHD";//Ultra HD, also 4K
            //case 2160 * 4096://8847360
            //    return "4K";
            /*
             * Other
             */
            case 480 * 960://460800
                return UNKNOWN;//Like Coolpad 9900
            case 1080 * 1800://1944000
                return UNKNOWN;//Like Meizu MX 3
            case 1152 * 1920://2211840
                return UNKNOWN;//Like Meizu MX 4
            case 1536 * 2560://3932160
                return UNKNOWN;//Like Meizu MX 4 Pro
            default:
                return UNKNOWN;
        }
    }
    
    public static String getSLSizeMaskStr(int sl_size_mask)
    {
        switch (sl_size_mask)
        {
            case Configuration.SCREENLAYOUT_SIZE_UNDEFINED://0
                return "SCREENLAYOUT_SIZE_UNDEFINED";
            case Configuration.SCREENLAYOUT_SIZE_SMALL://1
                return "SCREENLAYOUT_SIZE_SMALL";
            case Configuration.SCREENLAYOUT_SIZE_NORMAL://2
                return "SCREENLAYOUT_SIZE_NORMAL";
            case Configuration.SCREENLAYOUT_SIZE_LARGE://3
                return "SCREENLAYOUT_SIZE_LARGE";
            case Configuration.SCREENLAYOUT_SIZE_XLARGE://4
                return "SCREENLAYOUT_SIZE_XLARGE";
            default:
                return UNKNOWN;
        }
    }
    
    /*public static String getSLLongMaskStr(int sl_long_mask)
    {
        switch (sl_long_mask)
        {
            case Configuration.SCREENLAYOUT_LONG_UNDEFINED://0
                return "SCREENLAYOUT_LONG_UNDEFINED";
            case Configuration.SCREENLAYOUT_LONG_NO://16
                return "SCREENLAYOUT_LONG_NO";
            case Configuration.SCREENLAYOUT_LONG_YES://32
                return "SCREENLAYOUT_LONG_YES";
            default:
                return UNKNOWN;
        }
    }*/
    /**
     * 获取设备类型：Handset，Tablet
     */
    public static String getDeviceTypeStr(int sl_size_mask)
    {
        switch (sl_size_mask)
        {
            case Configuration.SCREENLAYOUT_SIZE_SMALL://1
            case Configuration.SCREENLAYOUT_SIZE_NORMAL://2
                return "Handset";
            case Configuration.SCREENLAYOUT_SIZE_LARGE://3
            case Configuration.SCREENLAYOUT_SIZE_XLARGE://4
                return "Tablet";
            default:
                return UNKNOWN;
        }
    }
    /**
     * 获取 OpenGL ES 版本，如：0x00030000 - 3.0
     * @param gles_version OpenGL ES 版本，高16位为主版本号，次16位为次版本号
     */
    public static String getGlEsVersion(int gles_version)
    {
        return String.format("%1$d.%2$d", gles_version >> 16, gles_version & 0x00001111);
    }
    
    public static String getNetworkTypeStr(int network_type)
    {
        switch (network_type)
        {
            case TelephonyManager.NETWORK_TYPE_UNKNOWN://0
                return "NETWORK_TYPE_UNKNOWN";
            case TelephonyManager.NETWORK_TYPE_GPRS://1
                return "NETWORK_TYPE_GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE://2
                return "NETWORK_TYPE_EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS://3
                return "NETWORK_TYPE_UMTS";
            case TelephonyManager.NETWORK_TYPE_CDMA://4
                return "NETWORK_TYPE_CDMA";
            case TelephonyManager.NETWORK_TYPE_EVDO_0://5
                return "NETWORK_TYPE_EVDO_0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A://6
                return "NETWORK_TYPE_EVDO_A";
            case TelephonyManager.NETWORK_TYPE_1xRTT://7
                return "NETWORK_TYPE_1xRTT";
            case TelephonyManager.NETWORK_TYPE_HSDPA://8
                return "NETWORK_TYPE_HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA://9
                return "NETWORK_TYPE_HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA://10
                return "NETWORK_TYPE_HSPA";
            case TelephonyManager.NETWORK_TYPE_IDEN://11
                return "NETWORK_TYPE_IDEN";
            case TelephonyManager.NETWORK_TYPE_EVDO_B://12
                return "NETWORK_TYPE_EVDO_B";
            case TelephonyManager.NETWORK_TYPE_LTE://13
                return "NETWORK_TYPE_LTE";
            case TelephonyManager.NETWORK_TYPE_EHRPD://14
                return "NETWORK_TYPE_EHRPD";
            case TelephonyManager.NETWORK_TYPE_HSPAP://15
                return "NETWORK_TYPE_HSPAP";
            default:
                return UNKNOWN;
        }
    }
}