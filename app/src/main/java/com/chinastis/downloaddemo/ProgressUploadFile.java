package com.chinastis.downloaddemo;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class ProgressUploadFile {

    private static final OkHttpClient okHttpClient = new OkHttpClient();

    static void run(ProgressListener listener) {

        String filePath = Environment.getExternalStorageDirectory().getPath()+
                File.separator + "外业巡查" + File.separator + "巡查信息"
                +File.separator + "430781-87"
                +File.separator + "image.jpg";

        File file = new File(filePath);

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file",file.getName(),createCustomRequestBody(MultipartBody.FORM, file,listener));



        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url("http://192.168.1.120:8080/domain/service/rest/wyxc/saveData") //地址
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("response.body().string() = " + response.body().string());
            }
        });
    }

    private static RequestBody createCustomRequestBody(final MediaType contentType, final File file, final ProgressListener listener) {
        return new RequestBody() {
            @Override public MediaType contentType() {
                return contentType;
            }

            @Override public long contentLength() {
                return file.length();
            }

            @Override public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    Buffer buf = new Buffer();
                    Long remaining = contentLength();
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        listener.onProgress(contentLength(), remaining -= readCount, remaining == 0);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

}
