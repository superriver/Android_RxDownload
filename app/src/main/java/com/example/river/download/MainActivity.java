package com.example.river.download;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.river.download.rxdownload.DBHelper;
import com.example.river.download.rxdownload.DownloadObserver;
import com.example.river.download.rxdownload.DownloadRecord;
import com.example.river.download.rxdownload.RxDownload;

import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    private Button start;
    private Button restart;
    private Button cancel;

    private NumberProgressBar mProgressBar;
    private DownloadRecord fileInfo;

    private RecyclerView rvList;

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.start);
        mProgressBar = findViewById(R.id.number_progress_bar);
        rvList = findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(new DownloadAdapter());
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxDownload.of().url("http://www.21yey.com/clientdownload/android/family.apk").start(new DownloadObserver() {
                    @Override
                    public void onNext(DownloadRecord fileInfo) {
                        super.onNext(fileInfo);
                        mProgressBar.setMax((int) fileInfo.getTotalSize());
                        mProgressBar.setProgress((int) fileInfo.getProgress());
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        start.setText("已完成");
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        disposable = d;
                    }
                });
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(disposable.isDisposed()){
            disposable.dispose();
        }
    }

    private DownloadRecord checkDB() {
        DBHelper dbHelper = new DBHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //  fileInfo = dbHelper.queryData(db, "http://www.21yey.com/clientdownload/android/family.apk");
//        if (fileInfo.getFinished() > 0) {
//            mProgressBar.setProgress(fileInfo.getFinished() * 100 / fileInfo.getLen());
//            start.setText("继续");
//        } else {
//            fileInfo = new DownloadRecord("family.apk", "http://www.21yey.com/clientdownload/android/family.apk");
//
//        }
        return fileInfo;

    }

    private class DownloadAdapter extends RecyclerView.Adapter<BaseViewHolder> {

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }
    }
}
