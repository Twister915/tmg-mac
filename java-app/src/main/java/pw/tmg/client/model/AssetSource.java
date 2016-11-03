package pw.tmg.client.model;

import java.io.InputStream;

public interface AssetSource {
    InputStream getResourceAsStream(String name);
}
