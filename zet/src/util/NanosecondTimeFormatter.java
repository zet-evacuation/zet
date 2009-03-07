/*
 * NanosecondTimeFormatter.java
 *
 */

package util;

/**
 *
 */
public class NanosecondTimeFormatter {
    
    public static String formatTime(long timeInNanoseconds) {
        long time = timeInNanoseconds;
        int counter = 0;
        int last = 0;
        while (time >= 1000 && counter < 3) {
            last = (int) (time % 1000);
            time /= 1000;
            counter++;
        }
        switch (counter) {
            case 0: return String.format("%1$s ns", time);
            case 1: return String.format("%1$s.%2$03d ms", time, last);// habe hier was geändet, ka was vorher da stand, gab einen utf-8-fehler also "...d?s"
            case 2: return String.format("%1$s.%2$03d ms", time, last);
            case 3: return String.format("%1$s.%2$03d s", time, last);
            default:
                throw new AssertionError("This should not happen.");
        }
    }
    
}
