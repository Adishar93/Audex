package com.adishar.audex.Utility;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.adishar.audex.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PlayMediaAudio {

    public static MediaPlayer mediaPlayer=null;
    public static boolean resourceInUse=false;
    public static void playAudioFile(Context context, String fileNameWithExtension)
    {
        resourceInUse=true;

            Uri uri=Uri.fromFile(new File(context.getFilesDir(), fileNameWithExtension));
            Log.d("halwa",uri.toString());
            //FileInputStream wavFile=new FileInputStream(context.getFileStreamPath(fileNameWithExtension));
            mediaPlayer=MediaPlayer.create(context,uri);
            mediaPlayer.start();

    }

    public static void releaseMediaPlayerResource()
    {
        if(mediaPlayer!=null&&resourceInUse)
        {
            mediaPlayer.release();
            resourceInUse=false;
        }

    }
}
