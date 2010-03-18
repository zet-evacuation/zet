/*
 * InvalidFileFormatException.java
 *
 */

package zet.xml;

/**
 *
 * @author Martin Gro�
 */
public class InvalidFileFormatException extends Exception {
    
    public InvalidFileFormatException() {
        super();
    }

    public InvalidFileFormatException(String message) {
        super(message);
    }    
    
    public InvalidFileFormatException(String message, Throwable cause) {
        super(message,cause);
    }
    
}
