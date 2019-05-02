package com.redhat.demo.saga.insurance.rest;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class InsuranceDto {

    private Long id;

    private String orderId;

    private Long ticketId;

    private String accountId;

    private String name;

    private Double insuranceCost;

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

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
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

    public Double getInsuranceCost() {
        return insuranceCost;
    }

    public void setInsuranceCost(Double insuranceCost) {
        this.insuranceCost = insuranceCost;
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
}
