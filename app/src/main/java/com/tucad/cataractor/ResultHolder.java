package com.tucad.cataractor;

import android.support.annotation.Nullable;
import com.wonderkiln.camerakit.Size;


public class ResultHolder {

    private static byte[] image = null;
    private static Size nativeCaptureSize;
    private static long timeToCallback;


    public static void setImage(@Nullable byte[] image) {
        ResultHolder.image = image;
    }

    @Nullable
    public static byte[] getImage() {
        return image;
    }

    static void setNativeCaptureSize(@Nullable Size nativeCaptureSize) {
        ResultHolder.nativeCaptureSize = nativeCaptureSize;
    }

    @Nullable
    public static Size getNativeCaptureSize() {
        return nativeCaptureSize;
    }

    static void setTimeToCallback(long timeToCallback) {
        ResultHolder.timeToCallback = timeToCallback;
    }

    static long getTimeToCallback() {
        return timeToCallback;
    }

    static void dispose() {
        setImage(null);
        setNativeCaptureSize(null);
        setTimeToCallback(0);
    }
}
