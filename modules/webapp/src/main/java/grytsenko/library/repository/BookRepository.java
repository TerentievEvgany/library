package grytsenko.library.repository;

import grytsenko.library.model.Book;
import grytsenko.library.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository of books.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("FROM books book WHERE book.reservedBy = :user OR book.borrowedBy = :user")
    List<Book> findRelatedTo(@Param("user") User user);

}
