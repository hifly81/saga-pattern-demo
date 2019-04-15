package com.redhat.demo.saga.ticket.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.demo.saga.ticket.constant.PaymentEventType;
import com.redhat.demo.saga.ticket.event.ProcessedEvent;
import com.redhat.demo.saga.ticket.event.TicketEvent;
import com.redhat.demo.saga.ticket.constant.TicketEventType;
import com.redhat.demo.saga.ticket.model.Ticket;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.time.Instant;

import com.redhat.demo.saga.ticket.model.TicketState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class TicketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TicketService.class);

    @Inject
    EntityManager entityManager;

    @Inject
    EventService eventService;

    @Transactional
    public Ticket bookTicket(Ticket ticket) {


        Ticket existing = findTicketsByAccountAndState(ticket.getAccountId(), TicketState.TICKET_BOOKED_PENDING);
        if(existing != null) {
            LOGGER.error("A pending booking with orderId {} exists for account {}", existing.getOrderId(), ticket.getAccountId());
            existing.setMessageSeverityTicket("ERROR");
            existing.setMessageOnTicket("Pending booking, same account!");
            return existing;
        }

        existing = findTicketsByOrderIdAndState(ticket.getOrderId(), TicketState.TICKET_BOOKED);
        if(existing != null) {
            LOGGER.error("orderId {} already booked for account {}", existing.getOrderId(), ticket.getAccountId());
            existing.setMessageSeverityTicket("ERROR");
            existing.setMessageOnTicket("Already booked, same orderId!");
            return existing;
        }

        existing = findTicketsByOrderIdAndState(ticket.getOrderId(), TicketState.TICKET_PAYMENT_REFUSED);
        if(existing != null) {
            LOGGER.error("orderId {} already refused for account {}", existing.getOrderId(), ticket.getAccountId());
            existing.setMessageSeverityTicket("ERROR");
            existing.setMessageOnTicket("Already refused, same orderId!");
            return existing;
        }


        ticket.setState(TicketState.TICKET_BOOKED_PENDING);

        entityManager.persist(ticket);
        entityManager.flush();

        TicketEvent ticketEvent = new TicketEvent();
        ticketEvent.setCorrelationId(ticket.getOrderId());
        ticketEvent.setAccountId(ticket.getAccountId());
        ticketEvent.setCreatedOn(Instant.now());
        ticketEvent.setItemId(ticket.getId());
        ticketEvent.setTotalCost(ticket.getTotalCost());
        ticketEvent.setItemEventType(TicketEventType.TICKET_CREATED);
        ticketEvent.setInsuranceRequired(ticket.getInsuranceRequired());

        entityManager.persist(ticketEvent);
        entityManager.flush();

        return ticket;
    }

    public Ticket findTicketsByAccountAndState(String accountId, TicketState ticketState) {
        Ticket ticket = null;
        try {
            ticket = (Ticket) entityManager.createNamedQuery("Ticket.findByAccountAndState")
                    .setParameter("accountId", accountId)
                    .setParameter("state", ticketState)
                    .getSingleResult();
        }
        catch (NoResultException nre){ }
        return ticket;
    }

    public Ticket findTicketsByOrderIdAndState(String orderId, TicketState ticketState) {
        Ticket ticket = null;
        try {
            ticket = (Ticket) entityManager.createNamedQuery("Ticket.findByOrderIdAndState")
                    .setParameter("orderId", orderId)
                    .setParameter("state", ticketState)
                    .getSingleResult();
        }
        catch (NoResultException nre){ }
        return ticket;
    }

    @Transactional
    public void onPaymentReceived(String correlationId, JsonNode json) {

        String itemEventType = json.get("itemeventtype").asText();

        LOGGER.info("Received payment event: orderId {} - type {}" , correlationId, itemEventType);

        if(eventService.isEventProcessed(correlationId, itemEventType)) {
            LOGGER.error("A payment event with same orderId {} already processed, discard!", correlationId);
            return;
        }

        LOGGER.info("Finding ticket for orderId {} - state {}" , correlationId, TicketState.TICKET_BOOKED_PENDING);

        //find ticket
        Ticket ticket = findTicketsByOrderIdAndState(correlationId, TicketState.TICKET_BOOKED_PENDING);
        if(ticket != null) {

            LOGGER.info("Found ticket {}", ticket.getId());

            //verify item
            if(itemEventType.equals(PaymentEventType.PAYMENT_ACCEPTED.name())) {
                ticket.setState(TicketState.TICKET_BOOKED);

            } else if(itemEventType.equals(PaymentEventType.PAYMENT_REFUSED.name())) {
                ticket.setState(TicketState.TICKET_PAYMENT_REFUSED);
            }

            entityManager.merge(ticket);
            entityManager.flush();

            LOGGER.info("Ticket {} - new state {}", ticket.getId(), itemEventType);
        }

        //create ProcessedEvent
        ProcessedEvent processedEvent = new ProcessedEvent();
        processedEvent.setCorrelationId(correlationId);
        processedEvent.setReceivedOn(Instant.now());
        processedEvent.setEventType(itemEventType);
        eventService.processEvent(processedEvent);

    }

}
