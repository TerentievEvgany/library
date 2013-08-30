package grytsenko.library.view.controller;

import static grytsenko.library.view.Navigation.BOOK_ID_PARAM;
import static grytsenko.library.view.Navigation.CURRENT_USER_ATTR;
import static grytsenko.library.view.Navigation.SHARED_BOOK_PATH;
import static grytsenko.library.view.Navigation.getBookIdFromRequest;
import static grytsenko.library.view.Navigation.redirectToSearch;
import static grytsenko.library.view.Navigation.redirectToSharedBook;
import grytsenko.library.model.book.SharedBook;
import grytsenko.library.model.user.User;
import grytsenko.library.repository.NotFoundException;
import grytsenko.library.repository.NotUpdatedException;
import grytsenko.library.service.book.ManageSharedBooksService;
import grytsenko.library.service.book.SearchSharedBooksService;
import grytsenko.library.service.user.ManageUsersService;
import grytsenko.library.service.user.NotifyUsersService;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Processes requests for shared book.
 */
@Controller
@RequestMapping(value = SHARED_BOOK_PATH, params = BOOK_ID_PARAM)
@SessionAttributes(CURRENT_USER_ATTR)
public class SharedBookController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SharedBookController.class);

    @Autowired
    protected ManageUsersService manageUsersService;
    @Autowired
    protected NotifyUsersService notifyUsersService;

    @Autowired
    protected SearchSharedBooksService searchSharedBooksService;
    @Autowired
    protected ManageSharedBooksService manageSharedBooksService;

    @ModelAttribute(CURRENT_USER_ATTR)
    public User currentUser(Principal principal) {
        return manageUsersService.find(principal.getName());
    }

    /**
     * User views details about book.
     * 
     * @throws NotFoundException
     */
    @RequestMapping(method = RequestMethod.GET)
    public String getBook(@RequestParam(BOOK_ID_PARAM) Long bookId, Model model)
            throws NotFoundException {
        LOGGER.debug("Find book {}.", bookId);

        SharedBook book = searchSharedBooksService.find(bookId);
        model.addAttribute("book", book);
        return SHARED_BOOK_PATH;
    }

    /**
     * User reserves a book.
     */
    @RequestMapping(params = "reserve", method = RequestMethod.POST)
    public String reserve(@RequestParam(BOOK_ID_PARAM) Long bookId,
            @ModelAttribute(CURRENT_USER_ATTR) User user)
            throws NotFoundException, NotUpdatedException {
        LOGGER.debug("Reserve book {}.", bookId);

        SharedBook book = searchSharedBooksService.find(bookId);
        book = manageSharedBooksService.reserve(book, user);
        notifyUsersService.notifyReserved(book, user);

        return redirectToSharedBook(bookId);
    }

    /**
     * User or manager releases a book.
     */
    @RequestMapping(params = "release", method = RequestMethod.POST)
    public String release(@RequestParam(BOOK_ID_PARAM) Long bookId,
            @ModelAttribute(CURRENT_USER_ATTR) User user)
            throws NotFoundException, NotUpdatedException {
        LOGGER.debug("Release book {}.", bookId);

        SharedBook book = searchSharedBooksService.find(bookId);
        User wasReservedBy = book.getUsedBy();
        book = manageSharedBooksService.release(book, user);
        notifyUsersService.notifyReleased(book, wasReservedBy);
        notifyUsersService.notifyAvailable(book, book.getSubscribers());

        return redirectToSharedBook(bookId);
    }

    /**
     * Manager takes out a book from library.
     */
    @RequestMapping(params = "takeOut", method = RequestMethod.POST)
    public String takeOut(@RequestParam(BOOK_ID_PARAM) Long bookId,
            @ModelAttribute(CURRENT_USER_ATTR) User user)
            throws NotFoundException, NotUpdatedException {
        LOGGER.debug("Take out book {}.", bookId);

        SharedBook book = searchSharedBooksService.find(bookId);
        book = manageSharedBooksService.takeOut(book, user);
        notifyUsersService.notifyBorrowed(book, book.getUsedBy());

        return redirectToSharedBook(bookId);
    }

    /**
     * Manager takes back a book to library.
     */
    @RequestMapping(params = "takeBack", method = RequestMethod.POST)
    public String takeBack(@RequestParam(BOOK_ID_PARAM) Long bookId,
            @ModelAttribute(CURRENT_USER_ATTR) User user)
            throws NotFoundException, NotUpdatedException {
        LOGGER.debug("Take back book {}.", bookId);

        SharedBook book = searchSharedBooksService.find(bookId);
        User wasBorrowedBy = book.getUsedBy();
        book = manageSharedBooksService.takeBack(book, user);
        notifyUsersService.notifyReturned(book, wasBorrowedBy);
        notifyUsersService.notifyAvailable(book, book.getSubscribers());

        return redirectToSharedBook(bookId);
    }

    /**
     * Manager reminds that book is reserved or is borrowed by user.
     * 
     * @throws NotFoundException
     */
    @RequestMapping(params = "remind", method = RequestMethod.POST)
    public String remind(@RequestParam(BOOK_ID_PARAM) Long bookId,
            @ModelAttribute(CURRENT_USER_ATTR) User user)
            throws NotFoundException {
        LOGGER.debug("Remind about book {}.", bookId);

        SharedBook book = searchSharedBooksService.find(bookId);
        if (!book.isManagedBy(user)) {
            LOGGER.debug("Book {} is not managed by {}.", bookId,
                    user.getUsername());
            return redirectToSharedBook(bookId);
        }

        if (book.isReserved()) {
            notifyUsersService.notifyReserved(book, book.getUsedBy());
        } else if (book.isBorrowed()) {
            notifyUsersService.notifyBorrowed(book, book.getUsedBy());
        }

        return redirectToSharedBook(bookId);
    }

    /**
     * Creates subscription to the book.
     */
    @RequestMapping(params = "subscribe", method = RequestMethod.POST)
    public String subscribe(@RequestParam(BOOK_ID_PARAM) Long bookId,
            @ModelAttribute(CURRENT_USER_ATTR) User user)
            throws NotFoundException, NotUpdatedException {
        LOGGER.debug("Subscribe {} to emails about book {}.",
                user.getUsername(), bookId);

        SharedBook book = searchSharedBooksService.find(bookId);
        book = manageSharedBooksService.subscribe(book, user);

        return redirectToSharedBook(bookId);
    }

    /**
     * Cancels subscription to the book.
     */
    @RequestMapping(params = "unsubscribe", method = RequestMethod.POST)
    public String unsubscribe(@RequestParam(BOOK_ID_PARAM) Long bookId,
            @ModelAttribute(CURRENT_USER_ATTR) User user)
            throws NotFoundException, NotUpdatedException {
        LOGGER.debug("Unsubscribe {} from emails about book {}.",
                user.getUsername(), bookId);

        SharedBook book = searchSharedBooksService.find(bookId);
        book = manageSharedBooksService.unsubscribe(book, user);

        return redirectToSharedBook(bookId);
    }

    /**
     * If book was not updated, then notification should be shown.
     */
    @ExceptionHandler(NotUpdatedException.class)
    public String whenBookNotUpdated(NotUpdatedException exception,
            HttpServletRequest request) {
        Long bookId = getBookIdFromRequest(request);
        LOGGER.warn("Book {} was not updated, because: '{}'.", bookId,
                exception.getMessage());

        FlashMap attrs = RequestContextUtils.getOutputFlashMap(request);
        attrs.put("bookNotUpdated", true);

        return redirectToSharedBook(bookId);
    }

    /**
     * If book was not found, then we redirect user to list of books.
     */
    @ExceptionHandler(NotFoundException.class)
    public String whenBookNotFound(HttpServletRequest request) {
        Long bookId = getBookIdFromRequest(request);
        LOGGER.warn("Book {} was not found.", bookId);
        return redirectToSearch();
    }

}
