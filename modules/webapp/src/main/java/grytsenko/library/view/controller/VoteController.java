package grytsenko.library.view.controller;

import static grytsenko.library.view.Navigation.CURRENT_USER_ATTR;
import static grytsenko.library.view.Navigation.PAGE_NUM_PARAM;
import static grytsenko.library.view.Navigation.VOTE_PATH;
import static grytsenko.library.view.ViewConstants.THUMBNAILS_PER_PAGE;
import grytsenko.library.model.book.OfferedBook;
import grytsenko.library.model.book.SearchResults;
import grytsenko.library.model.user.User;
import grytsenko.library.repository.NotFoundException;
import grytsenko.library.service.book.SearchOfferedBooksService;
import grytsenko.library.service.user.ManageUsersService;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Processes a vote requests.
 */
@Controller
@RequestMapping(VOTE_PATH)
@SessionAttributes(CURRENT_USER_ATTR)
public class VoteController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(VoteController.class);

    @Autowired
    protected ManageUsersService manageUsersService;
    @Autowired
    protected SearchOfferedBooksService searchOfferedBooksService;

    @ModelAttribute(CURRENT_USER_ATTR)
    public User currentUser(Principal principal) throws NotFoundException {
        return manageUsersService.findByUsername(principal.getName());
    }

    /**
     * User views list of books.
     */
    @RequestMapping(method = RequestMethod.GET)
    public String searchAll(
            @RequestParam(value = PAGE_NUM_PARAM, defaultValue = "0") Integer pageNum,
            @ModelAttribute(CURRENT_USER_ATTR) User user, Model model) {
        LOGGER.debug("Find all offered books.");
        LOGGER.debug("Take page {}.", pageNum);

        SearchResults<OfferedBook> books = searchOfferedBooksService.findAll(
                pageNum, THUMBNAILS_PER_PAGE);
        model.addAttribute("searchResults", books);

        return VOTE_PATH;
    }

}
