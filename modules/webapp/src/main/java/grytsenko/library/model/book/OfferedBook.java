package grytsenko.library.model.book;

import grytsenko.library.model.user.User;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.persistence.CascadeType;

/**
 * Users can vote for offered book.
 */
@Entity
@Table(name = "offered_books")
public class OfferedBook implements Serializable {

    private static final long serialVersionUID = 6232675600589204785L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "details_id", nullable = false)
    private BookDetails details;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "offered_books_votes", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> voters;

    @Version
    private Integer version;

    public OfferedBook() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BookDetails getDetails() {
        return details;
    }

    public void setDetails(BookDetails details) {
        this.details = details;
    }

    public List<User> getVoters() {
        return voters;
    }

    public void setVoters(List<User> voters) {
        this.voters = voters;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * Returns total number of voters.
     */
    public int getVotersNum() {
        return voters.size();
    }

    /**
     * Checks that book has vote from user.
     */
    public boolean hasVoter(User user) {
        return voters.contains(user);
    }

    /**
     * Adds a voter.
     */
    public void addVoter(User user) {
        if (hasVoter(user)) {
            return;
        }

        voters.add(user);
    }

}
