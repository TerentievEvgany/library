package grytsenko.library.service.book;

import static grytsenko.library.repository.RepositoryUtils.delete;
import static grytsenko.library.repository.RepositoryUtils.save;
import static grytsenko.library.util.DateUtils.now;
import grytsenko.library.model.book.BookDetails;
import grytsenko.library.model.book.OfferedBook;
import grytsenko.library.model.book.SharedBook;
import grytsenko.library.model.user.User;
import grytsenko.library.repository.NotUpdatedException;
import grytsenko.library.repository.book.OfferedBooksRepository;
import grytsenko.library.repository.book.SharedBooksRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages offered books.
 */
@Service
public class ManageOfferedBooksService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ManageOfferedBooksService.class);

    @Autowired
    protected OfferedBooksRepository offeredBooksRepository;
    @Autowired
    protected SharedBooksRepository sharedBooksRepository;

    /**
     * Reserves a book for user.
     */
    @Transactional
    public OfferedBook vote(OfferedBook book, User user)
            throws NotUpdatedException {
        LOGGER.debug("Add vote from {} for book {}.", user.getUsername(),
                book.getId());
        if (book.hasVoter(user)) {
            throw new NotUpdatedException("User can vote once.");
        }

        book.addVoter(user);
        return save(book, offeredBooksRepository);
    }

    /**
     * Adds a shared book that corresponds to offered book.
     */
    @Transactional
    public SharedBook share(OfferedBook book, User manager)
            throws NotUpdatedException {
        LOGGER.debug("Share book {}.", book.getId());
        if (!manager.isManager()) {
            throw new NotUpdatedException("User has no permissions.");
        }

        LOGGER.debug("Delete offered book {}.", book.getId());
        delete(book, offeredBooksRepository);

        LOGGER.debug("Create shared book.", book.getId());
        BookDetails details = book.getDetails();
        SharedBook addedBook = SharedBook.create(details, manager, now());

        LOGGER.debug("Add voters as subscribers to shared book.");
        addedBook.setSubscribers(book.getVoters());

        LOGGER.debug("Add shared book.");
        return save(addedBook, sharedBooksRepository);
    }

    /**
     * Removes book from list of shared books.
     */
    public void remove(OfferedBook book, User manager)
            throws NotUpdatedException {
        LOGGER.debug("Remove book {}.", book.getId());
        if (!manager.isManager()) {
            throw new NotUpdatedException("User has no permissions.");
        }

        delete(book, offeredBooksRepository);
    }

}
