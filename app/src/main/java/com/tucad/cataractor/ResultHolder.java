package com.tucad.cataractor;

import android.support.annotation.Nullable;
import android.util.Size;

import com.camerakit.type.CameraSize;


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

    static void setNativeCaptureSize(@Nullable CameraSize nativeCaptureSize) {
        if (nativeCaptureSize != null) {
            int height = nativeCaptureSize.getHeight();
            int width = nativeCaptureSize.getWidth();
            ResultHolder.nativeCaptureSize = new Size(width, height);
        } else {
            ResultHolder.nativeCaptureSize = null;
        }
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
