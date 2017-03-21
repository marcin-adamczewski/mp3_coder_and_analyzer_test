package com.appunite.mediacodectest;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AmpChart extends View {

    private Paint paint;
    private int drawWidth;
    private float lineOffset;
    private int offsetInSamples;
    private List<Float> firstChannelSamples;
    private List<Float> secondChannelSamples;
    private int widthOfSamplePx;

    public AmpChart(final Context context) {
        super(context);
        init();
    }

    public AmpChart(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AmpChart(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("NewApi")
    public AmpChart(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        final int sidePadding = getResources().getDimensionPixelSize(R.dimen.amp_chart_padding);
        setPaddingRelative(sidePadding, 0, sidePadding, 0);
        widthOfSamplePx = getResources().getDimensionPixelSize(R.dimen.amp_chart_line_width) + getResources().getDimensionPixelSize(R.dimen.amp_samples_spacing);
        drawWidth = getResources().getDisplayMetrics().widthPixels - getPaddingStart() - getPaddingEnd();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(widthOfSamplePx);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.amp_chart));
    }

    public void bindData(@NonNull final List<Float> firstChannelSamples,
                         @NonNull final List<Float> secondChannelSamples) {
        this.firstChannelSamples = firstChannelSamples;
        this.secondChannelSamples = secondChannelSamples;

        final float maxSamplesThatCanBeDisplayed = drawWidth / widthOfSamplePx;
        final float samplesPerSampleWidth = (float) firstChannelSamples.size() / maxSamplesThatCanBeDisplayed;

        offsetInSamples = (int) Math.ceil(samplesPerSampleWidth);

        lineOffset = convertPixelsToDp(samplesPerSampleWidth >= 1 ? widthOfSamplePx : widthOfSamplePx / samplesPerSampleWidth);

        Log.d("lol", "samplesPerChannel: " + firstChannelSamples.size() + "   samplesPerSampleWidth: " + samplesPerSampleWidth +
                "      offsetInSamples: " + offsetInSamples + "   lineOffset: " + lineOffset);

        invalidate();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (firstChannelSamples == null || firstChannelSamples.size() == 0) {
            return;
        }

        for (int offset = 0, iteration = 0; offset < firstChannelSamples.size(); offset = offset + offsetInSamples, iteration++) {
            canvas.drawLine(
                    iteration * lineOffset,
                    (getHeight() / 2) - (getHeight() / 2 * Math.abs(findAbsMax(firstChannelSamples, offset, offset + offsetInSamples))),
                    iteration * lineOffset,
                    getHeight() / 2,
                    paint);

            if (offset < secondChannelSamples.size()) {
                canvas.drawLine(
                        iteration * lineOffset,
                        getHeight() / 2,
                        iteration * lineOffset,
                        (getHeight() / 2) + (getHeight() / 2 * Math.abs(findAbsMax(secondChannelSamples, offset, offset + offsetInSamples))),
                        paint);
            }
        }
    }

    private float findAbsMax(List<Float> allSamples, int offset, int limit) {
        if (limit >= allSamples.size()) {
            return 0;
        }

        final List<Float> floats = allSamples.subList(offset, limit);

        return Collections.max(floats, new Comparator<Float>() {
            @Override
            public int compare(final Float o1, final Float o2) {
                return Math.abs(o1) > Math.abs(o2) ?
                        1 :
                        o1.equals(o2) ? 0 : -1;
            }
        });
    }

    public float convertPixelsToDp(float dp){
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

}
