package com.example.embeddedproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.embeddedproject.ml.Model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.regex.Pattern;


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

    public void classifyImage(Bitmap image){
        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();

        long availableMemory = memoryInfo.availMem / (1024 * 1024);
        long usedMemory = (memoryInfo.totalMem - memoryInfo.availMem) / (1024 * 1024);
        TextView mTextView = findViewById(R.id.memoryusage);
        mTextView.setText("Available memory:"+availableMemory+"Used memory "+usedMemory);



        InputImage image2 = InputImage.fromBitmap(image, 0);
        if(availableMemory<300){
            LocalModel localModel =
                    new LocalModel.Builder()
                            .setAssetFilePath("newmodel.tflite")
                            // or .setAbsoluteFilePath(absolute file path to model file)
                            // or .setUri(URI to model file)
                            .build();
            CustomImageLabelerOptions customImageLabelerOptions =
                    new CustomImageLabelerOptions.Builder(localModel)
                            .setConfidenceThreshold(0.5f)
                            .setMaxResultCount(1)
                            .build();
            ImageLabeler labeler = ImageLabeling.getClient(customImageLabelerOptions);
            labeler.process(image2)
                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                        @Override
                        public void onSuccess(List<ImageLabel> labels) {
                            StringBuilder sb = new StringBuilder();

                            for (ImageLabel label : labels) {

                                sb.append(label.getText());
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
        else if(availableMemory>=300 && availableMemory<500 ){
            LocalModel localModel =
                    new LocalModel.Builder()
                            .setAssetFilePath("newmodel.tflite")
                            // or .setAbsoluteFilePath(absolute file path to model file)
                            // or .setUri(URI to model file)
                            .build();
            CustomImageLabelerOptions customImageLabelerOptions =
                    new CustomImageLabelerOptions.Builder(localModel)
                            .setConfidenceThreshold(0.5f)
                            .setMaxResultCount(1)
                            .build();
            ImageLabeler labeler = ImageLabeling.getClient(customImageLabelerOptions);
            labeler.process(image2)
                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                        @Override
                        public void onSuccess(List<ImageLabel> labels) {
                            StringBuilder sb = new StringBuilder();

                            for (ImageLabel label : labels) {

                                sb.append(label.getText());
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
        else if(availableMemory>=500){
            LocalModel localModel =
                    new LocalModel.Builder()
                            .setAssetFilePath("newmodel.tflite")
                            // or .setAbsoluteFilePath(absolute file path to model file)
                            // or .setUri(URI to model file)
                            .build();
            CustomImageLabelerOptions customImageLabelerOptions =
                    new CustomImageLabelerOptions.Builder(localModel)
                            .setConfidenceThreshold(0.5f)
                            .setMaxResultCount(1)
                            .build();
            ImageLabeler labeler = ImageLabeling.getClient(customImageLabelerOptions);
            labeler.process(image2)
                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                        @Override
                        public void onSuccess(List<ImageLabel> labels) {
                            StringBuilder sb = new StringBuilder();

                            for (ImageLabel label : labels) {

                                sb.append(label.getText());

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
        else {
            LocalModel localModel =
                    new LocalModel.Builder()
                            .setAssetFilePath("newmodel.tflite")
                            // or .setAbsoluteFilePath(absolute file path to model file)
                            // or .setUri(URI to model file)
                            .build();
            CustomImageLabelerOptions customImageLabelerOptions =
                    new CustomImageLabelerOptions.Builder(localModel)
                            .setConfidenceThreshold(0.5f)
                            .setMaxResultCount(1)
                            .build();
            ImageLabeler labeler = ImageLabeling.getClient(customImageLabelerOptions);
            labeler.process(image2)
                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                        @Override
                        public void onSuccess(List<ImageLabel> labels) {
                            StringBuilder sb = new StringBuilder();

                            for (ImageLabel label : labels) {

                                sb.append(label.getText());
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







//        try {
//            Model model = Model.newInstance(getApplicationContext());
//
//            // Creates inputs for reference.
//            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 32, 32, 3}, DataType.FLOAT32);
//            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
//            byteBuffer.order(ByteOrder.nativeOrder());
//
//            int[] intValues = new int[imageSize * imageSize];
//            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
//            int pixel = 0;
//            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
//            for(int i = 0; i < imageSize; i ++){
//                for(int j = 0; j < imageSize; j++){
//                    int val = intValues[pixel++]; // RGB
//                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
//                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
//                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
//                }
//            }
//
//            inputFeature0.loadBuffer(byteBuffer);
//
//            // Runs model inference and gets result.
//            Model.Outputs outputs = model.process(inputFeature0);
//            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
//
//            float[] confidences = outputFeature0.getFloatArray();
//            // find the index of the class with the biggest confidence.
//            int maxPos = 0;
//            float maxConfidence = 0;
//            for (int i = 0; i < confidences.length; i++) {
//                if (confidences[i] > maxConfidence) {
//                    maxConfidence = confidences[i];
//                    maxPos = i;
//                }
//            }
//            String[] classes = {"Apple", "Banana", "Orange"};
//            result.setText(classes[maxPos]);
//
//            // Releases model resources if no longer used.
//            model.close();
//        } catch (IOException e) {
//            // TODO Handle the exception
//        }
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

//
//public class CpuInfo {
//
//    /*
//     * return current cpu usage (0 to 100) guessed from core frequencies
//     */
//    public static int getCpuUsageFromFreq() {
//        return getCpuUsage(getCoresUsageGuessFromFreq());
//    }
//
//    /*
//     * @return total cpu usage (from 0 to 100) since last call of getCpuUsage or getCoresUsage
//     *         first call always returns 0 as previous value is not known
//     * ! deprecated since oreo !
//     */
//    public static int getCpuUsageSinceLastCall() {
//        if (Build.VERSION.SDK_INT < 26)
//            return getCpuUsage(getCoresUsageDeprecated());
//        else
//            return 0;
//    }
//
//    /* @return total cpu usage (from 0 to 100) from cores usage array
//     * @param coresUsage must come from getCoresUsageXX().
//     */
//    public static int getCpuUsage(int[] coresUsage) {
//        // compute total cpu usage from each core as the total cpu usage given by /proc/stat seems
//        // not considering offline cores: i.e. 2 cores, 1 is offline, total cpu usage given by /proc/stat
//        // is equal to remaining online core (should be remaining online core / 2).
//        int cpuUsage = 0;
//        if (coresUsage.length < 2)
//            return 0;
//        for (int i = 1; i < coresUsage.length; i++) {
//            if (coresUsage[i] > 0)
//                cpuUsage += coresUsage[i];
//        }
//        return cpuUsage / (coresUsage.length - 1);
//    }
//
//    /*
//     * guess core usage using core frequency (e.g. all core at min freq => 0% usage;
//     *   all core at max freq => 100%)
//     *
//     * This function is compatible with android oreo and later but is less precise than
//     *   getCoresUsageDeprecated.
//     * This function returns the current cpu usage (not the average usage since last call).
//     *
//     * @return array of cores usage
//     *   array size = nbcore +1 as the first element is for global cpu usage
//     *   array element: 0 => cpu at 0% ; 100 => cpu at 100%
//     */
//    public static synchronized int[] getCoresUsageGuessFromFreq()
//    {
//        initCoresFreq();
//        int nbCores = mCoresFreq.size() + 1;
//        int[] coresUsage = new int[nbCores];
//        coresUsage[0] = 0;
//        for (byte i = 0; i < mCoresFreq.size(); i++) {
//            coresUsage[i + 1] = mCoresFreq.get(i).getCurUsage();
//            coresUsage[0] += coresUsage[i + 1];
//        }
//        if (mCoresFreq.size() > 0)
//            coresUsage[0] /= mCoresFreq.size();
//        return coresUsage;
//    }
//
//    public static void initCoresFreq()
//    {
//        if (mCoresFreq == null) {
//            int nbCores = getNbCores();
//            mCoresFreq = new ArrayList<>();
//            for (byte i = 0; i < nbCores; i++) {
//                mCoresFreq.add(new CoreFreq(i));
//            }
//        }
//    }
//
//    private static int getCurCpuFreq(int coreIndex) {
//        return readIntegerFile("/sys/devices/system/cpu/cpu" + coreIndex + "/cpufreq/scaling_cur_freq");
//    }
//
//    private static int getMinCpuFreq(int coreIndex) {
//        return readIntegerFile("/sys/devices/system/cpu/cpu" + coreIndex + "/cpufreq/cpuinfo_min_freq");
//    }
//
//    private static int getMaxCpuFreq(int coreIndex) {
//        return readIntegerFile("/sys/devices/system/cpu/cpu" + coreIndex + "/cpufreq/cpuinfo_max_freq");
//    }
//
//
//    // return 0 if any pb occurs
//    private static int readIntegerFile(String path)
//    {
//        int ret = 0;
//        try {
//            RandomAccessFile reader = new RandomAccessFile(path, "r");
//
//            try {
//                String line = reader.readLine();
//                ret = Integer.parseInt(line);
//            } finally {
//                reader.close();
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return ret;
//    }
//
//
//    // from https://stackoverflow.com/questions/7962155/how-can-you-detect-a-dual-core-cpu-on-an-android-device-from-code
//    /**
//     * Gets the number of cores available in this device, across all processors.
//     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
//     * @return The number of cores, or 1 if failed to get result
//     */
//    public static int getNbCores() {
//        //Private Class to display only CPU devices in the directory listing
//        class CpuFilter implements FileFilter {
//            @Override
//            public boolean accept(File pathname) {
//                //Check if filename is "cpu", followed by one or more digits
//                if(Pattern.matches("cpu[0-9]+", pathname.getName())) {
//                    return true;
//                }
//                return false;
//            }
//        }
//
//        try {
//            //Get directory containing CPU info
//            File dir = new File("/sys/devices/system/cpu/");
//            //Filter to only list the devices we care about
//            File[] files = dir.listFiles(new CpuFilter());
//            //Return the number of cores (virtual CPU devices)
//            return files.length;
//        } catch(Exception e) {
//            //Default to return 1 core
//            return 1;
//        }
//    }
//
//
//    private static class CoreFreq {
//        int num, cur, min = 0, max = 0;
//
//        CoreFreq(int num) {
//            this.num = num;
//            min = getMinCpuFreq(num);
//            max = getMaxCpuFreq(num);
//        }
//
//        void updateCurFreq() {
//            cur = getCurCpuFreq(num);
//            // min & max cpu could not have been properly initialized if core was offline
//            if (min == 0)
//                min = getMinCpuFreq(num);
//            if (max == 0)
//                max = getMaxCpuFreq(num);
//        }
//
//        /* return usage from 0 to 100 */
//        int getCurUsage() {
//            updateCurFreq();
//            int cpuUsage = 0;
//            if (max - min > 0 && max > 0 && cur > 0) {
////                if (cur == min)
////                    cpuUsage = 2; // consider lowest freq as 2% usage (usually core is offline if 0%)
////                else
//                cpuUsage = (cur - min) * 100 / (max - min);
//            }
//            return cpuUsage;
//        }
//    }
//
//    // current cores frequencies
//    private static ArrayList<CoreFreq> mCoresFreq;
//
//
//    /*********************************/
//    /* !!! deprecated since oreo !!! */
//
//    /*
//     * @return array of cores usage since last call
//     *   (first call always returns -1 as the func has never been called).
//     *   array size = nbcore +1 as the first element is for global cpu usage
//     *   First element is global CPU usage from stat file (which does not consider offline core !
//     *     Use getCpuUsage do get proper global CPU usage)
//     *   array element: < 0 => cpu unavailable ; 0 => cpu min ; 100 => cpu max
//     */
//    public static synchronized int[] getCoresUsageDeprecated() {
//        int numCores = getNbCores() + 1; // +1 for global cpu stat
//
//        // ensure mPrevCores list is big enough
//        if (mPrevCoreStats == null)
//            mPrevCoreStats = new ArrayList<>();
//        while(mPrevCoreStats.size() < numCores)
//            mPrevCoreStats.add(null);//new CpuStat(-1, -1));
//
//        // init cpuStats
//        ArrayList<CoreStat> coreStats = new ArrayList<>();
//        while(coreStats.size() < numCores)
//            coreStats.add(null);
//
//        int[] coresUsage = new int[numCores];
//        for (byte i = 0; i < numCores; i++)
//            coresUsage[i] = -1;
//
//        try {
//            /* cat /proc/stat # example of possible output
//             *   cpu  193159 118453 118575 7567474 4615 6 2312 0 0 0
//             *   cpu0 92389 116352 96662 2125638 2292 5 2021 0 0 0
//             *   cpu3 47648 1264 11220 2378965 1286 0 9 0 0 0
//             *   ...
//             */
//            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
//
//            try {
//                CoreStat curCoreStat = null;
//                String line = reader.readLine();
//
//                for (byte i = 0; i < numCores; i++) {
//                    // cpu lines are only at the top of the file
//                    if (!line.contains("cpu"))
//                        break;
//
//                    // try get core stat number i
//                    curCoreStat = readCoreStat(i, line);
//                    if (curCoreStat != null) {
//                        CoreStat prevCoreStat = mPrevCoreStats.get(i);
//                        if (prevCoreStat != null) {
//                            float diffActive = curCoreStat.active - prevCoreStat.active;
//                            float diffTotal = curCoreStat.total - prevCoreStat.total;
//                            // check for strange values
//                            if (diffActive > 0 && diffTotal > 0)
//                                // compute usage
//                                coresUsage[i] = (int) (diffActive * 100 / diffTotal);
//                            // check strange values
//                            if (coresUsage[i] > 100)
//                                coresUsage[i] = 100;
//                            if (coresUsage[i] < 0)
//                                coresUsage[i] = 0;
//                        }
//
//                        // cur becomes prev (only if cpu online)
//                        mPrevCoreStats.set(i, curCoreStat);
//
//                        // load another line only if corresponding core has been found
//                        // otherwise try next core number with same line
//                        line = reader.readLine();
//                    }
//                }
//
//            } finally {
//                reader.close();
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        return coresUsage;
//    }
//
//
//    /* return CpuStat read, or null if it could not be read (e.g. cpu offline)
//     * @param coreNum coreNum=0 return global cpu state, coreNum=1 return first core
//     *
//     * adapted from https://stackoverflow.com/questions/22405403/android-cpu-cores-reported-in-proc-stat
//     */
//    private static CoreStat readCoreStat(int coreNum, String line) {
//        CoreStat coreStat = null;
//        try {
//            String cpuStr;
//            if (coreNum > 0)
//                cpuStr = "cpu" + (coreNum - 1) + " ";
//            else
//                cpuStr = "cpu ";
//
//            if (line.contains(cpuStr)) {
//                String[] toks = line.split(" +");
//
//                // we are recording the work being used by the user and
//                // system(work) and the total info of cpu stuff (total)
//                // http://stackoverflow.com/questions/3017162/how-to-get-total-cpu-usage-in-linux-c/3017438#3017438
//                // user  nice  system  idle  iowait  irq  softirq  steal
//                long active = Long.parseLong(toks[1]) + Long.parseLong(toks[2])
//                        + Long.parseLong(toks[3]);
//                long total = Long.parseLong(toks[1]) + Long.parseLong(toks[2])
//                        + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
//                        + Long.parseLong(toks[5]) + Long.parseLong(toks[6])
//                        + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
////                long active = total - Long.parseLong(toks[4]);
//
//                coreStat = new CoreStat(active, total);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return coreStat;
//    }
//
//    private static class CoreStat {
//        float active;
//        float total;
//
//        CoreStat(float active, float total) {
//            this.active = active;
//            this.total = total;
//        }
//    }
//
//    // previous stat read
//    private static ArrayList<CoreStat> mPrevCoreStats;
//
//}