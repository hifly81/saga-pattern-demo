package com.redhat.demo.saga.payment.event;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"correlationid", "eventtype"})
)
@NamedQuery(name = "ProcessedEvent.findByEventType",
        query = "SELECT p FROM ProcessedEvent p where p.correlationId = :correlationId and p.eventType = :eventType")
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String correlationId;

    private String eventType;

    private Instant receivedOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Instant getReceivedOn() {
        return receivedOn;
    }

    public void setReceivedOn(Instant receivedOn) {
        this.receivedOn = receivedOn;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

}
