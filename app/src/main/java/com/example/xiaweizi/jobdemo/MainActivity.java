package com.example.xiaweizi.jobdemo;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Job--Main::";
    private MyHandler mHandler;
    private ListView mListView;
    private MyAdapter mAdapter;
    private List<String> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new MyHandler(this);
        startService();
        mAdapter = new MyAdapter(this);
        mListView = findViewById(R.id.list_view);
        findViewById(R.id.bt_start_job).setOnClickListener(this);
        findViewById(R.id.bt_cancel_all).setOnClickListener(this);
        findViewById(R.id.bt_clear_log).setOnClickListener(this);

        mListView.setAdapter(mAdapter);
        mAdapter.setData(mData);
    }

    private void startService() {
        Intent jobService = new Intent(this, MyJobService.class);
        Messenger messenger = new Messenger(mHandler);
        jobService.putExtra(MyJobService.KEY_INTENT_MESSENGER, messenger);
        startService(jobService);
    }

    private void startJob() {
        Log.i(TAG, "startJob");
        getJobScheduler().schedule(createJobInfo(1));
    }

    private void cancelAllJobs() {
        Log.i(TAG, "cancelAllJobs");
        getJobScheduler().cancelAll();
    }

    private JobScheduler getJobScheduler() {
        return (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    private JobInfo createJobInfo(int jobId) {
        JobInfo.Builder build = new JobInfo.Builder(jobId, new ComponentName(getPackageName(), MyJobService.class.getName()));
        PersistableBundle extras = new PersistableBundle();
        extras.putString("key1", "value1");
        extras.putString("key2", "value2");
        build.setExtras(extras)

                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        build.setPeriodic(1000);
        build.setPersisted(true);
//        build.setRequiresCharging(true);
        return build.build();
    }

    private void addData(String content) {
        if (mAdapter != null) {
            mAdapter.addData((mAdapter.getCount() + 1) + ".   " + content);
            mListView.setSelection(mAdapter.getCount()-1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_start_job:
                startJob();
                break;
            case R.id.bt_cancel_all:
                cancelAllJobs();
                break;
            case R.id.bt_clear_log:
                clearLogData();
                break;
        }
    }

    private void clearLogData() {
        mData.clear();
        mAdapter.notifyDataSetChanged();
    }

    static class MyHandler extends Handler {
        WeakReference<MainActivity> mActivity;

        MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity theActivity = mActivity.get();
            if (theActivity == null || theActivity.isFinishing()) {
                return;
            }
            theActivity.addData(msg.obj.toString());
            switch (msg.what) {
//                case MyJobService.MSG_START_JOB:
//                    Log.i(TAG, "MSG_START_JOB: ");
//                    break;
//                case MyJobService.MSG_STOP_JOB:
//                    Log.i(TAG, "MSG_STOP_JOB: ");
//                    break;
                default:
                    break;
            }
        }
    }
}
