package com.tucad.cataractor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "PhotoActivity";
    private long captureStartTime;
    @BindView(R.id.takepicbutton) Button takepicbutton;
    @BindView(R.id.camera) CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }
    
    @OnClick(R.id.takepicbutton) void takepicture() {
        Log.e("TAG", "Take picture");
        captureStartTime = System.currentTimeMillis();
        cameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage event) {
                imageCaptured(event);
            }
        });
    }

    //@OnCameraKitEvent(CameraKitImage.class)
    public void imageCaptured(CameraKitImage image) {
        byte[] jpeg = image.getJpeg();

        long callbackTime = System.currentTimeMillis();
        ResultHolder.dispose();
        ResultHolder.setImage(jpeg);
        ResultHolder.setNativeCaptureSize(cameraView.getCaptureSize());
        ResultHolder.setTimeToCallback(callbackTime - captureStartTime);

        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void onBackPressed() {
        Log.e(TAG, "back canceled");
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }
}
