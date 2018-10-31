package com.tucad.cataractor;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int FILL_EYERECORD_FORM_REQUEST = 2;
    private static final String DATABASE_NAME = "eyerecord_db";
    private static final String TAG = "MainActivity";
//    public static EyeRecordDatabaseClient eyeRecordDatabase = null;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @BindView(R.id.eyerecList) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        EyeRecordDatabaseClient.deleteDatabase(getApplicationContext());
        EyeRecordDatabaseClient.setupEyeRecordDatabaseClient(getApplicationContext());
//        // getApplicationContext().deleteDatabase(DATABASE_NAME); //<<<< ADDED before building Database.
//        eyeRecordDatabase = Room.databaseBuilder(getApplicationContext(),
//                EyeRecordDatabase.class, DATABASE_NAME)
//                .fallbackToDestructiveMigration()
//                .build();
//
//        if (eyerecord_list == null) {
//            textView.setText("List not ready yet");
//            textView.setVisibility(View.VISIBLE);
//        }

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        getEyeRecords();
//        // specify an adapter (see also next example)
//        mAdapter = new EyeRecAdapter(eyeRecordDatabase);
//        mRecyclerView.setAdapter(mAdapter);
    }


    private void getEyeRecords() {
        class GetTasks extends AsyncTask<Void, Void, List<EyeRecord>> {

            @Override
            protected List<EyeRecord> doInBackground(Void... voids) {
                return EyeRecordDatabaseClient.getDatabase()
                        .daoAccess()
                        .fetchAllEyeRecords();
            }

            @Override
            protected void onPostExecute(List<EyeRecord> eyerecs) {
                super.onPostExecute(eyerecs);
                EyeRecAdapter mAdapter = new EyeRecAdapter(getApplicationContext(), eyerecs);
                mRecyclerView.setAdapter(mAdapter);
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

    @OnClick(R.id.addfab)
    public void startEyeRecForm(View view) {
        Intent intent = new Intent(this, FormActivity.class);
        startActivityForResult(intent, FILL_EYERECORD_FORM_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILL_EYERECORD_FORM_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                Log.e(TAG, "Add record okay");
                getEyeRecords();
            }
        }
    }


//    public static void addEyeRecord(EyeRecord eyerecord) {
//        eyeRecordDatabase.daoAccess().insertOnlySingleEyeRecord(eyerecord);
//    }

//    public void updateList() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                eyerecord_list = eyeRecordDatabase.daoAccess().fetchAllEyeRecords();
//                if (eyerecord_list == null) {
//                    Log.e(TAG, "eye record is null");
//                } else if (eyerecord_list.length <= 0) {
//                    Log.e(TAG, "List is empty");
//                    textView.setText("List is empty");
//                    textView.setVisibility(View.VISIBLE);
//                } else {
//                    Log.e(TAG, "list is not empty");
//                    textView.setText("List has items = " + eyerecord_list.length);
//                    textView.setVisibility(View.GONE);
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            updateEyeRecordList(eyerecord_list);
//                        }
//                    });
//
//                }
//                Log.e(TAG, "update eye record successful");
//            }
//        }).start();
//    }

//    private void updateEyeRecordList(EyeRecord[] eyerecord_list) {
//        tableLayout.removeAllViews();
//        final Activity activity = this;
//
//        //TODO: change layout of table row
//        for (int i = 0; i < eyerecord_list.length; i++) {
//            TableRow row= new TableRow(this);
//            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//            row.setPadding(0,0,0,10);
//
//            TextView firstname = new TextView(this);
//            firstname.setPadding(3,0,0,0);
//            firstname.setText(eyerecord_list[i].getFirstname() + " ");
//            row.addView(firstname);
//
//            TextView lastname = new TextView(this);
//            lastname.setPadding(3,0,3,0);
//            lastname.setText(eyerecord_list[i].getLastname()+ " ");
//            row.addView(lastname);
//
//            Log.e(TAG, eyerecord_list[i].getImagepath());
//            try {
//                //TODO: show question mark when image not available
//                Bitmap thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(eyerecord_list[i].getImagepath()), 192, 192);
//                ImageView thumbeye = new ImageView(this);
//                thumbeye.setImageBitmap(thumb);
//                row.addView(thumbeye);
//                Log.e(TAG,"DONE" + thumb.getHeight() + ", " + thumb.getWidth());
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e(TAG, "Fail load thumbnail");
//                ImageView unknown_img = new ImageView(this);
//                unknown_img.setImageResource(R.drawable.unknown_image);
//                unknown_img.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.MATCH_PARENT));
//                row.addView(unknown_img);
//            }
//
////            // Instantiate an ImageView and define its properties
//            ImageView seedetail = new ImageView(this);
//            seedetail.setImageResource(R.drawable.arrow_next);
//            seedetail.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.MATCH_PARENT));
//            row.addView(seedetail);
////            TextView seedetail = new TextView(this);
////            seedetail.setText(">");
////            row.addView(seedetail);
//
//            final EyeRecord eyerec = eyerecord_list[i];
//            row.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(activity, DetailActivity.class);
//                    Bundle extras = new Bundle();
//                    extras.putString(FormActivity.EXTRA_FIRSTNAME, eyerec.getFirstname());
//                    extras.putString(FormActivity.EXTRA_LASTNAME, eyerec.getLastname());
//                    extras.putString(FormActivity.EXTRA_AGE, eyerec.getAge());
//                    extras.putString(FormActivity.EXTRA_SEX, eyerec.getSex());
//                    extras.putString(FormActivity.EXTRA_IMAGEPATH, eyerec.getImagepath());
//                    intent.putExtras(extras);
//                    startActivity(intent);
//                }
//            });
//
//            tableLayout.addView(row, i, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.MATCH_PARENT));
//        }
//    }

//    public static Bitmap getThumbnail(ContentResolver cr, String path) {
//        Bitmap thumb = null;
//        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] {
//                MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?",
//                new String[] {path}, null);
//        if (ca != null) {
//            if (ca.moveToFirst()) {
//                int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
//                thumb = MediaStore.Images.Thumbnails.getThumbnail(
//                        cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null );
//            }
//            ca.close();
//        }
//        return thumb;
//    }
}
