package com.example.river.download.rxdownload;

/**
 * Created by river on 2018/4/6.
 */

public interface DownloadListener {

    /**
     * 通知当前的下载进度
     */
    void onProgress(DownloadRecord record);

    /**
     * 通知下载成功
     */
    void onSuccess(DownloadRecord record);

    /**
     * 通知下载失败
     */
    void onFailed(DownloadRecord record);

//    /**
//     * 通知下载暂停
//     */
//    void onPaused();
//
//    /**
//     * 通知下载取消事件
//     */
//    void onCanceled();
}
