package com.tucad.cataractor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int FILL_EYERECORD_FORM_REQUEST = 2;
    private static final String TAG = "MainActivity";

    @BindView(R.id.eyerecList) RecyclerView mRecyclerView;
    @BindView(R.id.loadingPanel) RelativeLayout loadingPanel;

    private EyeRecAdapter mAdapter;
    private ImageUploader imguploader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        loadingPanel.setVisibility(View.GONE);

        // Follow this tutorial to start a localhost for testing upload feature
        // https://stackoverflow.com/questions/43164971/how-can-i-upload-picture-from-android-app-to-server
//        EyeRecordDatabaseClient.deleteDatabase(getApplicationContext());
        EyeRecordDatabaseClient.setupEyeRecordDatabaseClient(getApplicationContext());

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new EyeRecAdapter(getApplicationContext(), new ArrayList<EyeRecord>());
        mRecyclerView.setAdapter(mAdapter);

        getEyeRecords();
    }

    /**
     * Open the form for entering a new eye record.
     */
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

    /**
     * Update list of eye records
     */
    private void getEyeRecords() {
        class GetTasks extends AsyncTask<Void, Void, List<EyeRecord>> {

            @Override
            protected List<EyeRecord> doInBackground(Void... voids) {
                return EyeRecordDatabaseClient.fetchallredcords();
            }

            @Override
            protected void onPostExecute(List<EyeRecord> eyerecs) {
                super.onPostExecute(eyerecs);
                mAdapter.setItems(eyerecs);
                mAdapter.notifyDataSetChanged();
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        loadingPanel.setVisibility(View.VISIBLE);
        //noinspection SimplifiableIfStatement
        if (id == R.id.sync_menu) {
            Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Visible");


            // Internet is normal permission, no need to ask user, just check internet availability
            // https://developer.android.com/guide/topics/permissions/overview#normal-dangerous
            if (isInternetConnected())
                syncEyeRecords();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void syncEyeRecords() {
        class SyncData extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                loadingPanel.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                EyeRecord eyerec = EyeRecordDatabaseClient.getOneEyeRecord();
                Log.e(TAG, eyerec.getImagepath());
                File imgfile = new File(eyerec.getImagepath());
                imguploader = new ImageUploader();
                imguploader.uploadFile(imgfile);
                Log.e(TAG, "sync eye records");
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                loadingPanel.setVisibility(View.GONE);
            }
        }

        SyncData gt = new SyncData();
        gt.execute();
    }

    public void setLoadPanelGone() {
        loadingPanel.setVisibility(View.GONE);
    }

    private Boolean isInternetConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Network network = cm.getActiveNetwork();
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        } else {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            Log.e(TAG, "Is connected = " + isConnected);
            boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            Log.e(TAG, "Is WiFi = " + isWiFi);
            return isConnected;
        }
    }
}
