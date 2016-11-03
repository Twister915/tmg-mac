package pw.tmg.client.model;

import rx.Single;

import java.io.File;

public interface ScreenShotter {
    Single<File> takeScreenshot(CaptureMode mode);
}
