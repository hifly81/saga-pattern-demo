package com.redhat.demo.saga.ticket.event;

import com.redhat.demo.saga.ticket.constant.TicketEventType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"correlationid", "itemeventtype"})
)
public class TicketEvent implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    //orderId is the correlationId
    @Column(nullable = false)
    private String correlationId;

    @Column(nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private String accountId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketEventType itemEventType;

    @Column(nullable = false)
    private Double totalCost;

    private String insuranceRequired;

    private Instant createdOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public TicketEventType getItemEventType() {
        return itemEventType;
    }

    public void setItemEventType(TicketEventType itemEventType) {
        this.itemEventType = itemEventType;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public String getInsuranceRequired() {
        return insuranceRequired;
    }

    public void setInsuranceRequired(String insuranceRequired) {
        this.insuranceRequired = insuranceRequired;
    }

}
