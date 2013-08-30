package grytsenko.library.repository.user;

import grytsenko.library.model.user.DsUser;

import java.text.MessageFormat;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Repository;

/**
 * Repository of users in corporate directory service.
 * 
 * <p>
 * We use LDAP to access directory service.
 */
@Repository
public class DsUsersRepository {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DsUsersRepository.class);

    @Autowired
    protected LdapContextSource ldapContextSource;

    @Value("#{ldapProperties['ldap.users']}")
    protected String base;
    @Value("#{ldapProperties['ldap.users.filter']}")
    protected String filterTemplate;

    @Value("#{ldapProperties['ldap.user.username']}")
    protected String usernameAttr;
    @Value("#{ldapProperties['ldap.user.firstname']}")
    protected String firstnameAttr;
    @Value("#{ldapProperties['ldap.user.lastname']}")
    protected String lastnameAttr;
    @Value("#{ldapProperties['ldap.user.mail']}")
    protected String mailAttr;

    /**
     * Finds a user in directory service.
     * 
     * <p>
     * If several users will be found, then the first user will be returned.
     * 
     * @param username
     *            the unique name of user.
     * 
     * @return the found user or <code>null</code> if user was not found.
     */
    public DsUser findByUsername(String username) {
        LOGGER.debug("Search user {} in DS.", username);

        LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource);
        String filter = MessageFormat.format(filterTemplate, username);
        LOGGER.debug("Base DN is '{}' and filter is '{}'.", base, filter);

        List<?> foundUsers = ldapTemplate.search(base, filter,
                new DsUserMapper());
        if (foundUsers.isEmpty()) {
            LOGGER.error("User {} not found in DS.", username);
            return null;
        }
        if (foundUsers.size() > 0) {
            LOGGER.warn("Several users were found in DS.", username);
        }

        DsUser foundUser = (DsUser) foundUsers.get(0);

        LOGGER.debug("User {} was found in DS.", foundUser.getUsername());
        return foundUser;
    }

    private class DsUserMapper implements AttributesMapper {

        @Override
        public DsUser mapFromAttributes(Attributes attributes)
                throws NamingException {
            DsUser user = new DsUser();

            Attribute username = attributes.get(usernameAttr);
            user.setUsername((String) username.get());

            Attribute firstname = attributes.get(firstnameAttr);
            if (firstname != null) {
                user.setFirstname((String) firstname.get());
            }

            Attribute lastname = attributes.get(lastnameAttr);
            if (lastname != null) {
                user.setLastname((String) lastname.get());
            }

            Attribute mail = attributes.get(mailAttr);
            if (mail != null) {
                user.setMail((String) mail.get());
            }

            return user;
        }

    }

}
