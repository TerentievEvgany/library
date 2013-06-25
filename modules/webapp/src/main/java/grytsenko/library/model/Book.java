package grytsenko.library.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * A copy of book.
 * 
 * <p>
 * The copy of book can be managed by users.
 */
@Entity(name = "books")
public class Book implements Serializable {

    private static final long serialVersionUID = 6759600794860542365L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "details_id", nullable = false)
    private BookDetails details;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private BookStatus status;
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

    @Version
    private Integer version;

    public Book() {
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

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
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
        return status == BookStatus.AVAILABLE;
    }

    /**
     * Checks that book can be reserved.
     */
    public boolean canBeReserved() {
        return isAvailable();
    }

    /**
     * Checks that book is reserved.
     */
    public boolean isReserved() {
        return status == BookStatus.RESERVED;
    }

    /**
     * Checks that book is borrowed.
     */
    public boolean isBorrowed() {
        return status == BookStatus.BORROWED;
    }

    /**
     * Checks that book is reserved by a specific user.
     */
    public boolean isUsedBy(User user) {
        return user.identicalTo(usedBy);
    }

    /**
     * Checks that book is managed by user.
     */
    public boolean isManagedBy(User user) {
        if (managedBy == null) {
            throw new IllegalStateException("Book is not managed by anyone.");
        }

        return user.identicalTo(managedBy);
    }

    /**
     * Reserves a book.
     */
    public void reserve(User reservedBy, Date reservedAt) {
        if (!canBeReserved()) {
            throw new IllegalStateException("Book can not be reserved.");
        }

        status = BookStatus.RESERVED;
        statusChanged = reservedAt;

        this.usedBy = reservedBy;
        this.usedSince = reservedAt;
    }

    /**
     * Checks that book can be released.
     * 
     * <p>
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

        status = BookStatus.AVAILABLE;
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

        status = BookStatus.BORROWED;
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

        status = BookStatus.AVAILABLE;
        statusChanged = returnedAt;

        usedBy = null;
        usedSince = null;
    }

}
