package grytsenko.library.validator;

import grytsenko.library.model.book.BookDetails;
import org.apache.commons.validator.UrlValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by Evgeny on 20.08.13.
 */
@Component
public class AddBookValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return BookDetails.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BookDetails bookDetails = (BookDetails) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "authors", "add.book.field.empty");
        String authors = bookDetails.getAuthors();
        if ((authors.length()) > BookDetails.AUTHORS_LENGTH_MAX) {
            errors.rejectValue("authors", "add.book.tooLong");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "add.book.field.empty");
        String title = bookDetails.getTitle();
        if ((title.length()) > BookDetails.TITLE_LENGTH_MAX) {
            errors.rejectValue("title", "add.book.tooLong");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "publisher", "add.book.field.empty");
        String publisher = bookDetails.getPublisher();
        if ((publisher.length()) > BookDetails.PUBLISHER_LENGTH_MAX) {
            errors.rejectValue("publisher", "add.book.tooLong");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "isbn", "add.book.field.empty");
        String isbn = bookDetails.getIsbn();
        if ((isbn.length()) > BookDetails.ISBN_LENGTH_MAX) {
            errors.rejectValue("isbn", "add.book.tooLong");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "language", "add.book.field.empty");
        String language = bookDetails.getLanguage();
        if ((language.length()) > BookDetails.LANGUAGE_LENGTH_MAX) {
            errors.rejectValue("language", "add.book.tooLong");
        }

        String imageUrl = bookDetails.getImageUrl();
        if ((imageUrl.length()) > BookDetails.URL_LENGTH_MAX) {
            errors.rejectValue("imageUrl", "add.book.tooLong");
        }
        if (!imageUrl.isEmpty()){
            UrlValidator urlValidator = new UrlValidator();
            if (!(urlValidator.isValid(imageUrl))){
                errors.rejectValue("imageUrl","add.book.invalidURL");
            }
        }


        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "year", "add.book.field.empty");
        Integer year = bookDetails.getYear();
        if (year != null) {
            if ((year.toString().length()) > BookDetails.YEAR_LENGTH_MAX){
                errors.rejectValue("year", "add.book.tooLong");
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pages", "add.book.field.empty");
        Integer pages = bookDetails.getPages();
        if (pages != null) {
            if ((pages.toString().length()) > BookDetails.PAGES_LENGTH_MAX){
                errors.rejectValue("pages", "add.book.tooLong");
            }
        }

        String thumbnailUrl = bookDetails.getThumbnailUrl();
        if ((thumbnailUrl.length()) > BookDetails.URL_LENGTH_MAX) {
            errors.rejectValue("thumbnailUrl", "add.book.tooLong");
        }
        if (!thumbnailUrl.isEmpty()){
            UrlValidator thumbnailUrlValidator = new UrlValidator();
            if (!(thumbnailUrlValidator.isValid(thumbnailUrl))){
                errors.rejectValue("thumbnailUrl","add.book.invalidURL");
            }
        }
    }
}
