package org.opencv.samples.mobilenet;

import android.content.Context;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import utils.PrivateFileRW;

/**
 * Created by uelordi on 4/10/17.
 */

public class ObjectDetectionHandler {


    public static final String DEFAULT_DEPLOY_FILENAME= "gazenet.prototxt";
    public static final String DEFAULT_MOBILENET_FILENAME= "gazenet.caffemodel";
    private long mNativeObj = 0;
    private String m_filePath;
    private static ObjectDetectionHandler _instance = null;

    public static synchronized  ObjectDetectionHandler getInstance() {
            if(_instance == null) {
                _instance = new ObjectDetectionHandler();
            }
            return _instance;
    }
    public void init(Context context, String neuralNet, String deploy) {
        //TODO copy the files to the priate directory
        //TODO create native object
        WriteFilesInTheStorage(context);
        m_filePath = context.getFilesDir().toString();
        createNativeObject(m_filePath, neuralNet, deploy);
    }
    public void setClassConfiguration(ObjectConfiguration object) {
        setConfiguration(object.getInWidth(), object.getInHeight(), object.getWHRatio(),
                                         object.getInScaleFactor(), object.getMeanVal(),
                                         object.getMinConfidence(), object.getClassNames());
    }
    public void start() {
        nativeStart(mNativeObj);
    }
    public void stop() {
        nativeStop(mNativeObj);
    }
    public void processFrame(Mat inputImage,MatOfRect rectangles) {
        processFrame(mNativeObj, inputImage.getNativeObjAddr(), rectangles.getNativeObjAddr());
    }
    private void WriteFilesInTheStorage(Context context) {
            PrivateFileRW privateFileHandler = new PrivateFileRW(context);
            Map<Integer,String> resources = new HashMap<Integer,String>();
            // 3D models
        resources.put(R.raw.gazenet_caffe,"gazenet.prototxt");
        resources.put(R.raw.gazenet,"gazenet.caffemodel");

//        resources.put(R.raw.new_candide_v02_updated_nopatch,"new_candide_v02_updated_nopatch.wfm");


            Iterator it = resources.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                privateFileHandler.writeFileToPrivateStorage((Integer)pair.getKey(),(String)pair.getValue());
            }
    }

    public static native long createNativeObject(String filesPath, String neuralNet, String deploy);
    public static native long destroyNativeObject(long thiz);
    public static native void setConfiguration(int inWidth, int inHeight, float WHRatio,
                                               float inScaleFactor, float meanVal,
                                               float minConfidence, String [] dictionary);
    public static native void nativeStart(long thiz);
    public static native void nativeStop(long thiz);
    public static native void processFrame(long thiz, long inputFrame, long faces);
}
