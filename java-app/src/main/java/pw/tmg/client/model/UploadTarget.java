package pw.tmg.client.model;

import okio.ByteString;

import java.io.IOException;

public interface UploadTarget {
    ByteString readData() throws IOException;
    String getMimeType();
    String getOriginalName();
}
