package com.example.dynamicmodule;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;

import static org.opencv.android.LoaderCallbackInterface.*;


public class DynamicModule implements IDynamicModule {

    @Override
    public String getText(Context context) {
        BaseLoaderCallback callback = new BaseLoaderCallback(context) {
            @Override
            public void onManagerConnected(int status) {
                if (status == SUCCESS) {
                    try {
                        Log.e("-----", "onManagerConnected SUCCESS");
                        Mat mat = new Mat(400, 400, CvType.CV_8UC3, new Scalar(0.0, 0.0, 0.0));
                        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.jpg");
                        f.createNewFile();
                        Imgcodecs.imwrite(f.getAbsolutePath(), mat);
                        Log.e("-----", "mat saved");
                    } catch (Exception e) {
                        Log.e("-----", "onManagerConnected ERROR: " + e.getMessage());
                        super.onManagerConnected(status);
                    }
                } else {
                    Log.e("-----", "onManagerConnected ERROR");
                    super.onManagerConnected(status);
                }
            }

            @Override
            public void onPackageInstall(int operation, InstallCallbackInterface callback) {
                super.onPackageInstall(operation, callback);
            }
        };

        if (!OpenCVLoader.initDebug(context)) {
            Log.e("-----", "Internal OpenCV library not found.");
            // OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, context, callback);
        } else {
            Log.e("-----", "OpenCV library found inside package. Using it!");
            callback.onManagerConnected(SUCCESS);
        }

       /* OpenCVLoader.initDebug();
        try {
            Mat mat = new Mat();
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.jpg");
            f.createNewFile();
            Imgcodecs.imwrite(f.getAbsolutePath(), mat);
            Log.e("-----", "mat saved");
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return Build.FINGERPRINT;
    }
}
