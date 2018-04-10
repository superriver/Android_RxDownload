package com.example.river.download.rxdownload;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by river on 2018/4/5.
 */

public class RxUtil {

    public static Function<String, ObservableSource<DownloadRecord>> fun1() {

        return new Function<String, ObservableSource<DownloadRecord>>() {
            @Override
            public ObservableSource<DownloadRecord> apply(String s) throws Exception {
                return null;
            }
        };
    }
}
