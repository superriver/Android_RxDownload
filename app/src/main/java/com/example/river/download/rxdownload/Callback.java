package com.example.river.download.rxdownload;

/**
 * Created by river on 2018/4/6.
 */

public interface Callback {
    void startDownload();

    void pauseDownload();

    void cancelDownload();
}