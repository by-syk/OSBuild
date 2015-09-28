/**
 * @author By_syk
 */

package com.by_syk.osbuild.widget;

import com.by_syk.osbuild.R;
import com.by_syk.osbuild.util.C;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView
{
    public MyTextView(Context context)
    {
        super(context, null);
    }
    
    public MyTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        
        init(context);
    }
    
    private void init(Context context)
    {
        //Modify typeface to Monaco, a monospaced font from Apple Mac.
        super.setTypeface(Typeface
            .createFromAsset(context.getAssets(), "Monaco.ttf"));
        
        //Set line space to 1.4 times.
        super.setLineSpacing(0f, 1.4f);
        
        //Set edge distance of page.
        int padding_page = getResources().getDimensionPixelSize(R.dimen.padding_text);
        if (C.SDK >= 16)
        {
            super.setPaddingRelative(padding_page, padding_page, padding_page, padding_page);
        }
        else
        {
            super.setPadding(padding_page, padding_page, padding_page, padding_page);
        }
    }
}
