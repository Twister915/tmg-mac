package pw.tmg.client.util;

import rx.functions.Func1;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
public final class TypeMap<E> {
    private final Map<Class<? extends E>, E> backingMap = new HashMap<>();
    private Map<Class<? extends E>, TypeMap<Annotation>> annotations = new HashMap<>();

    public <T extends E> T get(Class<T> type) {
        return (T) backingMap.get(type);
    }

    public void autoPutMany(Class... types) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (Class<?> type : types)
            autoPut((Class<? extends E>) type);
    }

    public void autoPutMany(Func1<Class<? extends E>, E> constructor, Class... types) {
        for (Class type : types)
            autoPut(type, (Func1) constructor);
    }

    public <T extends E> T autoPut(Class<T> type) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return put(type.getConstructor().newInstance());
    }

    public <T extends E> T autoPut(Class<T> type, Func1<Class<T>, T> constructor) {
        return put(type, constructor.call(type));
    }

    public <T extends E> T put(T instance) {
        return put((Class<T>) instance.getClass(), instance);
    }

    public <T extends E> T put(Class<T> type, T instance) {
        return (T) backingMap.put(type, instance);
    }

    public <T extends E> T remove(Class<T> type) {
        return (T)backingMap.remove(type);
    }

    public boolean containsKey(Class<? extends E> type) {
        return backingMap.containsKey(type);
    }

    public boolean containsValue(E type) {
        return backingMap.containsValue(type);
    }

    public Collection<E> values() {
        return backingMap.values();
    }

    public interface ForEachAction<E> {
        <T extends E> void perform(Class<T> clazz, T obj);
    }

    public void forEach(ForEachAction<E> action) {
        for (Map.Entry<Class<? extends E>, E> classEEntry : backingMap.entrySet())
            ((ForEachAction)action).perform(classEEntry.getKey(), classEEntry.getValue());
    }

    public <A extends Annotation> Optional<A> getAnnotation(Class<? extends E> target, Class<A> annotation) {
        A a = null;
        TypeMap<Annotation> annotationTypeMap = annotations.get(target);
        if (annotationTypeMap != null)
            a = annotationTypeMap.get(annotation);

        if (a == null) {
            a = target.getAnnotation(annotation);
            if (annotationTypeMap == null && a != null) {
                annotationTypeMap = new TypeMap<>();
                annotationTypeMap.put(a);
                annotations.put(target, annotationTypeMap);
            }
        }

        return Optional.ofNullable(a);
    }
}
