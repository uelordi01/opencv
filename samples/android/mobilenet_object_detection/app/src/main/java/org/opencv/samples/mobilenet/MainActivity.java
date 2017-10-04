package org.opencv.samples.mobilenet;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.Objdetect;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String LOG_TAG = "MainActivity";
    private Mat mRgba;
    private Mat mGray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        askPermissions();
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        MatOfRect faces = new MatOfRect();
        ObjectDetectionHandler.getInstance().processFrame(mRgba, faces);
        return mRgba;
    }

    private CameraBridgeViewBase mOpenCvCameraView;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(LOG_TAG, "OpenCV loaded successfully");
                    try {

                        System.loadLibrary("mobilenet_dnn");
                        ObjectDetectionHandler.getInstance()
                                .init(getApplicationContext(),
                                        ObjectDetectionHandler.DEFAULT_DEPLOY_FILENAME,
                                        ObjectDetectionHandler.DEFAULT_MOBILENET_FILENAME );

                        ObjectDetectionHandler.getInstance()
                                .setClassConfiguration(ObjectConfiguration
                                                .createDefaultConfiguration());

                        mOpenCvCameraView.enableView();
                    } catch (Exception e) {
                        Log.v(LOG_TAG, "Failed loading library mobilenet_dnn");
                    }
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(LOG_TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(LOG_TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    void askPermissions() {
        final String[] permissions_needed = {Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
                //,Manifest.permission.SYSTEM_ALERT_WINDOW
        };
        final int REQUEST_CODE_ASK_PERMISSIONS = 123;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            ArrayList array_aux = new ArrayList<String>();

            String[] permissions_requested;
            for (String aPermissions_needed : permissions_needed) {
                int hasPermission = ContextCompat.checkSelfPermission(this, aPermissions_needed);
                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    array_aux.add(aPermissions_needed);
                    Log.d("Activity", "request permission %s" + aPermissions_needed);
                }
            }
            if (array_aux.size() != 0) {
                //Log.d("Activity", "allow permission");
                permissions_requested = (String[]) array_aux.toArray(new String[array_aux.size()]);


                ActivityCompat.requestPermissions(this, permissions_requested,
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        } else {
            Log.v(LOG_TAG, "No need to ask permissions programatically");
        }

    }
    static {
        try {
            Log.v(LOG_TAG, "loading library -> opencv_java3");
            System.loadLibrary("opencv_java3");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }

    }
}


