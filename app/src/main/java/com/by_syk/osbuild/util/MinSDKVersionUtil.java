/**
 * @author By_syk
 */

package com.by_syk.osbuild.util;

import java.io.File;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import android.content.res.XmlResourceParser;
import org.xmlpull.v1.XmlPullParser;
import android.content.res.AssetManager;

public class MinSDKVersionUtil
{
    /**
     * Parses AndroidManifest.xml of the given apkFile and returns the value of
     * minSdkVersion using undocumented API which is marked as
     * "not to be used by applications"
     * 
     * @return minSdkVersion or -1 if not found in AndroidManifest.xml
     */
    public static int getMinSdkVersion(File apkFile)
    {
        if (apkFile == null)
        {
            return -1;
        }
        
        try
        {
            XmlResourceParser parser = getParserForManifest(apkFile);
            if (parser == null)
            {
                return -1;
            }
            while (parser.next() != XmlPullParser.END_DOCUMENT)
            {
                if (parser.getEventType() == XmlPullParser.START_TAG
                    && parser.getName().equals("uses-sdk"))
                {
                    for (int i = 0; i < parser.getAttributeCount(); ++ i)
                    {
                        if (parser.getAttributeName(i).equals("minSdkVersion"))
                        {
                            return parser.getAttributeIntValue(i, -1);
                        }
                    }
                }
            }
        }
        catch (XmlPullParserException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        return -1;
    }
    
    public static int getMinSdkVersion(String apk_path)
    {
        if (apk_path == null)
        {
            return -1;
        }
        return getMinSdkVersion(new File(apk_path));
    }

    /**
     * Tries to get the parser for the given apkFile from {@link AssetManager}
     * using undocumented API which is marked as
     * "not to be used by applications"
     *
     * @throws IOException
     */
    private static XmlResourceParser getParserForManifest(final File APKFILE) throws IOException
    {
        final Object ASSETMANAGERINSTANCE = getAssetManager();
        final int COOKIE = addAssets(APKFILE, ASSETMANAGERINSTANCE);
        return ((AssetManager)ASSETMANAGERINSTANCE)
            .openXmlResourceParser(COOKIE, "AndroidManifest.xml");
    }

    /**
     * Get the cookie of an asset using an undocumented API call that is marked
     * as "no to be used by applications" in its source code
     * 
     * @see <a href="http://androidxref.com/5.1.1_r6/xref/frameworks/base/core/java/android/content/res/AssetManager.java#612">AssetManager.java#612</a>
     * @return the cookie
     */
    private static int addAssets(final File APKFILE, final Object ASSETMANAGERINSTANCE)
    {
        try
        {
            Method addAssetPath = ASSETMANAGERINSTANCE.getClass()
                .getMethod("addAssetPath", new Class[] { String.class });
            return (Integer)addAssetPath.invoke(ASSETMANAGERINSTANCE,
                APKFILE.getAbsolutePath());
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        
        return -1;
    }

    /**
     * Get {@link AssetManager} using reflection
     */
    private static Object getAssetManager()
    {
        Class assetManagerClass = null;
        try
        {
            assetManagerClass = Class
                .forName("android.content.res.AssetManager");
            return assetManagerClass.newInstance();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        
        return null;
    }
}
