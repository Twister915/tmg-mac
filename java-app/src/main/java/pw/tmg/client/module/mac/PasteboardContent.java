package pw.tmg.client.module.mac;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
class PasteboardContent extends Structure {
    /** C type : const char* */
    public String type;

    /** C type : unsigned char* */
    public Pointer data;

    public long length;

    public PasteboardContent() {
        super();
    }

    protected List<? > getFieldOrder() {
        return Arrays.asList("type", "data", "length");
    }

    public PasteboardContent(Pointer peer) {
        super(peer);
    }
}