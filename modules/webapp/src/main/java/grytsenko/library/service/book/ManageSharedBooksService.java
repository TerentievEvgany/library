package grytsenko.library.service.book;

import static grytsenko.library.repository.RepositoryUtils.save;
import static grytsenko.library.util.DateUtils.now;
import grytsenko.library.model.book.SharedBook;
import grytsenko.library.model.user.User;
import grytsenko.library.repository.NotUpdatedException;
import grytsenko.library.repository.book.SharedBooksRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages shared books.
 */
@Service
public class ManageSharedBooksService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ManageSharedBooksService.class);

    @Autowired
    protected SharedBooksRepository sharedBooksRepository;

    /**
     * Reserves a book for user.
     */
    @Transactional
    public SharedBook reserve(SharedBook book, User user)
            throws NotUpdatedException {
        if (!book.canBeReserved()) {
            throw new NotUpdatedException("Book can not be reserved.");
        }

        LOGGER.debug("Reserve book {}.", book.getId());
        book.reserve(user, now());

        LOGGER.debug("Remove {} from subsribers.", user.getUsername());
        book.removeSubscriber(user);

        return save(book, sharedBooksRepository);
    }

    /**
     * Releases a book.
     */
    @Transactional
    public SharedBook release(SharedBook book, User user)
            throws NotUpdatedException {
        if (!book.canBeReleasedBy(user)) {
            throw new NotUpdatedException("Book can not be released.");
        }
        LOGGER.debug("Release book {}.", book.getId());

        book.release(user, now());
        return save(book, sharedBooksRepository);
    }

    /**
     * Takes out a book from library.
     */
    @Transactional
    public SharedBook takeOut(SharedBook book, User user)
            throws NotUpdatedException {
        if (!user.isManager()) {
            throw new NotUpdatedException("User has no permissions.");
        }
        if (!book.canBeTakenOutBy(user)) {
            throw new NotUpdatedException("Book can not be taken out.");
        }
        LOGGER.debug("Take out book {}.", book.getId());

        book.takeOut(user, now());
        return save(book, sharedBooksRepository);
    }

    /**
     * Takes back a book to library.
     */
    @Transactional
    public SharedBook takeBack(SharedBook book, User user)
            throws NotUpdatedException {
        if (!user.isManager()) {
            throw new NotUpdatedException("User has no permissions.");
        }
        if (!book.canBeTakenBackBy(user)) {
            throw new NotUpdatedException("Book can not be taken back.");
        }
        LOGGER.debug("Take back book {}.", book.getId());

        book.takeBack(user, now());
        return save(book, sharedBooksRepository);
    }

    /**
     * Creates subscription to book.
     */
    @Transactional
    public SharedBook subscribe(SharedBook book, User user)
            throws NotUpdatedException {
        LOGGER.debug("Add subscriber {} to the book {}.", user.getUsername(),
                book.getId());

        book.addSubscriber(user);
        return save(book, sharedBooksRepository);
    }

    /**
     * Removes subscription to book.
     */
    @Transactional
    public SharedBook unsubscribe(SharedBook book, User user)
            throws NotUpdatedException {
        LOGGER.debug("Remove subscriber {} of the book {}.",
                user.getUsername(), book.getId());

        book.removeSubscriber(user);
        return save(book, sharedBooksRepository);
    }

}
