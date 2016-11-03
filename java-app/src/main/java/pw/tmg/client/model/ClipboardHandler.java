package pw.tmg.client.model;

public interface ClipboardHandler {
    ClipboardContent getAllClipboardContent();
    void setClipboard(String clipboard);
}
