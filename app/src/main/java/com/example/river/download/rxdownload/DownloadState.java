package com.example.river.download.rxdownload;

import android.widget.Button;

/**
 * Created by river on 2018/4/6.
 */

public interface DownloadState {
    void setText(Button btnState);
    void handleClick(Callback callback);
}
