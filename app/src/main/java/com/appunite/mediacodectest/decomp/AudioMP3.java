// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.appunite.mediacodectest.decomp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

// Referenced classes of package com.music.player.mp3player.white.cutter:
//            AudioAnalizer

public class AudioMP3 extends AudioAnalizer
{

   /* private static final int k[] = {
        0, 32, 40, 48, 56, 64, 80, 96, 112, 128, 
        160, 192, 224, 256, 320, 0
    };
    private static final int l[] = {
        0, 8, 16, 24, 32, 40, 48, 56, 64, 80, 
        96, 112, 128, 144, 160, 0
    };
    private static final int sampleRates[] = {
        44100, 48000, 32000, 0
    };
    private static final int sampleRatePerChannel[] = {
        22050, 24000, 16000, 0
    };
    private int numFrames;
    private int frameOffsets[];
    private int frameLens[];
    private int frameGains[];
    private int fileSize;
    private int avgBitRate;
    private int sampleRate;
    private int channels;

    private AudioMP3()
    {
    }

    AudioMP3(byte byte0)
    {
        this();
    }

    public static AudioAnalizer.Factory getFactory()
    {
        return new AudioAnalizer.Factory() {

            public final AudioAnalizer create()
            {
                return new AudioMP3((byte)0);
            }

            public final String[] getSupportedExtensions()
            {
                return (new String[] {
                    "mp3"
                });
            }

        };
    }

    public void ReadFile(File file)
    {
        super.ReadFile(file);
        numFrames = 0;
        frameOffsets = new int[64];
        frameLens = new int[64];
        frameGains = new int[64];
        fileSize = (int)b.length();
        file = new FileInputStream(b);
        int i1 = 0;
        byte abyte0[] = new byte[12];
        int j1;
        int k1;
        int l1;
        j1 = 0;
        l1 = 0;
        k1 = 64;
//_L18:
        if (j1 >= fileSize - 12) goto _L2; else goto _L1
//_L1:
        if (i1 >= 12)
        {
            break; *//* Loop/switch isn't completed *//*
        }
        i1 += file.read(abyte0, i1, 12 - i1);
        if (true) goto _L1; else goto _L3
//_L13:
        if (a != null && !a.reportProgress(((double)j1 * 1.0D) / (double) fileSize)) goto _L2; else goto _L4
//_L14:
        if (i1 != 1) goto _L6; else goto _L5
_L5:
        int i2;
        int j2;
        j2 = k[(abyte0[2] & 0xf0) >> 4];
        i2 = sampleRates[(abyte0[2] & 0xc) >> 2];
          goto _L7
_L6:
        j2 = l[(abyte0[2] & 0xf0) >> 4];
        i2 = sampleRatePerChannel[(abyte0[2] & 0xc) >> 2];
          goto _L7
_L16:
        sampleRate = i2;
        int k2 = abyte0[2];
        k2 = (j2 * 144 * 1000) / i2 + ((k2 & 2) >> 1);
        if ((abyte0[3] & 0xc0) != 192) goto _L9; else goto _L8
_L8:
        channels = 1;
        int ai[];
        int ai1[];
        int ai2[];
        if (i1 == 1)
        {
            i1 = ((abyte0[10] & 1) << 7) + ((abyte0[11] & 0xfe) >> 1);
        } else
        {
            i1 = ((abyte0[9] & 3) << 6) + ((abyte0[10] & 0xfc) >> 2);
        }
_L12:
        l1 = j2 + l1;
        frameOffsets[numFrames] = j1;
        frameLens[numFrames] = k2;
        frameGains[numFrames] = i1;
        numFrames = numFrames + 1;
        i1 = k1;
        if (numFrames != k1)
        {
            break MISSING_BLOCK_LABEL_536;
        }
        avgBitRate = l1 / numFrames;
        i1 = ((((fileSize / avgBitRate) * i2) / 0x23280) * 11) / 10;
        if (i1 < k1 * 2)
        {
            i1 = k1 * 2;
        }
        ai = new int[i1];
        ai1 = new int[i1];
        ai2 = new int[i1];
        k1 = 0;
_L11:
        if (k1 >= numFrames)
        {
            break; *//* Loop/switch isn't completed *//*
        }
        ai[k1] = frameOffsets[k1];
        ai1[k1] = frameLens[k1];
        ai2[k1] = frameGains[k1];
        k1++;
        if (true) goto _L11; else goto _L10
_L9:
        channels = 2;
        if (i1 == 1)
        {
            i1 = ((abyte0[9] & 0x7f) << 1) + ((abyte0[10] & 0x80) >> 7);
        } else
        {
            i1 = 0;
        }
        if (true) goto _L12; else goto _L10
_L10:
        frameOffsets = ai;
        frameLens = ai1;
        frameGains = ai2;
        file.skip(k2 - 12);
        i2 = 0;
        j1 += k2;
        k1 = i1;
        i1 = i2;
        continue; *//* Loop/switch isn't completed *//*
_L2:
        if (numFrames > 0)
        {
            avgBitRate = l1 / numFrames;
            return;
        }
        try
        {
            avgBitRate = 0;
            return;
        }
        // Misplaced declaration of an exception variable
        catch (File file)
        {
            System.out.println("Error Line:278:47   CheapMp3");
        }
        file.printStackTrace();
        return;
_L3:
        i1 = 0;
        while (i1 < 12 && abyte0[i1] != -1) 
        {
            i1++;
        }
          goto _L13
_L4:
        if (i1 > 0)
        {
            for (i2 = 0; i2 < 12 - i1; i2++)
            {
                abyte0[i2] = abyte0[i1 + i2];
            }

            i2 = 12 - i1;
            j1 += i1;
            i1 = i2;
            continue; *//* Loop/switch isn't completed *//*
        }
        if (abyte0[1] == -6 || abyte0[1] == -5)
        {
            i1 = 1;
        } else
        if (abyte0[1] == -14 || abyte0[1] == -13)
        {
            i1 = 2;
        } else
        {
            for (i1 = 0; i1 < 11; i1++)
            {
                abyte0[i1] = abyte0[i1 + 1];
            }

            i1 = 11;
            j1++;
            continue; *//* Loop/switch isn't completed *//*
        }
          goto _L14
_L7:
        if (j2 != 0 && i2 != 0) goto _L16; else goto _L15
_L15:
        for (i1 = 0; i1 < 10; i1++)
        {
            abyte0[i1] = abyte0[i1 + 2];
        }

        i1 = 10;
        j1 += 2;
        if (true) goto _L18; else goto _L17
_L17:
    }

    public void WriteFile(File file, int i1, int j1)
    {
        FileInputStream fileinputstream;
        (new StringBuilder("InputFile:")).append(b.toString());
        (new StringBuilder("Output File:")).append(file.toString());
        file.createNewFile();
        fileinputstream = new FileInputStream(b);
        file = new FileOutputStream(file);
        int k1;
        int l1;
        k1 = 0;
        l1 = 0;
_L7:
        if (k1 >= j1) goto _L2; else goto _L1
_L1:
        int i2 = l1;
        if (frameLens[i1 + k1] > l1)
        {
            i2 = frameLens[i1 + k1];
        }
          goto _L3
_L2:
        byte abyte0[] = new byte[l1];
        i2 = 0;
        k1 = 0;
_L6:
        if (i2 >= j1) goto _L5; else goto _L4
_L4:
        l1 = frameOffsets[i1 + i2];
        int j2;
        j2 = l1 - k1;
        long l2;
        try
        {
            l1 = frameLens[i1 + i2];
        }
        catch (Exception exception)
        {
            l1 = j2;
            break MISSING_BLOCK_LABEL_270;
        }
_L8:
        if (j2 <= 0)
        {
            break MISSING_BLOCK_LABEL_180;
        }
        l2 = j2;
        fileinputstream.skip(l2);
        k1 = j2 + k1;
        fileinputstream.read(abyte0, 0, l1);
        file.write(abyte0, 0, l1);
        k1 += l1;
        i2++;
          goto _L6
_L5:
        try
        {
            fileinputstream.close();
            file.close();
            return;
        }
        // Misplaced declaration of an exception variable
        catch (File file)
        {
            System.out.println("Error Line:310:47   CheapMp3");
        }
        file.printStackTrace();
        return;
_L3:
        k1++;
        l1 = i2;
          goto _L7
        Exception exception1;
        exception1;
        l1 = 0;
        boolean flag = false;
        j2 = l1;
        l1 = ((flag) ? 1 : 0);
          goto _L8
    }

    public int getAvgBitrateKbps()
    {
        return avgBitRate;
    }

    public int getChannels()
    {
        return channels;
    }

    public int getFileSizeBytes()
    {
        return fileSize;
    }

    public String getFiletype()
    {
        return "MP3";
    }

    public int[] getFrameGains()
    {
        return frameGains;
    }

    public int[] getFrameLens()
    {
        return frameLens;
    }

    public int[] getFrameOffsets()
    {
        return frameOffsets;
    }

    public int getNumFrames()
    {
        return numFrames;
    }

    public int getSampleRate()
    {
        return sampleRate;
    }

    public int getSamplesPerFrame()
    {
        return 1152;
    }

    public int getSeekableFrameOffset(int i1)
    {
        if (i1 <= 0)
        {
            return 0;
        }
        int j1;
        try
        {
            if (i1 >= numFrames)
            {
                return fileSize;
            }
            j1 = frameOffsets[i1];
        }
        catch (Exception exception)
        {
            System.out.println("Error Line:107:47   CheapMp3");
            exception.printStackTrace();
            return i1;
        }
        return j1;
    }*/

}
