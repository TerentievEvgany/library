package grytsenko.library.repository;

/**
 * Thrown if entity was not found in repository.
 */
public class NotFoundException extends Exception {

    private static final long serialVersionUID = -7437494766998594233L;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
