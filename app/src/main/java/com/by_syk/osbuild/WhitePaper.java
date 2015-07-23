/**
 * @author By_syk
 */

package com.by_syk.osbuild;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.content.res.Configuration;
import android.view.Window;

public class WhitePaper extends Activity
{
    Window window = null;
    WindowManager.LayoutParams layout_params = null;
    
    //Used to switch brightness of this window.
    boolean max_brightness = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        window = getWindow();
        layout_params = window.getAttributes();
        
        //Hide navigation bar if there is one.
        /*if (SDK >= 14)
        {
            //window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            //    | View.SYSTEM_UI_FLAG_FULLSCREEN);
            layout_params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;  
            window.setAttributes(layout_params);
        }*/
        
        //Set background to pure white.
        window.setBackgroundDrawableResource(R.drawable.white_paper);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_UP:
                max_brightness = !max_brightness;
                
                layout_params.screenBrightness = max_brightness ?
                    WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
                    : WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
                window.setAttributes(layout_params);
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }
}
