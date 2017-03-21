package com.appunite.mediacodectest;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.semantive.waveformandroid.waveform.soundfile.CheapSoundFile;

public class AmpChartLib extends View {

    private Paint paint;
    private int drawWidth;
    private float lineOffset;
    private float offsetInSamples;
    private int widthOfSampleWithPaddingPx;
    private CheapSoundFile cheapSoundFile;
    private float range;
    private float minGain;

    public AmpChartLib(final Context context) {
        super(context);
        init();
    }

    public AmpChartLib(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AmpChartLib(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("NewApi")
    public AmpChartLib(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        final int sidePadding = getResources().getDimensionPixelSize(R.dimen.amp_chart_padding);
        setPaddingRelative(sidePadding, 0, sidePadding, 0);
        int widthOfSamplePx = getResources().getDimensionPixelSize(R.dimen.amp_chart_line_width);
        widthOfSampleWithPaddingPx = widthOfSamplePx + getResources().getDimensionPixelSize(R.dimen.amp_samples_spacing);
        drawWidth = getResources().getDisplayMetrics().widthPixels - getPaddingStart() - getPaddingEnd();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(widthOfSamplePx);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.amp_chart));
    }

    public void bindData(@NonNull final CheapSoundFile cheapSoundFile) {
        this.cheapSoundFile = cheapSoundFile;
        computeRange();
        final float maxSamplesThatCanBeDisplayed = drawWidth / widthOfSampleWithPaddingPx;
        final float samplesPerSampleWidth = (float) cheapSoundFile.getNumFrames() / maxSamplesThatCanBeDisplayed;

        offsetInSamples = samplesPerSampleWidth;

        lineOffset = samplesPerSampleWidth >= 1 ? widthOfSampleWithPaddingPx : widthOfSampleWithPaddingPx / samplesPerSampleWidth;

        Log.d("lol", "numFrames: " + cheapSoundFile.getNumFrames() + "   samplesPerSampleWidth: " + samplesPerSampleWidth +
                "      offsetInSamples: " + offsetInSamples + "   lineOffset: " + lineOffset);

        invalidate();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (cheapSoundFile == null) {
            return;
        }

        final int centerY = getMeasuredHeight() / 2;

        for (int offset = 0, iteration = 0; offset < cheapSoundFile.getNumFrames(); offset = countOffsetForIteration(iteration, offset), iteration++) {
            int h = (int) (getZoomedOutHeight(offset) * (float) this.getMeasuredHeight() / 2.0F);
            canvas.drawLine(iteration * lineOffset, centerY - h, iteration * lineOffset, centerY + h, paint);
        }
    }

    private int countOffsetForIteration(int iteration, int offset) {
        return (int) (iteration % 2 == 0 ?
                        Math.ceil(offset + offsetInSamples) :
                        Math.floor(offset + offsetInSamples));
    }

    protected float getGain(int i, int numFrames, int[] frameGains) {
        int x = Math.min(i, numFrames - 1);
        if (numFrames < 2) {
            return frameGains[x];
        } else {
            if (x == 0) {
                return (frameGains[0] / 2.0f) + (frameGains[1] / 2.0f);
            } else if (x == numFrames - 1) {
                return (frameGains[numFrames - 2] / 2.0f) + (frameGains[numFrames - 1] / 2.0f);
            } else {
                return (frameGains[x - 1] / 3.0f) + (frameGains[x] / 3.0f) + (frameGains[x + 1] / 3.0f);
            }
        }
    }

    protected float getHeight(int frame, int numFrames, int[] frameGains, float minGain, float range) {
        float value = (getGain(frame, numFrames, frameGains) - minGain) / range;
        if (value < 0.0 || value > 1.0) {
            value = 0.0f;
        }
        return value;
    }

    protected float getZoomedOutHeight(int frame) {
        float x1 = getHeight(frame, cheapSoundFile.getNumFrames(), cheapSoundFile.getFrameGains(), minGain, range);
        float x2 = getHeight(frame + 1, cheapSoundFile.getNumFrames(), cheapSoundFile.getFrameGains(), minGain, range);
        return 0.5F * (x1 + x2);
    }

    protected void computeRange() {
        int numFrames = cheapSoundFile.getNumFrames();

        // Make sure the range is no more than 0 - 255
        float maxGain = 1.0f;
        for (int i = 0; i < numFrames; i++) {
            float gain = getGain(i, numFrames, cheapSoundFile.getFrameGains());
            if (gain > maxGain) {
                maxGain = gain;
            }
        }

        // Build histogram of 256 bins and figure out the new scaled max
        maxGain = 0;
        int gainHist[] = new int[256];
        for (int i = 0; i < numFrames; i++) {
            int smoothedGain = (int) getGain(i, numFrames, cheapSoundFile.getFrameGains());
            if (smoothedGain < 0)
                smoothedGain = 0;
            if (smoothedGain > 255)
                smoothedGain = 255;

            if (smoothedGain > maxGain)
                maxGain = smoothedGain;

            gainHist[smoothedGain]++;
        }

        // Re-calibrate the min to be 5%
        minGain = 0;
        int sum = 0;
        while (minGain < 255 && sum < numFrames / 20) {
            sum += gainHist[(int) minGain];
            minGain++;
        }

        // Re-calibrate the max to be 99%
        sum = 0;
        while (maxGain > 2 && sum < numFrames / 100) {
            sum += gainHist[(int) maxGain];
            maxGain--;
        }

        range = maxGain - minGain;
    }

}
