package grytsenko.library.model.book;

import grytsenko.library.model.user.User;
import grytsenko.library.util.DateUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.persistence.CascadeType;

/**
 * A copy of book that is shared.
 */
@Entity
@Table(name = "shared_books")
public class SharedBook implements Serializable {

    private static final long serialVersionUID = 6759600794860542365L;

    /**
     * Creates a shared book.
     */
    public static SharedBook create(BookDetails details, User managedBy,
            Date managedSince) {
        SharedBook book = new SharedBook();
        book.details = details;

        book.setStatus(SharedBookStatus.AVAILABLE);
        book.setStatusChanged(managedSince);

        book.setManagedBy(managedBy);
        book.setManagedSince(managedSince);

        return book;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "details_id", nullable = false)
    private BookDetails details;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = SharedBookStatus.LENGTH_MAX)
    private SharedBookStatus status;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_changed")
    private Date statusChanged;

    @ManyToOne
    @JoinColumn(name = "used_by")
    private User usedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "used_since")
    private Date usedSince;

    @ManyToOne
    @JoinColumn(name = "managed_by", nullable = false)
    private User managedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "managed_since")
    private Date managedSince;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "shared_books_subscribers", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> subscribers;

    @Version
    private Integer version;

    public SharedBook() {
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

    public SharedBookStatus getStatus() {
        return status;
    }

    public void setStatus(SharedBookStatus status) {
        this.status = status;
    }

    public Date getStatusChanged() {
        return statusChanged;
    }

    public void setStatusChanged(Date statusChanged) {
        this.statusChanged = statusChanged;
    }

    public User getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(User usedBy) {
        this.usedBy = usedBy;
    }

    public Date getUsedSince() {
        return usedSince;
    }

    public void setUsedSince(Date usedSince) {
        this.usedSince = usedSince;
    }

    public User getManagedBy() {
        return managedBy;
    }

    public void setManagedBy(User managedBy) {
        this.managedBy = managedBy;
    }

    public Date getManagedSince() {
        return managedSince;
    }

    public void setManagedSince(Date managedSince) {
        this.managedSince = managedSince;
    }

    public List<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<User> subscribers) {
        this.subscribers = subscribers;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * Checks that book is available.
     */
    public boolean isAvailable() {
        return status == SharedBookStatus.AVAILABLE;
    }

    /**
     * Checks that book is reserved.
     */
    public boolean isReserved() {
        return status == SharedBookStatus.RESERVED;
    }

    /**
     * Checks that book is borrowed.
     */
    public boolean isBorrowed() {
        return status == SharedBookStatus.BORROWED;
    }

    /**
     * Checks that book is reserved by a specific user.
     */
    public boolean isUsedBy(User user) {
        return user.equals(usedBy);
    }

    /**
     * Determines the number of days within which the book is used.
     * <p/>
     * <p/>
     * The minimum value is the one day, i.e. today.
     */
    public int usedWithin() {
        if (isAvailable()) {
            throw new IllegalStateException("Book is not used.");
        }
        if (usedSince == null) {
            throw new IllegalStateException(
                    "Unknown when used has started to use book.");
        }

        return DateUtils.daysAfter(usedSince) + 1;
    }

    /**
     * Checks that book is managed by user.
     */
    public boolean isManagedBy(User user) {
        if (managedBy == null) {
            throw new IllegalStateException("Book is not managed by anyone.");
        }

        return user.equals(managedBy);
    }

    /**
     * Checks that book can be reserved.
     */
    public boolean canBeReserved() {
        return isAvailable();
    }

    /**
     * Reserves a book.
     */
    public void reserve(User reservedBy, Date reservedAt) {
        if (!canBeReserved()) {
            throw new IllegalStateException("Book can not be reserved.");
        }

        status = SharedBookStatus.RESERVED;
        statusChanged = reservedAt;

        this.usedBy = reservedBy;
        this.usedSince = reservedAt;
    }

    /**
     * Checks that book can be released.
     * <p/>
     * <p/>
     * Only user who reserved a book or user who managed a book can release it.
     */
    public boolean canBeReleasedBy(User releasedBy) {
        return isReserved()
                && (isUsedBy(releasedBy) || isManagedBy(releasedBy));
    }

    /**
     * Releases a book.
     */
    public void release(User releasedBy, Date releasedAt) {
        if (!canBeReleasedBy(releasedBy)) {
            throw new IllegalStateException("Book can not be released.");
        }

        status = SharedBookStatus.AVAILABLE;
        statusChanged = releasedAt;

        usedBy = null;
        usedSince = null;
    }

    /**
     * Checks that book can be taken out from library.
     */
    public boolean canBeTakenOutBy(User user) {
        return isReserved() && isManagedBy(user);
    }

    /**
     * Takes out a book.
     */
    public void takeOut(User manager, Date borrowedAt) {
        if (!canBeTakenOutBy(manager)) {
            throw new IllegalStateException("Book can not be taken out.");
        }

        status = SharedBookStatus.BORROWED;
        statusChanged = borrowedAt;

        usedSince = borrowedAt;
    }

    /**
     * Checks that book can be taken back to library.
     */
    public boolean canBeTakenBackBy(User user) {
        return isBorrowed() && isManagedBy(user);
    }

    /**
     * Takes back a book to library.
     */
    public void takeBack(User manager, Date returnedAt) {
        if (!canBeTakenBackBy(manager)) {
            throw new IllegalStateException("Book can not be taken back.");
        }

        status = SharedBookStatus.AVAILABLE;
        statusChanged = returnedAt;

        usedBy = null;
        usedSince = null;
    }

    /**
     * Checks that book has a subscriber.
     */
    public boolean hasSubscriber(User subscriber) {
        return subscribers.contains(subscriber);
    }

    /**
     * Returns total number of subscribers.
     */
    public int getSubscribersNum() {
        return subscribers.size();
    }

    /**
     * Adds new subscriber.
     */
    public void addSubscriber(User subscriber) {
        if (!hasSubscriber(subscriber)) {
            subscribers.add(subscriber);
        }
    }

    /**
     * Removes a subscriber.
     */
    public void removeSubscriber(User subscriber) {
        subscribers.remove(subscriber);
    }

}
