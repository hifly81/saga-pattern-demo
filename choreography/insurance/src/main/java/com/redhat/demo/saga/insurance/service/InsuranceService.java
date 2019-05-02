package com.redhat.demo.saga.insurance.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.demo.saga.insurance.constant.OrderEventType;
import com.redhat.demo.saga.insurance.constant.PaymentEventType;
import com.redhat.demo.saga.insurance.constant.TicketEventType;
import com.redhat.demo.saga.insurance.event.*;
import com.redhat.demo.saga.insurance.model.Insurance;
import com.redhat.demo.saga.insurance.model.InsuranceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.time.Instant;

import static com.redhat.demo.saga.insurance.model.InsuranceState.INSURANCE_NOT_NEEDED;
import static com.redhat.demo.saga.insurance.model.InsuranceState.INSURANCE_PREPARED;

@ApplicationScoped
public class InsuranceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsuranceService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    EntityManager entityManager;

    @Inject
    EventService eventService;

    @Transactional
    public Insurance bookInsurance(Insurance insurance) {
        Insurance existing = findInsurancesByAccountAndState(insurance.getAccountId(), InsuranceState.INSURANCE_BOOKED_PENDING);
        if(existing != null) {
            LOGGER.error("A pending booking with orderId {} exists for account {}", existing.getOrderId(), insurance.getAccountId());
            existing.setMessageSeverityTicket("ERROR");
            existing.setMessageOnTicket("Pending booking, same account!");
            return existing;
        }

        //find the insurance
        existing = findInsurancesByOrderIdAndState(insurance.getOrderId(), INSURANCE_PREPARED);
        if(existing == null) {
            LOGGER.warn("No insurance in state PREPARED for orderId {}", insurance.getOrderId());
            existing = new Insurance();
            existing.setMessageSeverityTicket("ERROR");
            existing.setMessageOnTicket("No insurance PREPARED!");
            return existing;
        }

        LOGGER.info("Insurance acquired, can complete order {}", existing.getOrderId());

        // Update insurance
        existing.setName(insurance.getName());
        existing.setInsuranceCost(insurance.getInsuranceCost());
        existing.setState(InsuranceState.INSURANCE_BOOKED_PENDING);

        entityManager.persist(existing);
        entityManager.flush();

        // Create Order
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setCreatedOn(Instant.now());
        orderEvent.setItemEventType(OrderEventType.ORDER_COMPLETED);
        orderEvent.setAccountId(existing.getAccountId());
        orderEvent.setCorrelationId(existing.getOrderId());
        //total amount for order
        orderEvent.setTotalCost(existing.getTicketCost() + existing.getInsuranceCost());

        entityManager.persist(orderEvent);
        entityManager.flush();

        return existing;

    }


    @Transactional
    public void onEventReceived(String orderId, String payload) {

        LOGGER.info("Received event: orderId {} - payload {}" , orderId, payload);

        JsonNode json;
        String accountId;
        String itemEventType;
        Long itemId;
        Double itemCost;
        String insuranceRequired;

        try {
            json = objectMapper.readTree(payload);
            accountId = json.get("accountid").asText();
            itemEventType = json.get("itemeventtype").asText();
            itemId = json.get("itemid").asLong();
            itemCost = json.get("totalcost").asDouble();
            insuranceRequired = json.get("insurancerequired").asText();
        } catch (Exception e) {
            LOGGER.error("Can't create JsonNode {}", e);
            return;
        }

        //verify if item is already processed
        if(eventService.isEventProcessed(orderId, itemEventType)) {
            LOGGER.error("An event with same order id {}, itemEventType {} already processed, discard!", orderId, itemEventType);
            return;
        }


        Insurance existing = findInsurancesByAccountAndState(accountId, InsuranceState.INSURANCE_BOOKED_PENDING);
        if(existing != null) {
            LOGGER.error("A pending booking with orderId {} exists for account {}", existing.getOrderId(), accountId);
            return;
        }

        LOGGER.info("Received eventtype {} for order id {}", itemEventType, orderId);

        //verify item
        if(itemEventType.equals(TicketEventType.TICKET_CREATED.name())) {

            if(Boolean.valueOf(insuranceRequired)) {

                LOGGER.info("Insurance is required for orderId {} and ticketId {}", orderId, itemId);

                //create insurance
                Insurance insurance = new Insurance();
                insurance.setState(INSURANCE_PREPARED);
                insurance.setOrderId(orderId);
                insurance.setAccountId(accountId);
                insurance.setTicketId(itemId);
                insurance.setTicketCost(itemCost);

                entityManager.persist(insurance);
                entityManager.flush();


            } else {

                LOGGER.info("Insurance is NOT required for orderId {} and ticketId {}", orderId, itemId);

                //create insurance
                Insurance insurance = new Insurance();
                insurance.setState(INSURANCE_NOT_NEEDED);
                insurance.setOrderId(orderId);
                insurance.setAccountId(accountId);
                insurance.setTicketId(itemId);
                insurance.setTicketCost(itemCost);

                entityManager.persist(insurance);
                entityManager.flush();


                // Create Order
                OrderEvent orderEvent = new OrderEvent();
                orderEvent.setCreatedOn(Instant.now());
                orderEvent.setItemEventType(OrderEventType.ORDER_COMPLETED);
                orderEvent.setAccountId(existing.getAccountId());
                orderEvent.setCorrelationId(existing.getOrderId());
                //total amount for order
                orderEvent.setTotalCost(existing.getTicketCost());

                entityManager.persist(orderEvent);
                entityManager.flush();

            }

            //create ProcessedEvent
            ProcessedEvent processedEvent = new ProcessedEvent();
            processedEvent.setCorrelationId(orderId);
            processedEvent.setReceivedOn(Instant.now());
            processedEvent.setEventType(itemEventType);
            eventService.processEvent(processedEvent);



        }  else {
            LOGGER.error("Event type not recognized!");
            return;
        }

    }

    @Transactional
    public void onPaymentReceived(String correlationId, JsonNode json) {

        String itemEventType = json.get("itemeventtype").asText();

        LOGGER.info("Received payment event: orderId {} - type {}" , correlationId, itemEventType);

        if(eventService.isEventProcessed(correlationId, itemEventType)) {
            LOGGER.error("A payment event with same orderId {} itemEventType {} already processed, discard!", correlationId, itemEventType);
            return;
        }

        LOGGER.info("Finding insurance for orderId {} - state {}" , correlationId, InsuranceState.INSURANCE_BOOKED_PENDING);

        //find insurance
        Insurance insurance = findInsurancesByOrderIdAndState(correlationId, InsuranceState.INSURANCE_BOOKED_PENDING);
        if(insurance != null) {

            LOGGER.info("Found insurance {}", insurance.getId());

            //verify item
            if(itemEventType.equals(PaymentEventType.PAYMENT_ACCEPTED.name())) {
                insurance.setState(InsuranceState.INSURANCE_BOOKED);

            } else if(itemEventType.equals(PaymentEventType.PAYMENT_REFUSED.name())) {
                insurance.setState(InsuranceState.INSURANCE_PAYMENT_REFUSED);
            }

            entityManager.merge(insurance);
            entityManager.flush();

            LOGGER.info("Insurance {} - new state {}", insurance.getId(), itemEventType);
        }

        //create ProcessedEvent
        ProcessedEvent processedEvent = new ProcessedEvent();
        processedEvent.setCorrelationId(correlationId);
        processedEvent.setReceivedOn(Instant.now());
        processedEvent.setEventType(itemEventType);
        eventService.processEvent(processedEvent);

    }

    public Insurance findInsurancesByAccountAndState(String accountId, InsuranceState insuranceState) {
        Insurance insurance = null;
        try {
            insurance = (Insurance) entityManager.createNamedQuery("Insurance.findByAccountAndState")
                    .setParameter("accountId", accountId)
                    .setParameter("state", insuranceState)
                    .getSingleResult();
        }
        catch (NoResultException nre){ }
        return insurance;
    }

    public Insurance findInsurancesByOrderIdAndState(String orderId, InsuranceState insuranceState) {

        Insurance insurance = null;
        try {
            insurance = (Insurance) entityManager.createNamedQuery("Insurance.findByOrderIdAndState")
                    .setParameter("orderId", orderId)
                    .setParameter("state", insuranceState)
                    .getSingleResult();
        }
        catch (NoResultException nre){ }
        return insurance;
    }


}
