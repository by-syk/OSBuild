/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.by_syk.osbuild.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.Window;

public class Compass extends GraphicsActivity
{
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SampleView mView;
    private float[] mValues;

    private final SensorEventListener mListener = new SensorEventListener()
    {
        public void onSensorChanged(SensorEvent event)
        {
            mValues = event.values;
            if (mView != null)
            {
                mView.invalidate();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {}
    };

    @Override
    protected void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        
        // We don't need a title either.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mView = new SampleView(this);
        setContentView(mView);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        mSensorManager.registerListener(mListener, mSensor,
            SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop()
    {
        mSensorManager.unregisterListener(mListener);
        
        super.onStop();
    }

    private class SampleView extends View
    {
        private Paint mPaint = new Paint();
        private Path mPath = new Path();
        
        public SampleView(Context context)
        {
            super(context);
            
            float density = getResources().getDisplayMetrics().density;

            // Construct a wedge-shaped path
            mPath.moveTo(0, -25 * density);
            mPath.lineTo(-10 * density, 30 * density);
            mPath.lineTo(0, 25 * density);
            mPath.lineTo(10 * density, 30 * density);
            mPath.close();
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            Paint paint = mPaint;

            //canvas.drawColor(Color.BLACK);

            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);

            int w = canvas.getWidth();
            int h = canvas.getHeight();
            int cx = w / 2;
            int cy = h / 2;

            canvas.translate(cx, cy);
            if (mValues != null)
            {
                canvas.rotate(-mValues[0]);
            }
            canvas.drawPath(mPath, mPaint);
        }

        @Override
        protected void onAttachedToWindow()
        {
            super.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow()
        {
            super.onDetachedFromWindow();
        }
    }
}
