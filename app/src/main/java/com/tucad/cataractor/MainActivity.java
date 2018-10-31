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
    private static final String TAG = "MainActivity";

    @BindView(R.id.eyerecList) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        EyeRecordDatabaseClient.deleteDatabase(getApplicationContext());
        EyeRecordDatabaseClient.setupEyeRecordDatabaseClient(getApplicationContext());

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

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
}
