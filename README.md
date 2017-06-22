# ProgressUpload
## okhttp 带进度上传
### 核心代码：

重写RequestBody，添加进度监听<br>

```java
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
    }//java
