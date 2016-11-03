package pw.tmg.client.model;

import rx.Single;
import rx.functions.Func0;

public interface TmgAction extends Func0<Single<UploadTarget>> {}
