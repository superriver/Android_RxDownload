//package com.example.river.download;
//
//import android.app.IntentService;
//import android.content.Intent;
//
//import com.example.river.download.rxdownload.DownloadRecord;
//
///**
// * Created by Administrator on 2017/10/18.
// */
//
//public class DownloadService extends IntentService {
//    public static final String ACTION_START = "start";
//    public static final String ACTION_RESTART = "restart";
//    public static final String ACTION_CANCEL = "cancel";
//    public static final String ACTION_UPDATE = "update";
//
//    public DownloadService() {
//        super("download");
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        TaskManager task = TaskManager.getInstance();
//        DownloadRecord fileInfo = (DownloadRecord) intent.getSerializableExtra("fileInfo");
//        if (intent.getAction().equals(ACTION_START)) {
//            task.start(DownloadService.this, fileInfo);
//
//        }
//        if (intent.getAction().equals(ACTION_RESTART)) {
//            task.restart(DownloadService.this, fileInfo);
//        }
//        if (intent.getAction().equals(ACTION_CANCEL)) {
//            task.cancel();
//        }
//
//    }
//
//
//}
