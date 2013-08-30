package grytsenko.library.service.user;

import static java.text.MessageFormat.format;
import grytsenko.library.model.user.DsUser;
import grytsenko.library.model.user.User;
import grytsenko.library.repository.NotFoundException;
import grytsenko.library.repository.NotUpdatedException;
import grytsenko.library.repository.RepositoryUtils;
import grytsenko.library.repository.user.DsUsersRepository;
import grytsenko.library.repository.user.UsersRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Manages users of library.
 */
@Service
public class ManageUsersService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ManageUsersService.class);

    @Autowired
    protected UsersRepository usersRepository;
    @Autowired
    protected DsUsersRepository dsUsersRepository;

    /**
     * Finds a user by name.
     * 
     * @param username
     *            the name of user.
     * 
     * @throws NotFoundException
     *             if user not found.
     */
    public User findByUsername(String username) throws NotFoundException {
        LOGGER.debug("Search user {}.", username);

        User user = usersRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException(format("User {0} not found.", username));
        }

        return user;
    }

    /**
     * Performs login for authenticated user.
     * 
     * @param username
     *            the name of user.
     * 
     * @throws NotUpdatedException
     *             if login failed.
     */
    public void login(String username) throws NotUpdatedException {
        User user = usersRepository.findByUsername(username);
        if (user == null) {
            LOGGER.debug("Create new user {}.", username);
            user = User.create(username);
        }

        DsUser dsUser = dsUsersRepository.findByUsername(username);
        if (dsUser != null) {
            LOGGER.debug("Merge info from DS.");
            user.syncWith(dsUser);
        }

        RepositoryUtils.save(user, usersRepository);
    }

}
