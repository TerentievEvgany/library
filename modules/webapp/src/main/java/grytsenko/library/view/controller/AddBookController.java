package grytsenko.library.view.controller;

import grytsenko.library.model.book.BookDetails;
import grytsenko.library.model.book.SharedBook;
import grytsenko.library.model.book.SharedBookStatus;
import grytsenko.library.model.user.User;
import grytsenko.library.repository.NotFoundException;
import grytsenko.library.repository.NotUpdatedException;
import grytsenko.library.service.book.ManageSharedBooksService;
import grytsenko.library.service.user.ManageUsersService;
import grytsenko.library.validator.AddBookValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

import static grytsenko.library.view.Navigation.ADD_BOOK_PATH;
import static grytsenko.library.view.Navigation.CURRENT_USER_ATTR;
import static grytsenko.library.view.Navigation.redirectToSharedBook;

/**
 * Processes requests for adding a new book.
 */
@Controller
@RequestMapping(ADD_BOOK_PATH)
public class AddBookController {

    @Autowired
    private AddBookValidator addBookValidator;

    @Autowired
    private ManageSharedBooksService sharedBooksService;

    @Autowired
    protected ManageUsersService manageUsersService;

    @ModelAttribute(CURRENT_USER_ATTR)
    public User currentUser(Principal principal) throws NotFoundException {
        return manageUsersService.findByUsername(principal.getName());
    }

    @RequestMapping(method = RequestMethod.GET)
    public String add(Model model) {
        BookDetails bookDetails = new BookDetails();
        model.addAttribute("bookDetails", bookDetails);
        return "addBook";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String addBook(BookDetails bookDetails, BindingResult result, @ModelAttribute(CURRENT_USER_ATTR) User user)
            throws NotUpdatedException {
        addBookValidator.validate(bookDetails, result);
        if (result.hasErrors()) {
            return "addBook";
        }
        SharedBook sharedBook = new SharedBook();
        sharedBook.setDetails(bookDetails);
        sharedBook.setManagedBy(user);
        sharedBook.setStatus(SharedBookStatus.AVAILABLE);
        sharedBooksService.add(sharedBook, user);
        return redirectToSharedBook(sharedBook.getId());
    }
}
