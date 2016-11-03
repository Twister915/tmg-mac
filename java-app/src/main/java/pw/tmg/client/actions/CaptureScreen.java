package pw.tmg.client.actions;

import pw.tmg.client.model.CaptureMode;

import java.awt.event.KeyEvent;

@ActionMeta(name = "Capture Screen", defaultHotkeyCode = KeyEvent.VK_1)
public final class CaptureScreen extends TmgScreenCapture {
    @Override
    public CaptureMode getCaptureMode() {
        return CaptureMode.SCREEN;
    }
}
