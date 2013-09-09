package grytsenko.library.validator;

import grytsenko.library.model.book.BookDetails;
import org.apache.commons.validator.UrlValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validador called when a new book add
 */
@Component
public class AddBookValidator implements Validator {

    public static final String AUTHORS_INPUT_NAME = "authors";
    public static final String TITLE_INPUT_NAME = "title";
    public static final String PUBLISHER_INPUT_NAME = "publisher";
    public static final String ISBN_INPUT_NAME = "isbn";
    public static final String LANGUAGE_INPUT_NAME = "language";
    public static final String IMAGE_URL_INPUT_NAME = "imageUrl";
    public static final String THUMBNAIL_URL_INPUT_NAME = "thumbnailUrl";
    public static final String PAGES_INPUT_NAME = "pages";
    public static final String YEAR_INPUT_NAME = "year";

    @Override
    public boolean supports(Class<?> clazz) {
        return BookDetails.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BookDetails bookDetails = (BookDetails) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, AUTHORS_INPUT_NAME, "book.error.field.empty");
        checkStringField(AUTHORS_INPUT_NAME, errors, bookDetails.getAuthors(), BookDetails.AUTHORS_LENGTH_MAX);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, TITLE_INPUT_NAME, "book.error.field.empty");
        checkStringField(TITLE_INPUT_NAME, errors, bookDetails.getTitle(), BookDetails.TITLE_LENGTH_MAX);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, PUBLISHER_INPUT_NAME, "book.error.field.empty");
        checkStringField(PAGES_INPUT_NAME, errors, bookDetails.getPublisher(), BookDetails.PUBLISHER_LENGTH_MAX);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, ISBN_INPUT_NAME, "book.error.field.empty");
        checkStringField(ISBN_INPUT_NAME, errors, bookDetails.getIsbn(), BookDetails.ISBN_LENGTH_MAX);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, LANGUAGE_INPUT_NAME, "book.error.field.empty");
        checkStringField(LANGUAGE_INPUT_NAME, errors, bookDetails.getLanguage(), BookDetails.LANGUAGE_LENGTH_MAX);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, YEAR_INPUT_NAME, "book.error.field.empty");
        checkIntField(YEAR_INPUT_NAME, bookDetails.getYear(), errors, BookDetails.YEAR_LENGTH_MAX);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, PAGES_INPUT_NAME, "book.error.field.empty");
        checkIntField(PAGES_INPUT_NAME, bookDetails.getPages(), errors, BookDetails.PAGES_LENGTH_MAX);

        checkStringField(IMAGE_URL_INPUT_NAME, errors, bookDetails.getImageUrl(), BookDetails.URL_LENGTH_MAX);
        validateUrl(IMAGE_URL_INPUT_NAME, errors, bookDetails.getImageUrl());

        checkStringField(THUMBNAIL_URL_INPUT_NAME, errors, bookDetails.getThumbnailUrl(), BookDetails.URL_LENGTH_MAX);
        validateUrl(THUMBNAIL_URL_INPUT_NAME, errors, bookDetails.getThumbnailUrl());
    }

    private void checkStringField(String fieldName, Errors errors, String field, int lengthMax) {
        if (field.length() > lengthMax) {
            errors.rejectValue(fieldName, "book.error.field.lengthmax");
        }
    }

    private void checkIntField(String fieldName, Integer fieldValue, Errors errors, int lengthMAx) {
        if (fieldValue != null) {
            if (fieldValue.toString().length() > lengthMAx) {
                errors.rejectValue(fieldName, "book.error.field.lengthmax");
            }
        }
    }

    private void validateUrl(String fieldName, Errors errors, String field) {
        if (!field.isEmpty()) {
            UrlValidator urlValidator = new UrlValidator();
            if (!(urlValidator.isValid(field))) {
                errors.rejectValue(fieldName, "book.error.invalidURL");
            }
        }
    }

}
