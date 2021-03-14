package com.adishar.audex;

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
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int BROWSE_IMAGE=99;
    private static final int OPEN_CAMERA=999;

    private Button bOpenCamera;
    private Button bBrowseImage;
    private Button bPerformProcessing;
    private ImageView ivOriginal;
    private TextView tvOriginal;

    private AlertDialog aErrorAlert;

    private Uri mFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bOpenCamera=findViewById(R.id.bOpenCamera);
        bBrowseImage=findViewById(R.id.bBrowseImage);
        bPerformProcessing=findViewById(R.id.bPerformProcessing);
        bPerformProcessing.setVisibility(View.INVISIBLE);
        ivOriginal=findViewById(R.id.ivOriginal);
        tvOriginal=findViewById(R.id.tvOriginal);
        tvOriginal.setVisibility(View.INVISIBLE);

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

                startActivityForResult(intent, BROWSE_IMAGE);

            }
        });
    }

    public void setOpenCameraOnClick(Button bOpenCamera)
    {
        //Yet to add code
    }

    public void setPerformProcessingOnClick(Button bPerformProcessing)
    {
        //Yet to add code
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
                    Bitmap bitmap ;

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.getContentResolver(), mFileUri));
                        } else {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mFileUri);
                        }

                        tvOriginal.setVisibility(View.VISIBLE);
                        bPerformProcessing.setVisibility(View.VISIBLE);
                        Glide.with(getApplicationContext()).load(mFileUri).transform(new RoundedCorners(30)).into(ivOriginal);
                    }

                    catch(IOException ie)
                    {
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