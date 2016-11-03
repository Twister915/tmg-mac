package pw.tmg.client.model;

import java.io.IOException;

public interface UploadTarget {
    byte[] readData() throws IOException;
    String getMimeType();
    String getOriginalName();
}
