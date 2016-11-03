package pw.tmg.client.module.mac;

import com.sun.jna.Pointer;
import okio.ByteString;
import pw.tmg.client.model.ClipboardContent;
import pw.tmg.client.model.UploadTarget;
import rx.Single;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

final class MacClipboardContent implements ClipboardContent {
    private final List<PasteboardContent> content = new ArrayList<>();

    public MacClipboardContent(Pointer[] pointers) {
        for (Pointer pointer : pointers) {
            PasteboardContent pasteboardContent = new PasteboardContent(pointer);
            pasteboardContent.autoRead();
            content.add(pasteboardContent);
        }
        if (content.size() == 0)
            throw new IllegalStateException("Could not read pasteboard content!");
    }

    @Override
    public Single<UploadTarget> toUploadTarget() {
        PasteboardContent pasteboardContent = content.get(0);
        return Single.just(new UploadTarget() {
            @Override
            public ByteString readData() throws IOException {
                byte[] bytes = new byte[(int) pasteboardContent.length];
                pasteboardContent.data.read(0, bytes, 0, bytes.length);
                return ByteString.of(bytes);
            }

            @Override
            public String getMimeType() {
                return null;
            }

            @Override
            public String getOriginalName() {
                return null;
            }
        });
    }
}
