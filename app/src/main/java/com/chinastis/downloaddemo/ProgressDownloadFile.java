package com.chinastis.downloaddemo;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by xianglong on 2017/7/6.
 */

public class ProgressDownloadFile {

    private static OkHttpClient okHttpClient = new OkHttpClient() ;

    private static BufferedSource source;

    static void run (final ProgressListener listener, String url, final String filePath) {

//        Interceptor interceptor = new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//
//                Response originalResponse = chain.proceed(chain.request());
//
//                return  originalResponse.newBuilder()
//                        .body(createCustemResponsBody(originalResponse.body(),listener))
//                        .build();
//
//            }
//        };
//        okHttpClient.networkInterceptors().add(interceptor);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        okHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        InputStream is = null;
                        FileOutputStream fos = null;

                        File file = new File(filePath);
                        if( !file.exists()) {
                            Log.e("MENG","创建");
                            if(file.mkdirs()) {

                                Log.e("MENG","创建成功");
                            }


                        }

                        fos = new FileOutputStream(new File(filePath,"yaya.jpg"));


                        is = response.body().byteStream();

                        byte[] buf = new byte[1024];
                        int len = 0;
                        while((len = is.read(buf))!=-1) {
                            fos.write(buf,0,len);
                        }
                        fos.flush();
                        fos.close();

                        listener.onProgress(0,0,true);

                    }
                });

    }


    private static ResponseBody createCustemResponsBody(final ResponseBody body, final ProgressListener listener){
        return  new ResponseBody() {
            @Override
            public MediaType contentType() {
                return  body.contentType();
            }

            @Override
            public long contentLength() {
                return body.contentLength();
            }

            @Override
            public BufferedSource source() {
                if (source == null) {
                    //包装
                    source = Okio.buffer(getSource(body.source()));
                }
                return source;
            }


            private Source getSource(Source source) {

                return new ForwardingSource(source) {
                    //当前读取字节数
                    long totalBytesRead = 0L;
                    @Override public long read(Buffer sink, long byteCount) throws IOException {
                        long bytesRead = super.read(sink, byteCount);
                        //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                        totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                        //回调，如果contentLength()不知道长度，会返回-1

                        Log.e("MENG","total:"+contentLength()+",read:"+totalBytesRead);

                        listener.onProgress(contentLength(),totalBytesRead,bytesRead == -1);
//                        progressListener.onResponseProgress
// (totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                        return bytesRead;
                    }
                };
            }
        };

    }
}
