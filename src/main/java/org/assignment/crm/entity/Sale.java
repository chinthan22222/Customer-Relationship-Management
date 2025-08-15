package org.assignment.crm.entity;

import jakarta.persistence.*;
import org.assignment.crm.enums.SaleStatus;
import org.hibernate.annotations.OnDelete;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sale_details")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime saleDate;

    @Enumerated(EnumType.STRING)
    private SaleStatus status;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = org.hibernate.annotations.OnDeleteAction.SET_NULL)
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sales_rep_id", nullable = true)
    @OnDelete(action = org.hibernate.annotations.OnDeleteAction.SET_NULL)
    private User salesRep;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Sale(Long id, BigDecimal amount, LocalDateTime saleDate, SaleStatus status, String description, Customer customer, User salesRep, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.amount = amount;
        this.saleDate = saleDate;
        this.status = status;
        this.description = description;
        this.customer = customer;
        this.salesRep = salesRep;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Sale() {
        super();
    }

    public void setUpdatedAt(LocalDateTime updatedAt){
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getUpdatedAt(){
        return updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public SaleStatus getStatus() {
        return status;
    }

    public void setStatus(SaleStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public User getSalesRep() {
        return salesRep;
    }

    public void setSalesRep(User salesRep) {
        this.salesRep = salesRep;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", amount=" + amount +
                ", saleDate=" + saleDate +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", customer=" + customer +
                ", salesRep=" + salesRep +
                ", createdAt=" + createdAt +
                '}';
    }
}
