package com.example.river.download.rxdownload;

/**
 * Created by Administrator on 2018/3/29.
 */

public  class DownloadObserver extends BaseObserver<DownloadRecord>{


    @Override
    public void onError(Throwable e) {
       // Log.d("huang", "" + e.getMessage());
        super.onError(e);
        DBOption option = new DBOption(App.getContext());
        option.insert(t);

    }


}
