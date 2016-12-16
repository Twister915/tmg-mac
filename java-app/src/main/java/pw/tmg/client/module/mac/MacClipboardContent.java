package pw.tmg.client.module.mac;

import okio.ByteString;
import org.bridj.Pointer;
import pw.tmg.client.model.ClipboardContent;
import pw.tmg.client.model.UploadTarget;
import rx.Single;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

final class MacClipboardContent implements ClipboardContent {
    private final List<PasteboardContent> content = new ArrayList<>();

    public MacClipboardContent(Pointer<Pointer<PasteboardContent>> pointers) {
        for (Pointer<PasteboardContent> pointer : pointers) {
            if (pointer == null)
                break;

            PasteboardContent pasteboardContent = new PasteboardContent(pointer);
            content.add(pasteboardContent);
        }
        if (content.size() == 0)
            throw new IllegalStateException("Could not read pasteboard content!");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Clipboard (%d) -> \n", content.size()));
        for (PasteboardContent pasteboardContent : content) {
            byte[] bytes = (byte[]) pasteboardContent.data().getArray((int) pasteboardContent.length());
            String data;

            ByteString of = ByteString.of(bytes);
            if (bytes.length > 512)
                data = of.toString();
            else
                data = of.string(Charset.defaultCharset());

            builder.append(String.format("\t[type] = %s\n\t[length] = %d\n\t[data] = %s\n\n", pasteboardContent.type().getCString(), pasteboardContent.length(), data));
        }
        return builder.toString();

    }

    @Override
    public Single<UploadTarget> toUploadTarget() {
//        PasteboardContent pasteboardContent = content.get(0);
//        return Single.just(new UploadTarget() {
//            @Override
//            public ByteString readData() throws IOException {
//                byte[] bytes = new byte[(int) pasteboardContent.length];
//                pasteboardContent.data.read(0, bytes, 0, bytes.length);
//                return ByteString.of(bytes);
//            }
//
//            @Override
//            public String getMimeType() {
//                return null;
//            }
//
//            @Override
//            public String getOriginalName() {
//                return null;
//            }
//        });
        return null;
    }
}
