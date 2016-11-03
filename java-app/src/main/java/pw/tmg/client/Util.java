package pw.tmg.client;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

public class Util {
    @SuppressWarnings("unchecked")
    public static <T extends Structure> T[] read(Pointer[] pointers, Class<T> clazz) throws Exception {
        T[] os = (T[]) Array.newInstance(clazz, pointers.length);
        Constructor<T> constructor = clazz.getConstructor(Pointer.class);
        for (int i = 0; i < pointers.length; i++) {
            T t = constructor.newInstance(pointers[i]);
            t.autoRead();
            os[i] = t;
        }
        return os;
    }
}
