// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.appunite.mediacodectest.decomp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

// Referenced classes of package com.music.player.mp3player.white.cutter:
//            AudioAAC, AudioAMR, AudioMP3, AudioWAV

public class AudioAnalizer
{
   /* public static interface Factory
    {

        public abstract AudioAnalizer create();

        public abstract String[] getSupportedExtensions();
    }

    public static interface ProgressListener
    {

        public abstract boolean reportProgress(double d1);
    }


    private static final Factory c[] = {
        AudioAAC.getFactory(), AudioAMR.getFactory(), AudioMP3.getFactory(), AudioWAV.getFactory()
    };
    private static final ArrayList d;
    private static final HashMap e;
    private static final char f[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'a', 'b', 'c', 'd', 'e', 'f'
    };
    ProgressListener a;
    File b;

    AudioAnalizer()
    {
        a = null;
        b = null;
    }

    private static String a(byte abyte0[])
    {
        int j = 0;
        char ac[] = new char[abyte0.length * 2];
        for (int i = 0; i < abyte0.length; i++)
        {
            int k = j + 1;
            ac[j] = f[abyte0[i] >>> 4 & 0xf];
            j = k + 1;
            ac[k] = f[abyte0[i] & 0xf];
        }

        return new String(ac);
    }

    public static AudioAnalizer create(String s, ProgressListener progresslistener)
    {
        File file;
        try
        {
            file = new File(s);
            if (!file.exists())
            {
                throw new FileNotFoundException(s);
            }
        }
        // Misplaced declaration of an exception variable
        catch (String s)
        {
            System.out.println("Error Line:89:47   Cheapsoundfile");
            s.printStackTrace();
            return null;
        }
        s = file.getName().toLowerCase(Locale.getDefault()).split("\\.");
        if (s.length < 2)
        {
            return null;
        }
        s = (Factory)e.get(s[s.length - 1]);
        if (s == null)
        {
            return null;
        }
        s = s.create();
        s.a = progresslistener;
        s.ReadFile(file);
        return s;
    }

    public static String[] getSupportedExtensions()
    {
        return (String[])d.toArray(new String[d.size()]);
    }

    public static boolean isFilenameSupported(String s)
    {
        s = s.toLowerCase().split("\\.");
        if (s.length < 2)
        {
            return false;
        } else
        {
            return e.containsKey(s[s.length - 1]);
        }
    }

    void ReadFile(File file)
    {
        b = file;
    }

    public void WriteFile(File file, int i, int j)
    {
    }

    public String computeMd5OfFirst10Frames()
    {
        int i = 0;
        int ai[];
        String s;
        int ai1[];
        MessageDigest messagedigest;
        FileInputStream fileinputstream;
        byte abyte0[];
        int j;
        int k;
        int l;
        int i1;
        int j1;
        try
        {
            ai = getFrameOffsets();
            ai1 = getFrameLens();
            j = ai1.length;
        }
        catch (Exception exception)
        {
            System.out.println("Error Line:221:47   Cheapsoundfile");
            exception.printStackTrace();
            return null;
        }
        if (j > 10)
        {
            j = 10;
        }
        messagedigest = MessageDigest.getInstance("MD5");
        fileinputstream = new FileInputStream(b);
        k = 0;
_L2:
        if (k >= j)
        {
            break; *//* Loop/switch isn't completed *//*
        }
        j1 = ai[k] - i;
        i1 = ai1[k];
        l = i;
        if (j1 <= 0)
        {
            break MISSING_BLOCK_LABEL_97;
        }
        fileinputstream.skip(j1);
        l = i + j1;
        abyte0 = new byte[i1];
        fileinputstream.read(abyte0, 0, i1);
        messagedigest.update(abyte0);
        k++;
        i = l + i1;
        if (true) goto _L2; else goto _L1
_L1:
        fileinputstream.close();
        s = a(messagedigest.digest());
        return s;
    }

    public int getAvgBitrateKbps()
    {
        return 0;
    }

    public int getChannels()
    {
        return 0;
    }

    public int getFileSizeBytes()
    {
        return 0;
    }

    public String getFiletype()
    {
        return "Unknown";
    }

    public int[] getFrameGains()
    {
        return null;
    }

    int[] getFrameLens()
    {
        return null;
    }

    int[] getFrameOffsets()
    {
        return null;
    }

    public int getNumFrames()
    {
        return 0;
    }

    public int getSampleRate()
    {
        return 0;
    }

    public int getSamplesPerFrame()
    {
        return 0;
    }

    public int getSeekableFrameOffset(int i)
    {
        return -1;
    }

    static 
    {
        d = new ArrayList();
        e = new HashMap();
        Factory afactory[] = c;
        int k = afactory.length;
        for (int i = 0; i < k; i++)
        {
            Factory factory = afactory[i];
            String as[] = factory.getSupportedExtensions();
            int l = as.length;
            for (int j = 0; j < l; j++)
            {
                String s = as[j];
                d.add(s);
                e.put(s, factory);
            }

        }

    }*/
}
