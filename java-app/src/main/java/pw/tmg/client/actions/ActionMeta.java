package pw.tmg.client.actions;

import java.awt.event.KeyEvent;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ActionMeta {
    String name();
    int defaultHotkeyCode();
    int defaultHotkeyModifier() default KeyEvent.SHIFT_MASK | KeyEvent.META_MASK;
}
