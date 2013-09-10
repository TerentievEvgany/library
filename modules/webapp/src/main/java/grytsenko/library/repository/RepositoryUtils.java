package grytsenko.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Utilities for work with repositories.
 */
public final class RepositoryUtils {

    /**
     * Saves entity.
     * 
     * @return the saved entity.
     * 
     * @throws NotUpdatedException
     *             if save failed.
     */
    public static <T, R extends JpaRepository<T, Long>> T save(T entity,
            R repository) throws NotUpdatedException {
        try {
            return repository.saveAndFlush(entity);
        } catch (Exception exception) {
            throw new NotUpdatedException("Could not update entity.", exception);
        }
    }

    /**
     * Deletes entity.
     * 
     * @throws NotUpdatedException
     *             if delete failed.
     */
    public static <T, R extends JpaRepository<T, Long>> void delete(T entity,
            R repository) throws NotUpdatedException {
        try {
            repository.delete(entity);
        } catch (Exception exception) {
            throw new NotUpdatedException("Could not delete entity.", exception);
        }
    }

    private RepositoryUtils() {
    }

}
