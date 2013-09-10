package grytsenko.library.view.controller;

import static grytsenko.library.view.Navigation.CURRENT_USER_ATTR;
import static grytsenko.library.view.Navigation.USER_PATH;
import grytsenko.library.model.book.SharedBook;
import grytsenko.library.model.user.User;
import grytsenko.library.repository.NotFoundException;
import grytsenko.library.service.book.SearchSharedBooksService;
import grytsenko.library.service.user.ManageUsersService;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Processes a requests for user details.
 */
@Controller
@RequestMapping(USER_PATH)
@SessionAttributes(CURRENT_USER_ATTR)
public class UserController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(UserController.class);

    @Autowired
    protected ManageUsersService manageUsersService;
    @Autowired
    protected SearchSharedBooksService searchSharedBooksService;

    @ModelAttribute(CURRENT_USER_ATTR)
    public User currentUser(Principal principal) throws NotFoundException {
        return manageUsersService.findByUsername(principal.getName());
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getUserDetails(@ModelAttribute(CURRENT_USER_ATTR) User user,
            Model model) {
        LOGGER.debug("Get details about user {}.", user.getId());

        List<SharedBook> usedBooks = searchSharedBooksService.findUsedBy(user);
        LOGGER.debug("{} uses {} books.", user.getUsername(), usedBooks.size());
        model.addAttribute("usedBooks", usedBooks);

        List<SharedBook> expectedBooks = searchSharedBooksService
                .findExpectedBy(user);
        LOGGER.debug("{} expects for {} books.", user.getUsername(),
                expectedBooks.size());
        model.addAttribute("expectedBooks", expectedBooks);

        return USER_PATH;
    }

}
