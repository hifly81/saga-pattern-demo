package com.redhat.demo.saga.payment.event;

import com.redhat.demo.saga.payment.constant.PaymentEventType;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"correlationid", "itemeventtype"})
)
public class PaymentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    //orderId is the correlationId
    @Column(nullable = false)
    private String correlationId;

    @Column(nullable = false)
    private String accountId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentEventType itemeventtype;

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

    public PaymentEventType getItemeventtype() {
        return itemeventtype;
    }

    public void setItemeventtype(PaymentEventType itemeventtype) {
        this.itemeventtype = itemeventtype;
    }


}
