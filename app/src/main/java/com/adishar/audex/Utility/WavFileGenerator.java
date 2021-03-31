package com.adishar.audex.Utility;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;

public class WavFileGenerator {

    public static void saveWavFile(Context context,int stretch,int[] values,String fileName)
    {
        FileOutputStream outputWav = null;
        ByteBuffer byteBuffer = null;

        byte[] RIFF_HEADER=new byte[]{0x52,0x49,0x46,0x46};
        byte[] FORMAT_WAVE=new byte[]{0x57,0x41,0x56,0x45};
        byte[] FORMAT_TAG=new byte[]{0x66,0x6D,0x74,0x20};
        byte[] AUDIO_FORMAT=new byte[]{0x1,0x0};
        byte[] SUBCHUNK_ID=new byte[]{0x64,0x61,0x74,0x61};
        int BYTES_PER_SAMPLE=1;
        int sampleRate=16000;
        int channelcount=1;


        int dataLength=values.length*stretch*BYTES_PER_SAMPLE;
        int byteRate=sampleRate*channelcount*BYTES_PER_SAMPLE;
        int blockAlign=channelcount*BYTES_PER_SAMPLE;

        //Load output stream
        try {
            outputWav = context.openFileOutput(fileName+".wav", Context.MODE_PRIVATE);



            //Initializing wav files with meta data
            outputWav.write(RIFF_HEADER,0,RIFF_HEADER.length);
            byteBuffer = ByteBuffer.allocate(Integer.BYTES);
            outputWav.write(byteBuffer.putInt(dataLength+44).array(),0,4);
            outputWav.write(FORMAT_WAVE,0,FORMAT_WAVE.length);
            outputWav.write(FORMAT_TAG,0,FORMAT_TAG.length);
            byteBuffer = ByteBuffer.allocate(Integer.BYTES);
            outputWav.write(byteBuffer.putInt(16).array(),0,4);
            outputWav.write(AUDIO_FORMAT,0,AUDIO_FORMAT.length);
            byteBuffer = ByteBuffer.allocate(Integer.BYTES);
            outputWav.write(byteBuffer.putInt(channelcount).array(),2,2);
            byteBuffer = ByteBuffer.allocate(Integer.BYTES);
            outputWav.write(byteBuffer.putInt(sampleRate).array(),0,4);
            byteBuffer = ByteBuffer.allocate(Integer.BYTES);
            outputWav.write(byteBuffer.putInt(byteRate).array(),0,4);
            byteBuffer = ByteBuffer.allocate(Integer.BYTES);
            outputWav.write(byteBuffer.putInt(blockAlign).array(),2,2);
            byteBuffer = ByteBuffer.allocate(Integer.BYTES);
            outputWav.write(byteBuffer.putInt((BYTES_PER_SAMPLE*8)).array(),2,2);
            outputWav.write(SUBCHUNK_ID,0,SUBCHUNK_ID.length);
            byteBuffer = ByteBuffer.allocate(Integer.BYTES);
            outputWav.write(byteBuffer.putInt(dataLength).array(),0,4);


            //Writing the extracted wave data to wav file

            int minVal=MinMaxPrimitiveInteger.findMinInteger(values);
            int maxVal=MinMaxPrimitiveInteger.findMaxInteger(values);
            int lastV=0;

            for(int v:values)
            {
                double scaledV=(v- minVal)/(double)(maxVal-minVal)*255;

                for(int x=0;x<stretch;x++)
                {
                    double finalV=x/(double)stretch*scaledV+(1-x/(double)stretch)*lastV;
                    int convert=(int)finalV;
                    byteBuffer = ByteBuffer.allocate(Integer.BYTES);
                    outputWav.write(byteBuffer.putInt(convert).array()[3]);
                }
                lastV=(int)scaledV;
            }


        } catch (FileNotFoundException e) {
            Log.d("Halwa",e.toString());
        } catch (IOException e) {
            Log.d("Halwa",e.toString());
        } finally {
            if(outputWav!=null)
            {
                try {
                    outputWav.close();
                } catch (IOException e) {
                    Log.d("Halwa",e.toString());
                }
            }
        }
    }
}
