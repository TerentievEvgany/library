package grytsenko.library.model.book;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Information about book.
 */
@Entity
@Table(name = "books_details")
public class BookDetails implements Serializable {

    private static final long serialVersionUID = 341757671464199966L;

    public static final int TITLE_LENGTH_MAX = 100;
    public static final int AUTHORS_LENGTH_MAX = 100;
    public static final int PUBLISHER_LENGTH_MAX = 50;
    public static final int ISBN_LENGTH_MAX = 20;
    public static final int LANGUAGE_LENGTH_MAX = 20;
    public static final int URL_LENGTH_MAX = 200;
    public static final int YEAR_LENGTH_MAX = 4;
    public static final int PAGES_LENGTH_MAX = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "authors", length = AUTHORS_LENGTH_MAX)
    private String authors;
    @Column(name = "title", length = TITLE_LENGTH_MAX)
    private String title;

    @Column(name = "publisher", length = PUBLISHER_LENGTH_MAX)
    private String publisher;
    @Column(name = "year")
    private Integer year;

    @Column(name = "language", length = LANGUAGE_LENGTH_MAX)
    private String language;
    @Column(name = "pages")
    private Integer pages;

    @Column(name = "isbn", length = ISBN_LENGTH_MAX)
    private String isbn;

    @Column(name = "thumbnail_url", length = URL_LENGTH_MAX)
    private String thumbnailUrl;
    @Column(name = "image_url", length = URL_LENGTH_MAX)
    private String imageUrl;

    @Version
    private Integer version;

    public BookDetails() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}
