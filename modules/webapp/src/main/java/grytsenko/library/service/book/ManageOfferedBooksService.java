package grytsenko.library.service.book;

import static grytsenko.library.service.book.ManageBooksHelper.delete;
import static grytsenko.library.service.book.ManageBooksHelper.save;
import static grytsenko.library.util.DateUtils.now;
import grytsenko.library.model.book.BookDetails;
import grytsenko.library.model.book.OfferedBook;
import grytsenko.library.model.book.SharedBook;
import grytsenko.library.model.user.User;
import grytsenko.library.repository.OfferedBooksRepository;
import grytsenko.library.repository.SharedBooksRepository;

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
            throws BookNotUpdatedException {
        LOGGER.debug("Add vote from {} for book {}.", user.getUsername(),
                book.getId());
        if (book.hasVoter(user)) {
            throw new BookNotUpdatedException("User can vote once.");
        }

        book.addVoter(user);
        return save(book, offeredBooksRepository);
    }

    /**
     * Adds a shared book that corresponds to offered book.
     */
    @Transactional
    public SharedBook share(OfferedBook book, User manager)
            throws BookNotUpdatedException {
        LOGGER.debug("Share book {}.", book.getId());
        if (!manager.isManager()) {
            throw new BookNotUpdatedException("User has no permissions.");
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
            throws BookNotUpdatedException {
        LOGGER.debug("Remove book {}.", book.getId());
        if (!manager.isManager()) {
            throw new BookNotUpdatedException("User has no permissions.");
        }

        delete(book, offeredBooksRepository);
    }

    @Transactional
    public OfferedBook add(OfferedBook book, User manager)
            throws BookNotUpdatedException {
        if (!manager.isManager()) {
            throw new BookNotUpdatedException("User has no permissions.");
        }

        return save(book, offeredBooksRepository);
    }
}
