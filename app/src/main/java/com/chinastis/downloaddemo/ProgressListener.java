package com.chinastis.downloaddemo;

/**
 * Created by MENG on 2017/4/7.
 */

public interface ProgressListener {
    void onProgress(long totalBytes, long remainingBytes, boolean done);
}
