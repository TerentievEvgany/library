package grytsenko.library.repository;

import grytsenko.library.service.book.BookNotUpdatedException;
import grytsenko.library.service.book.ManageOfferedBooksService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Utilities for books.
 */
public class BooksRepositoryUtils {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ManageOfferedBooksService.class);

    /**
     * Updates book in repository.
     */
    public static <T, R extends JpaRepository<T, Long>> T save(T book,
            R repository) throws BookNotUpdatedException {
        try {
            return repository.saveAndFlush(book);
        } catch (Exception exception) {
            LOGGER.warn("Can not save book, because: '{}'.",
                    exception.getMessage());

            throw new BookNotUpdatedException("Can not save book.");
        }
    }

    /**
     * Deletes book from repository.
     */
    public static <T, R extends JpaRepository<T, Long>> void delete(T book,
            R repository) throws BookNotUpdatedException {
        try {
            repository.delete(book);
        } catch (Exception exception) {
            LOGGER.warn("Can not delete book, because: '{}'.",
                    exception.getMessage());

            throw new BookNotUpdatedException("Can not delete book.");
        }
    }

}
