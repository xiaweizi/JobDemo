package com.example.xiaweizi.jobdemo;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author : xiaweizi
 *     class  : com.example.xiaweizi.jobdemo.MyJobService
 *     e-mail : 1012126908@qq.com
 *     time   : 2019/04/17
 *     desc   :
 * </pre>
 */

public class MyJobService extends JobService {
    private static final String TAG = "Job--Service::";
    public static final String KEY_INTENT_MESSENGER = "key_intent_messenger";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob");
        new MyAsyncTask(this).execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob: ");
        addData("stop:" + params.getJobId());
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        addData("on destroy");
    }

    private void addData(String content) {
        if (MainActivity.mainActivity != null) {
            MainActivity.mainActivity.addData("service--" + content, MainActivity.SERVICE_COLOR);
        }
    }

    static class MyAsyncTask extends AsyncTask<JobParameters, Void, String> {
        WeakReference<MyJobService> mService;

        MyAsyncTask(MyJobService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        protected void onPreExecute() {
            MyJobService theService = mService.get();
            if (theService == null) return;
            Log.i(TAG, "onPreExecute: ");
            theService.addData("start job work!");
        }

        @Override
        protected String doInBackground(JobParameters... jobParameters) {
            MyJobService theService = mService.get();
            if (theService == null) return "service is null";
            Log.i(TAG, "doInBackground: " + Thread.currentThread().getName());
            try {
                // 模拟耗时操作
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            theService.jobFinished(jobParameters[0], false);
            return jobParameters[0].getJobId() + " work finish!";
        }

        @Override
        protected void onPostExecute(String s) {
            MyJobService theService = mService.get();
            if (theService == null) return;
            Log.i(TAG, "onPostExecute: " + s);
            theService.addData(s);
        }
    }


}
