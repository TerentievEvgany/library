package grytsenko.library.test;

import static grytsenko.library.test.Users.manager;
import static grytsenko.library.util.DateUtils.now;
import grytsenko.library.model.book.SharedBook;
import grytsenko.library.model.book.SharedBookStatus;
import grytsenko.library.model.user.User;

/**
 * Utilities for work with books in tests.
 */
public final class Books {

    public static final long BOOK_ID = 100L;

    public static SharedBook availableBook() {
        SharedBook book = new SharedBook();
        book.setId(BOOK_ID);
        book.setStatus(SharedBookStatus.AVAILABLE);
        book.setManagedBy(manager());
        return book;
    }

    public static SharedBook reservedBook(User reservedBy) {
        SharedBook book = availableBook();
        book.setStatus(SharedBookStatus.RESERVED);
        book.setUsedBy(reservedBy);
        book.setUsedSince(now());
        return book;
    }

    public static SharedBook borrowedBook(User borrowedBy) {
        SharedBook book = availableBook();
        book.setStatus(SharedBookStatus.BORROWED);
        book.setUsedBy(borrowedBy);
        book.setUsedSince(now());
        return book;
    }

    private Books() {
    }

}
