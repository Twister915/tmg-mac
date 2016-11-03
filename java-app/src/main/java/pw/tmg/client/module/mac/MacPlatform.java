package pw.tmg.client.module.mac;

import com.tulskiy.keymaster.common.Provider;
import pw.tmg.client.model.*;
import rx.Single;
import rx.functions.Action0;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

@Singleton
final class MacPlatform implements ClipboardHandler, KeybindHandler, NotificationHandler, ScreenShotter {
    @Inject MacBridge bridge;
    private Provider keybindProvider = Provider.getCurrentProvider(false);

    @Override
    public ClipboardContent getAllClipboardContent() {
        return new MacClipboardContent(bridge.getPasteboardContents());
    }

    @Override
    public void setClipboard(String clipboard) {
        bridge.writeToPasteboard(clipboard);
    }

    @Override
    public Single<Void> bindKey(KeyStroke stroke, Action0 handler) {
        return Single.create(s -> {
            keybindProvider.register(stroke, k -> handler.call());
            s.onSuccess(null);
        });
    }

    @Override
    public Single<Void> postNotification(Notification notification) {
        return Single.create(s -> {
            int i = bridge.sendNotification(notification.getTitle(), null, notification.getMessage(), notification.getSound(), () -> notification.getClickHandler().call());
            if (i != 0)
                s.onError(new Exception());

            s.onSuccess(null);
        });
    }

    @Override
    public Single<File> takeScreenshot(CaptureMode mode) {
        return Single.create(subscriber -> {
            System.out.println("Calling screencapture....");
            String modeFlag;
            switch (mode) {
                case WINDOW:
                    modeFlag = "-w";
                    break;
                case REGION:
                    modeFlag = "-s";
                    break;
                default:
                    modeFlag = "";
            }

            File tempFile;
            try {
                tempFile = File.createTempFile("tmg", "upload");
            } catch (IOException e) {
                subscriber.onError(e);
                return;
            }

            if (tempFile.exists() && !tempFile.delete()) {
                subscriber.onError(new IOException("Could not delete temporary file!"));
                return;
            }

            Process start;
            try {
                start = new ProcessBuilder("screencapture", modeFlag, "-x", "-r", tempFile.getAbsolutePath()).start();
                start.waitFor();
            } catch (IOException | InterruptedException e) {
                subscriber.onError(e);
                return;
            }
            int code = start.exitValue();
            System.out.println(code);

            if (code != 0)
                subscriber.onSuccess(null);
            else if (tempFile.exists())
                subscriber.onSuccess(tempFile);
            else
                subscriber.onError(new IOException("Could not save the file for a screenshot! (does not exist after process exit...)"));
        });
    }
}
