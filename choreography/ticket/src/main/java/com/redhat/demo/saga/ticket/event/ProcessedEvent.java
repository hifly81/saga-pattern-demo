package com.redhat.demo.saga.ticket.event;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"correlationid", "eventtype"})
)
@NamedQuery(name = "ProcessedEvent.findByEventType",
        query = "SELECT p FROM ProcessedEvent p where p.correlationId = :correlationId and p.eventType = :eventType")
public class ProcessedEvent implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String correlationId;

    private String eventType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getReceivedOn() {
        return receivedOn;
    }

    public void setReceivedOn(Instant receivedOn) {
        this.receivedOn = receivedOn;
    }

    private Instant receivedOn;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }



}
