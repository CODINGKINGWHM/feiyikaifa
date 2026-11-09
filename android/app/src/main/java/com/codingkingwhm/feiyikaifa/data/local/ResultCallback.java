package com.codingkingwhm.feiyikaifa.data.local;

public interface ResultCallback<T> {

    void onSuccess(T result);

    void onError(Throwable error);
}
