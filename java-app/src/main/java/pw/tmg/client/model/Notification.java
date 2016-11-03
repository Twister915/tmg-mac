package pw.tmg.client.model;

import lombok.Data;
import rx.functions.Action0;

@Data public final class Notification {
    private final String title, message, sound;
    private final Action0 clickHandler;
}
