package pw.tmg.client.model;

import rx.Single;
import rx.functions.Action0;

import javax.swing.*;

public interface KeybindHandler {
    Single<Void> bindKey(KeyStroke stroke, Action0 handler);
}
