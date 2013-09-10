package grytsenko.library.repository.user;

import grytsenko.library.model.user.User;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository of users.
 */
public interface UsersRepository extends JpaRepository<User, Long> {

    /**
     * Find user by its name.
     */
    User findByUsername(String username);

}
