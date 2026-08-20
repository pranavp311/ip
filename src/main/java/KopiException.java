/**
 * Represents an error caused by an invalid command from the user.
 */
public class KopiException extends Exception {
    public KopiException(String message) {
        super(message);
    }
}
