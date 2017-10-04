package org.opencv.samples.mobilenet;

/**
 * Created by uelordi on 4/10/17.
 */

public class ObjectConfiguration {
    private static final int DEFAULT_IN_WIDTH_VALUE = 300;
    private static final int DEFAULT_IN_HEIGHT_VALUE = 300;
    private static final float DEFAULT_WHRATIO = (float) DEFAULT_IN_WIDTH_VALUE / (float) DEFAULT_IN_HEIGHT_VALUE;
    private static final float DEFAULT_IN_SCALE_FACTOR = 0.007843f;
    private static final float DEFAULT_MEAN_VAL = 127.5f;
    private static final float DEFAULT_MIN_CONFIDENCE = 0.2f;

     int  inWidth;
     int inHeight;
     float WHRatio;
     float inScaleFactor;
     float meanVal;
     float minConfidence;

    public ObjectConfiguration(int inWidth, int inHeight,
                               float WHRatio, float inScaleFactor,
                               float meanVal, float minConfidence) {
        this.inWidth = inWidth;
        this.inHeight = inHeight;
        this.WHRatio = WHRatio;
        this.inScaleFactor = inScaleFactor;
        this.meanVal = meanVal;
        this.minConfidence = minConfidence;
    }

    String classNames[] = {"background",
                "aeroplane", "bicycle", "bird", "boat",
                "bottle", "bus", "car", "cat", "chair",
                "cow", "diningtable", "dog", "horse",
                "motorbike", "person", "pottedplant",
                "sheep", "sofa", "train", "tvmonitor"};
    public static ObjectConfiguration createDefaultConfiguration() {
        return new ObjectConfiguration(DEFAULT_IN_WIDTH_VALUE,
                                        DEFAULT_IN_HEIGHT_VALUE,
                                        DEFAULT_WHRATIO,
                                        DEFAULT_IN_SCALE_FACTOR,
                                        DEFAULT_MEAN_VAL,
                                        DEFAULT_MIN_CONFIDENCE
                                        );
    }

    public int getInWidth() {
        return inWidth;
    }

    public void setInWidth(int inWidth) {
        this.inWidth = inWidth;
    }

    public int getInHeight() {
        return inHeight;
    }

    public void setInHeight(int inHeight) {
        this.inHeight = inHeight;
    }

    public float getWHRatio() {
        return WHRatio;
    }

    public void setWHRatio(float WHRatio) {
        this.WHRatio = WHRatio;
    }

    public float getInScaleFactor() {
        return inScaleFactor;
    }

    public void setInScaleFactor(float inScaleFactor) {
        this.inScaleFactor = inScaleFactor;
    }

    public float getMeanVal() {
        return meanVal;
    }

    public void setMeanVal(float meanVal) {
        this.meanVal = meanVal;
    }

    public String[] getClassNames() {
        return classNames;
    }

    public void setClassNames(String[] classNames) {
        this.classNames = classNames;
    }

    public float getMinConfidence() {
        return minConfidence;
    }

    public void setMinConfidence(float minConfidence) {
        this.minConfidence = minConfidence;
    }
}
