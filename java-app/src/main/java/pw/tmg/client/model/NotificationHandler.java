package pw.tmg.client.model;

import rx.Single;

public interface NotificationHandler {
    Single<Void> postNotification(Notification notification);
}
