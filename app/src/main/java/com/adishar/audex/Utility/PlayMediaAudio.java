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
    public static void playAudioFile(Context context, String fileNameWithExtension){
        resourceInUse = true;

        // Build the file path
        File file = new File(context.getFilesDir(), fileNameWithExtension);
        Uri uri = Uri.fromFile(file);

        // Log the URI to ensure it's correct
        Log.d("PlayMediaAudio", "File URI: " + uri.toString());

        // Check if the file exists before attempting to play it
        if (file.exists()) {
            // Create and play media from the file URI
            mediaPlayer = MediaPlayer.create(context, uri);

            if (mediaPlayer != null) {
                mediaPlayer.start();
            } else {
                Log.e("PlayMediaAudio", "MediaPlayer failed to create for URI: " + uri.toString());
            }
        } else {
            Log.e("PlayMediaAudio", "File not found: " + file.getAbsolutePath());
        }

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
