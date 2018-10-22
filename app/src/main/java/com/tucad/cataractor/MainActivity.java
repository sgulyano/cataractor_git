package com.tucad.cataractor;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String DATABASE_NAME = "eyerecord_db";
    private static final String TAG = "MainActivity";
    private static EyeRecordDatabase eyeRecordDatabase = null;
    private EyeRecord[] eyerecord_list = null;

    @BindView(R.id.textView) TextView textView;
    @BindView(R.id.linearLayout) LinearLayout linearLayout;
    @BindView(R.id.tableLayout) TableLayout tableLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        getApplicationContext().deleteDatabase(DATABASE_NAME); //<<<< ADDED before building Database.
        eyeRecordDatabase = Room.databaseBuilder(getApplicationContext(),
                EyeRecordDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();

        if (eyerecord_list == null) {
            textView.setText("List not ready yet");
        }

        ImageButton fab = findViewById(R.id.addfab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRecord();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    private void addRecord() {
        Intent intent = new Intent(this, FormActivity.class);
        startActivity(intent);
    }

    public static void addEyeRecord(EyeRecord eyerecord) {
        eyeRecordDatabase.daoAccess().insertOnlySingleEyeRecord(eyerecord);
    }

    public void updateList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                eyerecord_list = eyeRecordDatabase.daoAccess().fetchAllEyeRecords();
                if (eyerecord_list == null) {
                    Log.e(TAG, "eye record is null");
                } else if (eyerecord_list.length <= 0) {
                    Log.e(TAG, "List is empty");
                    textView.setText("List is empty");
                } else {
                    Log.e(TAG, "list is not empty");
                    textView.setText("List has items = " + eyerecord_list.length);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateEyeRecordList(eyerecord_list);
                        }
                    });

                }
                Log.e(TAG, "update eye record successful");
            }
        }).start();
    }

    private void updateEyeRecordList(EyeRecord[] eyerecord_list) {
        tableLayout.removeAllViews();
        final Activity activity = this;

        //TODO: change layout of table row
        for (int i = 0; i < eyerecord_list.length; i++) {
            TableRow row= new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));

            TextView firstname = new TextView(this);
            firstname.setText(eyerecord_list[i].getFirstname() + " ");
            TextView lastname = new TextView(this);
            lastname.setText(eyerecord_list[i].getLastname()+ " ");

            row.addView(firstname);
            row.addView(lastname);

//            TextView imagename = new TextView(this);
//            imagename.setText(eyerecord_list[i].getImagepath());
            Log.e(TAG, eyerecord_list[i].getImagepath());

            try {
                //TODO: show question mark when image not available
                Bitmap thumb = getThumbnail(getContentResolver(), eyerecord_list[i].getImagepath());
                ImageView thumbeye = new ImageView(this);
                thumbeye.setImageBitmap(thumb);
                row.addView(thumbeye);
                Log.e(TAG,"DONE" + thumb.getHeight() + ", " + thumb.getWidth());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Fail load thumbnail");
            }
//            if( cursor != null && cursor.getCount() > 0 ) {
//                cursor.moveToFirst();//**EDIT**
//                String uri = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Thumbnails.DATA ) );
//                Log.e(TAG, "URI" + uri);
//            } else {
//                Log.e(TAG, "cursor is null");
//            }

//            Log.e(TAG, eyerecord_list[i].getImagepath());
//            ExifInterface exif = null;
//            try {
//                exif = new ExifInterface(eyerecord_list[i].getImagepath());
//                byte[] imageData = exif.getThumbnail();
//                Log.d(TAG, imageData.length + "");
//                Bitmap thumbnail= BitmapFactory.decodeByteArray(imageData,0,imageData.length);
//
//                ImageView thumbeye = new ImageView(this);
//                thumbeye.setImageBitmap(thumbnail);
//                row.addView(thumbeye);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            final EyeRecord eyerec = eyerecord_list[i];
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.e(TAG, getParent().toString());
                    Intent intent = new Intent(activity, DetailActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString(FormActivity.EXTRA_FIRSTNAME, eyerec.getFirstname());
                    extras.putString(FormActivity.EXTRA_LASTNAME, eyerec.getLastname());
                    extras.putString(FormActivity.EXTRA_AGE, eyerec.getAge());
                    extras.putString(FormActivity.EXTRA_SEX, eyerec.getSex());
                    extras.putString(FormActivity.EXTRA_IMAGEPATH, eyerec.getImagepath());
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });


            tableLayout.addView(row,i);
        }
    }

    public static Bitmap getThumbnail(ContentResolver cr, String path) throws Exception {

        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?", new String[] {path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null );
        }
        ca.close();
        return null;
    }
}
