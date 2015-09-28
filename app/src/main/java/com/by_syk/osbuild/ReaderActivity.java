/**
 * @author By_syk
 */

package com.by_syk.osbuild;

import com.by_syk.osbuild.util.C;
import com.by_syk.osbuild.widget.MyTextView;
import com.by_syk.osbuild.util.ExtraUtil;
import com.by_syk.osbuild.util.AndroidManifestUtil;

import android.app.Activity;
import android.os.Bundle;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import java.io.FileNotFoundException;
import android.view.MenuItem;
import android.view.Menu;
import android.content.res.Configuration;
import android.view.ViewGroup;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.nio.charset.Charset;
import android.text.TextUtils;
import java.nio.charset.IllegalCharsetNameException;
import java.util.SortedMap;
import android.view.Window;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.View;
import android.annotation.TargetApi;
import android.os.Handler;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

public class ReaderActivity extends Activity
{
    SharedPreferences sharedPreferences;
    
    MyTextView mtv_reader;
    
    //A URI reference of source file.
    Uri uri_file = null;
    String file_name = null;
    
    //Text of the file. (Only part if the file is too big.)
    String text = "";
    
    ScaleGestureDetector scaleGestureDetector = null;
    
    float scaled_density = 1.0f;
    float text_size_dp_default = 14.0f;
    
    //Default: UTF-8
    MyCharset myCharset = null;
    
    //Mark the status of current Activity, running or not.
    boolean isRunning = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //Indeterminate progress can be showed on the ActionBar or TitleBar.
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        setContentView(R.layout.activity_reader);
        
        init();
        
        //Load data in another thread.
        uri_file = getIntent().getData();
        file_name = ExtraUtil.getUriFileName(ReaderActivity.this, uri_file);
        
        myCharset = new MyCharset();
        
        (new LoadDataTask()).execute();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        isRunning = false;
    }
    
    private void init()
    {
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        
        mtv_reader = (MyTextView) findViewById(R.id.mtv_reader);
        
        scaled_density = getResources().getDisplayMetrics().scaledDensity;
        text_size_dp_default = mtv_reader.getTextSize() / scaled_density;
        
        mtv_reader.setTextSize(text_size_dp_default +
            sharedPreferences.getFloat("reader_text_size_dp_extra", 0.0f));
        
        scaleGestureDetector = new ScaleGestureDetector(this, new OnScaleGestureListener()
        {
            @Override
            public boolean onScale(ScaleGestureDetector p1)
            {
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector p1)
            {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector p1)
            {
                final float SCALE_FACTOR = p1.getScaleFactor();
                if (SCALE_FACTOR >= 0.8f && SCALE_FACTOR <= 1.2f)
                {
                    return;
                }

                final float OLD_TEXT_SIZE_DP = mtv_reader.getTextSize() / scaled_density;
                if (OLD_TEXT_SIZE_DP <= text_size_dp_default - 4.0f && SCALE_FACTOR <= 1.0f)
                {
                    return;
                }
                if (OLD_TEXT_SIZE_DP >= text_size_dp_default + 4.0f && SCALE_FACTOR > 1.0f)
                {
                    return;
                }
                
                final float NEW_TEXT_SIZE_DP = SCALE_FACTOR > 1.0f ? (OLD_TEXT_SIZE_DP + 4.0f)
                    : (OLD_TEXT_SIZE_DP - 4.0f);
                
                //Unit: dp
                sharedPreferences.edit().putFloat("reader_text_size_dp_extra",
                    NEW_TEXT_SIZE_DP - text_size_dp_default).commit();
                mtv_reader.setTextSize(NEW_TEXT_SIZE_DP);
            }
        });
        
        mtv_reader.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View p1, MotionEvent p2)
            {
                switch (p2.getPointerCount())
                {
                    case 2:
                    {
                        //Disallow HorizontalScrollView to intercept touch events.
                        p1.getParent().requestDisallowInterceptTouchEvent(true);
                        //Disallow ScrollView to intercept touch events.
                        p1.getParent().getParent().requestDisallowInterceptTouchEvent(true);
                        return scaleGestureDetector.onTouchEvent(p2);
                    }
                    default:
                        return false;
                }
            }
        });
    }
    
    @TargetApi(11)
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
            
            if (C.SDK >= 11)
            {
                //Show the file name on the ActionBar.
                //Set to null to disable the subtitle entirely.
                getActionBar().setSubtitle(file_name);
            }
        }

        @Override
        protected String doInBackground(String[] p1)
        {
            //Print file path firstly.
            /*StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(uri_file.getPath());
            stringBuilder.append("\n\n").append(loadData());*/
            
            text = loadData();
            
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            
            //Fill data to text views.
            fillData();
            
            //Hide round progress bar on the ActionBar.
            setProgressBarIndeterminateVisibility(false);
        }
    }
    
    private String loadData()
    {
        String result = "";
        
        if (uri_file == null)
        {
            return result;
        }

        //getContentResolver().openInputStream
        //Accepts the following URI schemes:
        //    content (SCHEME_CONTENT)
        //    android.resource (SCHEME_ANDROID_RESOURCE)
        //    file (SCHEME_FILE)
        try
        {
            //For testing.
            if (file_name.equals("AndroidManifest.xml"))
            {
                result = AndroidManifestUtil.readAM(getContentResolver()
                    .openInputStream(uri_file));
            }
            /*if (file_name.endsWith(".apk"))
            {
                result = AXMLPrinter.getManifestXMLFromAPK(uri_file.getPath());
            }*/
            
            if (result.length() < 64)
            {
                //Limit lines of the file to 1024.
                result = ExtraUtil.readFile(getContentResolver()
                    .openInputStream(uri_file), myCharset.charset, 1024);
            
                /*System.out.println("File: " + uri_file.getPath());
            
                long start = System.currentTimeMillis();
                result = ExtraUtil.readFile(getContentResolver()
                    .openInputStream(uri_file), myCharset.charset);
                System.out.println("Read File 1: " + (System.currentTimeMillis() - start));
            
                start = System.currentTimeMillis();
                result = ExtraUtil.fileChannelRead(uri_file.getPath());
                System.out.println("Read File 2: " + (System.currentTimeMillis() - start));*/
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return result;
    }
    
    private void fillData()
    {
        if (TextUtils.isEmpty(text))
        {
            return;
        }
        mtv_reader.setText(text);
    }
    
    /**
     * Dialog: User
     * Set self-defining charset.
     */
    @TargetApi(11)
    private void setCharsetDialog()
    {
        ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_input, null);
        final EditText ET_CHARSET = (EditText) viewGroup.findViewById(R.id.et_text);
        ET_CHARSET.setText(myCharset.charsets[2]);
        
        AlertDialog alertDialog = (C.SDK >= 21 ? (new AlertDialog.Builder(this,
            R.style.AlertDialogStyle)) : (new AlertDialog.Builder(this)))
            .setTitle(R.string.dia_title_user)
            .setView(viewGroup)
            .setPositiveButton(R.string.dia_pos_ok, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    if (myCharset.addCharset(ET_CHARSET.getText().toString()))
                    {
                        //Load data in another thread.
                        (new LoadDataTask()).execute();

                        if (C.SDK >= 11)
                        {
                            invalidateOptionsMenu();
                        }
                    }
                    else
                    {
                        setCharsetDialog();
                    }
                }
            })
            .setNegativeButton(R.string.dia_neg_cancel, null)
            .setNeutralButton(R.string.dia_neu_charsets, new DialogInterface.OnClickListener()
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
                            if (!isRunning)
                            {
                                return;
                            }
                            //List all.
                            setCharsetDialog(myCharset.availCharsets);
                        }
                    }, 600);
                }
            })
            .create();
        alertDialog.show();
        
        final Button BT_POS = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        ET_CHARSET.setOnEditorActionListener(new OnEditorActionListener()
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
     * Dialog: User
     * List all system available charsets.
     */
    @TargetApi(11)
    private void setCharsetDialog(final String[] CHARSETS)
    {
        if (CHARSETS == null)
        {
            setCharsetDialog();
            return;
        }

        AlertDialog alertDialog = (C.SDK >= 21 ? (new AlertDialog.Builder(this,
            R.style.AlertDialogStyle)) : (new AlertDialog.Builder(this)))
            .setTitle(R.string.dia_title_user)
            .setItems(CHARSETS, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    if (myCharset.addCharset(CHARSETS[p2]))
                    {
                        //Load data in another thread.
                        (new LoadDataTask()).execute();

                        if (C.SDK >= 11)
                        {
                            invalidateOptionsMenu();
                        }
                    }
                    else
                    {
                        setCharsetDialog();
                    }
                }
            })
            .setPositiveButton(R.string.dia_pos_back, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    setCharsetDialog();
                }
            })
            .setNegativeButton(R.string.dia_neg_cancel, null)
            .create();
        alertDialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_reader, menu);
        
        return true;
    }
    
    /**
     * Call it when expanding the menu every time for API 10-.
     * Call only once after calling "onCreateOptionsMenu" for API 11+.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.getItem(0).setTitle(myCharset.charset + "...");

        menu.getItem(0).getSubMenu()
            .getItem(myCharset.id_charset).setChecked(true);

        return true;
    }

    @Override
    @TargetApi(11)
    public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean changed = false;
        switch (item.getItemId())
        {
            case R.id.action_sub_charset_utf8:
                changed = myCharset.setCharset(0);
                break;
            case R.id.action_sub_charset_gbk:
                changed = myCharset.setCharset(1);
                break;
            case R.id.action_sub_charset_user:
                changed = myCharset.setCharset(2);
        }
        
        if (changed)
        {
            //Load data in another thread.
            (new LoadDataTask()).execute();
            
            if (C.SDK >= 11)
            {
                invalidateOptionsMenu();
            }
        }
        
        return true;
    }
    
    private class MyCharset
    {
        //All system available charsets.
        public String[] availCharsets = null;
        
        //Charsets:
        //    ISO-8859-1, US-ASCII, UTF-16, UTF-16BE, UTF-16LE, UTF-8
        //    GB2313， Big5, GBK, GB18030
        public String[] charsets = { "UTF-8", "GBK", "" };
        
        //Set "UTF-8" as default.
        public int id_charset = 0;
        public String charset = charsets[0];
        
        public MyCharset()
        {
            //Get all system available charsets.
            SortedMap<String, Charset> mapAvailCharsets = Charset.availableCharsets();
            availCharsets = new String[mapAvailCharsets.size()];
            int i = 0;
            for (SortedMap.Entry<String, Charset> entry : Charset.availableCharsets().entrySet())
            {
                availCharsets[i ++] = entry.getKey();
            }
        }
        
        public boolean setCharset(int id_charset)
        {
            if (id_charset == 2)
            {
                setCharsetDialog();
                return false;
            }
            
            if (id_charset > this.charsets.length - 1 || id_charset == this.id_charset)
            {
                return false;
            }
            
            this.id_charset = id_charset;
            this.charset = this.charsets[id_charset];
            
            return true;
        }
        
        public boolean addCharset(String charset)
        {
            boolean result = false;
            if (TextUtils.isEmpty(charset))
            {
                return result;
            }
            
            try
            {
                if (Charset.isSupported(charset))
                {
                    this.charsets[2] = charset;
                    this.id_charset = 2;
                    this.charset = charset;
                
                    result = true;
                }
            }
            catch (IllegalCharsetNameException e)
            {
                e.printStackTrace();
            }
            
            return result;
        }
    }
}
