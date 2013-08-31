package grytsenko.library.view;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

/**
 * Constants for requests mapping.
 */
public final class Navigation {

    public static final String LOGIN_PATH = "/login";
    public static final String SEARCH_PATH = "/search";
    public static final String USER_PATH = "/user";
    public static final String SHARED_BOOK_PATH = "/shared-book";

    public static final String BOOK_ID_PARAM = "bookId";
    public static final String PAGE_NUM_PARAM = "pageNum";

    public static final String CURRENT_USER_ATTR = "currentUser";

    public static String redirectToSearch() {
        return "redirect:" + SEARCH_PATH;
    }

    public static String redirectToSharedBook(Long bookId) {
        return MessageFormat.format("redirect:{0}?{1}={2}", SHARED_BOOK_PATH,
                BOOK_ID_PARAM, bookId);
    }

    public static Long getBookIdFromRequest(HttpServletRequest request) {
        return Long.parseLong(request.getParameter(BOOK_ID_PARAM));
    }

    private Navigation() {
    }

}
