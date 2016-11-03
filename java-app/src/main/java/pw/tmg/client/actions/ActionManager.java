package pw.tmg.client.actions;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import pw.tmg.client.Constants;
import pw.tmg.client.model.*;
import pw.tmg.client.util.TypeMap;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Singleton
public final class ActionManager {
    private final TypeMap<TmgAction> actions;
    @Inject private Provider<Uploader> uploader;
    @Inject private NotificationHandler notificationHandler;
    @Inject private ClipboardHandler clipboardHandler;
    @Inject private AudioEngine audioEngine;

    @Inject public ActionManager(Injector injector, KeybindHandler keystroke) {
        System.out.println("Init");
        actions = new TypeMap<>();
        actions.autoPutMany(injector::getInstance, CaptureRegion.class, CaptureScreen.class, CaptureWindow.class, UploadClipboard.class);
        actions.forEach(new TypeMap.ForEachAction<TmgAction>() {
            @SuppressWarnings("MagicConstant")
            @Override
            public <T extends TmgAction> void perform(Class<T> clazz, T obj) {
                ActionMeta actionMeta = actions.getAnnotation(clazz, ActionMeta.class).orElseThrow(() -> new IllegalStateException("All actions must have a defined meta type!"));
                keystroke.bindKey(KeyStroke.getKeyStroke(actionMeta.defaultHotkeyCode(), actionMeta.defaultHotkeyModifier()), () -> go(obj)).subscribe(v -> {}, e -> {
                    System.out.println("Error registering keybind...");
                    e.printStackTrace();
                });
                System.out.println("Registered " + actionMeta.name());
            }
        });
    }

    public void go(TmgAction action) {
        System.out.println("called");
        action.call().toObservable().filter(e -> e != null).lift(uploader.get()).subscribe(result -> {
            clipboardHandler.setClipboard(result.getUrl());
            notificationHandler.postNotification(new Notification("Uploaded", "Uploaded to: " + result.getUrl(), null, () -> {
                try {
                    Desktop.getDesktop().browse(new URI(result.getUrl()));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            })).subscribe();
            audioEngine.tryPlaySound(Constants.UPLOAD_SOUND).subscribe();
        }, ex -> {
            ex.printStackTrace();
            audioEngine.tryPlaySound(Constants.FAIL_SOUND).subscribe();
        });
    }
}
