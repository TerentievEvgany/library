package grytsenko.library.repository.user;

import static java.text.MessageFormat.format;
import grytsenko.library.model.user.DsUser;

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
    protected String usernameAttrId;
    @Value("#{ldapProperties['ldap.user.firstname']}")
    protected String firstnameAttrId;
    @Value("#{ldapProperties['ldap.user.lastname']}")
    protected String lastnameAttrId;
    @Value("#{ldapProperties['ldap.user.mail']}")
    protected String mailAttrId;

    /**
     * Finds a user in directory service.
     * 
     * @param username
     *            the unique name of user.
     * 
     * @return the found user or <code>null</code> if user was not found.
     */
    public DsUser findByUsername(String username) {
        LOGGER.debug("Search user {}.", username);

        LdapTemplate template = new LdapTemplate(ldapContextSource);
        String filter = format(filterTemplate, username);

        List<?> foundUsers = template.search(base, filter, new UserMapper());
        if (foundUsers.isEmpty()) {
            LOGGER.error("User {} not found.", username);
            return null;
        }
        if (foundUsers.size() > 1) {
            LOGGER.warn("Several users were found.", username);
        }

        DsUser foundUser = (DsUser) foundUsers.get(0);

        LOGGER.debug("User {} was found.", username);
        return foundUser;
    }

    private class UserMapper implements AttributesMapper {

        @Override
        public DsUser mapFromAttributes(Attributes attrs)
                throws NamingException {
            DsUser user = new DsUser();

            String username = asString(usernameAttrId, attrs);
            user.setUsername(username);

            String firstname = asString(firstnameAttrId, attrs);
            user.setFirstname(firstname);

            String lastname = asString(lastnameAttrId, attrs);
            user.setLastname(lastname);

            String mail = asString(mailAttrId, attrs);
            user.setMail(mail);

            return user;
        }

        private String asString(String attrId, Attributes attrs)
                throws NamingException {
            Attribute attr = attrs.get(attrId);
            return attr != null ? (String) attr.get() : "";
        }

    }

}
