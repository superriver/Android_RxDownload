package com.example.river.download.rxdownload;

import com.example.river.download.FileInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/3/30.
 */

public class DownloadSubscriber implements ObservableOnSubscribe<FileInfo> {
    private FileInfo fileInfo;
    private OkHttpClient client;
    private Map<String, Call> map;

    public DownloadSubscriber(FileInfo fileInfo, Map<String, Call> map) {
        this.fileInfo = fileInfo;
        this.map = map;
        client = new OkHttpClient();
    }

    @Override
    public void subscribe(ObservableEmitter e) throws Exception {
        long finishedLen = fileInfo.getProgress();
        long totalLen = fileInfo.getLen();
        String url = fileInfo.getUrl();
        Request request = new Request.Builder()
                .addHeader("RANGE", "bytes=" + finishedLen + "-" + totalLen)
                .url(url)
                .build();
        Call call = client.newCall(request);
        map.put(url, call);
        Response response = call.execute();
        File file = new File("", fileInfo.getFileName());
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            is = response.body().byteStream();
            fos = new FileOutputStream(file, true);
            byte[] buffer = new byte[2048];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                finishedLen += len;
                fileInfo.setProgress(finishedLen);
                e.onNext(fileInfo);
            }
            fos.flush();
            map.remove(url);
        } finally {
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }

        }
        e.onComplete();
    }
}
