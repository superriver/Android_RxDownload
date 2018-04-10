package com.example.river.download.rxdownload;

/**
 * Created by Administrator on 2017/10/18.
 */

public class DownloadRecord {
    //文件名
    private String fileName;
    //文件地址
    private String url;
    //文件的总大小
    private long totalSize;
    //文件下载进度
    private long progress;

    //文件保存路径
    private String savePath;
    //是否下载完成
    private boolean isFinished = false;

    private String state;

    public DownloadRecord() {

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }


    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
