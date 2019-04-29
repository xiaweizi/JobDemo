package com.example.xiaweizi.jobdemo;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Job--Main::";
    private static final int DEBUG_COLOR = Color.parseColor("#5d8bdf");
    public static final int SERVICE_COLOR = Color.parseColor("#2fcc45");
    private ListView mListView;
    private MyAdapter mAdapter;
    private List<LogContent> mData = new ArrayList<>();
    public static MainActivity mainActivity; // 方便测试使用 static，真正场景严禁使用
    private MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        mAdapter = new MyAdapter(this);
        mListView = findViewById(R.id.list_view);
        findViewById(R.id.bt_start_job).setOnClickListener(this);
        findViewById(R.id.bt_cancel_all).setOnClickListener(this);
        findViewById(R.id.bt_clear_log).setOnClickListener(this);
        final TextView marqueeTextView = findViewById(R.id.tv_marquee);
        final ViewGroup.LayoutParams layoutParams = marqueeTextView.getLayoutParams();

        mListView.setAdapter(mAdapter);
        mAdapter.setData(mData);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutParams.width = 400;
                marqueeTextView.setLayoutParams(layoutParams);
            }
        }, 2000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                marqueeTextView.setLayoutParams(layoutParams);
            }
        }, 4000);

    }

    private void startJob() {
        addData("click start job", DEBUG_COLOR);
        mHandler.reset();
        mHandler.removeMessages(1);
        mHandler.sendEmptyMessage(1);
        getJobScheduler().schedule(createJobInfo(110));
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
        build.setMinimumLatency(15000);
        build.setOverrideDeadline(10000);
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

    static class MyHandler extends Handler {
        WeakReference<MainActivity> mActivity;
        private int count = 0;

        MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity theActivity = mActivity.get();
            if (theActivity == null || theActivity.isFinishing()) {
                return;
            }
            JobScheduler jobScheduler = (JobScheduler) theActivity.getSystemService(JOB_SCHEDULER_SERVICE);
            if (jobScheduler != null) {
                List<JobInfo> allPendingJobs = jobScheduler.getAllPendingJobs();
                if (allPendingJobs != null) {
                    Log.i(TAG, "pending jobs size: " + allPendingJobs.size());
                    if (allPendingJobs.size() > 0) {
                        Log.i(TAG, "job id: " + allPendingJobs.get(0).getId());
                    }
                }
            }
            theActivity.addData("count--" + count);
            count ++;
            sendEmptyMessageDelayed(1, 1000);
        }

        void reset() {
            count = 0;
        }
    }
}
