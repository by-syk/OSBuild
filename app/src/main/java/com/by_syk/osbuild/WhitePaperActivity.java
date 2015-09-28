/**
 * @author By_syk
 */

package com.by_syk.osbuild;

import com.by_syk.osbuild.widget.MyTextView;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.content.res.Configuration;
import android.view.Window;

public class WhitePaperActivity extends Activity
{
    MyTextView mtv_lightness;
    
    Window window = null;
    WindowManager.LayoutParams layout_params = null;
    
    //Used to switch brightness of this window.
    boolean max_brightness = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_paper);
        
        init();
    }
    
    private void init()
    {
        window = getWindow();
        layout_params = window.getAttributes();
        
        /*View decorView = getWindow().getDecorView();
        //Hide both the navigation bar and the status bar.
        //SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        //a general rule, you should design your app to hide the status bar whenever you
        //hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);*/
        
        mtv_lightness = (MyTextView) findViewById(R.id.mtv_lightness);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_UP:
            {
                max_brightness = !max_brightness;
                
                layout_params.screenBrightness = max_brightness ?
                    WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
                    : WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
                window.setAttributes(layout_params);
                
                mtv_lightness.setText(max_brightness
                    ? "100%" : getString(R.string.light_auto));
            }
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }
}
