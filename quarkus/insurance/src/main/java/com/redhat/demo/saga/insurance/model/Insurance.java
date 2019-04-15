package com.redhat.demo.saga.insurance.model;

import javax.persistence.*;

@Entity
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"orderid", "state", "ticketid"})
)
@NamedQuery(name = "Insurance.findByAccountAndState",
        query = "SELECT i FROM Insurance i where i.accountId = :accountId and i.state = :state")
@NamedQuery(name = "Insurance.findByOrderIdAndState",
        query = "SELECT i FROM Insurance i where i.orderId = :orderId and i.state = :state")
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Long ticketId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InsuranceState state;

    @Column(nullable = false)
    private String accountId;

    private String name;

    private Double ticketCost;

    private Double insuranceCost;

    @Transient
    private String messageOnTicket;

    @Transient
    private String messageSeverityTicket;

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public InsuranceState getState() {
        return state;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setState(InsuranceState state) {
        this.state = state;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
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

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTicketCost() {
        return ticketCost;
    }

    public void setTicketCost(Double ticketCost) {
        this.ticketCost = ticketCost;
    }

    public Double getInsuranceCost() {
        return insuranceCost;
    }

    public void setInsuranceCost(Double insuranceCost) {
        this.insuranceCost = insuranceCost;
    }
}
