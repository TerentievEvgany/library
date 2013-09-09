package grytsenko.library.view.controller;

import grytsenko.library.model.book.BookDetails;
import grytsenko.library.model.book.OfferedBook;
import grytsenko.library.model.user.User;
import grytsenko.library.service.book.BookNotUpdatedException;
import grytsenko.library.service.book.ManageOfferedBooksService;
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
import static grytsenko.library.view.Navigation.redirectToOfferedBook;

/**
 * Processes requests for adding a new book.
 */
@Controller
@RequestMapping(ADD_BOOK_PATH)
public class AddBookController {

    @Autowired
    private AddBookValidator addBookValidator;

    @Autowired
    private ManageOfferedBooksService manageOfferedBooksService;

    @Autowired
    protected ManageUsersService manageUsersService;

    @ModelAttribute(CURRENT_USER_ATTR)
    public User currentUser(Principal principal) {
        return manageUsersService.find(principal.getName());
    }

    @RequestMapping(method = RequestMethod.GET)
    public String add(Model model) {
        BookDetails bookDetails = new BookDetails();
        model.addAttribute("bookDetails", bookDetails);
        return "addBook";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String addBook(BookDetails bookDetails, BindingResult result, @ModelAttribute(CURRENT_USER_ATTR) User user)
            throws BookNotUpdatedException {
        addBookValidator.validate(bookDetails, result);
        if (result.hasErrors()) {
            return "addBook";
        }
        OfferedBook offeredBook = new OfferedBook();
        offeredBook.setDetails(bookDetails);
        manageOfferedBooksService.add(offeredBook, user);
        return redirectToOfferedBook(offeredBook.getId());
    }
}
