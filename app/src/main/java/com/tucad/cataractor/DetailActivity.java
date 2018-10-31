package com.tucad.cataractor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String TAG = "DetailActivity";
    private static final int REQUEST_WRITE_EXTERNAL_PERMISSION = 200;

    @BindView(R.id.takepicbutton) Button takepicbutton;
    @BindView(R.id.linearLayout) LinearLayout linearLayout;

    @BindView(R.id.fullname) TextView fullname;
    @BindView(R.id.agesex) TextView agesex;
    @BindView(R.id.diagnose) TextView diagnose;
    @BindView(R.id.treatment) TextView treatment;
    @BindView(R.id.imageView) ImageView imageView;
    @BindView(R.id.actualResolution) TextView actualResolution;
    @BindView(R.id.approxUncompressedSize) TextView approxUncompressedSize;
    @BindView(R.id.captureLatency) TextView captureLatency;

    private Thread t = null;
    private Bitmap bitmap = null;
    String albumName = "Cataractor";

    String firstname_str;
    String lastname_str;
    String age_str;
    String sex_str;
    String image_path_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // get data from form
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        assert extras != null;
        firstname_str = extras.getString(FormActivity.EXTRA_FIRSTNAME);
        lastname_str = extras.getString(FormActivity.EXTRA_LASTNAME);
        age_str = extras.getString(FormActivity.EXTRA_AGE);
        sex_str = extras.getString(FormActivity.EXTRA_SEX);
        image_path_str =  extras.getString(FormActivity.EXTRA_IMAGEPATH);

        fullname.setText(fromHtml("คุณ " + firstname_str + " " + lastname_str));
        agesex.setText(fromHtml("<string><font color=\"gray\">" + "อายุ " + age_str + " ปี, " +
                "เพศ " + sex_str + "</font></string>"));

        diagnose.setText(fromHtml("ไม่ป่วยเป็นโรคต้อกระจก"));
        treatment.setText(fromHtml("ควรตรวจสุขภาพตาทุกๆ 12 เดือน"));


        if (image_path_str != null) {
            // Info is already on record, show it
            File imgfile = new File(image_path_str);
            if(imgfile.exists()) {
                imageView.setImageURI(Uri.fromFile(imgfile));
                imageView.setBackgroundResource(android.R.color.transparent);
            }
        } else {
            // Load image
            byte[] jpeg = ResultHolder.getImage();
            if (jpeg != null) {
                bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);

                    Resources res = getResources();
                    actualResolution.setText(String.format(
                            res.getString(R.string.show_actual_resolution),
                            bitmap.getWidth(),
                            bitmap.getHeight(),
                            ResultHolder.getNativeCaptureSize()));
                    approxUncompressedSize.setText(String.format(
                            res.getString(R.string.show_approx_uncompressed_size),
                            getApproximateFileMegabytes(bitmap)));
                    captureLatency.setText(String.format(
                            res.getString(R.string.show_capture_latency),
                            ResultHolder.getTimeToCallback()));
                } else {
                    Log.e(TAG, "bitmap (onCreate) is null");
                    setResult(Activity.RESULT_CANCELED, new Intent());
                    finish();
                }
            } else {
                Log.e(TAG, "jpeg is null");
                setResult(Activity.RESULT_CANCELED, new Intent());
                finish();
            }

            // Ask for permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.request_permission)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            REQUEST_WRITE_EXTERNAL_PERMISSION);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getParent(), "Cannot save eye record",
                                                    Toast.LENGTH_LONG).show();
                                            Log.e(TAG, "Cannot save eye record");
                                        }
                                    })
                            .create();
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_PERMISSION);
                }
            } else {
                saveEyeRecord();
            }
        }
    }

    @OnClick(R.id.takepicbutton) void takepicture() {
        Log.e(TAG, "finish");
        try {
            if (t != null) {
                t.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setResult(Activity.RESULT_OK, new Intent());
        finish();
    }


    @Override
    public void onBackPressed() {
        Log.e(TAG, "You shall not pass");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    saveEyeRecord();
                } else {
                    // permission denied, boo!
                    Log.e(TAG, "Eye record is not saved");
                }
            }
        }
    }

    void saveEyeRecord() {
        // Create an image file name
        File myDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "IMG_" + timeStamp + ".jpg";

        EyeRecord eyeRecord = new EyeRecord();
        eyeRecord.setFirstname(firstname_str);
        eyeRecord.setLastname(lastname_str);
        eyeRecord.setAge(age_str);
        eyeRecord.setSex(sex_str);
        eyeRecord.setImagepath(saveEyeImage(myDir, fname));
        saveEyeRecord2Room(eyeRecord);
    }

    private String saveEyeImage(File myDir, String fname) {
        if (bitmap == null) {
            Log.e(TAG, "bitmap is null");
            return "";
        }

        // create directory
        if (!myDir.exists()) {
            Log.e(TAG, "Directory not exist " + myDir);
            if (myDir.mkdirs()) {
                Log.e(TAG, "Fail to create directory: " + myDir);
            }
        }

        File file = new File(myDir, fname);
        if (file.exists()) {
            Log.e(TAG, "Duplicate file exists");
            if (file.delete()) {
                Log.e(TAG, "File successfully deletes duplicate");
            } else {
                Log.e(TAG, "File fails to delete duplicate");
            }
        }
        try {
            Log.e(TAG, "Save to " + file.getPath());
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getPath();
    }

    private void saveEyeRecord2Room(final EyeRecord eyerec) {
        // save to Room database
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                EyeRecordDatabaseClient.addEyeRecord(eyerec);
                Log.e(TAG, "save eye record successfully");
            }
        });
        t.start();
    }

    private static float getApproximateFileMegabytes(Bitmap bitmap) {
        return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024 / 1024;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }
}
