package com.redhat.demo.saga.ticket.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"orderid", "state"})
)
@NamedQuery(name = "Ticket.findByAccountAndState",
        query = "SELECT t FROM Ticket t where t.accountId = :accountId and t.state = :state")
@NamedQuery(name = "Ticket.findByOrderIdAndState",
        query = "SELECT t FROM Ticket t where t.orderId = :orderId and t.state = :state")
public class Ticket implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketState state;

    @Column(nullable = false)
    private String accountId;

    private String name;

    private String numberOfPersons;

    private Double totalCost;

    private String insuranceRequired;

    @Transient
    private String messageOnTicket;

    @Transient
    private String messageSeverityTicket;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumberOfPersons() {
        return numberOfPersons;
    }

    public void setNumberOfPersons(String numberOfPersons) {
        this.numberOfPersons = numberOfPersons;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public TicketState getState() {
        return state;
    }

    public void setState(TicketState state) {
        this.state = state;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMessageOnTicket() {
        return messageOnTicket;
    }

    public void setMessageOnTicket(String messageOnTicket) {
        this.messageOnTicket = messageOnTicket;
    }

    public String getMessageSeverityTicket() {
        return messageSeverityTicket;
    }

    public void setMessageSeverityTicket(String messageSeverityTicket) {
        this.messageSeverityTicket = messageSeverityTicket;
    }

    public String getInsuranceRequired() {
        return insuranceRequired;
    }

    public void setInsuranceRequired(String insuranceRequired) {
        this.insuranceRequired = insuranceRequired;
    }
}
