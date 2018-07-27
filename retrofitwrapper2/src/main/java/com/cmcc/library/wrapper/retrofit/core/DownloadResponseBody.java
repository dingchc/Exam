package com.cmcc.library.wrapper.retrofit.core;


import com.cmcc.library.wrapper.retrofit.listener.DownloadProgressListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by liupeng_a on 2017/1/10.
 */

public class DownloadResponseBody extends ResponseBody {

    private ResponseBody responseBody;
    private DownloadProgressListener listener;
    private BufferedSource bufferedSource;

    public DownloadResponseBody(ResponseBody responseBody, DownloadProgressListener listener) {
        this.responseBody = responseBody;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {

        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(soruce(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source soruce(Source source) {
        return new ForwardingSource(source) {

            long current = 0L;
            long total = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long byteRead = super.read(sink, byteCount);

                if (listener != null) {
                    if (total == 0) {
                        total = responseBody.contentLength();
                    }

                    current += byteRead != -1 ? byteRead : 0;

                    listener.progress(current, total, current == total);
                }

                return byteRead;
            }
        };
    }
}
