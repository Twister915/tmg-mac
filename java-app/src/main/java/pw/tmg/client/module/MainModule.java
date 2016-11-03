package pw.tmg.client.module;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.sun.jna.Platform;
import pw.tmg.client.TmgClient;
import pw.tmg.client.TmgTrayIcon;
import pw.tmg.client.actions.ActionManager;
import pw.tmg.client.model.*;
import pw.tmg.client.module.cross.audio.JavaAudio;
import pw.tmg.client.module.mac.MacPlatformModule;
import pw.tmg.client.module.cross.okhttp.TmgUploadedOkHttp;

public final class MainModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TmgClient.class);
        bind(TmgTrayIcon.class);
        bind(ActionManager.class).asEagerSingleton();
        bind(AssetSource.class).to(TmgClient.class);
        bind(AudioEngine.class).to(JavaAudio.class);

        requireBinding(ClipboardHandler.class);
        requireBinding(NotificationHandler.class);
        requireBinding(KeybindHandler.class);
        requireBinding(ScreenShotter.class);

        switch (Platform.getOSType()) {
            case Platform.MAC:
                install(new MacPlatformModule());
                break;
            default:
                addError("Cannot create platform module, invalid platform!");
        }

        bind(Uploader.class).to(TmgUploadedOkHttp.class);
        bind(String.class).annotatedWith(Names.named("api-key")).toInstance("hi123");
    }
}
