package com.example.embeddedproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.List;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Debug;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button camera, gallery;
    ImageView imageView;
    TextView result;
    int imageSize = 32;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        camera = findViewById(R.id.button);
        gallery = findViewById(R.id.button2);

        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });


    }
    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    public void runmodel(String modelname,Bitmap image,int modelNumber){

        InputImage image2 = InputImage.fromBitmap(image, 0);


        LocalModel localModel =
                new LocalModel.Builder()
                        .setAssetFilePath(modelname)
                        // or .setAbsoluteFilePath(absolute file path to model file)
                        // or .setUri(URI to model file)
                        .build();
        CustomImageLabelerOptions customImageLabelerOptions =
                new CustomImageLabelerOptions.Builder(localModel)
                        .setConfidenceThreshold(0.7f)
                        .setMaxResultCount(1)
                        .build();
        ImageLabeler labeler = ImageLabeling.getClient(customImageLabelerOptions);

        labeler.process(image2)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        if(image2==null){
                            Toast.makeText(MainActivity.this, "no image", Toast.LENGTH_SHORT).show();
                        }
                        StringBuilder sb = new StringBuilder();
                        for (ImageLabel label : labels) {
                            sb.append(label.getText()).append(": ").append(label.getConfidence()).append("\nmodel number:"+modelNumber);
                            Toast.makeText(MainActivity.this, "switched to: "+ modelNumber+" model", Toast.LENGTH_SHORT).show();
                            //sb.append(labels.get(0));
                        }
                        if (labels.isEmpty()) {
                            result.setText("Could not identify!!");
                        } else {
                            result.setText(sb.toString());
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.setText("error has occured !");
                    }
                });
    }

    public void classifyImage(Bitmap image){
        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();
        // Get the number of available processors
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        TextView processors = findViewById(R.id.processors);
        processors.setText("No. of Processors :"+availableProcessors+"\n");

        long availableMemory = memoryInfo.availMem / (1024 * 1024);
        long usedMemory = (memoryInfo.totalMem - memoryInfo.availMem) / (1024 * 1024);
        TextView mTextView = findViewById(R.id.memoryusage);
        mTextView.setText("Available memory:"+availableMemory+"\nUsed memory "+usedMemory);

        InputImage image2 = InputImage.fromBitmap(image, 0);

        if(availableMemory<500){
            runmodel("mnasnet_0.75_224_1_metadata_1.tflite",image,1);
        }
        else if(availableMemory>=500 && availableMemory<800 ){
            runmodel("mnasnet_0.50_224_1_metadata_1.tflite",image,2);

        }
        else if(availableMemory>=800 && availableMemory<900){
            runmodel("mnasnet_1.0_128_1_metadata_1.tflite",image,3);

        }
        else if(availableMemory>=900 && availableMemory<1000 ) {
            runmodel("mnasnet_1.0_192_1_metadata_1.tflite",image,4);
        }
         else {
            runmodel("mnasnet_1.3_224_1_metadata_1.tflite",image,5);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 3){
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }else{
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);

                classifyImage(image);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

