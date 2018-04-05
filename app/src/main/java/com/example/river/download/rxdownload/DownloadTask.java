package com.example.river.download.rxdownload;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

/**
 * Created by Administrator on 2017/10/19.
 */

public class DownloadTask extends Thread {
    public static String FILE_PATH = Environment.getExternalStorageDirectory() + "/river";//文件下载保存路径
    private Context context;
    private DownloadRecord info;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private int finished = 0;//当前已下载完成的进度

    public DownloadTask(DownloadRecord info, Context context) {
        this.info = info;
        this.context = context;
        dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();

    }

    @Override
    public void run() {

    }


}
