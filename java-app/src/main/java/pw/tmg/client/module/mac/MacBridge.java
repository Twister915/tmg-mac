package pw.tmg.client.module.mac;

import com.sun.jna.*;

interface MacBridge extends Library {
    String JNA_LIBRARY_NAME = "mac-utils";
    NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(MacBridge.JNA_LIBRARY_NAME);
    MacBridge INSTANCE = (MacBridge)Native.loadLibrary(MacBridge.JNA_LIBRARY_NAME, MacBridge.class);

    interface notification_callback extends Callback {
        void apply();
    }

    /**
     * Original signature : <code>int sendNotification(const char*, const char*, const char*, const char*)</code><br>
     * <i>native declaration : line 15</i>
     */
    int sendNotification(String title, String subtitle, String message, String sound, notification_callback callback);

    /**
     * Original signature : <code>PasteboardContent** getPasteboardContents()</code><br>
     * <i>native declaration : line 16</i>
     */
    Pointer[] getPasteboardContents();

    /**
     * Original signature : <code>int writeToPasteboard(const char*)</code><br>
     * <i>native declaration : line 17</i>
     */
    int writeToPasteboard(String data);
}
