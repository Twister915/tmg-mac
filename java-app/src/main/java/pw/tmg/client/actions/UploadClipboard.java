package pw.tmg.client.actions;

import pw.tmg.client.model.TmgAction;
import pw.tmg.client.model.UploadTarget;
import rx.Single;

import java.awt.event.KeyEvent;

@ActionMeta(name = "Upload Clipboard", defaultHotkeyCode = KeyEvent.VK_5)
public final class UploadClipboard implements TmgAction {
    @Override
    public Single<UploadTarget> call() {
        return null;
    }
}
