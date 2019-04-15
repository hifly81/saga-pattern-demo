package com.redhat.demo.saga.ticket.rest;

import com.redhat.demo.saga.ticket.model.TicketState;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TicketDto {

    private Long id;

    private String orderId;

    private TicketState state;

    private String accountId;

    private String name;

    private String numberOfPersons;

    private Double totalCost;

    private String insuranceRequired;

    private String messageOnTicket;

    private String messageSeverityTicket;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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
