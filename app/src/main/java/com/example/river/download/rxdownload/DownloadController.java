package com.example.river.download.rxdownload;

import android.widget.Button;

/**
 * Created by river on 2018/4/6.
 */

public class DownloadController {

    private DownloadState mState;
    private Button button;

    public DownloadController(Button button) {
        this.button = button;
        setState(new NormalState());
    }

    public void setState(DownloadState state) {
        mState = state;
        mState.setText(button);
    }

    public void handleStateChanged(DownloadEvent event) {
        int state = event.getState();
        switch (state) {
            case DownloadManager.NORMAL_STATE:
                setState(new DownloadController.NormalState());
                break;
            case DownloadManager.START_STATE:
                setState(new DownloadController.StartState());
                break;
            case DownloadManager.PAUSE_STATE:
                setState(new DownloadController.PauseState());
                break;
            case DownloadManager.CANCEL_STATE:
                setState(new DownloadController.StartState());
                break;
            case DownloadManager.COMPLETE_STATE:
                setState(new DownloadController.StartState());
                break;
        }
    }

    public void handleClick(Callback callback) {
        mState.handleClick(callback);
    }

    public static class NormalState implements DownloadState {

        @Override
        public void setText(Button btnState) {
            btnState.setText("开始");
        }

        @Override
        public void handleClick(Callback callback) {
            callback.startDownload();
        }
    }

    public static class StartState implements DownloadState {

        @Override
        public void setText(Button btnState) {
            btnState.setText("暂停");
        }

        @Override
        public void handleClick(Callback callback) {
            callback.pauseDownload();
        }
    }

    public static class PauseState implements DownloadState {

        @Override
        public void setText(Button btnState) {
            btnState.setText("继续");
        }

        @Override
        public void handleClick(Callback callback) {
            callback.startDownload();
        }
    }


}
