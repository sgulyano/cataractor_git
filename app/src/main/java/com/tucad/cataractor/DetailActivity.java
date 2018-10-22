package com.tucad.cataractor;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity {

    public static final String TAG = "DetailActivity";
    private static int REQUEST_WRITE_EXTERNAL_PERMISSION = 200;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Ask for permissions
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

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final String firstname_str = extras.getString(FormActivity.EXTRA_FIRSTNAME);
        final String lastname_str = extras.getString(FormActivity.EXTRA_LASTNAME);
        final String age_str = extras.getString(FormActivity.EXTRA_AGE);
        final String sex_str = extras.getString(FormActivity.EXTRA_SEX);
        final String image_path_str =  extras.getString(FormActivity.EXTRA_IMAGEPATH);

        fullname.setText(Html.fromHtml("คุณ " + firstname_str + " " + lastname_str));
        agesex.setText(Html.fromHtml("<string><font color=\"gray\">" + "อายุ " + age_str + " ปี, " +
                    "เพศ " + sex_str + "</font></string>"));

        diagnose.setText(Html.fromHtml("ไม่ป่วยเป็นโรคต้อกระจก"));
        treatment.setText(Html.fromHtml("ควรตรวจสุขภาพตาทุกๆ 12 เดือน"));


        if (image_path_str != null) {
            imageView.setImageURI(Uri.fromFile(new File(image_path_str)));
        } else {
            byte[] jpeg = ResultHolder.getImage();

            if (jpeg != null) {
                bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);

                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);

                    actualResolution.setText(bitmap.getWidth() + " x " + bitmap.getHeight());
                    approxUncompressedSize.setText(getApproximateFileMegabytes(bitmap) + "MB");
                    captureLatency.setText(ResultHolder.getTimeToCallback() + " milliseconds");
                }
            }

            // save to Room database
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (bitmap != null) {
                        EyeRecord eyeRecord = new EyeRecord();
                        eyeRecord.setFirstname(firstname_str);
                        eyeRecord.setLastname(lastname_str);
                        eyeRecord.setAge(age_str);
                        eyeRecord.setSex(sex_str);

                        // Create an image file name
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                        File myDir = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), albumName);
                        myDir.mkdir();
                        if (!myDir.exists()) {
                            Log.e(TAG, "Fail " + myDir);
                        }

                        String fname = "IMG_" + timeStamp + ".jpg";

                        File file = new File(myDir, fname);
                        Log.e(TAG, file.getPath());
                        if (file.exists())
                            file.delete();
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        eyeRecord.setImagepath(file.getPath());
                        MainActivity.addEyeRecord(eyeRecord);
                        Log.e(TAG, "save eye record successful at " + file.getPath());
                    } else {
                        Log.e(TAG, "bitmap is null");
                    }
                }
            });
            t.start();
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
        finish();
    }


    @Override
    public void onBackPressed() {
        Log.e(TAG, "You shall not pass");
    }

    private static float getApproximateFileMegabytes(Bitmap bitmap) {
        return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024 / 1024;
    }
}
