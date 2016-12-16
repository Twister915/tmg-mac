package pw.tmg.client.actions;

import com.google.inject.Inject;
import pw.tmg.client.model.ClipboardHandler;
import pw.tmg.client.model.TmgAction;
import pw.tmg.client.model.UploadTarget;
import rx.Single;

import java.awt.event.KeyEvent;

@ActionMeta(name = "Upload Clipboard", defaultHotkeyCode = KeyEvent.VK_5)
public final class UploadClipboard implements TmgAction {
    @Inject private ClipboardHandler clipboard;

    @Override
    public Single<UploadTarget> call() {
        System.out.println(clipboard.getAllClipboardContent());
        return null;
    }
}
