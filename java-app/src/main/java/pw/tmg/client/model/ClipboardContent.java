package pw.tmg.client.model;

import rx.Single;

public interface ClipboardContent {
    Single<UploadTarget> toUploadTarget();
}
