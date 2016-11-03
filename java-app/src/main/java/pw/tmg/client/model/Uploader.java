package pw.tmg.client.model;

import rx.Observable;

public interface Uploader extends Observable.Operator<UploadResult, UploadTarget> {}
