package com.example.xiaweizi.jobdemo;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Job--Main::";
    private static final int DEBUG_COLOR = Color.parseColor("#5d8bdf");
    public static final int SERVICE_COLOR = Color.parseColor("#2fcc45");
    private Handler mHandler;
    private ListView mListView;
    private MyAdapter mAdapter;
    private List<LogContent> mData = new ArrayList<>();
    public static MainActivity mainActivity; // 方便测试使用 static，真正场景严禁使用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        mHandler = new Handler();
        mAdapter = new MyAdapter(this);
        mListView = findViewById(R.id.list_view);
        findViewById(R.id.bt_start_job).setOnClickListener(this);
        findViewById(R.id.bt_cancel_all).setOnClickListener(this);
        findViewById(R.id.bt_clear_log).setOnClickListener(this);

        mListView.setAdapter(mAdapter);
        mAdapter.setData(mData);
    }

    private void startJob() {
        addData("click start job", DEBUG_COLOR);
        getJobScheduler().schedule(createJobInfo(1));
    }

    private void cancelAllJobs() {
        addData("click cancel all jobs", DEBUG_COLOR);
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
        build.setExtras(extras);
        build.setRequiresCharging(true);
        build.setOverrideDeadline(2000);
        return build.build();
    }

    private void addData(String content) {
        addData(content, Color.parseColor("#2fcc45"));
    }

    public void addData(final String content, final int color) {
        if (mAdapter != null && mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    LogContent logContent = new LogContent();
                    logContent.color = color;
                    logContent.content = (mAdapter.getCount() + 1) + ".   " + content;
                    mAdapter.addData(logContent);
                    mListView.smoothScrollToPosition(mAdapter.getCount() - 1);
                }
            });
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

}
