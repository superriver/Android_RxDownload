package com.example.river.download.rxdownload;

/**
 * Created by river on 2018/4/7.
 */

public class DownloadEvent {
    private int state = DownloadManager.NORMAL_STATE;
    private DownloadState downloadState;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public DownloadState getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(DownloadState downloadState) {
        this.downloadState = downloadState;
    }
}
