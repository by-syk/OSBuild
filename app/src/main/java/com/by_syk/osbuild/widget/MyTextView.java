package com.by_syk.osbuild.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView
{
    public MyTextView(Context context)
    {
        super(context);
    }
    
    public MyTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        changeTypeFace(context, attrs);
    }
    /**
     * 自定义等宽字体
     */
    private void changeTypeFace(Context context, AttributeSet attrs)
    {
        if (attrs != null)
        {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(),
                "Monaco.ttf");
            super.setTypeface(typeface);
        }
    }
}
