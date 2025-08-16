package org.assignment.crm.entity;

import jakarta.persistence.*;
import org.assignment.crm.enums.InteractionType;

import java.time.LocalDateTime;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "customer_interactions")
public class CustomerInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private InteractionType type;

    @Column(nullable = false)
    private LocalDateTime interactionDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User performedBy;

    private LocalDateTime createdTime;
    private LocalDateTime updateTime;

    @Override
    public String toString() {
        return "CustomerInteraction{" +
                "id=" + id +
                ", type=" + type +
                ", interactionDate=" + interactionDate +
                ", notes='" + notes + '\'' +
                ", customer=" + customer +
                ", performedBy=" + performedBy +
                ", createdTime=" + createdTime +
                ", updateTime=" + updateTime +
                '}';
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public CustomerInteraction(Long id, InteractionType type, LocalDateTime interactionDate, String notes, Customer customer, User performedBy, LocalDateTime createdTime, LocalDateTime updateTime) {
        this.id = id;
        this.type = type;
        this.interactionDate = interactionDate;
        this.notes = notes;
        this.customer = customer;
        this.performedBy = performedBy;
        this.createdTime=createdTime;
        this.updateTime=updateTime;
    }

    public CustomerInteraction() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InteractionType getType() {
        return type;
    }

    public void setType(InteractionType type) {
        this.type = type;
    }

    public LocalDateTime getInteractionDate() {
        return interactionDate;
    }

    public void setInteractionDate(LocalDateTime interactionDate) {
        this.interactionDate = interactionDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

}
