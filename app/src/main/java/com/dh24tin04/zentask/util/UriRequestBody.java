package com.dh24tin04.zentask.util;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class UriRequestBody extends RequestBody {

    private final ContentResolver resolver;
    private final Uri uri;
    private final MediaType mediaType;
    private final long size;

    public UriRequestBody(ContentResolver resolver, Uri uri, MediaType mediaType, long size) {
        this.resolver = resolver;
        this.uri = uri;
        this.mediaType = mediaType;
        this.size = size;
    }

    @Override
    public MediaType contentType() {
        return mediaType;
    }

    @Override
    public long contentLength() {
        return size; // -1 => OkHttp tự chuyển sang chunked upload
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        try (InputStream in = resolver.openInputStream(uri)) {
            if (in == null) throw new IOException("Không mở được file đã chọn");
            try (Source source = Okio.source(in)) {
                sink.writeAll(source);
            }
        }
    }
}