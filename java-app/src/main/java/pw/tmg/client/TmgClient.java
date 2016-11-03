package pw.tmg.client;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import pw.tmg.client.model.AssetSource;
import pw.tmg.client.module.MainModule;

import java.awt.*;
import java.io.InputStream;

@Singleton
public final class TmgClient implements AssetSource {
    public static void main(String[] args) throws Exception {
        Guice.createInjector(new MainModule()).getInstance(TmgClient.class).go();
    }

    @Inject private TmgTrayIcon trayIcon;

    public void go() throws AWTException {
        if (!SystemTray.isSupported())
            throw new IllegalStateException("Could not find system tray!");
        SystemTray.getSystemTray().add(trayIcon.getTrayIcon());
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream resourceAsStream = getClass().getResourceAsStream(name);
        if (resourceAsStream == null)
            throw new IllegalStateException("The resource is null!");
        return resourceAsStream;
    }
}
