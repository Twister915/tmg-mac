package pw.tmg.client.model;

import rx.Single;
import rx.schedulers.Schedulers;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public interface AudioEngine {
    void playSound(String assetName) throws IOException, LineUnavailableException, UnsupportedAudioFileException, InterruptedException;
    default Single<Void> tryPlaySound(String assetName) {
        return Single.<Void>create(s -> {
            try {
                playSound(assetName);
                s.onSuccess(null);
            } catch (Exception e) {
                s.onError(e);
            }
        }).subscribeOn(Schedulers.newThread());
    }
}
