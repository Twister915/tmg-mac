package pw.tmg.client.actions;

import pw.tmg.client.model.CaptureMode;

import java.awt.event.KeyEvent;

@ActionMeta(name = "Capture Selected Window", defaultHotkeyCode = KeyEvent.VK_2)
public final class CaptureWindow extends TmgScreenCapture {
    @Override
    public CaptureMode getCaptureMode() {
        return CaptureMode.WINDOW;
    }
}
