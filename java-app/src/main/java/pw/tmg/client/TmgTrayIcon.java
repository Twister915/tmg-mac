package pw.tmg.client;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Data;
import pw.tmg.client.model.AssetSource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Singleton
@Data
public final class TmgTrayIcon {
    private final TrayIcon trayIcon;

    @Inject public TmgTrayIcon(AssetSource pool) throws IOException {
        BufferedImage read = ImageIO.read(pool.getResourceAsStream(Constants.ICON_PATH));
        trayIcon = new TrayIcon(read, "tmg.pw");
    }
}
