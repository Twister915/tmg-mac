package pw.tmg.client.module.cross.audio;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import pw.tmg.client.Constants;
import pw.tmg.client.model.AssetSource;
import pw.tmg.client.model.AudioEngine;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;

@Singleton
public final class JavaAudio implements AudioEngine {
    @Inject private AssetSource assets;

    private static class AudioListener implements LineListener {
        private boolean done = false;

        @Override
        public synchronized void update(LineEvent event) {
            LineEvent.Type eventType = event.getType();
            if (eventType == LineEvent.Type.STOP || eventType == LineEvent.Type.CLOSE) {
                done = true;
                notifyAll();
            }
        }

        public synchronized void waitUntilDone() throws InterruptedException {
            while (!done) {
                wait();
            }
        }
    }

    @Override
    public void playSound(String assetName) throws IOException, LineUnavailableException, UnsupportedAudioFileException, InterruptedException {
        try (AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(assets.getResourceAsStream(String.format("%s/%s.wav", Constants.ASSETS_PATH, assetName))))) {
            AudioListener lineListener = new AudioListener();
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clip.addLineListener(lineListener);
            try {
                clip.start();
                lineListener.waitUntilDone();
            } finally {
                clip.close();
            }
        }
    }
}
