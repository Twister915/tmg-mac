package pw.tmg.client.module.mac;

import com.google.common.net.MediaType;
import okio.ByteString;
import org.apache.tika.Tika;
import org.bridj.Pointer;
import pw.tmg.client.model.ClipboardContent;
import pw.tmg.client.model.UploadTarget;
import rx.Single;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Provider;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class MacClipboardContent implements ClipboardContent {
    private static final Tika tika = new Tika();
    private final Map<String, PasteboardContent> content = new HashMap<>();

    public MacClipboardContent(Pointer<Pointer<PasteboardContent>> pointers) {
        for (Pointer<PasteboardContent> pointer : pointers) {
            if (pointer == null)
                break;

            PasteboardContent pasteboardContent = new PasteboardContent(pointer);
            content.put(pasteboardContent.type().getCString(), pasteboardContent);
        }
        if (content.size() == 0)
            throw new IllegalStateException("Could not read pasteboard content!");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Clipboard (%d) -> \n", content.size()));
        for (PasteboardContent pasteboardContent : content.values()) {
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

    private static Pattern FILE_URI_PATTERN = Pattern.compile("(?:file://|<string>)(/[^<]+)(?:</string>)");

    @Override
    public Single<UploadTarget> toUploadTarget() {
        return Single.create(sub -> {
            //first, check if we have a file
            PasteboardContent filename = content.get("filename");
            if (filename != null) {
                String s = new String(filename.data().getBytes((int) filename.length()));
                Matcher matcher = FILE_URI_PATTERN.matcher(s);
                if (matcher.find()) {
                    File file = new File(matcher.group(1));
                    if (!file.exists() || !file.isFile()) {
                        sub.onError(new Exception("invalid file selected..."));
                    }
                    Path filePath = file.toPath();

                    String name = filePath.getFileName().toString();

                    String mime = null;
                    try {
                        mime = tika.detect(filePath);
                    } catch (IOException ignored) {}

                    if (mime == null || mime.length() == 0)
                        mime = "application/octet-stream";

                    System.out.println(mime);

                    String finalMime = mime;
                    sub.onSuccess(new UploadTarget() {
                        @Override
                        public ByteString readData() throws IOException {
                            return ByteString.of(Files.readAllBytes(filePath));
                        }

                        @Override
                        public String getMimeType() {
                            return finalMime;
                        }

                        @Override
                        public String getOriginalName() {
                            return name;
                        }
                    });
                    return;
                }
            }

            //next, check if we have an html representation of the text in clipboard
            String title = "Clipboard at todo";

            PasteboardContent html = content.get("html"), string = content.get("string");
            Provider<ByteString> loader;
            String mime;
            if (html != null) {
                mime = "text/html";
                loader = () -> ByteString.of(html.data().getBytes((int) html.length()));
            } else if (string != null) {
                mime = "text/plain";
                loader = () -> ByteString.of(string.data().getBytes((int) string.length()));
            } else {
                sub.onError(new Exception("Could not route pasteboard content into something that can be uploaded..."));
                return;
            }

            sub.onSuccess(new UploadTarget() {
                @Override
                public ByteString readData() throws IOException {
                    return loader.get();
                }

                @Override
                public String getMimeType() {
                    return mime;
                }

                @Override
                public String getOriginalName() {
                    return title;
                }
            });
        });
    }
}
