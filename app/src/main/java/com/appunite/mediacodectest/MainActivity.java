package com.appunite.mediacodectest;

import android.Manifest;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "lol";
    private MediaPlayer mediaPlayer;
    private AmpChart ampChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ampChart = (AmpChart) findViewById(R.id.amp_chart);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        mediaPlayer = new MediaPlayer();

        findViewById(R.id.dupa_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                decodeAndPlay();
            }
        });

        findViewById(R.id.amp_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                drawAmpChart();
            }
        });

    }

    private void drawAmpChart() {
        try {
            final File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/dupa.wav");
            if (outputFile.exists()) {
                outputFile.delete();
            }
            final boolean fileCreated = outputFile.createNewFile();
            Log.d(TAG, "outputFile created: " + fileCreated + " in path: " + outputFile.getAbsolutePath());
            Log.d(TAG, "Decode started");
            final AudioCoder audioCoder = new AudioCoder();
            final AssetFileDescriptor testFile = getResources().openRawResourceFd(R.raw.zintest);
            final AudioCoder.DecodedData decodeData = audioCoder.decodeAudio(audioCoder.getMediaExtractor(testFile), 0, true);

            final byte[] decodedPCMData = decodeData.decodedPCMData;
            Log.d(TAG, "decoded bytes length: " + decodedPCMData.length);
            final int bytesPerSample = decodeData.bitsPerSample / 8;
            Log.d(TAG, "bytes per sample: " + bytesPerSample);
            final int samples = decodedPCMData.length / bytesPerSample;
            Log.d(TAG, "samples: " + samples);

            final List<Float> firstChannelSamples = new ArrayList<>();
            final List<Float> secondChannelSamples = new ArrayList<>();
            final float maxSampleValue = (float) (Math.pow(2, decodeData.bitsPerSample) - 1);

            if (bytesPerSample == 2) {
                final ShortBuffer shortBuffer = ByteBuffer.wrap(decodedPCMData).order(ByteOrder.nativeOrder()).asShortBuffer();
                while (shortBuffer.position() + 1 < shortBuffer.limit()) {
                    final float lValue = (float) shortBuffer.get() / maxSampleValue;
                    final float rValue = (float) shortBuffer.get() / maxSampleValue;

                    firstChannelSamples.add(lValue);
                    if (decodeData.channelsNum == 2) {
                        secondChannelSamples.add(rValue);
                    } else {
                        firstChannelSamples.add(rValue);
                    }
                }
            } /*else { // TODO for 8, 24, 32 bit PCM

            }*/

            ampChart.bindData(firstChannelSamples, secondChannelSamples);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "error: " + e.getMessage());
        }
    }

    private void decodeAndPlay() {
        try {
            final File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/dupa.wav");
            if (outputFile.exists()) {
                outputFile.delete();
            }

            final boolean fileCreated = outputFile.createNewFile();
            Log.d(TAG, "outputFile created: " + fileCreated + " in path: " + outputFile.getAbsolutePath());

            Log.d(TAG, "Decode started");
            final AudioCoder audioCoder = new AudioCoder();
            final AssetFileDescriptor testFile = getResources().openRawResourceFd(R.raw.zintest);
            final AudioCoder.DecodedData decodeData = audioCoder.decodeAudio(audioCoder.getMediaExtractor(testFile), 0.6f, false);
            
            audioCoder.rawToWave(decodeData, outputFile);

            playWavAudio(outputFile);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "error: " + e.getMessage());
        }
    }

    private void playWavAudio(final File file) throws IOException {
        Log.d(TAG, "Loading media started");
        mediaPlayer.reset();
        mediaPlayer.setDataSource(this, Uri.fromFile(file));
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                Log.d(TAG, "Loading media finished");
            }
        });
        mediaPlayer.prepare();
        mediaPlayer.start();
    }
}
