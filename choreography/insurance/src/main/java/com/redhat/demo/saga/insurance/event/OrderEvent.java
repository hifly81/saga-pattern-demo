package com.redhat.demo.saga.insurance.event;

import com.redhat.demo.saga.insurance.constant.OrderEventType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"correlationid", "itemeventtype"})
)
public class OrderEvent implements Serializable {


    //orderId is the correlationId
    @Id
    private String correlationId;

    @Column(nullable = false)
    private String accountId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderEventType itemEventType;

    @Column(nullable = false)
    private Double totalCost;

    private Instant createdOn;

    public OrderEventType getItemEventType() {
        return itemEventType;
    }

    public void setItemEventType(OrderEventType itemEventType) {
        this.itemEventType = itemEventType;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
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

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
