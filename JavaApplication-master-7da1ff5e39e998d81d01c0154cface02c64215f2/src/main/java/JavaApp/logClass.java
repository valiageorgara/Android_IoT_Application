package JavaApp;

import static JavaApp.JavaApp.quietMode;

public class logClass {
    /**
     * Utility method to handle logging. If 'quietMode' is set, this method does nothing
     * @param message the message to log
     */
    public static void log(String message) {
        if (!quietMode) {
            System.out.println(message);
        }
    }
}
