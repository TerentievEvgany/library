package grytsenko.library.service.user;

import grytsenko.library.model.user.DsUser;
import grytsenko.library.model.user.User;
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
     * Finds a user by its name.
     * 
     * <p>
     * If user is not found, then new user will be created.
     */
    public User find(String username) {
        LOGGER.debug("Search user {}.", username);
        return usersRepository.findByUsername(username);
    }

    /**
     * Updates user.
     */
    public void update(User user) {
        LOGGER.debug("Update user {}.", user.getUsername());
        usersRepository.saveAndFlush(user);
    }

    /**
     * Searches for a user in DS and then synchronizes it.
     */
    public void syncWithDs(User user) {
        String username = user.getUsername();
        LOGGER.debug("Sync user {} with DS.", username);

        DsUser dsUser = dsUsersRepository.findByUsername(username);

        if (dsUser == null) {
            LOGGER.debug("User {} not found in DS.", username);
            return;
        }

        user.syncWith(dsUser);

        LOGGER.debug("User {} synced with DS.", username);
    }

}
