//package com.example.river.download.rxdownload;
//
//import io.reactivex.Observer;
//import io.reactivex.disposables.Disposable;
//
///**
// * Created by river on 2018/4/2.
// */
//
//public abstract class BaseObserver<T> implements Observer<T> {
//    protected String errMsg = "";
//    protected Disposable disposable;
//    protected T t;
//    @Override
//    public void onSubscribe(Disposable d) {
//        disposable = d;
//    }
//
//    @Override
//    public void onNext(T t) {
//        this.t = t;
//    }
//
//    @Override
//    public void onError(Throwable e) {
//        if (disposable != null && !disposable.isDisposed()) {
//            disposable.dispose();
//        }
//
//    }
//
//    @Override
//    public void onComplete() {
//        if (disposable != null && !disposable.isDisposed()) {
//            disposable.dispose();
//        }
//    }
//    abstract void unSubscription();
//}
