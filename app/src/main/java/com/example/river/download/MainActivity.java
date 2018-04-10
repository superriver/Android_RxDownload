package com.example.river.download;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.river.download.rxdownload.Callback;
import com.example.river.download.rxdownload.DBOption;
import com.example.river.download.rxdownload.DownloadController;
import com.example.river.download.rxdownload.DownloadListener;
import com.example.river.download.rxdownload.DownloadRecord;
import com.example.river.download.rxdownload.RxDownload;
import com.trello.rxlifecycle.components.RxActivity;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class MainActivity extends RxActivity {
    private Button mBtn;

    private NumberProgressBar mProgressBar;
    private DownloadRecord mRecord;

    private RecyclerView rvList;

    private TextView tvSize;
    private String default_url = "http://www.21yey.com/clientdownload/android/family.apk";

    private DownloadController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtn = findViewById(R.id.start);
        tvSize = findViewById(R.id.tv_size);
        mProgressBar = findViewById(R.id.number_progress_bar);
        mController = new DownloadController(mBtn);
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
                });
            }
        });

    }

    private void checkDB() {
        RxDownload.of().with(this).getDownloadRecordFromDB().filter(new Predicate<DownloadRecord>() {
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
                Log.d("huang", "onProgress" + record.getTotalSize());
                mRecord = record;
                mProgressBar.setMax((int) record.getTotalSize());
                mProgressBar.setProgress((int) record.getProgress());
                // tvSize.setText(getString(R.string.size, record.getProgress()/1024, record.getTotalSize()/1024));
            }

            @Override
            public void onSuccess(DownloadRecord record) {
                mBtn.setText("已完成");
                record.setFinished(true);
            }

            @Override
            public void onFailed(DownloadRecord record) {
                DBOption option = DBOption.getSingleton(MainActivity.this);
                option.insert(record);
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


    //    new DownloadObserver() {
//        @Override
//        public void onNext(DownloadRecord fileInfo) {
//            super.onNext(fileInfo);
//            mProgressBar.setMax((int) fileInfo.getTotalSize());
//            mProgressBar.setProgress((int) fileInfo.getProgress());
//            tvSize.setText(getString(R.string.size, fileInfo.getProgress(), fileInfo.getTotalSize()));
//        }
//
//        @Override
//        public void onError(Throwable e) {
//            super.onError(e);
//        }
//
//        @Override
//        public void onComplete() {
//            super.onComplete();
//            start.setText("已完成");
//        }
//
//        @Override
//        public void onSubscribe(Disposable d) {
//            super.onSubscribe(d);
//        }
//    }
//    private DownloadRecord checkDB() {
//        DBHelper dbHelper = new DBHelper(MainActivity.this);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        //  fileInfo = dbHelper.queryData(db, "http://www.21yey.com/clientdownload/android/family.apk");
////        if (fileInfo.getFinished() > 0) {
////            mProgressBar.setProgress(fileInfo.getFinished() * 100 / fileInfo.getLen());
////            start.setText("继续");
////        } else {
////            fileInfo = new DownloadRecord("family.apk", "http://www.21yey.com/clientdownload/android/family.apk");
////
////        }
//        return r;
//
//    }


}
