package com.example.river.download;

/**
 * Created by Administrator on 2017/10/18.
 */

public class FileInfo{
    //文件名
    private String fileName;
    //文件地址
    private String url;
    //文件的总大小
    private long len;

    //文件下载进度
    private long progress;


    private boolean isDownloading;

    public FileInfo() {

    }

    public FileInfo(String fileName, String url) {
        this.fileName = fileName;
        this.url = url;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getLen() {
        return len;
    }

    public void setLen(long len) {
        this.len = len;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }
}
