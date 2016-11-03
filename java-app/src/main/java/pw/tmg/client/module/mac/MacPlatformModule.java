package pw.tmg.client.module.mac;

import com.google.inject.AbstractModule;
import pw.tmg.client.model.*;

public final class MacPlatformModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MacBridge.class).toInstance(MacBridge.INSTANCE);
        bind(ClipboardHandler.class).to(MacPlatform.class);
        bind(KeybindHandler.class).to(MacPlatform.class);
        bind(NotificationHandler.class).to(MacPlatform.class);
        bind(ScreenShotter.class).to(MacPlatform.class);
    }
}
