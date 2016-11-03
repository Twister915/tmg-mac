package pw.tmg.client.actions;

import pw.tmg.client.model.CaptureMode;

import java.awt.event.KeyEvent;

@ActionMeta(name = "Capture Screen Region", defaultHotkeyCode = KeyEvent.VK_4)
public final class CaptureRegion extends TmgScreenCapture {
    @Override
    public CaptureMode getCaptureMode() {
        return CaptureMode.REGION;
    }
}
