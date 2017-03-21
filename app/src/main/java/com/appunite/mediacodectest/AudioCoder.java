package com.appunite.mediacodectest;


import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioCoder {

    private static final String TAG = AudioCoder.class.getSimpleName();

    @NonNull
    public MediaExtractor getMediaExtractor(final AssetFileDescriptor fileDescriptor) throws IOException {
        final MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
        fileDescriptor.close();
        return extractor;
    }

    @NonNull
    public MediaExtractor getMediaExtractor(@NonNull final String filePath) throws IOException {
        final MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(filePath);
        return extractor;
    }

    @NonNull
    public DecodedData decodeAudio(final MediaExtractor extractor) throws Exception {
        return decodeAudio(extractor, 0, false);
    }

    @NonNull
    public DecodedData decodeAudio(final MediaExtractor extractor, final float seekToPercent, boolean isCheapDecode) throws Exception {
        final MediaFormat format = extractor.getTrackFormat(0);
        final String mime = format.getString(MediaFormat.KEY_MIME);

        final MediaCodec codec = MediaCodec.createDecoderByType(mime);
        codec.configure(format, null, null, 0);
        codec.start();

        final ByteBuffer[] codecInputBuffers = codec.getInputBuffers();
        ByteBuffer[] codecOutputBuffers = codec.getOutputBuffers();

        extractor.selectTrack(0);

        final MediaFormat trackFormat = extractor.getTrackFormat(0);
        final int sampleRate = trackFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        final int channelsCount = trackFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
        final boolean hasPcmEncoding = trackFormat.containsKey(MediaFormat.KEY_PCM_ENCODING);

        final int pcmEncoding;
        if (!hasPcmEncoding) {
            pcmEncoding = AudioFormat.ENCODING_PCM_16BIT;
        } else {
            pcmEncoding = trackFormat.getInteger(MediaFormat.KEY_PCM_ENCODING);
        }

        final long durationUs = trackFormat.getLong(MediaFormat.KEY_DURATION);
        final long seekToUs = (long) ((float) durationUs * seekToPercent);
        extractor.seekTo(seekToUs, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);

        // start decoding
        final long kTimeOutUs = 500;
        final MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean sawInputEOS = false;
        boolean sawOutputEOS = false;

        final ByteArrayOutputStream decodedBytes = new ByteArrayOutputStream();

        while (!sawOutputEOS) {
            if (!sawInputEOS) {
                int inputBufIndex = codec.dequeueInputBuffer(kTimeOutUs);
                if (inputBufIndex >= 0) {
                    ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];
                    int sampleSize = extractor.readSampleData(dstBuf, 0);
                    long presentationTimeUs = 0;
                    if (sampleSize < 0) {
                        Log.d(TAG, "saw input EOS.");
                        sawInputEOS = true;
                        sampleSize = 0;
                    } else {
                        presentationTimeUs = extractor.getSampleTime();
                    }
                    codec.queueInputBuffer(
                            inputBufIndex,
                            0 /* offset */,
                            sampleSize,
                            presentationTimeUs,
                            sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);

                    if (!sawInputEOS) {
                        if (isCheapDecode) {
                            extractor.seekTo(presentationTimeUs + DateUtils.SECOND_IN_MILLIS * 1000L, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                        } else {
                            extractor.advance();
                        }
                    }
                }
            }

            int res = codec.dequeueOutputBuffer(info, kTimeOutUs);
            if (res >= 0) {
                int outputBufIndex = res;
                final ByteBuffer outputBuffer = codecOutputBuffers[outputBufIndex];
                final byte[] chunk = new byte[info.size];
                outputBuffer.get(chunk); // Read the buffer all at once
                outputBuffer.clear();

                decodedBytes.write(chunk);

                codec.releaseOutputBuffer(outputBufIndex, false);

                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    Log.d(TAG, "saw output EOS.");
                    sawOutputEOS = true;
                }
            } else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                codecOutputBuffers = codec.getOutputBuffers();
            } else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat oformat = codec.getOutputFormat();
                Log.d(TAG, "output format has changed to " + oformat);
            }
        }

        codec.stop();
        codec.release();

        final short bitsPerSample = (short) (pcmEncoding == AudioFormat.ENCODING_PCM_16BIT ? 16 : 8);
        return new DecodedData(decodedBytes.toByteArray(), (short) channelsCount, sampleRate, pcmEncoding, bitsPerSample);
    }

    public void rawToWave(@NonNull final DecodedData decodedData, @NonNull final File outputFile) throws IOException {
        final short bitsPerSample = decodedData.bitsPerSample;
        final byte[] decodedPCMData = decodedData.decodedPCMData;
        final short channelsNum = decodedData.channelsNum;
        final int sampleRate = decodedData.sampleRate;

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(outputFile));
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + decodedPCMData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, channelsNum); // number of channels
            writeInt(output, sampleRate); // sample rate
            writeInt(output, sampleRate * channelsNum * bitsPerSample / 8); // byte rate
            writeShort(output, (short) (channelsNum * bitsPerSample / 8)); // block align
            writeShort(output, bitsPerSample); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, decodedPCMData.length); // subchunk 2 size

            output.write(decodedPCMData);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }

    public static class DecodedData {
        final byte[] decodedPCMData; // big-endian !
        final short channelsNum;
        final int sampleRate;
        final int pcmEncoding;
        final short bitsPerSample;

        public DecodedData(final byte[] decodedPCMData,
                           final short channelsNum,
                           final int sampleRate,
                           final int pcmEncoding,
                           final short bitsPerSample) {
            this.decodedPCMData = decodedPCMData;
            this.channelsNum = channelsNum;
            this.sampleRate = sampleRate;
            this.pcmEncoding = pcmEncoding;
            this.bitsPerSample = bitsPerSample;
        }
    }
}
