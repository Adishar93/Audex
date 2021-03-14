package com.adishar.audex;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int BROWSE_IMAGE=99;
    private static final int OPEN_CAMERA=999;

    private Button bOpenCamera;
    private Button bBrowseImage;
    private Button bPerformProcessing;
    private ImageView ivOriginal;
    private TextView tvOriginal;
    private TextView tvProcessed;
    private TextView tvProgressTag;

    private AlertDialog aErrorAlert;
    private GraphView gvGraph;
    private ProgressBar pbProcessingProgress;

    private Uri mFileUri;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bOpenCamera=findViewById(R.id.bOpenCamera);
        bBrowseImage=findViewById(R.id.bBrowseImage);

        bPerformProcessing=findViewById(R.id.bPerformProcessing);
        bPerformProcessing.setVisibility(View.INVISIBLE);

        ivOriginal=findViewById(R.id.ivOriginal);
        ivOriginal.setVisibility(View.INVISIBLE);

        gvGraph=findViewById(R.id.graph);
        gvGraph.setVisibility(View.INVISIBLE);

        tvOriginal=findViewById(R.id.tvOriginal);
        tvOriginal.setVisibility(View.INVISIBLE);

        tvProcessed=findViewById(R.id.tvProcessed);
        tvProcessed.setVisibility(View.INVISIBLE);

        tvProgressTag=findViewById(R.id.tvProgressTag);
        tvProgressTag.setVisibility(View.INVISIBLE);

        pbProcessingProgress=findViewById(R.id.pbProcessingProgress);
        pbProcessingProgress.setVisibility(View.INVISIBLE);


        setBrowseImageOnClick(bBrowseImage);
        setOpenCameraOnClick(bOpenCamera);
        setPerformProcessingOnClick(bPerformProcessing);
        setupAlertDialog();
    }

    public void setBrowseImageOnClick(Button bBrowseImage)
    {

        bBrowseImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                String mimeTypes[]= {"image/*"};
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

                //Hide unusable elements
                tvOriginal.setVisibility(View.INVISIBLE);
                ivOriginal.setVisibility(View.INVISIBLE);
                bPerformProcessing.setVisibility(View.INVISIBLE);
                tvProcessed.setVisibility(View.INVISIBLE);
                gvGraph.setVisibility(View.INVISIBLE);
                pbProcessingProgress.setVisibility(View.INVISIBLE);
                tvProgressTag.setVisibility(View.INVISIBLE);

                startActivityForResult(intent, BROWSE_IMAGE);

            }
        });
    }

    public void setOpenCameraOnClick(Button bOpenCamera)
    {
        bOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide unusable elements
                tvOriginal.setVisibility(View.INVISIBLE);
                ivOriginal.setVisibility(View.INVISIBLE);
                bPerformProcessing.setVisibility(View.INVISIBLE);
                tvProcessed.setVisibility(View.INVISIBLE);
                gvGraph.setVisibility(View.INVISIBLE);
                pbProcessingProgress.setVisibility(View.INVISIBLE);
                tvProgressTag.setVisibility(View.INVISIBLE);

                //Yet to add code
            }
        });

    }

    public void setPerformProcessingOnClick(Button bPerformProcessing)
    {
        bPerformProcessing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bPerformProcessing.setEnabled(false);
                tvProgressTag.setVisibility(View.VISIBLE);
                pbProcessingProgress.setVisibility(View.VISIBLE);
                processBitmapImage();

            }
        });
    }


    public void setupAlertDialog()
    {
        AlertDialog.Builder dialogBuilder=new MaterialAlertDialogBuilder(this);
        dialogBuilder.setMessage("Sorry! error occured while opening the image");
        dialogBuilder.setPositiveButton("OK",new AlertDialog.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        aErrorAlert=dialogBuilder.create();

    }

    public void processBitmapImage()
    {
        //Main Application Logic for step one

        //List<Long> plotPoints=new ArrayList<>();
        DataPoint[] plotPoints=new DataPoint[mBitmap.getWidth()*2];
        int maxY=0;
        int minY=0;
        boolean foundMin=false;
        int index=0;

        //Set max progress
        pbProcessingProgress.setMax(mBitmap.getWidth());

        for(int i=0;i<mBitmap.getWidth();i++)
        {

            for(int j=0;j<mBitmap.getHeight();j++)
            {

                int color=mBitmap.getPixel(i,j);
                int A = (color >> 24) & 0xff;
                int R = (color >> 16) & 0xff;
                int G = (color >>  8) & 0xff;
                int B = (color      ) & 0xff;

                if(!foundMin&&((R+G+B/3)>122))
                {
                    minY=j;
                    foundMin=true;
                } else if(foundMin&&((R+G+B/3)>122))
                {
                    maxY=j;
                }

            }

            //Store obtained Data
            plotPoints[index]=new DataPoint(i,minY);
            index++;
            plotPoints[index]=new DataPoint(i,maxY);
            index++;

            //Reset variables
            minY=0;
            maxY=0;
            foundMin=false;

            //Update Progress
            pbProcessingProgress.setProgress(i);
        }

        bPerformProcessing.setEnabled(true);
        prepareGraph(plotPoints);

    }

    public void prepareGraph(DataPoint[] plotPoints)
    {
        gvGraph.setVisibility(View.VISIBLE);
        tvProcessed.setVisibility(View.VISIBLE);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(plotPoints);
        gvGraph.addSeries(series);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {

            case OPEN_CAMERA:

//                if (resultCode == RESULT_OK) {
//                    Log.d("OpenCamera:", "Result OK");
//                    //Toast.makeText(mActivityReference,"Process Image Called!",Toast.LENGTH_SHORT).show();
//                    processImageText();
//                }
//                else
//                {
//                    Log.d("OpenCamera:", "ERROR");
//                    Toast.makeText(mActivityReference,"Camera Error Occured!",Toast.LENGTH_SHORT).show();
//                }
                break;

            case BROWSE_IMAGE:

                if(resultCode == RESULT_OK)
                {
                    Log.d("BrowseImage:", "Result OK");


                    mFileUri=data.getData();


                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            mBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.getContentResolver(), mFileUri)).copy(Bitmap.Config.RGBA_F16, true);
                        } else {
                            mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mFileUri);
                        }
                        //Show further steps elements
                        tvOriginal.setVisibility(View.VISIBLE);
                        ivOriginal.setVisibility(View.VISIBLE);
                        bPerformProcessing.setVisibility(View.VISIBLE);
                        Glide.with(getApplicationContext()).load(mFileUri).transform(new RoundedCorners(30)).into(ivOriginal);
                    }

                    catch(IOException ie)
                    {
                        //Hide unusable elements
                        tvOriginal.setVisibility(View.INVISIBLE);
                        ivOriginal.setVisibility(View.INVISIBLE);
                        bPerformProcessing.setVisibility(View.INVISIBLE);
                        tvProcessed.setVisibility(View.INVISIBLE);
                        gvGraph.setVisibility(View.INVISIBLE);
                        pbProcessingProgress.setVisibility(View.INVISIBLE);
                        tvProgressTag.setVisibility(View.INVISIBLE);
                        aErrorAlert.show();
                        //Snackbar.make(getWindow().getDecorView().getRootView(),"Error while selecting image",Snackbar.LENGTH_SHORT).show();
                    }

                }

                break;

            default:
                //Do something

        }

    }

}