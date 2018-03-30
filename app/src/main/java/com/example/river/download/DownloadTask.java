//package com.example.river.download;
//
//import android.content.Context;
//import android.content.Intent;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Environment;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.RandomAccessFile;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
///**
// * Created by Administrator on 2017/10/19.
// */
//
//public class DownloadTask extends Thread{
//    public static String FILE_PATH = Environment.getExternalStorageDirectory() + "/river";//文件下载保存路径
//    private Context context;
//    private  FileInfo info;
//    private DBHelper dbHelper;
//    private SQLiteDatabase db;
//
//    private int finished = 0;//当前已下载完成的进度
//    public DownloadTask(FileInfo info,Context context){
//        this.info = info;
//        this.context = context;
//        dbHelper = new DBHelper(context);
//        db =dbHelper.getReadableDatabase();
//
//    }
//
//    @Override
//    public void run() {
//        int length = -1;
//        try {
//            HttpURLConnection conn = null;
//            URL url = new URL(info.getUrl());
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setConnectTimeout(5000);
//
//            if(conn.getResponseCode() ==200){
//                length =conn.getContentLength();
//            }
//            if(length<0){
//                return;
//            }
//            File dir = new File(DownloadTask.FILE_PATH);
//            if(!dir.exists()){
//                dir.mkdir();
//            }
//            info.setLen(length);
//            conn.disconnect();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        HttpURLConnection connection = null;
//        RandomAccessFile raf = null;
//        try {
//            URL urls = new URL(info.getUrl());
//            connection = (HttpURLConnection) urls.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setConnectTimeout(3000);
//            //获取上次下载位置
//            int start = info.getFinished();
//            connection.setRequestProperty("Range","bytes="+start+"-"+length);
//            //设置文件写入位置
//            File file = new File(FILE_PATH,info.getFileName());
//            raf= new RandomAccessFile(file,"rwd");
//            raf.seek(start);
//            finished += info.getFinished();
//            if(connection.getResponseCode() == 206){
//                InputStream is =connection.getInputStream();
//                byte[] bytes = new byte[1024*4];
//                int len;
//                while ((len = is.read(bytes))!=-1){
//                    raf.write(bytes,0,len);
//                    finished+=len;
//                    info.setFinished(finished);
//                    if(TaskManager.isPause){
//                        info.setDownloading(false);
//                        dbHelper.insert(db,info);
//                        db.close();
//                        return;
//                    }
//                    if(TaskManager.isCancel){
//                        TaskManager.isCancel = false;
//                        info.setDownloading(false);
//                        info.setFinished(0);
//                        dbHelper.insert(db,info);
//                        db.close();
//                        Intent intent = new Intent(DownloadService.ACTION_UPDATE);
//                        intent.putExtra("finished", 0);
//                        intent.setAction("android.intent.action.ProgressBroadcast");
//                        context.sendBroadcast(intent);
//                        return;
//                    }
//                        Intent intent = new Intent(DownloadService.ACTION_UPDATE);
//                        intent.putExtra("finished", finished * 100 / length);
//                        intent.setAction("android.intent.action.ProgressBroadcast");
//                        context.sendBroadcast(intent);
//                }
//                info.setDownloading(false);
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            if(raf!=null) {
//                try {
//                    raf.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//
//
//}
