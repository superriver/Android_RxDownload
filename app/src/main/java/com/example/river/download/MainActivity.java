package com.example.river.download;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.river.download.rxdownload.Callback;
import com.example.river.download.rxdownload.DBOption;
import com.example.river.download.rxdownload.DownloadController;
import com.example.river.download.rxdownload.DownloadListener;
import com.example.river.download.rxdownload.DownloadRecord;
import com.example.river.download.rxdownload.FileHelper;
import com.example.river.download.rxdownload.RxDownload;
import com.example.river.download.rxdownload.Utils;
import com.trello.rxlifecycle.components.RxActivity;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class MainActivity extends RxActivity {
    private Button mBtn;

    private NumberProgressBar mProgressBar;
    private DownloadRecord mRecord;
    private TextView tvSize, tvName;
    private String default_url = "http://www.21yey.com/clientdownload/android/family.apk";

    private DownloadController mController;
    private DBOption mOption;
    private String name = "family.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtn = findViewById(R.id.start);
        tvSize = findViewById(R.id.tv_size);
        tvName = findViewById(R.id.tv_name);
        mProgressBar = findViewById(R.id.number_progress_bar);
        tvName.setText(name);
        mController = new DownloadController(mBtn);
        mOption = DBOption.getSingleton(MainActivity.this);
        checkDB();
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mController.handleClick(new Callback() {
                    @Override
                    public void startDownload() {
                        start();
                    }

                    @Override
                    public void pauseDownload() {
                        pause();
                    }

                    @Override
                    public void cancelDownload() {
                        cancel();
                    }

                    @Override
                    public void completeDownload() {
                        complete();
                    }
                });
            }
        });

    }

    private void checkDB() {
        RxDownload.of().with(this).getDownloadRecordFromDB(default_url).filter(new Predicate<DownloadRecord>() {
            @Override
            public boolean test(DownloadRecord record) throws Exception {
                return !record.isFinished();
            }
        }).subscribe(new Consumer<DownloadRecord>() {
            @Override
            public void accept(DownloadRecord record) throws Exception {
                mProgressBar.setMax((int) record.getTotalSize());
                mProgressBar.setProgress((int) record.getProgress());

            }
        });
    }

    public void start() {
        mController.setState(new DownloadController.StartState());

        RxDownload.of().url(default_url).start(new DownloadListener() {
            @Override
            public void onProgress(DownloadRecord record) {
                mRecord = record;
                mProgressBar.setMax((int) record.getTotalSize());
                mProgressBar.setProgress((int) record.getProgress());
                tvSize.setText(getString(R.string.size, Utils.formatSize(record.getProgress()), Utils.formatSize(record.getTotalSize())));
            }

            @Override
            public void onSuccess(DownloadRecord record) {
                mController.setState(new DownloadController.CompleteState());
                record.setFinished(true);
                mOption.delete(record.getUrl());
            }

            @Override
            public void onFailed(DownloadRecord record) {
                mOption.insert(record);
            }

        });
    }

    public void pause() {
        mController.setState(new DownloadController.PauseState());
        RxDownload.of().pause(mRecord);

    }

    public void cancel() {
        mController.setState(new DownloadController.NormalState());
        RxDownload.of().cancel(mRecord);

    }

    public void complete() {
        FileHelper.openDirect(MainActivity.this, mRecord);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOption.closeDB();
    }
}
