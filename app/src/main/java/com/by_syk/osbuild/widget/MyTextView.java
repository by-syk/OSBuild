package com.by_syk.osbuild.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author By_syk
 */
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
     * Modify typeface to Monaco, a monospaced font from Apple Mac.
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
