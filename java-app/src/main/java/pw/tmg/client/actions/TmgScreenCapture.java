package pw.tmg.client.actions;

import pw.tmg.client.model.*;
import rx.Single;
import rx.schedulers.Schedulers;

import javax.inject.Inject;

public abstract class TmgScreenCapture implements TmgAction {
    @Inject private ScreenShotter screenShotter;

    @Override
    public Single<UploadTarget> call() {
        return screenShotter.takeScreenshot(getCaptureMode()).subscribeOn(Schedulers.io()).map(screenshot -> {
            if (screenshot == null)
                return null;

            return new FileUploadTarget(screenshot, "Screenshot at todo", "image/png");
        });
    }

    public abstract CaptureMode getCaptureMode();
}
