package grytsenko.library.repository;

/**
 * Thrown if entity was not updated in repository.
 */
public class NotUpdatedException extends Exception {

    private static final long serialVersionUID = -6798114787417409556L;

    public NotUpdatedException(String message) {
        super(message);
    }

    public NotUpdatedException(String message, Throwable cause) {
        super(message, cause);
    }

}
