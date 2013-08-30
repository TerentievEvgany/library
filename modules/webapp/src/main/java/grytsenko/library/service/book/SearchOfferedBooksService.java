package grytsenko.library.service.book;

import static java.text.MessageFormat.format;
import grytsenko.library.model.book.OfferedBook;
import grytsenko.library.model.book.SearchResults;
import grytsenko.library.repository.NotFoundException;
import grytsenko.library.repository.book.OfferedBooksRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Searches shared books.
 */
@Service
public class SearchOfferedBooksService {

    @Autowired
    protected OfferedBooksRepository offeredBooksRepository;

    /**
     * Finds a book.
     * 
     * @return the found book.
     * 
     * @throws NotFoundException
     *             if book was not found.
     */
    public OfferedBook find(long bookId) throws NotFoundException {
        OfferedBook book = offeredBooksRepository.findOne(bookId);

        if (book == null) {
            throw new NotFoundException(format("Book {0} was not found.",
                    bookId));
        }

        return book;
    }

    /**
     * Finds all books.
     */
    public SearchResults<OfferedBook> findAll(Integer pageNum, int pageSize) {
        if (pageNum < 0) {
            throw new IllegalArgumentException(
                    "The page number less than zero.");
        }

        PageRequest pageRequest = new PageRequest(pageNum, pageSize);
        Page<OfferedBook> page = offeredBooksRepository
                .findAllVoted(pageRequest);
        return SearchResults.create(page);
    }

}
