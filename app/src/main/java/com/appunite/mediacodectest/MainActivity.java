package com.appunite.mediacodectest;

import android.Manifest;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.semantive.waveformandroid.waveform.Segment;
import com.semantive.waveformandroid.waveform.WaveformFragment;
import com.semantive.waveformandroid.waveform.soundfile.CheapSoundFile;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "lol";
    private MediaPlayer mediaPlayer;
    private AmpChart ampChart;
    private AmpChartLib ampChartLib;
    private CheapSoundFile cheapSoundFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ampChart = (AmpChart) findViewById(R.id.amp_chart);
        ampChartLib = (AmpChartLib) findViewById(R.id.amp_chart_lib);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        mediaPlayer = new MediaPlayer();

        findViewById(R.id.decode_btn).setOnClickListener(new View.OnClickListener() {
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

        findViewById(R.id.amp_lib_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                drawAmpChartUsingLib();
            }
        });

    }

    private void drawAmpChartUsingLib() {
        try {
            cheapSoundFile = CheapSoundFile.create(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/zintest.mp3").getAbsolutePath(), null);
            ampChartLib.bindData(cheapSoundFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new CustomWaveformFragment())
                .commit();
    }

    public static class CustomWaveformFragment extends WaveformFragment {

        @Override
        protected String getFileName() {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/zintest.mp3";
        }

        @Override
        protected List<Segment> getSegments() {
            return Arrays.asList(
                    new Segment(55.2, 55.8, Color.rgb(238, 23, 104)),
                    new Segment(56.2, 56.6, Color.rgb(238, 23, 104)),
                    new Segment(58.4, 59.9, Color.rgb(184, 92, 184)));
        }
    }

    private void drawAmpChart() {
        try {
            final File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/yyy.wav");
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
