package com.adishar.audex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adishar.audex.Utility.ConverterClass;
import com.adishar.audex.Utility.PlayMediaAudio;
import com.adishar.audex.Utility.WavFileGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int BROWSE_IMAGE = 99;
    private static final int OPEN_CAMERA = 999;

    private Button bOpenCamera;
    private Button bBrowseImage;
    private Button bPerformProcessing;
    private Button bFilterGraph;
    private Button bPlayAudio;

    private ImageView ivOriginal;
    private ImageView ivGraph;
    private ImageView ivFilteredGraph;

    private TextView tvOriginal;
    private TextView tvProcessed;
    private TextView tvProgressTag;
    private TextView tvFilterLabel;
    private TextView tvFilterProgressTag;

    private Slider sFilter;

    private Handler mainUIHandler;

    private AlertDialog aErrorAlert;
    private GraphView gvGraph;
    private ProgressBar pbProcessingProgress;
    private ProgressBar pbFilteringProgress;

    private static DataPoint[] mObtainedDataPoints;
    private static DataPoint[] mFilteredDataPoints;

    private Uri mFileUri;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainUIHandler = new Handler(this.getMainLooper());
        bOpenCamera = findViewById(R.id.bOpenCamera);
        bBrowseImage = findViewById(R.id.bBrowseImage);

        bPerformProcessing = findViewById(R.id.bPerformProcessing);
        bFilterGraph = findViewById(R.id.bFilterGraph);
        bPlayAudio = findViewById(R.id.bPlayAudio);


        ivOriginal = findViewById(R.id.ivOriginal);
        gvGraph = findViewById(R.id.graph);
        ivGraph = findViewById(R.id.ivgraph);
        ivFilteredGraph = findViewById(R.id.ivfilteredGraph);

        tvOriginal = findViewById(R.id.tvOriginal);
        tvProcessed = findViewById(R.id.tvProcessed);
        tvProgressTag = findViewById(R.id.tvProgressTag);
        tvFilterLabel = findViewById(R.id.tvFilterLabel);
        tvFilterProgressTag = findViewById(R.id.tvFilterProgressTag);

        sFilter = findViewById(R.id.sFilter);

        pbProcessingProgress = findViewById(R.id.pbProcessingProgress);
        pbFilteringProgress = findViewById(R.id.pbFilteringProgress);


        hideUIElements();
        setBrowseImageOnClick(bBrowseImage);
        setOpenCameraOnClick(bOpenCamera);
        setPerformProcessingOnClick(bPerformProcessing);
        setFilterGraphOnClick(bFilterGraph);
        setPlayAudioOnClick(bPlayAudio);
        setFilterOnChangeListener(sFilter);
        setupAlertDialog();
    }

    public void hideUIElements() {
        bPerformProcessing.setVisibility(View.INVISIBLE);
        bFilterGraph.setVisibility(View.INVISIBLE);
        ivOriginal.setVisibility(View.INVISIBLE);
        gvGraph.setVisibility(View.INVISIBLE);
        ivGraph.setVisibility(View.INVISIBLE);
        ivFilteredGraph.setVisibility(View.INVISIBLE);
        tvOriginal.setVisibility(View.INVISIBLE);
        tvProcessed.setVisibility(View.INVISIBLE);
        tvProgressTag.setVisibility(View.INVISIBLE);
        tvFilterLabel.setVisibility(View.INVISIBLE);
        tvFilterProgressTag.setVisibility(View.INVISIBLE);
        sFilter.setVisibility(View.INVISIBLE);
        pbProcessingProgress.setVisibility(View.INVISIBLE);
        pbFilteringProgress.setVisibility(View.INVISIBLE);
    }

    public void setBrowseImageOnClick(Button bBrowseImage) {

        bBrowseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String mimeTypes[] = {"image/*"};
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

                hideUIElements();

                startActivityForResult(intent, BROWSE_IMAGE);

            }
        });
    }

    public void setOpenCameraOnClick(Button bOpenCamera) {
        bOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideUIElements();

                //Yet to add code
            }
        });

    }

    public void setPerformProcessingOnClick(Button bPerformProcessing) {
        bPerformProcessing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bPerformProcessing.setEnabled(false);
                tvProgressTag.setText("Processing...");
                tvProgressTag.setVisibility(View.VISIBLE);
                gvGraph.setVisibility(View.INVISIBLE);
                ivGraph.setVisibility(View.INVISIBLE);
                tvFilterLabel.setVisibility(View.INVISIBLE);
                sFilter.setVisibility(View.INVISIBLE);
                bFilterGraph.setVisibility(View.INVISIBLE);
                ivFilteredGraph.setVisibility(View.INVISIBLE);
                pbFilteringProgress.setVisibility(View.INVISIBLE);
                tvFilterProgressTag.setVisibility(View.INVISIBLE);

                processBitmapImage();

            }
        });
    }

    public void setFilterGraphOnClick(Button filterGraph) {
        filterGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bFilterGraph.setEnabled(false);
                tvFilterProgressTag.setText("Filtering...");
                tvFilterProgressTag.setVisibility(View.VISIBLE);
                gvGraph.setVisibility(View.INVISIBLE);
                ivFilteredGraph.setVisibility(View.INVISIBLE);

                processFilterDataPoints();
            }
        });
    }

    public void setFilterOnChangeListener(Slider slider) {
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                tvFilterLabel.setText("Filter Strength  " + ((int) value));
            }
        });
    }

    public void setPlayAudioOnClick(Button bPlayAudio) {
        bPlayAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WavFileGenerator.saveWavFile(getApplicationContext(), 9, ConverterClass.convertDataPointListToIntegerArray(mFilteredDataPoints), "Test10");
                PlayMediaAudio.playAudioFile(getApplicationContext(), "Test10.wav");

            }
        });
    }


    public void setupAlertDialog() {
        AlertDialog.Builder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setMessage("Sorry! error occured while opening the image");
        dialogBuilder.setPositiveButton("OK", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        aErrorAlert = dialogBuilder.create();

    }

    public void processBitmapImage() {
        //Main Application Logic for generating graph points from image

        //Set max progress
        pbProcessingProgress.setMax(mBitmap.getWidth());

        //Make progress bas visible
        pbProcessingProgress.setVisibility(View.VISIBLE);

        //Running calculations on a different thread
        new Thread(new Runnable() {
            public void run() {

                int bmHeight = mBitmap.getHeight();
                int bmWidth = mBitmap.getWidth();
                //List<Long> plotPoints=new ArrayList<>();
                DataPoint[] plotPoints = new DataPoint[bmWidth * 2];
                int index = 0;


                for (int i = 0; i < bmWidth; i++) {
                    int maxY = 0;
                    int minY = bmHeight;
                    for (int j = 0; j < bmHeight; j++) {
                        int color = mBitmap.getPixel(i, j);
                        int A = (color >> 24) & 0xff;
                        int R = (color >> 16) & 0xff;
                        int G = (color >> 8) & 0xff;
                        int B = (color) & 0xff;

                        if (((R + G + B / 3) > 122)) {
                            minY = Math.min(minY,j);
                            maxY = Math.max(maxY,j);
                        }

                    }

                    //Store obtained Data,but due to inverted y axis conventions for graph and bitmap, invert y values
                    plotPoints[index] = new DataPoint(index, bmHeight - minY - 1);
                    index++;
                    plotPoints[index] = new DataPoint(index, bmHeight - maxY - 1);
                    index++;

                    final int xProgress = i;

                    //Update Progress
                    mainUIHandler.post(new Runnable() {
                        public void run() {

                            pbProcessingProgress.setProgress(xProgress);

                        }
                    });

                }
                mObtainedDataPoints = plotPoints;

                //Update Graph
                mainUIHandler.post(new Runnable() {
                    public void run() {

                        tvProgressTag.setText("Done.");
                        bPerformProcessing.setEnabled(true);

                        Bitmap graphImage = prepareGraph(plotPoints);
                        Glide.with(getApplicationContext()).load(graphImage).transform(new RoundedCorners(30)).into(ivGraph);
                        ivGraph.setVisibility(View.VISIBLE);
                        tvProcessed.setVisibility(View.VISIBLE);
                        tvFilterLabel.setVisibility(View.VISIBLE);
                        sFilter.setVisibility(View.VISIBLE);
                        bFilterGraph.setVisibility(View.VISIBLE);
                    }
                });


            }
        }).start();


    }

    public void processFilterDataPoints() {
        final int filter = (int) sFilter.getValue();
        //Set max progress
        pbFilteringProgress.setMax(mBitmap.getWidth() - filter);

        //Make progress bas visible
        pbFilteringProgress.setVisibility(View.VISIBLE);

        //Running calculations on a different thread
        new Thread(new Runnable() {
            public void run() {

                int bmHeight = mBitmap.getHeight();
                int bmWidth = mBitmap.getWidth();


                //Copy Obtained Values to plotPoints
                final DataPoint[] plotPoints = mObtainedDataPoints.clone();
                if (filter != 0) {

                    //Average values to next filter values
                    for (int i = 0; i < bmWidth*2 - filter; i++) {
                        //Log.d("Halwa","test"+i);
                        long sum = 0;
                        long avg = 0;

                        for (int j = i; j < i + filter; j++) {
                            sum = sum + (int) plotPoints[j].getY();
                        }
                        avg = sum /filter;

                        //Log.d("Halwa","original"+plotPoints[i].getY());
                        //Log.d("Halwa","new"+avg);

                        plotPoints[i] = new DataPoint(plotPoints[i].getX(), avg);

                        final int temp = i;

                        //Update Progress
                        mainUIHandler.post(new Runnable() {
                            public void run() {

                                pbFilteringProgress.setProgress(temp);

                            }
                        });

                    }
                }
                mFilteredDataPoints = plotPoints;

                //Update Graph
                mainUIHandler.post(new Runnable() {
                    public void run() {


                        tvFilterProgressTag.setText("Done.");
                        bFilterGraph.setEnabled(true);

                        Bitmap graphImage = prepareGraph(plotPoints);
                        Glide.with(getApplicationContext()).load(graphImage).transform(new RoundedCorners(30)).into(ivFilteredGraph);
                        ivFilteredGraph.setVisibility(View.VISIBLE);
                    }
                });


            }
        }).start();

    }

    public Bitmap prepareGraph(DataPoint[] plotPoints) {
        gvGraph.removeAllSeries();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(plotPoints);
        gvGraph.addSeries(series);
        gvGraph.setVisibility(View.VISIBLE);
        gvGraph.setVisibility(View.INVISIBLE);
        return gvGraph.takeSnapshot();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

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

                if (resultCode == RESULT_OK) {
                    Log.d("BrowseImage:", "Result OK");


                    mFileUri = data.getData();


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
                    } catch (IOException ie) {
                        hideUIElements();
                        //Snackbar.make(getWindow().getDecorView().getRootView(),"Error while selecting image",Snackbar.LENGTH_SHORT).show();
                    }

                }

                break;

            default:
                //Do something

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        PlayMediaAudio.releaseMediaPlayerResource();
    }
}