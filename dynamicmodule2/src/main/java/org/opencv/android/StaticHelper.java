package org.opencv.android;

import org.opencv.core.Core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.StringTokenizer;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

class StaticHelper {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean initOpenCV(boolean InitCuda, Context context) {
        boolean result;
        String libs = "";

        if (InitCuda) {
            loadLibrary("cudart", null);
            loadLibrary("nppc", null);
            loadLibrary("nppi", null);
            loadLibrary("npps", null);
            loadLibrary("cufft", null);
            loadLibrary("cublas", null);
        }

        Log.d(TAG, "Trying to get library list");

        try {
            System.loadLibrary("opencv_info");
            libs = Environment.getExternalStorageDirectory().getAbsolutePath() + "/libraries/x86/";//getLibraryList();
            Log.e("----", "trying to load: " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/libraries/x86/");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "OpenCV error: Cannot load info library for OpenCV");
        }

        Log.d(TAG, "Library list: \"" + libs + "\"");
        Log.d(TAG, "First attempt to load libs");
        if (initOpenCVLibs(libs, context)) {
            Log.d(TAG, "First attempt to load libs is OK");
            String eol = System.getProperty("line.separator");
            for (String str : Core.getBuildInformation().split(eol))
                Log.i(TAG, str);

            result = true;
        } else {
            Log.d(TAG, "First attempt to load libs fails");
            result = false;
        }

        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static boolean loadLibrary(String Name, Context context) {
        boolean result = true;

        Log.d(TAG, "Trying to load library " + Name);
        try {
            String originalLookup = System.getProperty("java.library.path");
            Log.e("----", "stored java.library.path: " + originalLookup);
            //System.setProperty("java.library.path", Environment.getExternalStorageDirectory().getAbsolutePath() + "/libraries/x86/");
            //  System.loadLibrary(Name);
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/libraries/x86/libopencv_java3.so");
            File copiedFiled = new File(context.getFilesDir().getAbsolutePath() + "/libopencv_java3.so");
            if (!copiedFiled.exists() && file.exists() && context != null) {
                Log.e("----", file.getAbsolutePath());


                //file.renameTo(copiedFiled);
                try {
                    Files.copy(file.toPath(), copiedFiled.toPath());
                } catch (IOException e) {
                    Log.e("------", " Files.copy:  " + e.getMessage());
                }
                Log.e("------", "copied file to: " + copiedFiled.getAbsolutePath());
                System.load(copiedFiled.getAbsolutePath());//Environment.getExternalStorageDirectory().getAbsolutePath() + "/libraries/x86/libopencv_java3.so");
            }
            //System.setProperty("java.library.path",originalLookup);
            Log.d(TAG, "Library " + Name + " loaded");
        } catch (UnsatisfiedLinkError e) {
            Log.d(TAG, "Cannot load library \"" + Name + "\"");
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static boolean initOpenCVLibs(String Libs, Context context) {
        Log.d(TAG, "Trying to init OpenCV libs");

        boolean result = true;

        if ((null != Libs) && (Libs.length() != 0)) {
            Log.d(TAG, "Trying to load libs by dependency list");
            StringTokenizer splitter = new StringTokenizer(Libs, ";");
            while (splitter.hasMoreTokens()) {
                result &= loadLibrary(splitter.nextToken(), null);
            }
        } else {
            // If dependencies list is not defined or empty.
            result = loadLibrary("opencv_java3", context);
        }

        return result;
    }

    private static final String TAG = "OpenCV/StaticHelper";

    private static native String getLibraryList();
}
