package pw.tmg.client.model;

import lombok.Data;
import okio.ByteString;
import okio.Okio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Data public final class FileUploadTarget implements UploadTarget {
    private final File target;
    private final String originalName, mimeType;

    @Override
    public ByteString readData() throws IOException {
        return Okio.buffer(Okio.source(target)).readByteString();
    }
}
