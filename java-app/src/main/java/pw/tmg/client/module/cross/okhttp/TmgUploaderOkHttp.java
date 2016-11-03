package pw.tmg.client.module.cross.okhttp;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import okhttp3.*;
import pw.tmg.client.model.UploadResult;
import pw.tmg.client.model.UploadTarget;
import pw.tmg.client.model.Uploader;
import rx.Subscriber;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public final class TmgUploaderOkHttp implements Uploader {
    @Inject @Named("api-key") private String apiKey;
    private final OkHttpClient client = new OkHttpClient.Builder().followRedirects(false).build();

    @Override
    public Subscriber<? super UploadTarget> call(Subscriber<? super UploadResult> subscriber) {
        return new Subscriber<UploadTarget>() {
            private AtomicBoolean finished = new AtomicBoolean(false), completed = new AtomicBoolean(false);

            @Override
            public void onCompleted() {
                if (finished.get() && !completed.get())
                    callCompleted();
            }

            private void callCompleted() {
                subscriber.onCompleted();
                completed.set(true);
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onNext(UploadTarget uploadTarget) {
                try {
                    Request file = new Request.Builder().url("https://tmg.pw/up").header("API-KEY", apiKey).post(
                            new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("file",
                                            uploadTarget.getOriginalName(),
                                            RequestBody.create(MediaType.parse(uploadTarget.getMimeType()), uploadTarget.readData()))
                                    .build())
                            .build();
                    client.newCall(file).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            onError(e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.code() != 302) {
                                onFailure(call, new IOException("Failed to upload, status " + response.code()));
                                return;
                            }
                            subscriber.onNext(new UploadResult("https://tmg.pw" + response.header("Location")));
                            if (!finished.getAndSet(true))
                                callCompleted();
                        }
                    });
                } catch (IOException e) {
                    onError(e);
                }
            }
        };
    }
}
