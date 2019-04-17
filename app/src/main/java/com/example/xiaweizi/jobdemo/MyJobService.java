package com.example.xiaweizi.jobdemo;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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
    private Messenger mMessenger;
    public static final String KEY_INTENT_MESSENGER = "key_intent_messenger";
    public static final int MSG_START_JOB = 1;
    public static final int MSG_STOP_JOB = 2;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        mMessenger = intent.getParcelableExtra(KEY_INTENT_MESSENGER);
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob");
        new MyAsyncTask(this).execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob: ");
        sendMessage(MSG_STOP_JOB, params.getJobId());
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    private void sendMessage(int messageId, Object params) {
        if (mMessenger == null) return;
        Message message = Message.obtain();
        message.what = messageId;
        message.obj = "service--" + params;
        try {
            mMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    static class MyAsyncTask extends AsyncTask<Void, Void, String> {
        WeakReference<MyJobService> mService;

        MyAsyncTask(MyJobService service) {
            mService = new WeakReference<MyJobService>(service);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // 模拟耗时操作
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "result";
        }

        @Override
        protected void onPostExecute(String s) {
            MyJobService theService = mService.get();
            if (theService == null) return;
            theService.sendMessage(MSG_START_JOB, s);
        }
    }


}
