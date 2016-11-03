package pw.tmg.client.model;

import lombok.Data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Data public final class FileUploadTarget implements UploadTarget {
    private final File target;
    private final String originalName, mimeType;

    @Override
    public byte[] readData() throws IOException {
        byte[] bytes = new byte[(int)target.length()];
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(target))) {
            if (stream.read(bytes) != bytes.length)
                throw new IOException("Couldn't read into buffer...");
        }
        return bytes;
    }
}
